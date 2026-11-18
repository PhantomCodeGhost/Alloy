package io.github.nexalloy

import io.github.nexalloy.browzomje.pinterest.PinterestPatches
import io.github.nexalloy.hoodles.duolingo.DuolingoPatches
import io.github.nexalloy.hoodles.morphe.alltrails.AllTrailsPatches
import io.github.nexalloy.hoodles.xodo.XodoPatches
import io.github.nexalloy.morphe.music.YTMusicPatches
import io.github.nexalloy.morphe.reddit.RedditPatches
import io.github.nexalloy.morphe.youtube.YouTubePatches
import io.github.nexalloy.pablobaro.spotify.SpotifyPatches
import io.github.nexalloy.revanced.meta.MetaPatches
import io.github.nexalloy.revanced.strava.StravaPatches
import io.github.nexalloy.vippium.officereader.OfficeReaderPatches

class AppPatchInfo(val appName: String, val packageName: String, val patches: Array<Patch>)

val appPatchConfigurations = listOf(
    AppPatchInfo("YouTube", "com.google.android.youtube", YouTubePatches),
    AppPatchInfo("YT Music", "com.google.android.apps.youtube.music", YTMusicPatches),
    AppPatchInfo("Reddit", "com.reddit.frontpage", RedditPatches),
    AppPatchInfo("Threads", "com.instagram.barcelona", MetaPatches),
    AppPatchInfo("Strava", "com.strava", StravaPatches),
    AppPatchInfo("AllTrails", "com.alltrails.alltrails", AllTrailsPatches),
    AppPatchInfo("Spotify", "com.spotify.music", SpotifyPatches),
    AppPatchInfo("Pinterest", "com.pinterest", PinterestPatches),
    AppPatchInfo("Duolingo", "com.duolingo", DuolingoPatches),
    AppPatchInfo("Office Reader", "net.sjava.officereader", OfficeReaderPatches),
    AppPatchInfo("Xodo", "com.xodo.pdf.reader", XodoPatches),
)

val patchesByPackage = appPatchConfigurations.associate { it.packageName to it.patches }
