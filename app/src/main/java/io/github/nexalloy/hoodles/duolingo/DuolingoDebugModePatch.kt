package io.github.nexalloy.hoodles.duolingo

import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

val DuolingoDebugModePatch = patch(
    name = "Enable debug menu",
    description = "Enables hidden Duolingo developer settings menu.",
    use = false
) {
    try {
        val configClass = ::buildConfigFingerprint.clazz
        configClass.constructors.forEach { ctor ->
            ctor.hookMethod {
                after { param ->
                    val obj = param.thisObject ?: return@after
                    for (field in obj.javaClass.declaredFields) {
                        if (field.type == Boolean::class.javaPrimitiveType || field.type == java.lang.Boolean::class.java) {
                            field.isAccessible = true
                            try {
                                field.set(obj, true)
                            } catch (_: Throwable) {
                            }
                        }
                    }
                }
            }
        }
    } catch (_: Throwable) {
    }
}
