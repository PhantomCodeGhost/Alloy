package io.github.nexalloy.browzomje.pinterest.privacy

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

private val TRACKING_PARAMS = setOf(
    "invite_code", "sender", "sfo", "utm_source", "utm_medium", "utm_campaign", "utm_term", "utm_content", "ref"
)

private fun sanitizeUrl(text: String): String {
    if (!text.contains("pinterest.com") && !text.contains("pin.it")) return text
    return try {
        val uri = Uri.parse(text)
        if (uri.scheme == null || uri.host == null) return text
        val builder = uri.buildUpon().clearQuery()
        for (param in uri.queryParameterNames) {
            if (param !in TRACKING_PARAMS && !param.startsWith("utm_")) {
                builder.appendQueryParameter(param, uri.getQueryParameter(param))
            }
        }
        builder.build().toString()
    } catch (_: Throwable) {
        text
    }
}

val PinterestSanitizeLinksPatch = patch(
    name = "Sanitize sharing links",
    description = "Removes tracking parameters and invite codes when copying or sharing Pinterest links.",
    use = true
) {
    // 1. Hook Intent.putExtra(String, String)
    XposedHelpers.findMethodExactIfExists(
        Intent::class.java,
        "putExtra",
        String::class.java,
        String::class.java
    )?.hookMethod {
        before { param ->
            val value = param.args[1] as? String ?: return@before
            if (value.contains("pinterest.com") || value.contains("pin.it")) {
                param.args[1] = sanitizeUrl(value)
            }
        }
    }

    // 2. Hook ClipboardManager.setPrimaryClip(ClipData)
    XposedHelpers.findMethodExactIfExists(
        ClipboardManager::class.java,
        "setPrimaryClip",
        ClipData::class.java
    )?.hookMethod {
        before { param ->
            val clipData = param.args[0] as? ClipData ?: return@before
            if (clipData.itemCount > 0) {
                val item = clipData.getItemAt(0)
                val text = item?.text?.toString() ?: return@before
                if (text.contains("pinterest.com") || text.contains("pin.it")) {
                    val sanitized = sanitizeUrl(text)
                    param.args[0] = ClipData.newPlainText(clipData.description?.label, sanitized)
                }
            }
        }
    }
}
