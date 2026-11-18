# Upstream Repository & Patch Tracker

This document tracks all external patch repositories, upstream forks, and submodule references used by **Alloy** (`com.alloy.phantom`). When an upstream repository publishes updates or adds support for newer APK versions, use this tracker to update Alloy.

---

## 1. Upstream Repositories

| Component / App | Upstream Repository | License | Branch / Ref | Tested APK Version | Status in Alloy |
|:---|:---|:---|:---|:---|:---|
| **Base Module Framework** | [NexAlloy/NexAlloy](https://github.com/NexAlloy/NexAlloy) | GPL-3.0 | `main` | Android 8.1 – 17 (API 37) | Base fork (`alloy`) |
| **Morphe Extension Library** | [MorpheApp/morphe-patches-library](https://github.com/MorpheApp/morphe-patches-library) | GPL-3.0 | `main` | Submodule (`morphe-patches-library`) | Core helpers & bytecode utils |
| **Morphe Patches Submodule** | [MorpheApp/morphe-patches](https://github.com/MorpheApp/morphe-patches) | GPL-3.0 | `main` | Various (see Section 2) | Submodule (`morphe-patches`) |
| **Spotify Patches & Extension** | [pablobaro/pablobaro-morphe-patches](https://github.com/pablobaro/pablobaro-morphe-patches) & [dunecache/oyasumi-patches](https://github.com/dunecache/oyasumi-patches) | GPL-3.0 | `main` / `dev` | **`9.1.90.2270` – `9.1.94.1929+` (v9.1.x)** | Integrated in `io.github.nexalloy.pablobaro.spotify` & `app.pablobaro.extension` |
| **Office Reader Patches** | [vippium/brokenn-patches](https://github.com/vippium/brokenn-patches) | GPL-3.0 | `dev` | **`5.0.4`** | Ported in `io.github.nexalloy.vippium.officereader` |
| **Pinterest Patches** | [browzomje/browzomje-patches](https://github.com/browzomje/browzomje-patches) | GPL-3.0 | `main` | **`14.23.0` – `14.44.0`** | Ported in `io.github.nexalloy.browzomje.pinterest` |
| **Duolingo Patches** | [hoo-dles/morphe-patches](https://github.com/hoo-dles/morphe-patches) | GPL-3.0 | `main` | **`6.98.4`** | Ported in `io.github.nexalloy.hoodles.duolingo` |
| **CubeX Solver Patches** | [MiguelNinja19/miguel-morphe-patches](https://github.com/MiguelNinja19/miguel-morphe-patches) | GPL-3.0 | `main` | **`4.1.1`** | Ported in `io.github.nexalloy.miguelninja.cubex` |
| **Cronometer Patches** | [arandomhooman/hoomans-morphe-patches](https://github.com/arandomhooman/hoomans-morphe-patches) | GPL-3.0 | `main` | **`4.57.2`** | Ported in `io.github.nexalloy.arandomhooman.cronometer` |
| **AI Detector Patches** | [GitGitro/Gitro-Morphe-Patches](https://github.com/GitGitro/Gitro-Morphe-Patches) | GPL-3.0 | `main` | **`22.0.0`** | Ported in `io.github.nexalloy.gitgitro.aidetector` |
| **SD Maid SE Patches** | [rushiranpise/morphe-patches](https://github.com/rushiranpise/morphe-patches) | GPL-3.0 | `main` | **`2.0.3-rc0`** | Ported in `io.github.nexalloy.doom.sdmaidse` |
| **Mark Patches** | [rushiforai/paresh-patches](https://github.com/rushiforai/paresh-patches) | GPL-3.0 | `main` | **`6.6`** | Ported in `io.github.nexalloy.paresh.mark` |
| **Xodo Patches** | [hoo-dles/morphe-patches](https://github.com/hoo-dles/morphe-patches) | GPL-3.0 | `main` | **`11.2.0`** | Ported in `io.github.nexalloy.hoodles.xodo` |
| **Moises Patches** | [mwbra130/morphe-patches-custom](https://github.com/mwbra130/morphe-patches-custom) | GPL-3.0 | `main` | **`2.141.0`** | Ported in `io.github.nexalloy.mwbra.moises` |

---

## 2. Tested & Supported Target Versions

| App | Package Name | Recommended Version | Update Frequency |
|:---|:---|:---|:---|
| **YouTube** | `com.google.android.youtube` | `19.34.42` / `19.43.41` / Latest Stable | Dynamic DexKit fingerprints (MorpheApp) |
| **YouTube Music** | `com.google.android.apps.youtube.music` | `6.43.52` – `7.25.52` / Latest Stable | Dynamic DexKit fingerprints (MorpheApp) |
| **Spotify** | `com.spotify.music` | **`9.1.90.2270` – `9.1.94.1929+` (v9.1.x)** | Dynamic DexKit fingerprints (pablobaro & oyasumi) |
| **Office Reader** | `net.sjava.officereader` | **`5.0.4`** | Pinned to vippium releases |
| **CubeX Solver** | `diozz.cubex` | **`4.1.1`** | Pinned to MiguelNinja19 releases |
| **Cronometer** | `com.cronometer.android.gold` | **`4.57.2`** | Pinned to aRandomHooman releases |
| **AI Detector** | `com.aidetector.app` | **`22.0.0`** | Pinned to GitGitro releases |
| **SD Maid SE** | `eu.darken.sdmse` | **`2.0.3-rc0`** | Pinned to Doom releases |
| **Mark** | `com.mark0ne.ss_app` | **`6.6`** | Pinned to Paresh releases |
| **Xodo** | `com.xodo.pdf.reader` | **`11.2.0`** | Pinned to hoo-dles releases |
| **Moises** | `ai.moises` | **`2.141.0`** | Pinned to Mwbra130 releases |
| **Pinterest** | `com.pinterest` | **`14.23.0` – `14.44.0`** | Pinned to browzomje releases |
| **Duolingo** | `com.duolingo` | **`6.98.4`** | Pinned to hoo-dles releases |
| **Reddit** | `com.reddit.frontpage` | `2024.17.0` – `2026.45.0` / Latest Stable | Dynamic DexKit fingerprints (MorpheApp) |
| **Threads** | `com.instagram.barcelona` | `340.0.0.32.108` / `350.x` | NexAlloy Meta hooks |
| **Google Photos** | `com.google.android.apps.photos` | Any Recent Stable | Device spoof hook |
| **Photomath** | `com.microblink.photomath` | `8.30.0` – `8.32.0` | Plus subscription hook |
| **Strava** | `com.strava` | `360.10` – `370.x` | Subscription hook |
| **AllTrails** | `com.alltrails.alltrails` | `18.15.0` – `19.x` | Membership hook |

---

## 3. How to Update Patches for Newer App Versions

When an upstream patch repo releases support for a new version:

### 1. Office Reader (`vippium/brokenn-patches`)
1. Check `vippium/brokenn-patches` repo for updated bytecode fingerprints in `patches/src/main/kotlin/app/brokenn/patches/officereader/`.
2. Update `io.github.nexalloy.vippium.officereader.UnlockPremiumPatch` and `AmoledThemePatch`.

### 2. Pinterest (`browzomje/browzomje-patches`)
1. Check `browzomje/browzomje-patches` for changes to `@SerializedName` ad markers or feed response constructors.
2. Update `PinterestAdDetector.kt` and `PinterestFingerprints.kt`.

### 3. Duolingo (`hoo-dles/morphe-patches`)
1. Check `hoo-dles/morphe-patches` for changes in `User`, `LoggedInState`, or `EnergyConfig` constructors.
2. Update `Fingerprints.kt` and `DuolingoEnablePremiumPatch.kt`.

### 4. Spotify (`pablobaro` & `dunecache`)
1. Check `pablobaro/pablobaro-morphe-patches` and `dunecache/oyasumi-patches` for new releases.
2. Verify `app/src/main/java/io/github/nexalloy/pablobaro/spotify/Fingerprints.kt` and `HideAdViewsPatch.kt`.

### 5. YouTube & Root Morphe Patches (`MorpheApp/morphe-patches`)
1. Update `morphe-patches` submodule from root `MorpheApp/morphe-patches`:
   ```bash
   git submodule update --remote morphe-patches
   ```
2. Recompile and run tests:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 4. Submodule Management

```bash
# Clone with all submodules
git clone --recurse-submodules https://github.com/PhantomCodeGhost/Alloy.git

# Initialize / update existing checkout
git submodule update --init --recursive
```
