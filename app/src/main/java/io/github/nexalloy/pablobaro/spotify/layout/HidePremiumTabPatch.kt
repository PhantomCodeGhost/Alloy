package io.github.nexalloy.pablobaro.spotify.layout

import app.pablobaro.extension.spotify.HidePremiumTabPatch
import io.github.nexalloy.hookMethod
import io.github.nexalloy.pablobaro.spotify.navigationTabSetConstructor
import io.github.nexalloy.patch

val HidePremiumTab = patch(
    name = "Hide Premium tab",
    description = "Removes the \"Premium\" tab from the bottom navigation bar.",
) {
    runCatching {
        val constructor = runCatching { ::navigationTabSetConstructor.dexMethod }.getOrNull()
        constructor?.hookMethod {
            before { param ->
                val newTabs = HidePremiumTabPatch.filterTabs(
                    param.args[0],
                    param.args[1],
                    param.args[2],
                    param.args[3],
                    param.args[4]
                )
                if (newTabs != null) {
                    for (i in 0 until minOf(5, param.args.size - 1)) {
                        param.args[i] = newTabs[i]
                    }
                    if (param.args.size > 5 && param.args[5] is Int) {
                        param.args[5] = HidePremiumTabPatch.filterFlags(param.args[5] as Int)
                    }
                }
            }
        }
    }
}
