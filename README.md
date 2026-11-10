<p align="center">
  <img src="branding/master_logo.svg" width="128" height="128" alt="Alloy Logo" />
</p>

# Alloy

Alloy (`com.alloy.phantom`) is an unofficial LSPosed Xposed module that applies runtime DexKit hooks directly to target apps without needing APK repacking or re-signing.

---

## ⚠️ Important: Recommended Versions & Compatibility

> [!IMPORTANT]
> **Why versions matter:**
> DexKit hooks target methods at runtime using structural bytecode fingerprints. When an app updates past its supported version, obfuscation and internal class structures change, which causes the module to show **"error while applying following patches"** or allows ads to bypass the hook.

For best results, download the **recommended APK versions** below (preferably from trusted repositories like [APKMirror](https://www.apkmirror.com/)) and **disable auto-updates in Google Play Store** for patched apps:

| App | Package ID | Tested / Recommended Version | Patch Source | Compatibility Notes |
|:---|:---|:---|:---|:---|
| **YouTube** | `com.google.android.youtube` | **19.34.42** / **19.43.41** / Latest Stable | Morphe (`MorpheApp/morphe-patches`) | Dynamic fingerprints; broadly compatible with stable releases. |
| **YouTube Music** | `com.google.android.apps.youtube.music` | **6.43.52** – **7.25.52** / Latest Stable | Morphe (`MorpheApp/morphe-patches`) | Broadly compatible across stable builds. |
| **Spotify** | `com.spotify.music` | **8.9.18.512** – **8.9.84.594** (v8.9.x – 9.1.x) | 2eno (`2eno-patches`) | Dynamic ad view suppression & audio muting. Enable *"Device Broadcast Status"* in Spotify settings for optimal ad muting. |
| **Pinterest** | `com.pinterest` | **14.23.0** – **14.39.0** (v14.x) | `browzomje` | Filters sponsored pins, collapses promo views, sanitizes shared links. |
| **Duolingo** | `com.duolingo` | **6.98.4** (v6.x) | `hoo-dles` | Unlocks Super subscriber features, unlimited hearts/energy, debug menu, rampup time. |
| **Reddit** | `com.reddit.frontpage` | **2024.17.0** – **2026.45.0** / Latest Stable | Morphe (`MorpheApp/morphe-patches`) | Compatible with recent stable versions. |
| **Threads** | `com.instagram.barcelona` | **340.0.0.32.108** / **350.x** | NexAlloy / Meta | Stable across 340.x - 350.x. |
| **Google Photos** | `com.google.android.apps.photos` | **Any Recent Stable** | ReVanced | Pixel XL device spoofing is version-independent. |
| **Photomath** | `com.microblink.photomath` | **8.30.0** – **8.32.0** | NexAlloy | Plus subscription unlock. |
| **Strava** | `com.strava` | **360.10** – **370.x** | NexAlloy | Subscription feature unlock. |
| **AllTrails** | `com.alltrails.alltrails` | **18.15.0** – **19.x** | NexAlloy | Peak membership enable. |

> [!TIP]
> **Troubleshooting "error while applying following patches":**
> If LSPosed reports an error while applying a patch, check your installed app version against the table above. Downgrading the target app to its tested version will resolve fingerprint mismatches.

---

## 📱 Supported Apps & Patches

### 🎥 YouTube (`com.google.android.youtube`)
- Remove ads
- SponsorBlock
- Return YouTube Dislike
- DeArrow (alternative titles and thumbnails)
- Background playback
- Swipe controls & Seekbar customization
- Remove share link tracking parameters
- Video quality presets & memory

### 🎵 YouTube Music (`com.google.android.apps.youtube.music`)
- Remove audio/video ads
- Background playback
- Hide upgrade buttons & premium prompts
- Exclusive audio playback

### 🎧 Spotify (`com.spotify.music`)
*(Ad-blocking & anti-tracking only — no Premium unlock)*
- Block popup ads
- Hide ad sections & banner views
- Hide video ads & playlist ads
- Hide context menu upsells
- Hide Premium tab
- Mute audio ads *(works best when "Device Broadcast Status" is enabled in Spotify settings)*
- Fix third-party launcher widgets
- Sanitize sharing links

### 📌 Pinterest (`com.pinterest`)
*(Ported from browzomje)*
- **Disable ads**: Removes sponsored pins and promoted items from home, search, related and board feeds
- **Hide ad views**: Collapses promotional view modules and sponsored cards
- **Sanitize sharing links**: Removes tracking parameters and invite codes when copying or sharing links

### 🦉 Duolingo (`com.duolingo`)
*(Ported from hoo-dles)*
- **Enable Super / Max features**: Unlocks subscriber features and provides unlimited hearts/energy
- **Enable debug menu**: Unlocks developer debug settings menu
- **Unlimited RampUp time**: Sets challenge timer to 3600 seconds

### 🧵 Threads (`com.instagram.barcelona`)
- Hide ads

### 🤖 Reddit (`com.reddit.frontpage`)
- Hide promoted feed posts & banner ads
- Sanitize sharing links
- Start as guest

### 📷 Google Photos (`com.google.android.apps.photos`)
- Spoof Pixel XL for unlimited backup

### ➗ Photomath (`com.microblink.photomath`)
- Unlock plus features

### 🏃 Strava (`com.strava`)
- Unlock subscription features
- Disable subscription suggestions

### 🌲 AllTrails (`com.alltrails.alltrails`)
- Enable Peak membership

---

## 🛠️ Building from Source

### Prerequisites
- JDK 17
- Android SDK Platform 37 (installed automatically via Gradle)
- Git with `core.longpaths = true`

```bash
git clone --recurse-submodules https://github.com/PhantomCodeGhost/Alloy.git
cd Alloy
./gradlew assembleDebug
```

The compiled APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

---

## 📜 Credits & Acknowledgments

- **NexAlloy** by ChsBuffer: Original base project (GPL-3.0)
- **MorpheApp**: Upstream Morphe patches repository and extension library
- **DexKit** by LuckyPray: High-performance runtime dex parsing library
- **2eno**: Spotify patch implementations and reference Xposed port
- **browzomje**: Pinterest patch logic and ad detection
- **hoo-dles**: Duolingo subscriber & debug patch logic
- **Morphe** & **ReVanced**: Bytecode patch libraries and inspiration

## ⚖️ License

GNU General Public License v3.0 (GPL-3.0). See [LICENSE](LICENSE) and [NOTICE](NOTICE) for full terms.
