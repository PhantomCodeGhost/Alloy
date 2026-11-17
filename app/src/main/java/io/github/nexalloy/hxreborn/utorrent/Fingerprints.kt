package io.github.nexalloy.hxreborn.utorrent

import io.github.nexalloy.morphe.findClassDirect
import io.github.nexalloy.morphe.findMethodDirect

val utorrentMainActivityClass = findClassDirect {
    findClass {
        matcher {
            className("com.bittorrent.app.main.MainActivity")
        }
    }.first()
}

val proStatusMethod = findMethodDirect {
    runCatching {
        findMethod {
            matcher {
                returnType = "boolean"
                usingStrings("str_pro_setting")
            }
        }.first()
    }.getOrElse {
        findMethod {
            matcher {
                returnType = "boolean"
                name = "isPro"
            }
        }.first()
    }
}
