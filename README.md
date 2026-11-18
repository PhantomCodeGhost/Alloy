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
| **Spotify** | `com.spotify.music` | **9.1.90.2270** – **9.1.94.1929+** (v9.1.x) | Pablobaro & Oyasumi (`pablobaro` / `dunecache`) | Dynamic ad view suppression, Esperanto ad blocking, Smart Shuffle disable & audio muting. |
| **Office Reader** | `net.sjava.officereader` | **5.0.4** | Vippium (`vippium/brokenn-patches`) | Unlock premium & pure black AMOLED dark mode. |
| **CubeX Solver** | `diozz.cubex` | **4.1.1** | MiguelNinja19 (`miguel-morphe-patches`) | Unlock Premium (Advanced Solver / Kociemba, custom schemes). |
| **Cronometer** | `com.cronometer.android.gold` | **4.57.2** | aRandomHooman (`hoomans-morphe-patches`) | Unlock Gold (custom charts, fasting tracker, ad-free). |
| **AI Detector** | `com.aidetector.app` | **22.0.0** | GitGitro (`Gitro-Morphe-Patches`) | PairIP license bypass & Pro scanner unlock. |
| **SD Maid SE** | `eu.darken.sdmse` | **2.0.3-rc0** | Doom / rushiranpise | Unlock SD Maid SE Pro features and license verification. |
| **Mark** | `com.mark0ne.ss_app` | **6.6** | Paresh (`rushiforai/paresh-patches`) | Unlock Premium screenshot tools & PairIP license bypass. |
| **Xodo** | `com.xodo.pdf.reader` | **11.2.0** | hoo-dles (`hoo-dles/morphe-patches`) | Unlock Pro PDF editing, converting & viewing suite. |
| **Moises** | `ai.moises` | **2.141.0** | Mwbra130 (`mwbra130/morphe-patches-custom`) | Unlock Premium, high quality stem extraction & audio export tools. |
| **Pinterest** | `com.pinterest` | **14.23.0** – **14.44.0** (v14.x) | `browzomje` | Filters sponsored pins, collapses promo views, sanitizes shared links. |
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
*(Patches from Pablobaro & Oyasumi / dunecache — stable 9.1.90 – 9.1.94+)*
- **Disable ad breaks**: Disables ad slots ("stream" and "preroll") that trigger ads between songs via Esperanto
- **Disable ads**: Intercepts `GetAds` Esperanto calls to prevent server ad delivery
- **Block popup ads**: Blocks fullscreen promotional popups ("Pendragon" messages)
- **Hide ad sections & banner views**: Removes promotional modules and dynamically collapses ad layouts
- **Hide video ads & playlist ads**: Suppresses embedded video plugins and playlist promo cards
- **Hide context menu upsells**: Cleans Premium upsells using modern signalling fingerprints
- **Hide Premium tab**: Removes the bottom navigation bar Premium tab
- **Disable Smart Shuffle default**: Keeps playback following normal queue order instead of Smart Shuffle
- **Mute audio ads**: Backstop muting during ad playback *(best with "Device Broadcast Status" enabled in settings)*
- **Fix third-party launcher widgets**: Resolves widget binding permissions on custom launchers
- **Sanitize sharing links**: Strips `si` and `utm_source` tracking parameters from links
- **Material You theme & Rounded corners**: System color tinting and rounded album artwork

### 📄 Office Reader (`net.sjava.officereader`)
*(Ported from Vippium)*
- **Unlock premium**: Unlocks premium subscription features and removes Family-apps promotions from About screens
- **AMOLED theme**: Makes dark mode pure black for AMOLED screens while preserving standard light mode

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

### 🎲 CubeX Solver (`diozz.cubex`)
*(Ported from MiguelNinja19)*
- **Unlock premium**: Unlocks Advanced Solver (Kociemba), custom color schemes, VIP support, and removes relaunch screens

### 🥗 Cronometer (`com.cronometer.android.gold`)
*(Ported from aRandomHooman)*
- **Unlock Gold**: Unlocks Gold charts, advanced nutritional reports, fasting tracker, custom biometrics, and ad-free experience

### 🔍 AI Detector (`com.aidetector.app`)
*(Ported from GitGitro)*
- **Bypass license check**: Bypasses PairIP license checks
- **Unlocks pro**: Unlocks Pro scanner capabilities and removes ad units

### 🧹 SD Maid SE (`eu.darken.sdmse`)
*(Ported from Doom / rushiranpise)*
- **Unlock Pro**: Unlocks SD Maid SE Pro management features and removes license barriers

### 📸 Mark (`com.mark0ne.ss_app`)
*(Ported from Paresh)*
- **Mark Premium**: Unlocks automatic screenshot management and removes ads with PairIP license bypass

### 📑 Xodo (`com.xodo.pdf.reader`)
*(Ported from hoo-dles)*
- **Enable Pro**: Unlocks all subscription-locked PDF viewing, editing, and conversion features

### 🎼 Moises (`ai.moises`)
*(Ported from Mwbra130)*
- **Unlock Premium**: Unlocks unlimited stem separations, removes monthly track limits, and bypasses paywalls
- **High Quality Audio Export & Audio Tools**: Enables 320kbps MP3 and WAV export options, pitch shift, and smart metronome

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
- **pablobaro**: Spotify patch bundle (ad breaks, popup blocking, dynamic layout and view filters)
- **dunecache (Oyasumi)**: Spotify Esperanto ad suppression & Smart Shuffle defaults
- **Vippium**: Office Reader premium unlock & AMOLED theme patch logic
- **MiguelNinja19**: CubeX Solver PremiumHelper unlock logic
- **aRandomHooman**: Cronometer Gold patch logic
- **GitGitro**: AI Detector license bypass & Pro unlock
- **Doom (rushiranpise)**: SD Maid SE Pro unlock
- **Paresh**: Mark screenshot manager premium unlock
- **Mwbra130**: Moises stem extraction & audio tool unlock
- **browzomje**: Pinterest patch logic and ad detection
- **hoo-dles**: Duolingo subscriber & debug patch logic, and Xodo Pro unlock
- **Morphe** & **ReVanced**: Bytecode patch libraries and inspiration

## ⚖️ License

GNU General Public License v3.0 (GPL-3.0). See [LICENSE](LICENSE) and [NOTICE](NOTICE) for full terms.
