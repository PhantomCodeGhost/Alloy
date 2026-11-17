package io.github.nexalloy.pablobaro.spotify

import io.github.nexalloy.pablobaro.spotify.ads.BlockPopupAds
import io.github.nexalloy.pablobaro.spotify.ads.DisableAdBreaks
import io.github.nexalloy.pablobaro.spotify.ads.DisableAds
import io.github.nexalloy.pablobaro.spotify.ads.DisableSmartShuffleDefault
import io.github.nexalloy.pablobaro.spotify.ads.HideAdSections
import io.github.nexalloy.pablobaro.spotify.ads.HideAdViews
import io.github.nexalloy.pablobaro.spotify.ads.HideContextMenuUpsells
import io.github.nexalloy.pablobaro.spotify.ads.HidePlaylistAds
import io.github.nexalloy.pablobaro.spotify.ads.HideVideoAds
import io.github.nexalloy.pablobaro.spotify.ads.MuteAudioAds
import io.github.nexalloy.pablobaro.spotify.layout.HidePremiumTab
import io.github.nexalloy.pablobaro.spotify.misc.FixThirdPartyLaunchersWidgets
import io.github.nexalloy.pablobaro.spotify.privacy.SanitizeSharingLinks
import io.github.nexalloy.pablobaro.spotify.theme.MaterialYouTheme
import io.github.nexalloy.pablobaro.spotify.theme.RoundedCorners

val SpotifyPatches = arrayOf(
    MuteAudioAds,
    DisableAdBreaks,
    DisableAds,
    HideAdSections,
    HideAdViews,
    HideContextMenuUpsells,
    BlockPopupAds,
    HidePlaylistAds,
    HideVideoAds,
    HidePremiumTab,
    FixThirdPartyLaunchersWidgets,
    SanitizeSharingLinks,
    DisableSmartShuffleDefault,
    MaterialYouTheme,
    RoundedCorners,
)
