package io.github.nexalloy.doom.pixelhabittracker

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch
import java.util.Locale

val UnlockProPatch = patch(
    name = "Unlock PRO",
    description = "Unlocks all PRO features by permanently reporting a purchased state.",
    use = true
) {
    // 1. Hook SharedPreferencesImpl for "billing_prefs" and "pro_purchased"
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
                        if (key == "pro_purchased" || key.contains("pro") || key.contains("purchased")) {
                            param.result = true
                        }
                    }
                })
            }
        }
    }

    // 2. Hook PurchaseRepository class: constructor and setter/getter methods
    runCatching {
        val repoClass = runCatching { ::purchaseRepositoryClass.clazz }.getOrNull()
        if (repoClass != null) {
            // Hook all constructors to pre-set boolean fields (like field `c`) to true
            for (constructor in repoClass.declaredConstructors) {
                constructor.hookMethod {
                    after { param ->
                        val instance = param.thisObject ?: return@after
                        for (field in repoClass.declaredFields) {
                            if (field.type == Boolean::class.javaPrimitiveType || field.type == java.lang.Boolean::class.java) {
                                runCatching {
                                    field.isAccessible = true
                                    field.setBoolean(instance, true)
                                }
                            }
                        }
                    }
                }
            }

            // Hook all methods on the repository: force boolean arguments (setter `f(Z)V`) to true
            // and force boolean return types (getters `isPro()Z`) to true
            for (method in repoClass.declaredMethods) {
                val paramTypes = method.parameterTypes
                if (paramTypes.size == 1 && (paramTypes[0] == Boolean::class.javaPrimitiveType || paramTypes[0] == java.lang.Boolean::class.java)) {
                    method.hookMethod {
                        before { param ->
                            param.args[0] = true
                        }
                    }
                } else if (paramTypes.isEmpty() && (method.returnType == Boolean::class.javaPrimitiveType || method.returnType == java.lang.Boolean::class.java)) {
                    method.hookMethod(XC_MethodReplacement.returnConstant(true))
                }
            }
        }
    }

    // 3. Bypass PairIP License checks
    runCatching {
        val licenseClientClass = XposedHelpers.findClassIfExists("com.pairip.licensecheck.LicenseClient", classLoader)
        if (licenseClientClass != null) {
            for (method in licenseClientClass.declaredMethods) {
                val name = method.name
                if (name == "initializeLicenseCheck" || name == "checkLicense" || name == "handleError") {
                    method.hookMethod(XC_MethodReplacement.DO_NOTHING)
                } else if (name == "processResponse" && (method.returnType == Int::class.javaPrimitiveType || method.returnType == java.lang.Integer::class.java)) {
                    method.hookMethod(XC_MethodReplacement.returnConstant(0))
                }
            }
        }

        val providerClass = XposedHelpers.findClassIfExists("com.pairip.licensecheck.LicenseContentProvider", classLoader)
        providerClass?.let {
            XposedHelpers.findMethodExactIfExists(it, "onCreate")?.hookMethod(XC_MethodReplacement.returnConstant(true))
        }
    }

    // 4. Google Play Billing purchase spoofing
    runCatching {
        val purchaseClass = XposedHelpers.findClassIfExists("com.android.billingclient.api.Purchase", classLoader)
        if (purchaseClass != null) {
            XposedHelpers.findMethodExactIfExists(purchaseClass, "getPurchaseState")
                ?.hookMethod(XC_MethodReplacement.returnConstant(1))
            XposedHelpers.findMethodExactIfExists(purchaseClass, "isAcknowledged")
                ?.hookMethod(XC_MethodReplacement.returnConstant(true))
        }
    }
}
