package io.github.nexalloy.kiraio.drofus

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch
import java.util.Locale

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
                        if (key.contains("premium") || key.contains("pro") || key.contains("ad_free") || key.contains("purchased")) {
                            param.result = true
                        }
                    }
                })
            }
        }
    }
}
