# Upstream Repository & Patch Tracker

This document tracks all external patch repositories, upstream forks, and submodule references used by **Alloy** (`com.alloy.phantom`). When an upstream repository publishes updates or adds support for newer APK versions, use this tracker to update Alloy.

---

## 1. Upstream Repositories

| Component / App | Upstream Repository | License | Branch / Ref | Tested APK Version | Status in Alloy |
|:---|:---|:---|:---|:---|:---|
| **Base Module Framework** | [NexAlloy/NexAlloy](https://github.com/NexAlloy/NexAlloy) | GPL-3.0 | `main` | Android 8.1 – 17 (API 37) | Base fork (`alloy`) |
| **Morphe Extension Library** | [NexAlloy/morphe-patches-library](https://github.com/NexAlloy/morphe-patches-library) | GPL-3.0 | `nexalloy` | Submodule (`morphe-patches-library`) | Core helpers & bytecode utils |
| **Morphe Patches Submodule** | [NexAlloy/morphe-patches](https://github.com/NexAlloy/morphe-patches) | GPL-3.0 | `nexalloy` | Various (see Section 2) | Submodule (`morphe-patches`) |
| **Spotify Patches & Extension** | [2eno/morphe-patches](https://github.com/2eno/morphe-patches) / [2eno-patches](https://github.com/2eno/2eno-patches) | GPL-3.0 | `main` | **`8.9.18.512` – `8.9.84.594`** | Submodule (`2eno-patches`), ported in `io.github.nexalloy.twoeno.spotify` |
| **Brave Origin Patch** | [dh6k/morphe-patches](https://github.com/dh6k/morphe-patches) & [bufferk/morphe-patches](https://github.com/bufferk/morphe-patches) | GPL-3.0 | `main` | **`1.92.140`** | Ported in `io.github.nexalloy.brave` |
| **Pinterest Patches** | [browzomje/browzomje-patches](https://github.com/browzomje/browzomje-patches) | GPL-3.0 | `main` | **`14.23.0` – `14.39.0`** | Ported in `io.github.nexalloy.browzomje.pinterest` |
| **Duolingo Patches** | [hoo-dles/morphe-patches](https://github.com/hoo-dles/morphe-patches) | GPL-3.0 | `main` | **`6.98.4`** | Ported in `io.github.nexalloy.hoodles.duolingo` |

---

## 2. Tested & Supported Target Versions

| App | Package Name | Recommended Version | Update Frequency |
|:---|:---|:---|:---|
| **YouTube** | `com.google.android.youtube` | `19.34.42` / `19.43.41` / Latest Stable | Dynamic DexKit fingerprints |
| **YouTube Music** | `com.google.android.apps.youtube.music` | `6.43.52` – `7.25.52` / Latest Stable | Dynamic DexKit fingerprints |
| **Brave Browser** | `com.brave.browser`<br>`com.brave.browser_beta`<br>`com.brave.browser_nightly` | **`1.92.140`** | Pinned to dh6k / bufferk releases |
| **Spotify** | `com.spotify.music` | **`8.9.18.512` – `8.9.84.594`** | Pinned to 2eno releases |
| **Pinterest** | `com.pinterest` | **`14.23.0` – `14.39.0`** | Pinned to browzomje releases |
| **Duolingo** | `com.duolingo` | **`6.98.4`** | Pinned to hoo-dles releases |
| **Reddit** | `com.reddit.frontpage` | `2024.17.0` – `2024.40.0` / Latest Stable | Dynamic DexKit fingerprints |
| **Threads** | `com.instagram.barcelona` | `340.0.0.32.108` / `350.x` | NexAlloy Meta hooks |
| **Google Photos** | `com.google.android.apps.photos` | Any Recent Stable | Device spoof hook |
| **Photomath** | `com.microblink.photomath` | `8.30.0` – `8.32.0` | Plus subscription hook |
| **Strava** | `com.strava` | `360.10` – `370.x` | Subscription hook |
| **AllTrails** | `com.alltrails.alltrails` | `18.15.0` – `19.x` | Membership hook |

---

## 3. How to Update Patches for Newer App Versions

When an upstream patch repo releases support for a new version:

### 2. Brave (`dh6k/morphe-patches` / `bufferk/morphe-patches`)
1. Compare `BraveOriginPatch` in `dh6k/morphe-patches` against new Brave Chromium releases.
2. If method signatures or `Profile` parameter types change, update `app/src/main/java/io/github/nexalloy/brave/Fingerprints.kt`.

### 3. Pinterest (`browzomje/browzomje-patches`)
1. Check `browzomje/browzomje-patches` for changes to `@SerializedName` ad markers or feed response constructors.
2. Update `PinterestAdDetector.kt` and `PinterestFingerprints.kt`.

### 4. Duolingo (`hoo-dles/morphe-patches`)
1. Check `hoo-dles/morphe-patches` for changes in `User`, `LoggedInState`, or `EnergyConfig` constructors.
2. Update `Fingerprints.kt` and `DuolingoEnablePremiumPatch.kt`.

### 5. Spotify (`2eno/morphe-patches`)
1. Pull updates in the `2eno-patches` submodule:
   ```bash
   cd 2eno-patches
   git fetch origin
   git merge origin/main
   cd ..
   ```
2. Verify `app/src/main/java/io/github/nexalloy/twoeno/spotify/Fingerprints.kt`.

### 6. YouTube & General Morphe Patches
1. Update `morphe-patches` submodule:
   ```bash
   git submodule update --remote morphe-patches
   ```
2. Recompile and run unit tests:
   ```bash
   ./gradlew testDebugUnitTest
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
