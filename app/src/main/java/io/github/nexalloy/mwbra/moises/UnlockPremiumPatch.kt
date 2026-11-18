package io.github.nexalloy.mwbra.moises

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.patch
import java.util.Date

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
                val name = method.name.lowercase()
                if ((name == "ispro" || name == "ispremium" || name == "issubscriptionactive" || name == "hasactivepurchase") &&
                    (method.returnType == Boolean::class.javaPrimitiveType || method.returnType == java.lang.Boolean::class.java)) {
                    method.isAccessible = true
                    XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(true))
                } else if (name == "getmonthlyusage" &&
                    (method.returnType == Int::class.javaPrimitiveType || method.returnType == java.lang.Integer::class.java)) {
                    method.isAccessible = true
                    XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(0))
                }
            }
        }
    }

    // 2. Hook UserSubscriptionStatus
    runCatching {
        val subStatusClass = XposedHelpers.findClassIfExists("ai.moises.data.user.model.UserSubscriptionStatus", classLoader)
            ?: XposedHelpers.findClassIfExists("ai.moises.data.model.UserSubscriptionStatus", classLoader)
        if (subStatusClass != null) {
            for (method in subStatusClass.declaredMethods) {
                val name = method.name.lowercase()
                if ((name.contains("pro") || name.contains("premium") || name.contains("active")) &&
                    (method.returnType == Boolean::class.javaPrimitiveType || method.returnType == java.lang.Boolean::class.java)) {
                    method.isAccessible = true
                    XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(true))
                }
            }
        }
    }

    // 3. Hook RevenueCat EntitlementInfo & CustomerInfo
    runCatching {
        val entitlementClass = XposedHelpers.findClassIfExists("com.revenuecat.purchases.EntitlementInfo", classLoader)
        if (entitlementClass != null) {
            XposedHelpers.findMethodExactIfExists(entitlementClass, "isActive")?.let {
                XposedBridge.hookMethod(it, XC_MethodReplacement.returnConstant(true))
            }
            XposedHelpers.findMethodExactIfExists(entitlementClass, "getWillRenew")?.let {
                XposedBridge.hookMethod(it, XC_MethodReplacement.returnConstant(true))
            }
            XposedHelpers.findMethodExactIfExists(entitlementClass, "getExpirationDate")?.let {
                val farFuture = Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365 * 10)
                XposedBridge.hookMethod(it, XC_MethodReplacement.returnConstant(farFuture))
            }
        }

        val customerInfoClass = XposedHelpers.findClassIfExists("com.revenuecat.purchases.CustomerInfo", classLoader)
        if (customerInfoClass != null) {
            val subs = setOf("moises_premium", "moises_premium_monthly", "moises_premium_yearly", "pro")
            XposedHelpers.findMethodExactIfExists(customerInfoClass, "getActiveSubscriptions")?.let {
                XposedBridge.hookMethod(it, XC_MethodReplacement.returnConstant(subs))
            }
            XposedHelpers.findMethodExactIfExists(customerInfoClass, "getAllPurchasedProductIds")?.let {
                XposedBridge.hookMethod(it, XC_MethodReplacement.returnConstant(subs))
            }
        }
    }

    // 4. Hook Google Play Billing
    runCatching {
        val purchaseClass = XposedHelpers.findClassIfExists("com.android.billingclient.api.Purchase", classLoader)
        if (purchaseClass != null) {
            XposedHelpers.findMethodExactIfExists(purchaseClass, "getPurchaseState")?.let {
                XposedBridge.hookMethod(it, XC_MethodReplacement.returnConstant(1)) // PURCHASED
            }
            XposedHelpers.findMethodExactIfExists(purchaseClass, "isAcknowledged")?.let {
                XposedBridge.hookMethod(it, XC_MethodReplacement.returnConstant(true))
            }
        }
    }

    // 5. Bypass PairIP signature checks
    runCatching {
        val sigCheck = XposedHelpers.findClassIfExists("com.pairip.SignatureCheck", classLoader)
        if (sigCheck != null) {
            for (m in sigCheck.declaredMethods) {
                m.isAccessible = true
                if (m.returnType == Boolean::class.javaPrimitiveType) {
                    XposedBridge.hookMethod(m, XC_MethodReplacement.returnConstant(true))
                } else if (m.returnType == Void.TYPE) {
                    XposedBridge.hookMethod(m, XC_MethodReplacement.DO_NOTHING)
                }
            }
        }
    }
}
