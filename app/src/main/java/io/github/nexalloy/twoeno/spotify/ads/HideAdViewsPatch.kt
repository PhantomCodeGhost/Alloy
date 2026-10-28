package io.github.nexalloy.twoeno.spotify.ads

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import app.twoeno.extension.spotify.HideAdViewsPatch
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch
import java.util.Locale

private val AD_CLASS_NAME_MARKERS = arrayOf(
    "adsinternal", "brandad", "videoad", "audioad", "adslot", "countdownbar", "displayad", "hpto", "slate", "sponsored"
)
private val AD_RESOURCE_NAME_MARKERS = arrayOf(
    "audio_ad", "video_ad", "banner_ad", "ad_nudge", "brand_ad", "ad_banner", "hpto", "slate", "sponsor"
)
private val AD_DESCRIPTION_MARKERS = arrayOf(
    "advertisement", "werbung", "sponsored", "sponsored content"
)

private fun isAdView(view: View): Boolean {
    val className = view.javaClass.name.lowercase(Locale.ROOT)
    if (AD_CLASS_NAME_MARKERS.any { className.contains(it) }) return true

    var resourceName = ""
    try {
        if (view.id != View.NO_ID) {
            resourceName = view.resources.getResourceEntryName(view.id).lowercase(Locale.ROOT)
        }
    } catch (_: Throwable) {
    }
    if (AD_RESOURCE_NAME_MARKERS.any { resourceName.contains(it) }) return true

    val description = view.contentDescription?.toString()?.lowercase(Locale.ROOT) ?: ""
    return description == "ad" || AD_DESCRIPTION_MARKERS.any { description.contains(it) }
}

private fun hideAdViewsRecursively(root: View) {
    try {
        if (isAdView(root)) {
            HideAdViewsPatch.hideView(root)
        }
        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                hideAdViewsRecursively(root.getChildAt(i))
            }
        }
    } catch (_: Throwable) {
    }
}

val HideAdViews = patch(
    name = "Hide ad views",
    description = "Hides ad banners, display ads and the ad player.",
) {
    HideAdViewsPatch.AD_VIEW_CLASSES.forEach { className ->
        val adView = XposedHelpers.findClassIfExists(className, classLoader) ?: return@forEach

        XposedHelpers.findMethodExactIfExists(adView, "onMeasure", Int::class.java, Int::class.java)?.hookMethod {
            before { param ->
                HideAdViewsPatch.hideView(param.thisObject as View)
                param.result = null
            }
        }
        XposedHelpers.findMethodExactIfExists(adView, "onAttachedToWindow")?.hookMethod {
            after { param -> HideAdViewsPatch.hideView(param.thisObject as View) }
        }
    }

    // Dynamic child views added to layouts (such as Home tab recycler views)
    XposedHelpers.findMethodExactIfExists(
        ViewGroup::class.java,
        "addView",
        View::class.java,
        Int::class.javaPrimitiveType,
        ViewGroup.LayoutParams::class.java
    )?.hookMethod {
        after { param ->
            val child = param.args?.getOrNull(0) as? View ?: return@after
            hideAdViewsRecursively(child)
        }
    }

    // Ad views not covered by the known classes are found by their class, resource and content description names.
    Activity::class.java.getDeclaredMethod("onPostResume").hookMethod {
        after { param ->
            val decorView = (param.thisObject as Activity).window?.decorView ?: return@after
            hideAdViewsRecursively(decorView)
        }
    }
    Activity::class.java.getDeclaredMethod("onCreate", Bundle::class.java).hookMethod {
        after { param ->
            val decorView = (param.thisObject as Activity).window?.decorView ?: return@after
            decorView.post { hideAdViewsRecursively(decorView) }
        }
    }
}
