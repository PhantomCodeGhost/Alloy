package io.github.nexalloy.mwbra.moises

import io.github.nexalloy.morphe.findClassDirect

val moisesUserClass = findClassDirect {
    findClass {
        matcher {
            className("ai.moises.data.user.model.User")
        }
    }.firstOrNull() ?: findClass {
        matcher {
            className("ai.moises.data.model.User")
        }
    }.firstOrNull() ?: findClass {
        matcher {
            className("User", org.luckypray.dexkit.query.enums.StringMatchType.EndsWith)
        }
    }.first()
}
