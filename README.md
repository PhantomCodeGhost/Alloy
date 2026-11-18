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
| **Office Reader** | `net.sjava.officereader` | **5.0.4** | Vippium (`vippium/brokenn-patches`) | Tested on target release. |
| **CubeX Solver** | `diozz.cubex` | **4.1.1** | MiguelNinja19 (`miguel-morphe-patches`) | Tested on target release. |
| **Cronometer** | `com.cronometer.android.gold` | **4.57.2** | aRandomHooman (`hoomans-morphe-patches`) | Tested on target release. |
| **AI Detector** | `com.aidetector.app` | **22.0.0** | GitGitro (`Gitro-Morphe-Patches`) | Tested on target release. |
| **SD Maid SE** | `eu.darken.sdmse` | **2.0.3-rc0** | Doom / rushiranpise | Tested on target release. |
| **Xodo** | `com.xodo.pdf.reader` | **11.2.0** | hoo-dles (`hoo-dles/morphe-patches`) | Tested on target release. |
| **Moises** | `ai.moises` | **2.141.0** | Mwbra130 (`mwbra130/morphe-patches-custom`) | Tested on target release. |
| **Pinterest** | `com.pinterest` | **14.23.0** – **14.44.0** (v14.x) | `browzomje` | Dynamic feed and layout filters. |
| **Duolingo** | `com.duolingo` | **6.98.4** (v6.x) | `hoo-dles` | Tested on target release. |
| **Reddit** | `com.reddit.frontpage` | **2024.17.0** – **2026.45.0** / Latest Stable | Morphe (`MorpheApp/morphe-patches`) | Compatible with recent stable versions. |
| **Threads** | `com.instagram.barcelona` | **340.0.0.32.108** / **350.x** | NexAlloy / Meta | Stable across 340.x – 350.x. |
| **Strava** | `com.strava` | **360.10** – **370.x** | NexAlloy | Tested on target release. |
| **AllTrails** | `com.alltrails.alltrails` | **18.15.0** – **19.x** | NexAlloy | Tested on target release. |

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
- **MiguelNinja19**: CubeX Solver patch logic
- **aRandomHooman**: Cronometer patch logic
- **GitGitro**: AI Detector patch logic
- **Doom (rushiranpise)**: SD Maid SE patch logic
- **Mwbra130**: Moises patch logic
- **browzomje**: Pinterest patch logic
- **hoo-dles**: Duolingo and Xodo patch logic
- **Morphe** & **ReVanced**: Bytecode patch libraries and inspiration

## ⚖️ License

GNU General Public License v3.0 (GPL-3.0). See [LICENSE](LICENSE) and [NOTICE](NOTICE) for full terms.
