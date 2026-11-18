package io.github.nexalloy.paresh.mark

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.patch
import java.util.Locale

val MarkPremiumPatch = patch(
    name = "Mark Premium",
    description = "Unlocks premium features and removes ads.",
    use = true
) {
    // 1. Hook Mark Premium Class (j6.a or dynamic class containing mark_service_enabled)
    runCatching {
        val clazz = runCatching { ::markPremiumClass.clazz }.getOrNull()
            ?: XposedHelpers.findClassIfExists("j6.a", classLoader)

        if (clazz != null) {
            for (method in clazz.declaredMethods) {
                if ((method.returnType == Boolean::class.javaPrimitiveType || method.returnType == java.lang.Boolean::class.java) &&
                    method.parameterTypes.isEmpty()) {
                    method.isAccessible = true
                    XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(true))
                }
            }
        }
    }

    // 2. Kill PairIP LicenseContentProvider at the root (ContentProvider.onCreate returns true)
    runCatching {
        val providerClass = XposedHelpers.findClassIfExists("com.pairip.licensecheck.LicenseContentProvider", classLoader)
        if (providerClass != null) {
            XposedHelpers.findMethodExactIfExists(providerClass, "onCreate")?.let {
                XposedBridge.hookMethod(it, XC_MethodReplacement.returnConstant(true))
            }
        }
    }

    // 3. PairIP safety nets: block initializeLicenseCheck, handleError, checkLicense
    runCatching {
        val clientClass = XposedHelpers.findClassIfExists("com.pairip.licensecheck.LicenseClient", classLoader)
        if (clientClass != null) {
            for (method in clientClass.declaredMethods) {
                when (method.name) {
                    "initializeLicenseCheck", "handleError", "checkLicense" -> {
                        method.isAccessible = true
                        XposedBridge.hookMethod(method, XC_MethodReplacement.DO_NOTHING)
                    }
                    "processResponse" -> {
                        method.isAccessible = true
                        XposedBridge.hookMethod(method, object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam) {
                                // Force LICENSED status (code 0)
                                if (param.args.isNotEmpty() && param.args[0] is Int) {
                                    param.args[0] = 0
                                }
                            }
                        })
                    }
                }
            }
        }
    }

    // 4. PairIP ResponseValidator safety nets: skip validation
    runCatching {
        val validatorClasses = listOf(
            "com.pairip.licensecheck.ResponseValidator",
            "com.pairip.licensecheck.LicenseResponseHelper"
        )
        for (vName in validatorClasses) {
            val vClass = XposedHelpers.findClassIfExists(vName, classLoader) ?: continue
            for (method in vClass.declaredMethods) {
                if (method.name == "validateResponse") {
                    method.isAccessible = true
                    XposedBridge.hookMethod(method, XC_MethodReplacement.DO_NOTHING)
                }
            }
        }
    }

    // 5. Concrete SharedPreferencesImpl hook (NEVER interface)
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
                        if (key.contains("premium") || key.contains("ads_removed") || key.contains("is_premium") || key.contains("pro")) {
                            param.result = true
                        }
                    }
                })
            }
        }
    }

    // 6. Google Play Billing purchase spoofing
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
}
