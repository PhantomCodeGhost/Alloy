package io.github.nexalloy.hoodles.xodo

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch
import java.util.Locale

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
                        val key = (param.args[0] as? String)?.lowercase(Locale.ROOT) ?: return
                        if (key.contains("pro") || key.contains("subscription") || key.contains("subscribed")) {
                            param.result = true
                        }
                    }
                })
            }
        }
    }
}
