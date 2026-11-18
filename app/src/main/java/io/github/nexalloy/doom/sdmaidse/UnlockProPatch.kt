package io.github.nexalloy.doom.sdmaidse

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.patch

val UnlockProPatch = patch(
    name = "Unlock Pro",
    description = "Unlocks SD Maid SE Pro features and removes license restrictions.",
    use = true
) {
    // Layer 1: UpgradeRepoExtensionsKt.isPro(...) coroutine & regular extension
    runCatching {
        val extensionClasses = listOf(
            "eu.darken.sdmse.common.upgrade.UpgradeRepoExtensionsKt",
            "eu.darken.sdmse.common.upgrade.UpgradeRepoExtKt",
            "eu.darken.sdmse.common.upgrade.UpgradeExtensionsKt"
        )
        for (className in extensionClasses) {
            val extClass = XposedHelpers.findClassIfExists(className, classLoader) ?: continue
            for (method in extClass.declaredMethods) {
                if (method.name == "isPro" || method.name.startsWith("isPro")) {
                    method.isAccessible = true
                    XposedBridge.hookMethod(method, object : XC_MethodReplacement() {
                        override fun replaceHookedMethod(param: MethodHookParam): Any {
                            return java.lang.Boolean.TRUE
                        }
                    })
                }
            }
        }
    }

    // Layer 2: UpgradeRepoGplay$Info data class (constructors + isPro / getIsPro getters)
    runCatching {
        val infoClasses = listOf(
            "eu.darken.sdmse.common.upgrade.core.UpgradeRepoGplay\$Info",
            "eu.darken.sdmse.common.upgrade.core.UpgradeRepo\$Info",
            "eu.darken.sdmse.common.upgrade.UpgradeRepo\$Info"
        )
        for (className in infoClasses) {
            val infoClass = XposedHelpers.findClassIfExists(className, classLoader) ?: continue

            // Hook getters
            for (method in infoClass.declaredMethods) {
                val name = method.name.lowercase()
                if ((name == "ispro" || name == "getispro") && (method.returnType == Boolean::class.javaPrimitiveType || method.returnType == java.lang.Boolean::class.java)) {
                    method.isAccessible = true
                    XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(true))
                }
            }

            // Hook constructors to force isPro = true
            XposedBridge.hookAllConstructors(infoClass, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val instance = param.thisObject ?: return
                    for (field in instance.javaClass.declaredFields) {
                        if (field.type == Boolean::class.javaPrimitiveType && (field.name == "isPro" || field.name.contains("pro", ignoreCase = true))) {
                            field.isAccessible = true
                            field.setBoolean(instance, true)
                        }
                    }
                }
            })
        }
    }

    // Layer 3: Upgrade repositories
    runCatching {
        val repoClasses = listOf(
            "eu.darken.sdmse.common.upgrade.core.UpgradeRepoGplay",
            "eu.darken.sdmse.common.upgrade.core.UpgradeRepo",
            "eu.darken.sdmse.common.upgrade.UpgradeRepo"
        )
        for (className in repoClasses) {
            val repoClass = XposedHelpers.findClassIfExists(className, classLoader) ?: continue
            for (method in repoClass.declaredMethods) {
                val name = method.name.lowercase()
                if ((name == "ispro" || name == "ispropurchased" || name == "isupgraded") &&
                    (method.returnType == Boolean::class.javaPrimitiveType || method.returnType == java.lang.Boolean::class.java)) {
                    method.isAccessible = true
                    XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(true))
                }
            }
        }
    }
}
