package io.github.nexalloy.brave

import io.github.nexalloy.morphe.findMethodListDirect
import org.luckypray.dexkit.query.enums.StringMatchType

val subscriptionActiveFingerprint = findMethodListDirect {
    findMethod {
        matcher {
            returnType("Z")
            usingStrings(listOf("brave.origin.subscription_active_android"), StringMatchType.Contains)
        }
    }
}

val validSubscriptionTokensFingerprint = findMethodListDirect {
    findMethod {
        matcher {
            returnType("Z")
            usingStrings(listOf("brave.origin.order_id_android", "brave.origin.purchase_token_android"), StringMatchType.Contains)
        }
    }
}

val credentialSummaryCachedFingerprint = findMethodListDirect {
    findMethod {
        matcher {
            returnType("Z")
            usingStrings(listOf("brave_origin_credential_summary_cached"), StringMatchType.Contains)
        }
    }
}

val requestCredentialSummaryFingerprint = findMethodListDirect {
    findMethod {
        matcher {
            returnType("V")
            usingStrings(listOf("SkusService is null, cannot request credential summary"), StringMatchType.Contains)
        }
    }
}

val showOriginSettingsForRestartFingerprint = findMethodListDirect {
    findMethod {
        matcher {
            name("showOriginSettingsForRestart")
            returnType("V")
        }
    }
}

val featurePolicyGatekeepersFingerprint = findMethodListDirect {
    findMethod {
        matcher {
            returnType("Z")
            usingStrings(listOf("disabled_by_policy"), StringMatchType.Contains)
        }
    }
}
