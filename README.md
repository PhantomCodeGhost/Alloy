<p align="center">
  <img src="branding/master_logo.svg" width="128" height="128" alt="Alloy Logo" />
</p>

# Alloy

Alloy (`com.alloy.phantom`) is an unofficial LSPosed Xposed module that applies runtime DexKit hooks directly to target apps without needing APK repacking or re-signing.

---

## 📱 Supported Applications

For best results, use the recommended APK versions below and disable auto-updates in Google Play Store for hooked applications:

| App | Package ID | Recommended Version | Upstream Source | Notes |
|:---|:---|:---|:---|:---|
| **YouTube** | `com.google.android.youtube` | **19.34.42** / **19.43.41** / Latest Stable | Morphe (`MorpheApp/morphe-patches`) | Dynamic fingerprints; broadly compatible with stable releases. |
| **YouTube Music** | `com.google.android.apps.youtube.music` | **6.43.52** – **7.25.52** / Latest Stable | Morphe (`MorpheApp/morphe-patches`) | Broadly compatible across stable builds. |
| **Spotify** | `com.spotify.music` | **9.1.90.2270** – **9.1.94.1929+** (v9.1.x) | Pablobaro & Oyasumi (`pablobaro` / `dunecache`) | Dynamic UI, layout, and playback hooks. |
| **Office Reader** | `net.sjava.officereader` | **5.0.4** | Vippium (`vippium/brokenn-patches`) | Unlock premium & pure black AMOLED dark mode. |
| **Xodo** | `com.xodo.pdf.reader` | **11.2.0** | hoo-dles (`hoo-dles/morphe-patches`) | Unlock Pro PDF viewing and editing suite. |
| **Pinterest** | `com.pinterest` | **14.23.0** – **14.44.0** (v14.x) | `browzomje` | Dynamic feed and layout filters. |
| **Duolingo** | `com.duolingo` | **6.98.4** (v6.x) | `hoo-dles` | Unlocks Super subscriber features & unlimited hearts. |
| **Reddit** | `com.reddit.frontpage` | **2024.17.0** – **2026.45.0** / Latest Stable | Morphe (`MorpheApp/morphe-patches`) | Compatible with recent stable versions. |
| **Threads** | `com.instagram.barcelona` | **340.0.0.32.108** / **350.x** | NexAlloy / Meta | Stable across 340.x – 350.x. |
| **Strava** | `com.strava` | **360.10** – **370.x** | NexAlloy | Tested on target release. |
| **AllTrails** | `com.alltrails.alltrails` | **18.15.0** – **19.x** | NexAlloy | Tested on target release. |

---

## 🎯 Features by Application

### 🎥 YouTube (`com.google.android.youtube`)
- **Ad Blocking**: Comprehensive video ad removal, banner suppression, and popup prevention.
- **SponsorBlock**: Automatically skip non-music parts, sponsored segments, self-promos, and intros.
- **Return YouTube Dislike**: Accurate dislike counts restored using the RYD API.
- **DeArrow**: Crowdsourced titles and clear thumbnails replacing clickbait.
- **Background & PiP Playback**: Play videos in the background and picture-in-picture mode.
- **Gesture Controls**: Swipe controls for volume and brightness, tap to seek, and seekbar styling.
- **Playback & Quality**: Remember preferred video quality presets and playback speed preferences.
- **Link Sanitization**: Strip tracking parameters from shared URLs automatically.

### 🎵 YouTube Music (`com.google.android.apps.youtube.music`)
- **Ad-Free Music**: Eliminates audio and video ads seamlessly.
- **Background Playback**: Continuous playback with the screen turned off or in other apps.
- **Audio-Only Switch**: Force audio playback stream without loading video track data.
- **Clean UI**: Suppress upsells, upgrade banners, and premium promotion buttons.

### 🎧 Spotify (`com.spotify.music`)
*(Ported from pablobaro & dunecache / Oyasumi)*
- **Ad Interception**: Block audio ad breaks and dismiss popup overlays.
- **Layout & Feed Filtering**: Hide promoted cards, podcasts, audiobooks, and Premium tabs.
- **Smart Shuffle Bypass**: Maintain chronological queue order without forced Smart Shuffle.
- **Audio Mute Backstop**: Mute background audio during unexpected ad segments.
- **Third-Party Launcher Support**: Fix widget binding on custom Android launchers.
- **Sharing Sanitizer**: Remove `si` and `utm_source` tracking parameters from shared links.

### 📄 Office Reader (`net.sjava.officereader`)
*(Ported from Vippium)*
- **Unlock Premium**: Permanent unlocked subscription status and removal of Family-apps promos.
- **AMOLED Dark Theme**: Pure black backgrounds for reading on OLED/AMOLED displays.

### 📑 Xodo (`com.xodo.pdf.reader`)
*(Ported from hoo-dles)*
- **Unlock Pro**: Complete access to all subscription-locked PDF viewing, editing, converting, and annotating tools.

### 📌 Pinterest (`com.pinterest`)
*(Ported from browzomje)*
- **Disable Ads**: Remove sponsored pins and promoted items from home feeds and search grids.
- **Hide Ad Views**: Collapse empty sponsored slots and promo banners.
- **Sanitize Sharing Links**: Strip referral parameters and tracker codes.

### 🦉 Duolingo (`com.duolingo`)
*(Ported from hoo-dles)*
- **Super / Max Unlocked**: Unlimited hearts/energy and access to subscriber challenge modes.
- **Developer Menu**: Enable in-app debug preferences.
- **Unlimited RampUp**: Challenge timer extended for stress-free practice.

### 🤖 Reddit (`com.reddit.frontpage`)
- **Ad Removal**: Filter sponsored posts, promoted comments, and display ads from feeds.
- **Link Sanitizer**: Strip query tracking parameters when sharing posts.
- **Guest Mode**: Start Reddit directly as guest.

### 🧵 Threads (`com.instagram.barcelona`)
- **Hide Ads**: Suppress sponsored timeline posts and promoted content.

### 🏃 Strava (`com.strava`)
- **Subscription Features**: Unlock premium route analysis, segment leaderboards, and training features.
- **Disable Upsells**: Eliminate recurring subscription suggestions.

### 🌲 AllTrails (`com.alltrails.alltrails`)
- **Peak Membership**: Unlock offline trail maps, wrong-turn alerts, and detailed weather conditions.

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
- **pablobaro**: Spotify layout and ad suppression filters
- **dunecache (Oyasumi)**: Spotify Esperanto hooks
- **Vippium**: Office Reader patch logic
- **browzomje**: Pinterest patch logic
- **hoo-dles**: Duolingo and Xodo patch logic
- **Morphe** & **ReVanced**: Bytecode patch libraries and inspiration

## ⚖️ License

GNU General Public License v3.0 (GPL-3.0). See [LICENSE](LICENSE) and [NOTICE](NOTICE) for full terms.
