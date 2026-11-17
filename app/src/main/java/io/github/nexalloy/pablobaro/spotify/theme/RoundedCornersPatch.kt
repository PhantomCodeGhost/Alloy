package io.github.nexalloy.pablobaro.spotify.theme

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

val RoundedCorners = patch(
    name = "Rounded corners",
    description = "Rounds the corners of album art and thumbnails.",
) {
    ImageView::class.java.getDeclaredMethod("onAttachedToWindow").hookMethod {
        after { param ->
            val view = param.thisObject as ImageView
            val density = view.resources.displayMetrics.density
            val radius = 12f * density

            view.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(v: View, outline: Outline) {
                    outline.setRoundRect(0, 0, v.width, v.height, radius)
                }
            }
            view.clipToOutline = true
        }
    }
}
