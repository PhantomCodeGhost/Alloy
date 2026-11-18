package io.github.nexalloy.paresh.mark

import io.github.nexalloy.morphe.findClassDirect

val markPremiumClass = findClassDirect {
    findClass {
        matcher {
            usingStrings("mark_service_enabled")
        }
    }.firstOrNull() ?: findClass {
        matcher {
            className("j6.a")
        }
    }.firstOrNull() ?: findClass {
        matcher {
            usingStrings("mark_")
        }
    }.first()
}
