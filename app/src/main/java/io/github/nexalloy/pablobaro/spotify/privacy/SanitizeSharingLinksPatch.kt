package io.github.nexalloy.pablobaro.spotify.privacy

import app.pablobaro.extension.spotify.SanitizeSharingLinksPatch
import io.github.nexalloy.hookMethod
import io.github.nexalloy.pablobaro.spotify.formatAndroidShareSheetUrlMethod
import io.github.nexalloy.pablobaro.spotify.shareCopyUrlMethod
import io.github.nexalloy.patch

val SanitizeSharingLinks = patch(
    name = "Sanitize sharing links",
    description = "Removes the tracking parameters from shared and copied links.",
) {
    runCatching {
        val formatShare = runCatching { ::formatAndroidShareSheetUrlMethod.dexMethod }.getOrNull()
        formatShare?.hookMethod {
            before { param ->
                // The URL is the second parameter of the static method or the second parameter overall
                val urlIndex = if (param.args.size > 1) 1 else 0
                val url = param.args[urlIndex] as? String
                if (url != null) {
                    param.args[urlIndex] = SanitizeSharingLinksPatch.sanitizeUrl(url)
                }
            }
        }

        val shareCopy = runCatching { ::shareCopyUrlMethod.dexMethod }.getOrNull()
        shareCopy?.hookMethod {
            after { param ->
                val result = param.result
                if (result is String) {
                    param.result = SanitizeSharingLinksPatch.sanitizeUrl(result)
                } else if (result is CharSequence) {
                    param.result = SanitizeSharingLinksPatch.sanitizeText(result)
                }
            }
        }
    }
}
