package io.github.nexalloy.brave

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.patch

val BraveOriginPatch = patch(
    name = "Brave Origin",
    description = "Unlocks Brave Origin and enables feature toggle controls.",
) {
    ::subscriptionActiveFingerprint.hookMethod(XC_MethodReplacement.returnConstant(true))
    ::validSubscriptionTokensFingerprint.hookMethod(XC_MethodReplacement.returnConstant(true))
    ::credentialSummaryCachedFingerprint.hookMethod(XC_MethodReplacement.returnConstant(true))

    ::requestCredentialSummaryFingerprint.hookMethod(object : XC_MethodReplacement() {
        override fun replaceHookedMethod(param: MethodHookParam): Any? {
            val callback = param.args[1]
            if (callback != null) {
                XposedHelpers.callMethod(callback, "onResult", true)
            }
            return null
        }
    })

    ::showOriginSettingsForRestartFingerprint.hookMethod(XC_MethodReplacement.DO_NOTHING)
}
