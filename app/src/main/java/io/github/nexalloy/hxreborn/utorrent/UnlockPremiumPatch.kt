package io.github.nexalloy.hxreborn.utorrent

import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch
import java.util.Locale

val UnlockPremiumPatch = patch(
    name = "Unlock premium",
    description = "Unlocks Pro features and removes ads.",
    use = true
) {
    runCatching {
        val method = runCatching { ::proStatusMethod.dexMethod }.getOrNull()
        method?.hookMethod(XC_MethodReplacement.returnConstant(true))
    }

    // Intercept View addView to hide ad banners
    runCatching {
        XposedHelpers.findMethodExactIfExists(
            ViewGroup::class.java,
            "addView",
            View::class.java,
            Int::class.javaPrimitiveType,
            ViewGroup.LayoutParams::class.java
        )?.hookMethod {
            before { param ->
                val view = param.args[0] as? View ?: return@before
                val name = view.javaClass.name.lowercase(Locale.ROOT)
                val entryName = runCatching {
                    if (view.id != View.NO_ID) view.resources.getResourceEntryName(view.id).lowercase(Locale.ROOT) else ""
                }.getOrDefault("")
                if (name.contains("adview") || name.contains("adbanner") || entryName.contains("ad_container") || entryName.contains("banner_ad")) {
                    view.visibility = View.GONE
                    param.result = null
                }
            }
        }
    }
}
