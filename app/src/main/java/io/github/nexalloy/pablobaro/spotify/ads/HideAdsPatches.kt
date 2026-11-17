package io.github.nexalloy.pablobaro.spotify.ads

import android.media.MediaMetadata
import android.media.session.MediaSession
import app.pablobaro.extension.spotify.BlockPopupAdsPatch
import app.pablobaro.extension.spotify.DisableAdBreaksPatch
import app.pablobaro.extension.spotify.HideAdSectionsPatch
import app.pablobaro.extension.spotify.HidePlayerAdsPatch
import app.pablobaro.extension.spotify.MuteAudioAdsPatch
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.pablobaro.spotify.browseSectionsGetters
import io.github.nexalloy.pablobaro.spotify.casitaHomeSectionsGetters
import io.github.nexalloy.pablobaro.spotify.contextMenuViewModelClass
import io.github.nexalloy.pablobaro.spotify.coroutineClientBaseConstructor
import io.github.nexalloy.pablobaro.spotify.embeddedAdPlaylistPropertiesConstructor
import io.github.nexalloy.pablobaro.spotify.getAdsMethods
import io.github.nexalloy.pablobaro.spotify.homeSectionsGetters
import io.github.nexalloy.pablobaro.spotify.npvPluginEntryConstructors
import io.github.nexalloy.pablobaro.spotify.pendragonRequestMethods
import io.github.nexalloy.pablobaro.spotify.premiumUpsellField
import io.github.nexalloy.pablobaro.spotify.signallingStorageKeyMethod
import io.github.nexalloy.pablobaro.spotify.smartShuffleMethods
import io.github.nexalloy.patch
import java.lang.reflect.Constructor
import java.lang.reflect.Field

val MuteAudioAds = patch(
    name = "Mute audio ads",
    description = "Mutes the music stream while an audio ad plays and restores the volume afterwards. " +
        "Enabling \"Device broadcast status\" in the Spotify settings improves the ad detection.",
) {
    MuteAudioAdsPatch.initialize(appContext)

    MediaSession::class.java.getMethod("setMetadata", MediaMetadata::class.java).hookMethod {
        after { MuteAudioAdsPatch.onMetadataChanged(it.args[0] as MediaMetadata?) }
    }
}

val DisableAdBreaks = patch(
    name = "Disable ad breaks",
    description = "Disables the ad slots that play ads between songs via Esperanto transport.",
) {
    runCatching {
        val constructor = runCatching { ::coroutineClientBaseConstructor.dexMethod }.getOrNull()
        constructor?.hookMethod {
            before { param ->
                param.args.firstOrNull()?.let { transport ->
                    DisableAdBreaksPatch.onTransportCreated(transport)
                }
            }
        }
    }
}

val DisableAds = patch(
    name = "Disable ads",
    description = "Returns an empty ad response from GetAds esperanto call so no server audio/video ads are used.",
) {
    runCatching {
        val methods = runCatching { ::getAdsMethods.dexMethodList }.getOrElse { emptyList() }
        methods.forEach { method ->
            runCatching {
                method.hookMethod {
                    before { param ->
                        val responseClass = XposedHelpers.findClassIfExists("com.spotify.ads.esperanto.proto.GetAdsResponse", classLoader)
                        param.result = if (responseClass != null) {
                            runCatching {
                                XposedHelpers.callStaticMethod(responseClass, "r", ByteArray(0))
                            }.getOrElse {
                                runCatching {
                                    XposedHelpers.callStaticMethod(responseClass, "getDefaultInstance")
                                }.getOrNull()
                            }
                        } else {
                            null
                        }
                    }
                }
            }
        }
    }
}

val HideAdSections = patch(
    name = "Hide ad sections",
    description = "Removes brand ad sections from the home and search page.",
) {
    runCatching {
        val homeGetters = runCatching { ::casitaHomeSectionsGetters.dexMethodList }.getOrElse { emptyList() } +
            runCatching { ::homeSectionsGetters.dexMethodList }.getOrElse { emptyList() }
        val browseGetters = runCatching { ::browseSectionsGetters.dexMethodList }.getOrElse { emptyList() }

        homeGetters.forEach {
            runCatching {
                it.hookMethod {
                    after { param -> (param.result as? List<*>)?.let(HideAdSectionsPatch::removeHomeSections) }
                }
            }
        }
        browseGetters.forEach {
            runCatching {
                it.hookMethod {
                    after { param -> (param.result as? List<*>)?.let(HideAdSectionsPatch::removeBrowseSections) }
                }
            }
        }
    }
}

