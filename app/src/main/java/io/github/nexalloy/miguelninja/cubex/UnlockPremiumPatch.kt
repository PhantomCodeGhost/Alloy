package io.github.nexalloy.miguelninja.cubex

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

val UnlockPremiumPatch = patch(
    name = "Unlock premium",
    description = "Unlocks all premium-gated features (Advanced Solver / Kociemba, custom color schemes, VIP support).",
    use = true
) {
    runCatching {
        val method = runCatching { ::hasActivePurchaseMethod.dexMethod }.getOrNull()
        method?.hookMethod(XC_MethodReplacement.returnConstant(true))
    }

    // Intercept SharedPreferencesImpl.getBoolean for "has_active_purchase"
    runCatching {
        val prefImplClass = XposedHelpers.findClassIfExists("android.app.SharedPreferencesImpl", classLoader)
            ?: XposedHelpers.findClassIfExists("android.app.SharedPreferencesImpl", ClassLoader.getSystemClassLoader())

        if (prefImplClass != null) {
            val getBooleanMethod = XposedHelpers.findMethodExactIfExists(
                prefImplClass,
                "getBoolean",
                String::class.java,
                Boolean::class.javaPrimitiveType
            )
            getBooleanMethod?.let {
                XposedBridge.hookMethod(it, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val key = param.args[0] as? String
                        if (key == "has_active_purchase") {
                            param.result = true
                        }
                    }
                })
            }
        }
    }
}
