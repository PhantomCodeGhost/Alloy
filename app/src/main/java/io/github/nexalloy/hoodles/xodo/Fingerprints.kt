package io.github.nexalloy.hoodles.xodo

import io.github.nexalloy.morphe.findMethodDirect

val isProMethod = findMethodDirect {
    findMethod {
        matcher {
            returnType = "boolean"
            paramCount = 0
            declaredClass {
                usingStrings("Monthly", "Yearly")
            }
        }
    }.first()
}