private fun filterContextMenuItems(
    items: List<*>,
    signalType: String?,
    isPremiumUpsellField: Field?
): List<*> {
    if (items.isEmpty()) return items
    val filtered = ArrayList<Any?>(items.size)
    for (item in items) {
        if (item == null) {
            filtered.add(null)
            continue
        }
        var isUpsell = false
        // 1. Check signal for newer Spotify versions
        if (signalType != null) {
            try {
                var itemClass: Class<*>? = item.javaClass
                while (itemClass != null && itemClass != Any::class.java) {
                    for (field in itemClass.declaredFields) {
                        if (field.type.name == signalType) {
                            field.isAccessible = true
                            val signalObj = field.get(item)
                            if (signalObj != null) {
                                for (sField in signalObj.javaClass.declaredFields) {
                                    if (sField.type == Int::class.javaPrimitiveType || sField.type == java.lang.Integer::class.java) {
                                        sField.isAccessible = true
                                        val typeVal = sField.getInt(signalObj)
                                        if (typeVal == 1) { // PREMIUM_SIGNAL_TYPE
                                            isUpsell = true
                                            break
                                        }
                                    }
                                }
                            }
                        }
                        if (isUpsell) break
                    }
                    if (isUpsell) break
                    itemClass = itemClass.superclass
                }
            } catch (_: Throwable) {
            }
        }
        // 2. Fallback to boolean isPremiumUpsell field for older Spotify versions
        if (!isUpsell && isPremiumUpsellField != null) {
            try {
                if (isPremiumUpsellField.getBoolean(item)) {
                    isUpsell = true
                }
            } catch (_: Throwable) {
            }
        }
        if (!isUpsell) {
            filtered.add(item)
        }
    }
    return if (filtered.size == items.size) items else filtered
}

val HideContextMenuUpsells = patch(
    name = "Hide context menu upsells",
    description = "Removes \"Premium\" entries from the context menus of songs, albums and playlists.",
) {
    runCatching {
        val viewModelClazz = runCatching { ::contextMenuViewModelClass.clazz }.getOrNull()
        val signallingMethod = runCatching { ::signallingStorageKeyMethod.dexMethod }.getOrNull()
        val signalType = signallingMethod?.paramTypeNames?.firstOrNull()
        val isPremiumUpsell = runCatching { ::premiumUpsellField.field.apply { isAccessible = true } }.getOrNull()

        if (viewModelClazz != null) {
            XposedBridge.hookAllConstructors(viewModelClazz, object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val parameterTypes = (param.method as Constructor<*>).parameterTypes
                    parameterTypes.forEachIndexed { index, type ->
                        if (type != List::class.java) return@forEachIndexed
                        val items = param.args[index] as? List<*> ?: return@forEachIndexed
                        param.args[index] = filterContextMenuItems(items, signalType, isPremiumUpsell)
                    }
                }
            })
        }
    }
}

val BlockPopupAds = patch(
    name = "Block popup ads",
    description = "Blocks fullscreen promotions (\"Pendragon\" messages) shown when opening the app.",
) {
    runCatching {
        val requests = runCatching { ::pendragonRequestMethods.dexMethodList }.getOrElse { emptyList() }
        requests.forEach {
            runCatching {
                it.hookMethod {
                    after { param -> param.result = BlockPopupAdsPatch.replaceRequest(param.result) }
                }
            }
        }
    }
}

val HidePlaylistAds = patch(
    name = "Hide playlist ads",
    description = "Removes ads embedded into playlists.",
) {
    runCatching {
        val constructor = runCatching { ::embeddedAdPlaylistPropertiesConstructor }.getOrNull()
        constructor?.hookMethod {
            before { param ->
                val parameterTypes = (param.method as Constructor<*>).parameterTypes
                for (index in 0 until minOf(2, parameterTypes.size)) {
                    if (parameterTypes[index].isEnum) {
                        HidePlayerAdsPatch.noneOf(parameterTypes[index])?.let { param.args[index] = it }
                    }
                }
            }
        }
    }
}

val HideVideoAds = patch(
    name = "Hide video ads",
    description = "Disables the video ad plugin of the now playing view.",
) {
    runCatching {
        val entries = runCatching { ::npvPluginEntryConstructors.dexMethodList }.getOrElse { emptyList() }
        entries.forEach {
            runCatching {
                it.hookMethod {
                    before { param ->
                        val name = param.args[0] as? String
                        val enabled = (param.args[2] as? Boolean) ?: false
                        param.args[2] = HidePlayerAdsPatch.isPluginEnabled(name, enabled)
                    }
                }
            }
        }
    }
}

val DisableSmartShuffleDefault = patch(
    name = "Disable Smart Shuffle default",
    description = "Make Smart Shuffle not the default for playlists and liked songs, so playback follows the normal queue order.",
) {
    runCatching {
        val methods = runCatching { ::smartShuffleMethods.dexMethodList }.getOrElse { emptyList() }
        methods.forEach { method ->
            runCatching {
                method.hookMethod(XC_MethodReplacement.returnConstant(false))
            }
        }
    }
}
