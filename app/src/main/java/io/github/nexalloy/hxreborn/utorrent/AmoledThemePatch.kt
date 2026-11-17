package io.github.nexalloy.hxreborn.utorrent

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

val AmoledThemePatch = patch(
    name = "AMOLED dark theme",
    description = "Makes the torrent and media page backgrounds pure black in dark mode.",
    use = false
) {
    Activity::class.java.getMethod("onPostCreate", Bundle::class.java).hookMethod {
        after { param ->
            val activity = param.thisObject as? Activity ?: return@after
            val uiMode = activity.resources.configuration.uiMode
            if ((uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                activity.window?.apply {
                    setBackgroundDrawable(ColorDrawable(Color.BLACK))
                    statusBarColor = Color.BLACK
                    navigationBarColor = Color.BLACK
                }
            }
        }
    }
}
