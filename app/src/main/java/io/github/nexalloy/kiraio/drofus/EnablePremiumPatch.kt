package io.github.nexalloy.kiraio.drofus

import android.content.SharedPreferences
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

val EnablePremiumPatch = patch(
    name = "Enable Premium",
    description = "Unlock unlimited use of ISO Burner, disable ads and disable coins system.",
    use = true
) {
    runCatching {
        val method = runCatching { ::premiumMethod.dexMethod }.getOrNull()
        method?.hookMethod(XC_MethodReplacement.returnConstant(true))
    }

    runCatching {
        XposedHelpers.findMethodExactIfExists(
            SharedPreferences::class.java,
            "getBoolean",
            String::class.java,
            Boolean::class.javaPrimitiveType
        )?.hookMethod {
            before { param ->
                val key = (param.args[0] as? String)?.lowercase() ?: return@before
                if (key.contains("premium") || key.contains("pro") || key.contains("ad_free") || key.contains("purchased")) {
                    param.result = true
                }
            }
        }
    }
}
