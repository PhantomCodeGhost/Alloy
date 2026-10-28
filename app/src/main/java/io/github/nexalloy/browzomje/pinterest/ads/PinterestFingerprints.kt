package io.github.nexalloy.browzomje.pinterest.ads

import io.github.nexalloy.morphe.findClassDirect

val feedClassFingerprint = findClassDirect {
    findClass {
        matcher {
            usingStrings(listOf(", _items count:"))
        }
    }.first()
}

val pagedResponseClassFingerprint = findClassDirect {
    findClass {
        matcher {
            usingStrings(listOf("PagedResponse(bookmark="))
        }
    }.first()
}

val modelListWithBookmarkClassFingerprint = findClassDirect {
    findClass {
        matcher {
            usingStrings(listOf("ModelListWithBookmark(models="))
        }
    }.first()
}
