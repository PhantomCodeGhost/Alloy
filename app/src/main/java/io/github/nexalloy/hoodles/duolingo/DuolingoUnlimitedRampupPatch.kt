package io.github.nexalloy.hoodles.duolingo

import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

private val RAMPUP_STATE_CLASSES = listOf(
    "com.duolingo.data.session.state.RampupTimerState\$Paused",
    "com.duolingo.data.session.state.RampupTimerState\$Tick"
)

val DuolingoUnlimitedRampupPatch = patch(
    name = "Unlimited RampUp time",
    description = "Timer for RampUp challenges will not expire (sets countdown to 3600 seconds).",
    use = false
) {
    RAMPUP_STATE_CLASSES.forEach { className ->
        val clazz = XposedHelpers.findClassIfExists(className, classLoader) ?: return@forEach
        clazz.constructors.forEach { ctor ->
            ctor.hookMethod {
                before { param ->
                    if (param.args.isNotEmpty() && param.args[0] is Number) {
                        param.args[0] = 3600
                    }
                }
            }
        }
    }
}
