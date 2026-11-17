package io.github.nexalloy.pablobaro.spotify.ads

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.pablobaro.extension.spotify.HideAdViewsPatch
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch
import java.util.Locale

private val AD_LAYOUT_NAMES = setOf(
    "ads_mode_page_layout",
    "nowplayingmini_ads",
    "default_embeddedad_image_card",
    "default_embeddedad_video_card",
    "floating_cta_ad_card_npb",
    "ad_npb_attachment_layout"
)

private val AD_CLASS_NAME_MARKERS = arrayOf(
    "adsinternal", "brandad", "videoad", "audioad", "adslot", "countdownbar", "displayad",
    "hpto", "slate", "sponsored", "billboard", "adbanner", "adoverlay", "adview", "partnerad",
    "sponsoredcontent", "nativead", "adcontainer", "upsellcard", "premiumheader",
    "adsdisplay", "nowplayingmodes.adsmode"
)

private val AD_RESOURCE_NAME_MARKERS = arrayOf(
    "audio_ad", "video_ad", "banner_ad", "ad_nudge", "brand_ad", "ad_banner", "hpto", "slate",
    "sponsor", "sponsored", "ad_slot", "ad_overlay", "billboard", "promo_banner", "ad_container",
    "ad_view", "ad_break", "native_ad", "commercial", "leaderboard", "ad_item", "ad_card",
    "upsell_card", "sponsored_item", "sponsored_card", "sponsored_playlist", "ads_mode",
    "nowplayingmini_ads", "embeddedad"
)

private val AD_DESCRIPTION_MARKERS = arrayOf(
    "advertisement", "werbung", "sponsored", "sponsored content", "publicité", "anuncio", "patrocinado"
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
    if (description == "ad" || AD_DESCRIPTION_MARKERS.any { description.contains(it) }) return true

    val tag = view.tag?.toString()?.lowercase(Locale.ROOT) ?: ""
    if (tag == "ad" || tag == "sponsored" || tag.contains("banner_ad") || tag.contains("hpto")) return true

    if (view is TextView) {
        val text = view.text?.toString()?.trim()?.lowercase(Locale.ROOT) ?: ""
        if (text == "ad" || text == "sponsored" || text == "werbung" || text == "advertisement") {
            val parent = view.parent
            if (parent is ViewGroup && parent !is android.widget.ScrollView) {
                HideAdViewsPatch.hideView(parent)
            }
            return true
        }
    }

    return false
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

    // Intercept LayoutInflater to hide newly inflated ad layouts
    XposedHelpers.findMethodExactIfExists(
        LayoutInflater::class.java,
        "inflate",
        Int::class.javaPrimitiveType,
        ViewGroup::class.java,
        Boolean::class.javaPrimitiveType
    )?.hookMethod {
        after { param ->
            val layoutResId = param.args[0] as? Int ?: return@after
            val view = param.result as? View ?: return@after
            runCatching {
                val entryName = view.resources.getResourceEntryName(layoutResId).lowercase(Locale.ROOT)
                if (AD_LAYOUT_NAMES.contains(entryName) || AD_RESOURCE_NAME_MARKERS.any { entryName.contains(it) }) {
                    HideAdViewsPatch.hideView(view)
                }
            }
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
        before { param ->
            val child = param.args[0] as? View ?: return@before
            if (isAdView(child)) {
                HideAdViewsPatch.hideView(child)
            }
        }
    }

    // Sweep view hierarchies after activity creation and resume
    Activity::class.java.getMethod("onPostCreate", Bundle::class.java).hookMethod {
        after { param ->
            val activity = param.thisObject as? Activity ?: return@after
            activity.window?.decorView?.let(::hideAdViewsRecursively)
        }
    }
    Activity::class.java.getMethod("onResume").hookMethod {
        after { param ->
            val activity = param.thisObject as? Activity ?: return@after
            activity.window?.decorView?.let(::hideAdViewsRecursively)
        }
    }
}
