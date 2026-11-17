package io.github.nexalloy.vippium.officereader

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch
import java.util.Locale

val UnlockPremiumPatch = patch(
    name = "Unlock premium",
    description = "Unlocks premium and removes Family-apps promos from the About screens.",
    use = true
) {
    // 1. Hook OptionService.isPurchased() to return true
    runCatching {
        val optionService = runCatching { ::optionServiceClass.clazz }.getOrNull()
            ?: XposedHelpers.findClassIfExists("net.sjava.officereader.services.OptionService", classLoader)

        if (optionService != null) {
            XposedHelpers.findMethodExactIfExists(optionService, "isPurchased")?.hookMethod(
                XC_MethodReplacement.returnConstant(true)
            )

            // Hook any other boolean methods on OptionService related to purchase / premium
            for (method in optionService.declaredMethods) {
                val name = method.name.lowercase(Locale.ROOT)
                if (method.returnType == Boolean::class.javaPrimitiveType || method.returnType == java.lang.Boolean::class.java) {
                    if (name.contains("purchased") || name.contains("premium") || name.contains("vip") || name.contains("subscribed")) {
                        method.hookMethod(XC_MethodReplacement.returnConstant(true))
                    }
                }
            }
        }
    }

    // 2. Hook AboutFragment to suppress in-app rate prompts and hide promo cards
    runCatching {
        val aboutFragment = runCatching { ::aboutFragmentClass.clazz }.getOrNull()
            ?: XposedHelpers.findClassIfExists("net.sjava.officereader.ui.fragments.AboutFragment", classLoader)

        if (aboutFragment != null) {
            // Suppress showRateApp dialog
            for (method in aboutFragment.declaredMethods) {
                if (method.name == "showRateApp") {
                    method.hookMethod(XC_MethodReplacement.DO_NOTHING)
                }
            }

            // In onViewCreated, hide Family-apps and rate rows
            XposedHelpers.findMethodExactIfExists(
                aboutFragment,
                "onViewCreated",
                View::class.java,
                Bundle::class.java
            )?.hookMethod {
                after { param ->
                    val root = param.args[0] as? View ?: return@after
                    hideAboutPromos(root)
                }
            }
        }
    }
}

private fun hideAboutPromos(root: View) {
    try {
        var entryName = ""
        if (root.id != View.NO_ID) {
            try {
                entryName = root.resources.getResourceEntryName(root.id).lowercase(Locale.ROOT)
            } catch (_: Throwable) {
            }
        }
        if (entryName.contains("family") || entryName.contains("rate") || entryName.contains("about_rate") || entryName.contains("aboutfamily")) {
            root.visibility = View.GONE
        }
        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                hideAboutPromos(root.getChildAt(i))
            }
        }
    } catch (_: Throwable) {
    }
}
