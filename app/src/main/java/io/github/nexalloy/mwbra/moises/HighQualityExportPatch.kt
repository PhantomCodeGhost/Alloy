package io.github.nexalloy.mwbra.moises

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.patch
import java.util.Locale

val HighQualityExportPatch = patch(
    name = "High Quality Audio Export & Audio Tools",
    description = "Enables high quality audio export (WAV / 320kbps MP3), pitch shifting, and tempo tools.",
    use = true
) {
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
                        if (key.contains("high_quality") || key.contains("wav") || key.contains("audio_export") || key.contains("metronome") || key.contains("pitch") || key.contains("tempo")) {
                            param.result = true
                        }
                    }
                })
            }
        }
    }
}
