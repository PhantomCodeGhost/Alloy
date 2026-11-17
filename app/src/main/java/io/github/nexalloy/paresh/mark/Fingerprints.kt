package io.github.nexalloy.paresh.mark

import io.github.nexalloy.morphe.findClassDirect

val markPremiumClass = findClassDirect {
    findClass {
        matcher {
            usingStrings("mark_service_enabled")
        }
    }.first()
}
