package io.github.nexalloy.gitgitro.aidetector

import io.github.nexalloy.morphe.findMethodDirect

val proStatusMethod = findMethodDirect {
    findMethod {
        matcher {
            returnType = "boolean"
            paramCount = 0
            declaredClass("n1.l")
        }
    }.firstOrNull() ?: findMethod {
        matcher {
            returnType = "boolean"
            paramCount = 0
            usingStrings("pro", "is_pro")
        }
    }.firstOrNull() ?: findMethod {
        matcher {
            returnType = "boolean"
            paramCount = 0
        }
    }.first()
}
