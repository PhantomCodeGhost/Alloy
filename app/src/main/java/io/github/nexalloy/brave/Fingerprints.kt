package io.github.nexalloy.brave

import io.github.nexalloy.morphe.findMethodDirect
import io.github.nexalloy.morphe.strings
import org.luckypray.dexkit.query.enums.StringMatchType

val subscriptionActiveFingerprint = findMethodDirect {
    findMethod {
        matcher {
            returnType("Z")
            paramTypes("org.chromium.chrome.browser.profiles.Profile")
            strings("getIsSubscriptionActive profile is null", "brave.origin.subscription_active_android")
        }
    }.single()
}

val validSubscriptionTokensFingerprint = findMethodDirect {
    findMethod {
        matcher {
            returnType("Z")
            paramTypes("org.chromium.chrome.browser.profiles.Profile")
            strings("brave.origin.order_id_android", "brave.origin.purchase_token_android")
        }
    }.single()
}

val credentialSummaryCachedFingerprint = findMethodDirect {
    findMethod {
        matcher {
            returnType("Z")
            paramCount(0)
            strings("brave_origin_credential_summary_cached")
        }
    }.single()
}

val requestCredentialSummaryFingerprint = findMethodDirect {
    findMethod {
        matcher {
            returnType("V")
            paramTypes("org.chromium.chrome.browser.profiles.Profile", "org.chromium.base.Callback")
            strings("requestCredentialSummary profile is null", "SkusService is null, cannot request credential summary")
        }
    }.single()
}

val showOriginSettingsForRestartFingerprint = findMethodDirect {
    findMethod {
        matcher {
            name("showOriginSettingsForRestart")
            returnType("V")
            paramCount(0)
        }
    }.first()
}
