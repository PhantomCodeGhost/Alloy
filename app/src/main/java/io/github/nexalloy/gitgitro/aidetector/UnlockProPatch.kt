package io.github.nexalloy.gitgitro.aidetector

import android.content.SharedPreferences
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

val UnlockProPatch = patch(
    name = "Unlocks pro",
    description = "Unlocks pro and removes the ads.",
    use = true
) {
    runCatching {
        val method = runCatching { ::proStatusMethod.dexMethod }.getOrNull()
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
                if (key.contains("pro") || key.contains("premium")) {
                    param.result = true
                }
            }
        }
    }
}
