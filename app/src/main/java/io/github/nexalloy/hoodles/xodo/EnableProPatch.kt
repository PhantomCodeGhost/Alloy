package io.github.nexalloy.hoodles.xodo

import android.content.SharedPreferences
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

val EnableProPatch = patch(
    name = "Enable Pro",
    description = "Enables app features locked behind the subscription paywall.",
    use = true
) {
    runCatching {
        val method = runCatching { ::isProMethod.dexMethod }.getOrNull()
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
                if (key.contains("pro") || key.contains("subscription") || key.contains("subscribed")) {
                    param.result = true
                }
            }
        }
    }
}
