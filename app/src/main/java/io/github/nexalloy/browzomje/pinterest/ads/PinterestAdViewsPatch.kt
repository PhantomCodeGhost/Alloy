package io.github.nexalloy.browzomje.pinterest.ads

import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

private val AD_VIEW_CLASSES = listOf(
    "com.pinterest.featurelibrary.textads.TextAdView",
    "com.pinterest.activity.pin.view.modules.LegacyPromotedCloseupActionButtonModule",
    "com.pinterest.feature.pin.closeup.view.PromotedPinCloseupFloatingActionBarModule",
    "com.pinterest.feature.board.detail.header.view.lego.BoardSponsoredCuratorView"
)

private fun collapseView(view: View) {
    view.visibility = View.GONE
    val params = view.layoutParams
    if (params != null) {
        params.width = 0
        params.height = 0
        view.layoutParams = params
    }
}

val PinterestAdViewsPatch = patch(
    name = "Hide ad views",
    description = "Collapses dedicated promotional view modules and sponsored banner cards.",
    use = true
) {
    AD_VIEW_CLASSES.forEach { className ->
        val adViewClass = XposedHelpers.findClassIfExists(className, classLoader) ?: return@forEach

        XposedHelpers.findMethodExactIfExists(
            adViewClass,
            "onMeasure",
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType
        )?.hookMethod {
            before { param ->
                val view = param.thisObject as? View ?: return@before
                collapseView(view)
                try {
                    val setMeasuredDimension = View::class.java.getDeclaredMethod(
                        "setMeasuredDimension",
                        Int::class.javaPrimitiveType,
                        Int::class.javaPrimitiveType
                    )
                    setMeasuredDimension.isAccessible = true
                    setMeasuredDimension.invoke(view, 0, 0)
                } catch (_: Throwable) {
                }
                param.result = null
            }
        }

        XposedHelpers.findMethodExactIfExists(adViewClass, "onAttachedToWindow")?.hookMethod {
            after { param ->
                val view = param.thisObject as? View ?: return@after
                collapseView(view)
            }
        }
    }
}
