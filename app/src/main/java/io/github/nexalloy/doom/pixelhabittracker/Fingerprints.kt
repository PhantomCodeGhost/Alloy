package io.github.nexalloy.doom.pixelhabittracker

import io.github.nexalloy.morphe.findClassDirect

val purchaseRepositoryClass = findClassDirect {
    findClass {
        matcher {
            usingStrings("billing_prefs", "pro_purchased")
        }
    }.firstOrNull() ?: findClass {
        matcher {
            usingStrings("pro_purchased")
        }
    }.first()
}
