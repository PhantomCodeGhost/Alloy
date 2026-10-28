package io.github.nexalloy.brave

import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.patch

val BraveOriginPatch = patch(
    name = "Brave Origin",
    description = "Unlocks Brave Origin and enables feature toggle controls.",
    use = true
) {
    runCatching {
        ::subscriptionActiveFingerprint.dexMethodList.forEach {
            it.hookMethod(XC_MethodReplacement.returnConstant(true))
        }
    }

    runCatching {
        ::validSubscriptionTokensFingerprint.dexMethodList.forEach {
            it.hookMethod(XC_MethodReplacement.returnConstant(true))
        }
    }

    runCatching {
        ::credentialSummaryCachedFingerprint.dexMethodList.forEach {
            it.hookMethod(XC_MethodReplacement.returnConstant(true))
        }
    }

    runCatching {
        ::requestCredentialSummaryFingerprint.dexMethodList.forEach {
            it.hookMethod(object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam): Any? {
                    val callback = param.args.getOrNull(1)
                    if (callback != null) {
                        try {
                            XposedHelpers.callMethod(callback, "onResult", true)
                        } catch (_: Throwable) {
                        }
                    }
                    return null
                }
            })
        }
    }

    runCatching {
        ::showOriginSettingsForRestartFingerprint.dexMethodList.forEach {
            it.hookMethod(XC_MethodReplacement.DO_NOTHING)
        }
    }

    runCatching {
        ::featurePolicyGatekeepersFingerprint.dexMethodList.forEach {
            it.hookMethod(XC_MethodReplacement.returnConstant(false))
        }
    }
}
