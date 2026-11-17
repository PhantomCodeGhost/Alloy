package io.github.nexalloy.kiraio.drofus

import io.github.nexalloy.morphe.findMethodDirect

val premiumMethod = findMethodDirect {
    findMethod {
        matcher {
            returnType = "boolean"
            paramCount = 0
            addInvoke {
                name = "getValue"
                declaredClass("androidx.lifecycle.LiveData")
            }
        }
    }.first()
}
