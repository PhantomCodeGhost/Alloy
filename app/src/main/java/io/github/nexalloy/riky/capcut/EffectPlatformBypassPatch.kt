package io.github.nexalloy.riky.capcut

import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

/**
 * Bypasses the ByteDance Shark WAF block that prevents CapCut from loading
 * effects, transitions and templates ("No internet connection" / "Nessuna connessione ad Internet").
 *
 * Ported from riky-dev/morphe-patches (https://github.com/riky-dev/morphe-patches).
 * Spoofs device_platform to "windows" and deviceId to "0" in effect API requests.
 */
val EffectPlatformBypassPatch = patch(
    name = "Bypass effects region restriction",
    description = "Fixes effects/transitions not loading (Shark WAF block) by spoofing device_platform from \"android\" to \"windows\" and deviceId to \"0\" in effect API requests.",
) {
    runCatching {
        val builderClass = XposedHelpers.findClassIfExists(
            "com.ss.android.ugc.effectmanager.EffectConfiguration\$Builder",
            classLoader
        )
        if (builderClass != null) {
            XposedHelpers.findMethodExactIfExists(builderClass, "platform", String::class.java)?.hookMethod {
                before { param ->
                    param.args[0] = "windows"
                }
            }
            XposedHelpers.findMethodExactIfExists(builderClass, "deviceId", String::class.java)?.hookMethod {
                before { param ->
                    param.args[0] = "0"
                }
            }
        }

        val configClass = XposedHelpers.findClassIfExists(
            "com.ss.android.ugc.effectmanager.EffectConfiguration",
            classLoader
        )
        if (configClass != null) {
            XposedHelpers.findMethodExactIfExists(configClass, "setDeviceId", String::class.java)?.hookMethod {
                before { param ->
                    param.args[0] = "0"
                }
            }
        }
    }
}

