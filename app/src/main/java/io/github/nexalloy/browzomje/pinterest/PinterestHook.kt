package io.github.nexalloy.browzomje.pinterest

import io.github.nexalloy.browzomje.pinterest.ads.PinterestAdViewsPatch
import io.github.nexalloy.browzomje.pinterest.ads.PinterestAdsPatch
import io.github.nexalloy.browzomje.pinterest.privacy.PinterestSanitizeLinksPatch

val PinterestPatches = arrayOf(
    PinterestAdsPatch,
    PinterestAdViewsPatch,
    PinterestSanitizeLinksPatch,
)
