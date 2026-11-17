package io.github.nexalloy.hxreborn.utorrent

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

val FixScreenInsetsPatch = patch(
    name = "Fix screen insets",
    description = "Keeps main screens and settings clear of system bars and display cutouts on Android 15+.",
    use = true
) {
    if (Build.VERSION.SDK_INT >= 30) {
        Activity::class.java.getMethod("onPostCreate", Bundle::class.java).hookMethod {
            after { param ->
                val activity = param.thisObject as? Activity ?: return@after
                activity.window?.decorView?.fitsSystemWindows = true
            }
        }
    }
}
