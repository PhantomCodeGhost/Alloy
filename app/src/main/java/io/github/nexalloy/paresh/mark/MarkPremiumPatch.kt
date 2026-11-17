package io.github.nexalloy.paresh.mark

import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

val MarkPremiumPatch = patch(
    name = "Mark Premium",
    description = "Unlocks premium features and removes ads.",
    use = true
) {
    runCatching {
        val clazz = runCatching { ::markPremiumClass.clazz }.getOrNull()
        if (clazz != null) {
            for (method in clazz.declaredMethods) {
                if (method.returnType == Boolean::class.javaPrimitiveType && method.parameterTypes.isEmpty()) {
                    method.hookMethod(XC_MethodReplacement.returnConstant(true))
                }
            }
        }
    }

    // Bypass license ContentProvider
    runCatching {
        val providerClass = XposedHelpers.findClassIfExists("com.pairip.licensecheck.LicenseContentProvider", classLoader)
        if (providerClass != null) {
            XposedHelpers.findMethodExactIfExists(providerClass, "onCreate")?.hookMethod(
                XC_MethodReplacement.returnConstant(true)
            )
        }
    }
}
