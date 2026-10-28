<div align="center">
  <h1>Alloy</h1>
  <p><strong>LSPosed / Vector module for runtime app modifications</strong></p>
  <p>Package ID: <code>com.alloy.phantom</code> | Target Platform: Android 8.1 – Android 17 (API 37)</p>
</div>

---

**Alloy** is an open-source LSPosed runtime hook module (forked from NexAlloy, based on DexKit and libxposed API 101). It modifies supported apps at runtime without touching or resigning the original APKs.

> [!IMPORTANT]  
> - **Root access** and **LSPosed** or **Vector** (maintained LSPosed fork) are **strictly required**.
> - This project is an independent community fork and is **not affiliated with Morphe, ReVanced, or the original app developers**.
> - Android 17 (API 37) SDK is fully supported.

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

### 🦁 Brave Browser (`com.brave.browser`, `com.brave.browser_beta`, `com.brave.browser_nightly`)
- **Brave Origin**: Unlocks Brave Origin and enables internal feature toggle controls

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

### 📸 Instagram (`com.instagram.android`)
*(Ported from Piko)*
- Hide feed ads & sponsored reels
- Hide suggested content & suggested accounts
- Sanitize sharing links

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
- **DexKit** by LuckyPray: High-performance runtime dex parsing library
- **Piko** by crimera: Instagram patch logic and fingerprints
- **2eno**: Spotify patch implementations and reference Xposed port
- **bufferk** & **dh6k**: Brave Origin patch implementations
- **Morphe** & **ReVanced**: Bytecode patch libraries and inspiration

## ⚖️ License

GNU General Public License v3.0 (GPL-3.0). See [LICENSE](LICENSE) and [NOTICE](NOTICE) for full terms.
