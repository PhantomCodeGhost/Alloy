package io.github.nexalloy.gitgitro.aidetector

import io.github.nexalloy.morphe.findMethodDirect

val proStatusMethod = findMethodDirect {
    findMethod {
        matcher {
            returnType = "boolean"
            paramCount = 0
            declaredClass("n1.l")
        }
    }.first()
}
