package io.github.nexalloy.arandomhooman.cronometer

import android.content.SharedPreferences
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch
import java.util.Locale

val UnlockGoldPatch = patch(
    name = "Unlock Gold",
    description = "Unlocks Cronometer Gold features, custom charts, advanced reports, fasting tracker, and ad-free view.",
    use = true
) {
    runCatching {
        XposedHelpers.findMethodExactIfExists(
            SharedPreferences::class.java,
            "getBoolean",
            String::class.java,
            Boolean::class.javaPrimitiveType
        )?.hookMethod {
            before { param ->
                val key = (param.args[0] as? String)?.lowercase(Locale.ROOT) ?: return@before
                if (key.contains("gold") || key.contains("subscriber") || key.contains("subscription") || key.contains("premium")) {
                    param.result = true
                }
            }
        }
    }
}
