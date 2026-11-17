package io.github.nexalloy.pablobaro.spotify

import io.github.nexalloy.morphe.AccessFlags
import io.github.nexalloy.morphe.Opcode
import io.github.nexalloy.morphe.fingerprint
import io.github.nexalloy.morphe.findClassDirect
import io.github.nexalloy.morphe.findFieldDirect
import io.github.nexalloy.morphe.findMethodDirect
import io.github.nexalloy.morphe.findMethodListDirect
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.enums.StringMatchType

// region Ads

val contextMenuViewModelClass = findClassDirect {
    runCatching {
        fingerprint { strings("ContextMenuViewModel(header=") }
    }.getOrElse {
        fingerprint {
            accessFlags(AccessFlags.CONSTRUCTOR)
            strings("ContextMenuViewModel cannot contain items with duplicate itemResId. id=")
            parameters("L", "Ljava/util/List;", "Z")
        }
    }.declaredClass!!
}

val signallingStorageKeyMethod = findMethodDirect {
    findMethod {
        matcher {
            usingStrings("signalling_", "_")
            paramCount = 2
            modifiers = java.lang.reflect.Modifier.PUBLIC or java.lang.reflect.Modifier.STATIC
        }
    }.firstOrNull() ?: throw NoSuchElementException("signallingStorageKeyMethod not found")
}

/**
 * Older Spotify: The context menu item view model is `ViewModel(available, enabled, isPremiumUpsell, ...)`.
 */
val premiumUpsellField = findFieldDirect {
    val methods = findMethod {
        findFirst = true
        matcher { name = "getViewModel" }
    }
    val returnType = methods.firstOrNull()?.returnType ?: throw NoSuchElementException("getViewModel not found")
    val booleans = returnType.fields.filter { it.typeName == "boolean" }
    if (booleans.size > 1) booleans[1] else booleans.firstOrNull() ?: throw NoSuchElementException("premiumUpsellField not found")
}

private fun DexKitBridge.sectionsGetters(structureClass: String) = runCatching {
    findClass {
        matcher { className(structureClass, StringMatchType.EndsWith) }
    }.findMethod {
        matcher {
            paramCount = 0
        }
    }.filter { method ->
        method.usingFields.any { it.field.name == "sections_" || it.field.name.contains("section", ignoreCase = true) } ||
            method.name.contains("section", ignoreCase = true)
    }
}.getOrElse { emptyList() }

val casitaHomeSectionsGetters = findMethodListDirect { sectionsGetters("casita.v1.resolved.HomeStructure") }

val homeSectionsGetters = findMethodListDirect { sectionsGetters("homeapi.proto.HomeStructure") }

val browseSectionsGetters = findMethodListDirect { sectionsGetters("browsita.v1.resolved.BrowseStructure") }

private fun DexKitBridge.pendragonRequests(requestClass: String) = runCatching {
    findMethod {
        matcher {
            name = "apply"
            addInvoke {
                name = "<init>"
                declaredClass(requestClass, StringMatchType.EndsWith)
            }
        }
    }
}.getOrElse { emptyList() }

val pendragonRequestMethods = findMethodListDirect {
    pendragonRequests(".FetchMessageRequest") + pendragonRequests(".FetchMessageListRequest")
}

val coroutineClientBaseConstructor = findMethodDirect {
    val classes = findClass {
        matcher {
            className("com.spotify.esperanto.esperanto.CoroutineClientBase")
        }
    }
    classes.firstOrNull()?.findMethod {
        matcher {
            name = "<init>"
            paramCount = 1
        }
    }?.firstOrNull() ?: throw NoSuchElementException("coroutineClientBaseConstructor not found")
}

val embeddedAdPlaylistPropertiesConstructor = findMethodDirect {
    val classes = findClass {
        matcher { usingStrings("android-embeddedadplaylist", "embedded_ad_placement") }
    }
    classes.firstOrNull()?.findMethod {
        matcher { name = "<init>" }
    }?.firstOrNull { it.paramCount > 2 } ?: throw NoSuchElementException("embeddedAdPlaylistPropertiesConstructor not found")
}

/**
 * Plugin entries of the now playing view are created with `(String name, Plugin plugin, boolean enabled)`.
 */
