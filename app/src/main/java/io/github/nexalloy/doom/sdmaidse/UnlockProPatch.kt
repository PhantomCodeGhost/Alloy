package io.github.nexalloy.doom.sdmaidse

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.patch
import java.lang.reflect.Constructor

val UnlockProPatch = patch(
    name = "Unlock Pro",
    description = "Unlocks SD Maid SE Pro features and removes license restrictions.",
    use = true
) {
    runCatching {
        val infoClass = XposedHelpers.findClassIfExists("eu.darken.sdmse.common.upgrade.core.UpgradeRepoGplay\$Info", classLoader)
            ?: XposedHelpers.findClassIfExists("eu.darken.sdmse.common.upgrade.UpgradeRepo\$Info", classLoader)

        if (infoClass != null) {
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
}
