package io.github.nexalloy.gitgitro.aidetector

import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.patch

val BypassLicenseCheckPatch = patch(
    name = "Bypass license check",
    description = "Bypasses the PairIP license check.",
    use = true
) {
    runCatching {
        val licenseClientClass = XposedHelpers.findClassIfExists("com.pairip.licensecheck.LicenseClient", classLoader)
        if (licenseClientClass != null) {
            for (method in licenseClientClass.declaredMethods) {
                if (method.name == "checkLicense" || method.name.contains("License", ignoreCase = true)) {
                    method.isAccessible = true
                    XposedBridge.hookMethod(method, XC_MethodReplacement.DO_NOTHING)
                }
            }
        }
    }
}
