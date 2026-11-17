package io.github.nexalloy.mwbra.moises

import android.content.SharedPreferences
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch
import java.util.Locale

val HighQualityExportPatch = patch(
    name = "High Quality Audio Export & Audio Tools",
    description = "Enables high quality audio export (WAV / 320kbps MP3), pitch shifting, and tempo tools.",
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
                if (key.contains("high_quality") || key.contains("wav_export") || key.contains("audio_export") || key.contains("smart_metronome")) {
                    param.result = true
                }
            }
        }
    }
}
