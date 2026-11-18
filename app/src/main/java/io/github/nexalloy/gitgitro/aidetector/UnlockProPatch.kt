package io.github.nexalloy.gitgitro.aidetector

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.patch
import java.util.Locale

val UnlockProPatch = patch(
    name = "Unlocks pro",
    description = "Unlocks pro and removes the ads.",
    use = true
) {
    // 1. Dynamic method hook if proStatusMethod exists
    runCatching {
        val method = runCatching { ::proStatusMethod.dexMethod }.getOrNull()
        method?.toMember()?.let { member ->
            XposedBridge.hookMethod(member, XC_MethodReplacement.returnConstant(true))
        }
    }

    // 2. Concrete SharedPreferencesImpl hook (safe concrete class, NEVER interface)
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
                        if (key.contains("pro") || key.contains("premium") || key.contains("subscribed") || key.contains("remove_ad")) {
                            param.result = true
                        }
                    }
                })
            }
        }
    }

    // 3. Google Play Billing purchase state spoofing
    runCatching {
        val purchaseClass = XposedHelpers.findClassIfExists("com.android.billingclient.api.Purchase", classLoader)
        if (purchaseClass != null) {
            XposedHelpers.findMethodExactIfExists(purchaseClass, "getPurchaseState")?.let {
                // PurchaseState.PURCHASED = 1
                XposedBridge.hookMethod(it, XC_MethodReplacement.returnConstant(1))
            }
            XposedHelpers.findMethodExactIfExists(purchaseClass, "isAcknowledged")?.let {
                XposedBridge.hookMethod(it, XC_MethodReplacement.returnConstant(true))
            }
        }
    }
}
