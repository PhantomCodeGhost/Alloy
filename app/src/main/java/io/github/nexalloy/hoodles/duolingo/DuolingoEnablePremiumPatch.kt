package io.github.nexalloy.hoodles.duolingo

import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

val DuolingoEnablePremiumPatch = patch(
    name = "Enable Super / Max features",
    description = "Enables Duolingo Super/Max subscriber features and gives unlimited hearts/energy.",
    use = true
) {
    fun patchUserObject(user: Any?) {
        if (user == null) return
        var clazz: Class<*>? = user.javaClass
        while (clazz != null && clazz != Any::class.java) {
            for (field in clazz.declaredFields) {
                field.isAccessible = true
                val fieldName = field.name.lowercase()
                try {
                    if (field.type == Boolean::class.javaPrimitiveType || field.type == java.lang.Boolean::class.java) {
                        if (fieldName.contains("plus") || fieldName.contains("paid") || fieldName.contains("gold") || fieldName.contains("premium")) {
                            field.set(user, true)
                        }
                    } else if (field.type.isEnum) {
                        val constants = field.type.enumConstants
                        if (constants != null) {
                            for (constant in constants) {
                                val cName = constant.toString()
                                if (cName.equals("PREMIUM", ignoreCase = true) || cName.equals("GOLD", ignoreCase = true) || cName.equals("SUPER", ignoreCase = true)) {
                                    field.set(user, constant)
                                    break
                                }
                            }
                        }
                    } else if (field.type == String::class.java && fieldName.contains("subscriberlevel")) {
                        field.set(user, "PREMIUM")
                    }
                } catch (_: Throwable) {
                }
            }
            clazz = clazz.superclass
        }
    }

    // 1. Hook LoggedInState constructor to intercept the User object
    try {
        val loggedInClass = ::loggedInStateFingerprint.clazz
        loggedInClass.constructors.forEach { ctor ->
            ctor.hookMethod {
                after { param ->
                    param.args.forEach { arg ->
                        patchUserObject(arg)
                    }
                }
            }
        }
    } catch (_: Throwable) {
    }

    // 2. Hook User class directly for any boolean getter methods
    try {
        val userClass = ::userClassFingerprint.clazz
        userClass.constructors.forEach { ctor ->
            ctor.hookMethod {
                after { param ->
                    patchUserObject(param.thisObject)
                }
            }
        }
    } catch (_: Throwable) {
    }

    // 3. Hook EnergyConfig to ensure unlimited hearts/energy
    XposedHelpers.findClassIfExists("com.duolingo.core.energy.models.EnergyConfig", classLoader)?.let { energyConfig ->
        energyConfig.constructors.forEach { ctor ->
            ctor.hookMethod {
                before { param ->
                    if (param.args.size >= 2 && param.args[0] is Int && param.args[1] is Int) {
                        // Set current energy = max energy
                        param.args[0] = param.args[1]
                    }
                }
            }
        }
    }
}
