package io.github.nexalloy.twoeno.spotify.ads

import android.media.MediaMetadata
import android.media.session.MediaSession
import app.twoeno.extension.spotify.BlockPopupAdsPatch
import app.twoeno.extension.spotify.HideAdSectionsPatch
import app.twoeno.extension.spotify.HideContextMenuUpsellsPatch
import app.twoeno.extension.spotify.HidePlayerAdsPatch
import app.twoeno.extension.spotify.MuteAudioAdsPatch
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch
import io.github.nexalloy.twoeno.spotify.browseSectionsGetters
import io.github.nexalloy.twoeno.spotify.casitaHomeSectionsGetters
import io.github.nexalloy.twoeno.spotify.contextMenuViewModelClass
import io.github.nexalloy.twoeno.spotify.embeddedAdPlaylistPropertiesConstructor
import io.github.nexalloy.twoeno.spotify.homeSectionsGetters
import io.github.nexalloy.twoeno.spotify.npvPluginEntryConstructors
import io.github.nexalloy.twoeno.spotify.pendragonRequestMethods
import io.github.nexalloy.twoeno.spotify.premiumUpsellField
import java.lang.reflect.Constructor

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

val HideContextMenuUpsells = patch(
    name = "Hide context menu upsells",
    description = "Removes \"Premium\" entries from the context menus of songs, albums and playlists.",
) {
    runCatching {
        val isPremiumUpsell = runCatching { ::premiumUpsellField.field.apply { isAccessible = true } }.getOrNull()
        val viewModelClazz = runCatching { ::contextMenuViewModelClass.clazz }.getOrNull()

        if (viewModelClazz != null && isPremiumUpsell != null) {
            XposedBridge.hookAllConstructors(viewModelClazz, object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val parameterTypes = (param.method as Constructor<*>).parameterTypes
                    parameterTypes.forEachIndexed { index, type ->
                        if (type != List::class.java) return@forEachIndexed
                        val items = param.args[index] as? List<*> ?: return@forEachIndexed
                        param.args[index] = HideContextMenuUpsellsPatch.filterContextMenuItems(items, isPremiumUpsell)
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
                // The first two parameters are the ad placement enums.
                val parameterTypes = (param.method as Constructor<*>).parameterTypes
                for (index in 0..1) {
                    HidePlayerAdsPatch.noneOf(parameterTypes[index])?.let { param.args[index] = it }
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
                        param.args[2] = HidePlayerAdsPatch.isPluginEnabled(param.args[0] as String?, param.args[2] as Boolean)
                    }
                }
            }
        }
    }
}
