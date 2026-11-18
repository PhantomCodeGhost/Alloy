package io.github.nexalloy

import io.github.nexalloy.arandomhooman.cronometer.CronometerPatches
import io.github.nexalloy.browzomje.pinterest.PinterestPatches
import io.github.nexalloy.doom.sdmaidse.SdMaidSePatches
import io.github.nexalloy.gitgitro.aidetector.AiDetectorPatches
import io.github.nexalloy.hoodles.duolingo.DuolingoPatches
import io.github.nexalloy.hoodles.morphe.alltrails.AllTrailsPatches
import io.github.nexalloy.hoodles.xodo.XodoPatches
import io.github.nexalloy.miguelninja.cubex.CubeXPatches
import io.github.nexalloy.morphe.music.YTMusicPatches
import io.github.nexalloy.morphe.reddit.RedditPatches
import io.github.nexalloy.morphe.youtube.YouTubePatches
import io.github.nexalloy.mwbra.moises.MoisesPatches
import io.github.nexalloy.pablobaro.spotify.SpotifyPatches
import io.github.nexalloy.paresh.mark.MarkPatches
import io.github.nexalloy.revanced.meta.MetaPatches
import io.github.nexalloy.revanced.photomath.PhotomathPatches
import io.github.nexalloy.revanced.strava.StravaPatches
import io.github.nexalloy.vippium.officereader.OfficeReaderPatches

class AppPatchInfo(val appName: String, val packageName: String, val patches: Array<Patch>)

val appPatchConfigurations = listOf(
    AppPatchInfo("YouTube", "com.google.android.youtube", YouTubePatches),
    AppPatchInfo("YT Music", "com.google.android.apps.youtube.music", YTMusicPatches),
    AppPatchInfo("Reddit", "com.reddit.frontpage", RedditPatches),
    AppPatchInfo("Photomath", "com.microblink.photomath", PhotomathPatches),
    AppPatchInfo("Threads", "com.instagram.barcelona", MetaPatches),
    AppPatchInfo("Strava", "com.strava", StravaPatches),
    AppPatchInfo("AllTrails", "com.alltrails.alltrails", AllTrailsPatches),
    AppPatchInfo("Spotify", "com.spotify.music", SpotifyPatches),
    AppPatchInfo("Pinterest", "com.pinterest", PinterestPatches),
    AppPatchInfo("Duolingo", "com.duolingo", DuolingoPatches),
    AppPatchInfo("Office Reader", "net.sjava.officereader", OfficeReaderPatches),
    AppPatchInfo("CubeX Solver", "diozz.cubex", CubeXPatches),
    AppPatchInfo("Cronometer", "com.cronometer.android.gold", CronometerPatches),
    AppPatchInfo("AI Detector", "com.aidetector.app", AiDetectorPatches),
    AppPatchInfo("SD Maid SE", "eu.darken.sdmse", SdMaidSePatches),
    AppPatchInfo("Mark", "com.mark0ne.ss_app", MarkPatches),
    AppPatchInfo("Xodo", "com.xodo.pdf.reader", XodoPatches),
    AppPatchInfo("Moises", "ai.moises", MoisesPatches),
)

val patchesByPackage = appPatchConfigurations.associate { it.packageName to it.patches }
