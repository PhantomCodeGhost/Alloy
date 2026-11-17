package io.github.nexalloy.vippium.officereader

import android.app.Activity
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

private fun isDarkMode(view: View): Boolean {
    val uiMode = view.resources.configuration.uiMode
    return (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}

private fun shouldBlacken(color: Int): Boolean {
    val r = Color.red(color)
    val g = Color.green(color)
    val b = Color.blue(color)
    // Dark surfaces/backgrounds typically have rgb < 55
    return r <= 55 && g <= 55 && b <= 55 && !(r == 0 && g == 0 && b == 0)
}

val AmoledThemePatch = patch(
    name = "AMOLED theme",
    description = "Makes dark mode pure black for AMOLED screens. Light mode stays normal.",
    use = false
) {
    // 1. Force activity window and bars to pure black when in dark mode
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

    // 2. Map dark surface/background colors to pure black
    Color::class.java.getMethod("parseColor", String::class.java).hookMethod {
        after { param ->
            val color = param.result as Int
            if (shouldBlacken(color)) {
                param.result = Color.BLACK
            }
        }
    }

    Paint::class.java.getMethod("setColor", Int::class.java).hookMethod {
        before { param ->
            val color = param.args[0] as Int
            if (shouldBlacken(color)) {
                param.args[0] = Color.BLACK
            }
        }
    }

    GradientDrawable::class.java.getMethod("setColors", IntArray::class.java).hookMethod {
        before { param ->
            val gradient = param.args[0] as? IntArray ?: return@before
            for (i in gradient.indices) {
                if (shouldBlacken(gradient[i])) {
                    gradient[i] = Color.BLACK
                }
            }
        }
    }

    Resources::class.java.getMethod("getColor", Int::class.java, Resources.Theme::class.java).hookMethod {
        after { param ->
            val color = param.result as Int
            if (shouldBlacken(color)) {
                param.result = Color.BLACK
            }
        }
    }

    TypedArray::class.java.getMethod("getColor", Int::class.java, Int::class.java).hookMethod {
        after { param ->
            val color = param.result as Int
            if (shouldBlacken(color)) {
                param.result = Color.BLACK
            }
        }
    }
}
