package io.github.nexalloy.miguelninja.cubex

import android.content.SharedPreferences
import de.robv.android.xposed.XC_MethodReplacement
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

    // Intercept SharedPreferences.getBoolean for "has_active_purchase"
    runCatching {
        XposedHelpers.findMethodExactIfExists(
            SharedPreferences::class.java,
            "getBoolean",
            String::class.java,
            Boolean::class.javaPrimitiveType
        )?.hookMethod {
            before { param ->
                val key = param.args[0] as? String
                if (key == "has_active_purchase") {
                    param.result = true
                }
            }
        }
    }
}
