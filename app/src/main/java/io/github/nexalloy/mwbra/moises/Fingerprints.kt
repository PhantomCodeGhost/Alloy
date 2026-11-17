package io.github.nexalloy.mwbra.moises

import io.github.nexalloy.morphe.findClassDirect

val moisesUserClass = findClassDirect {
    runCatching {
        findClass {
            matcher {
                className("ai.moises.data.user.model.User")
            }
        }.first()
    }.getOrElse {
        findClass {
            matcher {
                className("ai.moises.data.model.User")
            }
        }.first()
    }
}
