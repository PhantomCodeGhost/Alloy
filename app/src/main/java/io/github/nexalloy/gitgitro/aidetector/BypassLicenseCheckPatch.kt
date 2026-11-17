package io.github.nexalloy.gitgitro.aidetector

import android.content.Context
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

val BypassLicenseCheckPatch = patch(
    name = "Bypass license check",
    description = "Bypasses the PairIP license check.",
    use = true
) {
    runCatching {
        val licenseClientClass = XposedHelpers.findClassIfExists("com.pairip.licensecheck.LicenseClient", classLoader)
        if (licenseClientClass != null) {
            XposedHelpers.findMethodExactIfExists(
                licenseClientClass,
                "checkLicense",
                Context::class.java
            )?.hookMethod(XC_MethodReplacement.DO_NOTHING)
        }
    }
}
