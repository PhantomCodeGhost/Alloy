package io.github.nexalloy.miguelninja.cubex

import io.github.nexalloy.morphe.findMethodDirect

val hasActivePurchaseMethod = findMethodDirect {
    findMethod {
        matcher {
            returnType = "boolean"
            paramCount = 0
            usingStrings("has_active_purchase")
        }
    }.first()
}
