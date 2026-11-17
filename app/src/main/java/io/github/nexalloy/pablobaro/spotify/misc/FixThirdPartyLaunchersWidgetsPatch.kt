package io.github.nexalloy.pablobaro.spotify.misc

import de.robv.android.xposed.XC_MethodReplacement
import io.github.nexalloy.hookMethod
import io.github.nexalloy.pablobaro.spotify.canBindAppWidgetPermissionMethod
import io.github.nexalloy.patch

val FixThirdPartyLaunchersWidgets = patch(
    name = "Fix third party launchers widgets",
    description = "Allows the Spotify widgets to be added to third party launchers.",
) {
    runCatching {
        val method = runCatching { ::canBindAppWidgetPermissionMethod.dexMethod }.getOrNull()
        method?.hookMethod(XC_MethodReplacement.returnConstant(true))
    }
}
