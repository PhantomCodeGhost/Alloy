package io.github.nexalloy.mwbra.moises

import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

val UnlockPremiumPatch = patch(
    name = "Unlock Premium",
    description = "Unlocks Moises Premium features, removes stem separation limits, and eliminates paywalls.",
    use = true
) {
    // 1. Hook Moises User data models
    runCatching {
        val userClass = runCatching { ::moisesUserClass.clazz }.getOrNull()
            ?: XposedHelpers.findClassIfExists("ai.moises.data.user.model.User", classLoader)
            ?: XposedHelpers.findClassIfExists("ai.moises.data.model.User", classLoader)

        if (userClass != null) {
            for (method in userClass.declaredMethods) {
                when (method.name) {
                    "isPro" -> method.hookMethod(XC_MethodReplacement.returnConstant(true))
                    "isSubscriptionActive", "q" -> {
                        if (method.returnType == java.lang.Boolean::class.java) {
                            method.hookMethod(XC_MethodReplacement.returnConstant(java.lang.Boolean.TRUE))
                        }
                    }
                    "getMonthlyUsage" -> {
                        if (method.returnType == java.lang.Integer::class.java) {
                            method.hookMethod(XC_MethodReplacement.returnConstant(0))
                        }
                    }
                }
            }
        }
    }

    // 2. Hook UserSubscriptionStatus
    runCatching {
        val subStatusClass = XposedHelpers.findClassIfExists("ai.moises.data.user.model.UserSubscriptionStatus", classLoader)
        if (subStatusClass != null) {
            for (method in subStatusClass.declaredMethods) {
                if ((method.name == "isPro" || method.name == "isPremium") && method.returnType == java.lang.Boolean::class.java) {
                    method.hookMethod(XC_MethodReplacement.returnConstant(java.lang.Boolean.TRUE))
                }
            }
        }
    }

    // 3. Hook RevenueCat EntitlementInfo
    runCatching {
        val entitlementClass = XposedHelpers.findClassIfExists("com.revenuecat.purchases.EntitlementInfo", classLoader)
        if (entitlementClass != null) {
            XposedHelpers.findMethodExactIfExists(entitlementClass, "isActive")?.hookMethod(
                XC_MethodReplacement.returnConstant(true)
            )
        }
    }

    // 4. Bypass PairIP signature checks
    runCatching {
        val sigCheck = XposedHelpers.findClassIfExists("com.pairip.SignatureCheck", classLoader)
        if (sigCheck != null) {
            XposedHelpers.findMethodExactIfExists(sigCheck, "verifyIntegrity")?.hookMethod(
                XC_MethodReplacement.DO_NOTHING
            )
            XposedHelpers.findMethodExactIfExists(sigCheck, "verifySignatureMatches")?.hookMethod(
                XC_MethodReplacement.returnConstant(true)
            )
        }
    }
}