val npvPluginEntryConstructors = findMethodListDirect {
    findMethod {
        matcher { usingStrings("EmbeddedAdNpvPlugin") }
    }.flatMap { it.invokes }.filter { method ->
        val parameters = method.paramTypeNames
        method.isConstructor && parameters.size == 3 &&
            parameters[0] == "java.lang.String" && parameters[2] == "boolean"
    }.distinctBy { it.descriptor }
}

val getAdsMethods = findMethodListDirect {
    findMethod {
        matcher {
            usingStrings("spotify.ads.esperanto.proto.Ads", "GetAds")
        }
    }
}

val smartShuffleMethods = findMethodListDirect {
    runCatching {
        findMethod {
            matcher {
                returnType = "boolean"
                paramCount = 0
                usingStrings("smart_shuffle_as_default_for_liked_songs", "smart_shuffle_as_default_for_playlist")
            }
        }
    }.getOrElse { emptyList() }
}

// endregion

// region Layout

val navigationTabEnumClass = findClassDirect {
    findClass {
        matcher {
            superClass("java.lang.Enum")
            usingStrings(listOf("HOME", "SEARCH", "YOUR_LIBRARY", "PREMIUM", "CREATE"), StringMatchType.Equals)
        }
    }.single()
}

/**
 * The bottom navigation is created with `(Tab, Tab, Tab, Tab, Tab, int flags)`.
 */
val navigationTabSetConstructor = findMethodDirect {
    val tabEnum = navigationTabEnumClass().name
    val tabClasses = findClass {
        matcher { addField { type(tabEnum) } }
    }.map { it.name }.toSet()

    findMethod {
        matcher {
            name = "<init>"
            paramCount = 6
        }
    }.single { method ->
        val parameters = method.paramTypeNames
        parameters[5] == "int" && parameters.take(5).distinct().singleOrNull() in tabClasses
    }
}

// endregion

// region Misc

val canBindAppWidgetPermissionMethod = findMethodDirect {
    runCatching {
        fingerprint {
            returns("Z")
            strings("android.permission.BIND_APPWIDGET")
            opcodes(Opcode.AND_INT_LIT8)
        }
    }.getOrElse {
        // Newer versions no longer check the system app flag in the same method.
        fingerprint {
            returns("Z")
            strings("android.permission.BIND_APPWIDGET")
        }
    }
}

// endregion

// region Privacy

val shareCopyUrlMethod = findMethodDirect {
    runCatching {
        fingerprint {
            returns("Ljava/lang/Object;")
            parameters("Ljava/lang/Object;")
            strings("clipboard", "Spotify Link")
            methodMatcher { name = "invokeSuspend" }
        }
    }.getOrElse {
        fingerprint {
            returns("Ljava/lang/Object;")
            parameters("Ljava/lang/Object;")
            strings("clipboard", "createNewSession failed")
            methodMatcher { name = "apply" }
        }
    }
}

val formatAndroidShareSheetUrlMethod = findMethodDirect {
    runCatching {
        fingerprint {
            accessFlags(AccessFlags.PUBLIC, AccessFlags.STATIC)
            returns("Ljava/lang/String;")
            parameters("L", "Ljava/lang/String;")
            methodMatcher {
                addCaller { usingStrings("android.intent.extra.TEXT", "text/plain") }
            }
        }
    }.recoverCatching {
        fingerprint {
            accessFlags(AccessFlags.PUBLIC, AccessFlags.STATIC)
            returns("Ljava/lang/String;")
            parameters("L", "Ljava/lang/String;")
            literal { '\n'.code }
        }
    }.recoverCatching {
        fingerprint {
            accessFlags(AccessFlags.PUBLIC, AccessFlags.STATIC)
            returns("Ljava/lang/String;")
            parameters("L", "Ljava/lang/String;")
            strings("\n")
        }
    }.getOrElse {
        fingerprint {
            accessFlags(AccessFlags.PUBLIC)
            returns("Ljava/lang/String;")
            parameters("Lcom/spotify/share/social/sharedata/ShareData;", "Ljava/lang/String;")
        }
    }
}

// endregion
