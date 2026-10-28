# PhantomCore: Execution Plan

**Goal:** Fork NexAlloy (LSPosed/Vector module), rename it **PhantomCore**, keep its existing logo and UI, and extend it with more apps and patches ported from community Morphe patch repos. Also add Android 17 support.

**Status:** Planning document only. Nothing is built yet.
**Prepared:** 28 Oct 2026

---

## 0. Read this first (critical notes)

These are the points most likely to cause a wrong turn if skipped.

1. **Morphe patches cannot be copied into NexAlloy. They must be ported.**
   Morphe patches rewrite an APK's bytecode before install. NexAlloy never touches the APK. It hooks the original app at runtime through LSPosed. So each patch from Piko, bufferk, dh6k or 2eno has to be re-implemented as a runtime hook (Xposed API + DexKit fingerprints). This is real engineering work, one patch at a time.

2. **Some patches cannot be ported at all.** Anything that changes the APK itself (package name, app icon, manifest flags, network security config, version code, added resources) has no runtime-hook equivalent. These are listed in section 6 and must be excluded or redesigned.

3. **The Brave "AMOLED theme" patch from `dh6k/morphe-patches` does not exist in that repo.**
   The repo README lists three patches: *Brave Origin* (for Brave, Beta and Nightly), and two universal patches, *Change app icon* and *Change app name*. There is no AMOLED theme. See section 7 for options. **Do not let the executing agent invent one silently.**

4. **Brave Origin appears in both Brave repos.** `bufferk` and `dh6k` list the same patch name, description and tested version (Brave 1.92.140). Implement it **once**, compare both implementations, keep the more robust one, credit both.

5. **A reference port already exists.** `2eno/NexAlloy` (a fork of NexAlloy, archived 14 Oct 2026) already ported Piko's Instagram patches and 2eno's Spotify patches into Xposed hooks. It is GPL-3.0 and is the best guide for how these ports are done. Its author has moved to a successor project, NexAlloyNext. Use the archived fork as read-only reference. Do not fork it as the base.

6. **I could not read NexAlloy's Kotlin source.** GitHub blocked automated access to the `app/` tree and raw files. This plan is based on the README, `DEVELOPMENT.md`, `.gitmodules`, the four patch repos' READMEs, and the 2eno fork's README. Anything about internals not stated in those files is marked **VERIFY** and is covered by Phase 0.

7. **`DEVELOPMENT.md` is partly out of date.** It refers to a `revanced-patches/` folder, but `.gitmodules` defines `morphe-patches` and `morphe-patches-library`. Trust the repository, not the doc.

8. **Licensing is GPL-3.0 throughout.** NexAlloy, Piko, bufferk, dh6k and 2eno are all GPL-3.0 (bufferk, dh6k and 2eno add Section 7 notes). Your fork must stay GPL-3.0, keep every LICENSE/NOTICE file, keep copyright headers in ported files, and publish source. Several of these repos forbid using the name "Morphe" for derivatives. "PhantomCore" is fine. Crediting Morphe as a dependency or inspiration is fine. Naming anything "Morphe ..." is not.

9. **Logo/branding:** keeping NexAlloy's logo and UI is fine as a temporary step, but it is the upstream project's identity. Plan to replace it before any public release so users do not mistake PhantomCore for the official NexAlloy.

---

## 1. How NexAlloy works (plain-language)

```
Morphe route (what you use today)          NexAlloy route (what PhantomCore keeps)
--------------------------------           ---------------------------------------
original APK                               original APK, untouched
   -> Morphe patches the bytecode          LSPosed/Vector injects the module into the app process
   -> re-sign with a different key         DexKit finds target methods by "fingerprints" at launch
   -> reinstall on every app update        module hooks those methods (replace / before / after)
   -> no root needed                       survives app updates if fingerprints still match
                                           ROOT + LSPosed/Vector required
```

Key moving parts, from `DEVELOPMENT.md`:

| Piece | Where | Purpose |
|---|---|---|
| Entry point | `app/src/main/java/io/github/nexalloy/MainHook.kt` | LSPosed calls this; it decides which app's patches to load |
| Project patches | `app/src/main/java/io/github/nexalloy/morphe/` | One package per app, then per category |
| Fingerprints | `Fingerprints.kt` beside each patch | DexKit queries that locate obfuscated methods |
| Patch | `SomethingPatch.kt` | `patch(name = "...") { ::someFingerprint.hookMethod(...) }` |
| App registry | `AppPatchInfo.kt` | `AppPatchInfo("Name", "package.id", Patches)` list (**VERIFY** exact path) |
| Scope | `AndroidManifest.xml` (package queries), `res/values/arrays.xml` (scope recommendation), `README.md` | Makes the app appear in LSPosed scope |
| Porting helper | `FingerprintCompat.kt` | Translates ReVanced/Morphe fingerprint style into DexKit matchers |
| Submodules | `morphe-patches`, `morphe-patches-library` (NexAlloy forks, branch `nexalloy`) | Shared extension code and reference patches |
| Tests | `app/src/test/.../FingerprintsKtTest.kt` | Fingerprint tests; APKs go in `app/binaries/` named `<package>-<version>.apk` |

**What a new app needs (per `DEVELOPMENT.md`):** a `Fingerprints.kt`, one or more patch files, an `<App>Hook.kt` exporting the patch array, an `AppPatchInfo(...)` line, and the three scope edits (manifest queries, `arrays.xml`, README).

---

## 2. Scope of work

| App | Package | Source to port | Action |
|---|---|---|---|
| Instagram | `com.instagram.android` | `crimera/piko` (Instagram) | **Remove** NexAlloy's existing Instagram patch(es), **replace** with Piko ports |
| Brave | `com.brave.browser`, `com.brave.browser_beta`, `com.brave.browser_nightly` | `bufferk/morphe-patches` (Brave Origin), `dh6k/morphe-patches` (Brave Origin; AMOLED not found) | Add Brave Origin once; AMOLED **blocked** pending decision |
| Spotify | `com.spotify.music` | `2eno/morphe-patches` (Spotify) | Add (10 patches, ad-block / anti-tracking only, **no Premium unlock**) |
| Everything else | n/a | n/a | **Do not touch** (YouTube, YT Music, Reddit, Google Photos, Photomath, Threads, Strava, AllTrails) |
| Platform | Android 17 (API 37) | n/a | Add support and verify |

Package names for Brave come from dh6k's README. Instagram and Spotify package IDs are the standard ones; **VERIFY** against the APKs.

---

## 3. Phased execution plan

Each phase ends with a **gate**. Do not start the next phase until the gate passes. Make **one commit (or PR) per phase** so any phase can be reverted cleanly.

### Phase 0: Prerequisites and reading (no code changes)

**Environment**
- JDK matching upstream (check the Gradle toolchain / GitHub Actions workflow in `.github/`) **VERIFY**
- Android SDK with the platform NexAlloy targets, plus platform 37 for Phase 2
- Git with submodule support
- A GitHub account/org that will own the fork
- A GitHub token with `read:packages`: the 2eno repo notes Morphe build tools are hosted on GitHub Packages, so builds may need it. **VERIFY** whether NexAlloy's own build requires it
- A rooted test device with Magisk/KernelSU + Zygisk + **Vector (the maintained LSPosed successor)**, ideally on Android 17 for Phase 2
- Original APKs for the target apps and versions (see Phase 3 to 5 for versions)

**Read, in this order**
1. `README.md`, `DEVELOPMENT.md`, `.gitmodules`, `.github/workflows/*`
2. `app/build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`, `gradle/libs.versions.toml` (if present)
3. `MainHook.kt`, `AppPatchInfo.kt`, `FingerprintCompat.kt`
4. One complete small app package end to end (for example Photomath: fingerprint, patch, hook, registry, scope)
5. How user-facing settings/toggles are exposed. This matters for Instagram, where Piko relies on in-app settings. **VERIFY**
6. The existing Instagram and Threads packages. **VERIFY whether they share code** (see Phase 3 risk)

**Gate 0:** a short written summary listing: build command, applicationId, namespace, compileSdk/targetSdk/minSdk, Xposed API version, DexKit version, how settings work, Instagram/Threads shared code, and any hard-coded references to `nexalloy` strings.

### Phase 1: Fork, rename, baseline build

1. Fork `NexAlloy/NexAlloy` to your account, rename the fork to `PhantomCore`.
2. **Fork the two submodule repos too** (`NexAlloy/morphe-patches`, `NexAlloy/morphe-patches-library`, branch `nexalloy`) into your account, then update `.gitmodules` URLs to your forks. This stops upstream rewrites or deletions from breaking your build. Keep the submodule commits pinned.
3. Clone with submodules:
   ```bash
   git clone --recurse-submodules https://github.com/<you>/PhantomCore.git
   cd PhantomCore
   git remote add upstream https://github.com/NexAlloy/NexAlloy.git
   git checkout -b phantomcore/main
   ```
4. **Build the unmodified fork first.** Install it, enable it in Vector, confirm an existing patch (for example YouTube ad removal) works. This proves your toolchain before any change.
5. Rename (minimum, safe set):
   - App display name in `strings.xml` to **PhantomCore**
   - `README.md` title and text (full rewrite is Phase 7)
   - Xposed module description string
   - Release artifact names in the CI workflow
6. **Decision, applicationId:**
   - *Keep* upstream's ID: PhantomCore replaces NexAlloy on a device; settings carry over; cannot be installed side by side.
   - *Change* only `applicationId` (for example `io.github.<you>.phantomcore`): installs side by side, clean identity. Do **not** rename Kotlin source packages (`io.github.nexalloy...`); renaming them is a large, risky refactor with no user benefit.
   - **Recommendation: change `applicationId` only**, but first grep the whole repo for hard-coded `nexalloy` / the old package ID (SharedPreferences names, content providers, intents, `xposed_init`, ProGuard rules) **VERIFY**.
7. Do **not** change the logo, launcher icon, theme or layouts.

**Gate 1:** renamed build installs, appears in Vector as PhantomCore, and an existing upstream patch still works exactly as before.

### Phase 2: Android 17 support

Framework support is mainly the job of Vector/Zygisk, not your module. Public release notes indicate Vector lists Android 8.1 to 17 Beta and a Zygisk Next release adapted to Android 17 QPR2 Beta 3. Your module's job is to not break on it.

1. Raise `compileSdk` (and `targetSdk` if upstream policy allows) to **37** **VERIFY the exact API level and SDK package name in SDK Manager**.
2. Check the Xposed API the module uses. One third-party module changelog mentions requiring an LSPosed build with libxposed API 101 for Android 17 support. **VERIFY** whether NexAlloy uses the legacy API or the newer one and whether any migration is needed.
3. Check the DexKit version supports Android 17 era dex/ART changes; bump only if a fix exists upstream.
4. Audit hidden-API/reflection use in shared extension code for anything newly restricted.
5. Test on a real Android 17 device: module loads, scope works, 2 to 3 existing patches work (YouTube, Reddit).

**Gate 2:** builds with the new SDK, no regression on Android 16 or older, and verified working on Android 17 hardware. If no Android 17 device is available, mark Android 17 as *"builds; runtime unverified"* and do not claim full support in the README.

### Phase 3: Instagram (replace with Piko)

**Remove:** NexAlloy's current Instagram patch (its README lists only "Hide ads").
**Risk:** Threads also has "Hide ads" and may share code with Instagram. Remove only Instagram-specific code and run Threads after the change. **VERIFY**

**Target version:** Piko's current README lists a single supported Instagram version, **447.0.0.55.81**. Fingerprints are version-specific. Use that APK for porting and testing, and state it in the README.

**Port in tiers** (see section 6 for the table):
- **Tier A:** patches the archived 2eno fork already ported. Re-implement against the 2eno code as reference (GPL-3.0, keep its `piko/NOTICE`). This is the fast path.
- **Tier B:** Piko patches not in the 2eno port that look hook-feasible. Evaluate one by one. Many need injected buttons or dialogs, which are harder in a hook module.
- **Tier C:** needs APK/resource/manifest changes. Exclude and document.

Add a `NOTICE` file crediting Piko (crimera) and the 2eno port, preserved from the reference.

**Gate 3:** Instagram launches clean, each ported patch toggles on/off and does what it says on 447.0.0.55.81, Threads unaffected, no old Instagram code remains, README list matches the code exactly.

### Phase 4: Brave

1. Register packages `com.brave.browser`, `com.brave.browser_beta`, `com.brave.browser_nightly` (scope edits as in section 1).
2. Implement **Brave Origin** once: "Unlocks Brave Origin and enables feature toggle controls." Read both `bufferk` and `dh6k` implementations, pick the cleaner/more robust, credit both. Tested version: **1.92.140**. Beta/Nightly are "experimental, version-unpinned" per dh6k.
3. **AMOLED theme: blocked.** Do not implement until a decision is made (section 7).

Side benefit worth testing: dh6k documents that re-signed Brave APKs can break WebAPK "Install" because the signature changes. A runtime hook does not re-sign the app, so this should not apply. **VERIFY on device.**

**Gate 4:** Brave 1.92.140 launches, Origin controls appear and work, no crash on Beta/Nightly if tested.

### Phase 5: Spotify

Port the 10 Spotify patches from `2eno/morphe-patches`:

Block popup ads, Fix third party launchers widgets, Hide Premium tab, Hide ad sections, Hide ad views, Hide context menu upsells, Hide playlist ads, Hide video ads, Mute audio ads, Sanitize sharing links.

Notes:
- These are **ad-blocking and anti-tracking only. There is no Premium unlock**, and the README must not imply one.
- 2eno states its extension code "only uses the Android framework and reflection", so NexAlloy-style hooks can compile the same code. This makes Spotify the **lowest-risk** app to port.
- "Mute audio ads" works better when *Device broadcast status* is enabled in Spotify settings; mention this in the README.
- Spotify version for 2eno's patches is not stated in the README. **VERIFY** from the repo's patch annotations and pin it.
- The archived 2eno fork also had "Material You theme" and "Rounded corners" for Spotify. They are **not** in the Morphe bundle you asked for. Treat as optional extras, not part of this scope.

**Gate 5:** Spotify launches, each patch verified on the pinned version.

### Phase 6: Scope and registry check

For every new app: `AppPatchInfo` line, manifest package query, `arrays.xml` scope recommendation, README entry. Confirm that Instagram, Brave (3 packages) and Spotify show up in Vector's scope list for PhantomCore.

### Phase 7: README and docs

Replace the NexAlloy README with the PhantomCore README (draft in section 9). Update `DEVELOPMENT.md` fixes (submodule names, new apps). Add `NOTICE` entries. Update Discord/Telegram/wiki links, which still point at NexAlloy's community channels; do not leave them implying they are PhantomCore's support channels.

### Phase 8: Final verification and release

- Full clean build from a fresh clone with submodules
- Run unit tests; add fingerprint tests for new apps (APKs in `app/binaries/`, never committed)
- Device matrix: at minimum Android 17 plus one older version
- Confirm no unrelated app regressed (YouTube, YT Music, Reddit, Threads, Strava, AllTrails, Photomath, Google Photos)
- Generate a signing key for releases and **store it safely**; losing it blocks updates for installed users
- Tag `v0.1.0`, publish release with source, license and changelog

---

## 4. Rules for the executing agent (do not break things)

1. Work on a branch. One commit per phase. Never force-push `main`.
2. Never change logo, launcher icon, theme, layouts or navigation of the module UI.
3. Never modify the submodule contents in place without forking them first (Phase 1, step 2).
4. Do not refactor, reformat or "clean up" existing upstream code.
5. Do not rename Kotlin source packages.
6. Never copy a Morphe patch file in as-is. It will not compile as a hook. Port it.
7. Never remove or alter license headers, `LICENSE` or `NOTICE` files.
8. Never claim a patch works unless it was verified on a device, on the pinned app version.
9. If a fingerprint does not match on the pinned version, stop and report. Do not loosen it until it "matches something".
10. If a patch needs resource, manifest or signature changes, mark it **Not portable** and move on.
11. Do not commit APKs, keystores or tokens.
12. Stop and ask when blocked by a decision listed in section 8.

---

## 5. Notes and caveats

- **Root is required.** This route trades Morphe's no-root convenience for patches that survive app updates.
- **App updates still matter.** Fingerprints can break when an app updates its obfuscation. Pin tested versions in the README and expect to refresh fingerprints per release.
- **Instagram is a moving target.** Piko currently supports one version. Instagram's server-side changes can also break patches regardless of version.
- **Account risk.** Patches such as anonymous viewing, Plus unlocks, or developer options can violate app terms and may put accounts at risk. State this in the README.
- **Upstream tracking.** Keep `upstream` as a remote so you can merge NexAlloy fixes. Expect conflicts in `README.md`, the app registry and manifests.
- **Not affiliated.** PhantomCore is not an official NexAlloy, Morphe, ReVanced, Piko, Brave or Spotify project.

---

## 6. Instagram patch triage (starting point, to be confirmed in Phase 3)

Piko lists 62 Instagram patches. Triage below is a **starting hypothesis** from patch names and descriptions, not a verified result.

**Tier A: ported previously in the archived 2eno fork (use as reference)**
Hide ads · Hide suggested content · Unlock Plus benefits · Disable analytics · Disable stories · Disable highlights · Disable explore · Disable comments · Disable discover people · View stories anonymously · View live anonymously · View DMs anonymously · Disable typing status · Disable screenshot detection · Sanitize share links · Open links externally · Hide reshare button · Hide Reels/Create/Search/Direct buttons · Hide notes tray · Disable double tap like · Disable Reels scrolling · Disable story flipping · Disable video autoplay · Stories audio autoplay · Disable onboarding permission prompts · Remove build expired popup · Limit feed to following profiles · Improve image viewing · Unlock developer options

Caution: that port targeted an earlier Piko version. Re-verify every fingerprint on 447.0.0.55.81.

**Tier B: in Piko, not in the 2eno port; evaluate individually (often need injected UI)**
Copy comment · Save media comment · Download media · Download voice message · External downloader · Mark chat as read manually · Friendship status indicator · Hide stories tray · Hide save buttons · Hide share button · Hide Reels follow button · Hide group creation button on sharesheet · Loop story · Filter stories · Make ephemeral media permanent · Disable swipe to create · Save deleted messages · Save Instants · View story mentions · More options on post · More options on profile · Customize navigation bar · Customise story timestamp · Inbox lock · Focus Lock · Change like animation · Unlock employee options · Recommended flags

**Tier C: likely not portable as runtime hooks (APK, manifest or resource changes). Confirm, then exclude**
Add settings (in-app settings injection; replace with the module's own settings) · Allow user network certificate · Change version code · Clone (package/app name change) · Theme (Material You/AMOLED resources) · Custom font · Customise story ring size · Remove empty bottom space

---

## 7. Brave AMOLED theme: options

The patch named in your request is not in the linked repository. Choose one:

| Option | What it means | Effort | Risk |
|---|---|---|---|
| **A. Provide the right repo/link** | You (or the morphe-patches.software listing, which loads dynamically and I could not read) point to the actual source | Low | Low |
| **B. Defer** | Ship Brave Origin now, add AMOLED later | None | None |
| **C. Write an original patch** | PhantomCore-authored hook for Brave's dark theme / night-mode handling | High (Chromium Java layer is obfuscated; theming is mostly resource-based) | Medium to high, may not be fully achievable with hooks |

**Recommendation: A, otherwise B.** Option C should only start after a feasibility spike.

---

## 8. Decisions needed from you before execution

1. **Brave AMOLED:** option A, B or C?
2. **applicationId:** keep upstream's or change (recommended)?
3. **GitHub owner** for the fork and submodule forks (username or org)?
4. **Instagram scope:** Tier A only for the first release, or attempt Tier B too?
5. **Test device:** which phone/ROM, and is it on Android 17?
6. **Branding timeline:** when will NexAlloy's logo be replaced?

---

## 9. README draft for PhantomCore

> Fill the Instagram list only with patches that passed Gate 3. Keep the other app lists as upstream has them.

```markdown
# PhantomCore

**An LSPosed/Vector module that applies app patches at runtime, no repackaging required.**
Forked from [NexAlloy](https://github.com/NexAlloy/NexAlloy) (by ChsBuffer), with additional apps and patches.

> [!IMPORTANT]
> - PhantomCore is **not** an official NexAlloy, Morphe, ReVanced, Piko, Brave or Spotify project. Do not ask their developers for help.
> - **Root access is required** (Magisk or KernelSU with Zygisk, plus LSPosed/Vector).
> - Patches are pinned to tested app versions. See the table below.

## Downloads
- Release: <link>
- Nightly: <link>

## Supported Android versions
Android 8.1 to 17 (Android 17: <verified / builds, runtime unverified>).

## Supported apps and patches

### YouTube, YouTube Music, Reddit, Google Photos, Photomath, Threads, Strava, AllTrails
(unchanged from upstream; copy the current lists)

### Instagram (new, based on Piko)  ·  tested: 447.0.0.55.81
- <list only verified patches>

### Brave (new)  ·  tested: 1.92.140
- Brave Origin: unlocks Brave Origin and enables feature toggle controls
  (Brave, Beta and Nightly packages; Beta/Nightly experimental)

### Spotify (new)  ·  tested: <pinned version>
- Mute audio ads · Hide ad sections · Hide ad views · Hide context menu upsells
- Block popup ads · Hide playlist ads · Hide video ads · Hide Premium tab
- Fix third party launchers widgets · Sanitize sharing links
- Ad-blocking and anti-tracking only. **No Premium unlock.**

## Setup
1. Install PhantomCore.
2. Enable it in LSPosed/Vector and tick the target apps in its scope.
3. Force-stop and reopen the target app.

## Disclaimer
Some patches (anonymous viewing, plan unlocks, developer options) may violate an app's terms of service and could put your account at risk. Use at your own risk.

## Credits
- [NexAlloy](https://github.com/NexAlloy/NexAlloy) / ChsBuffer: base module
- [Piko](https://github.com/crimera/piko) (crimera): Instagram patches
- [bufferk/morphe-patches](https://github.com/bufferk/morphe-patches) and [dh6k/morphe-patches](https://github.com/dh6k/morphe-patches): Brave Origin
- [2eno/morphe-patches](https://github.com/2eno/morphe-patches): Spotify patches
- [DexKit](https://luckypray.org/DexKit/en/), [Morphe](https://morphe.software), [ReVanced](https://revanced.app)

## License
GPL-3.0. See `LICENSE` and `NOTICE`.
```

---

## 10. Prompts for the executing agent

Use with Claude Code (or any coding agent) opened in an empty working folder. Run the prompts **in order**, one per session or one per phase, and review each gate before continuing.

### 10.1 Master prompt (paste first, keep for every phase)

```text
You are extending a fork of the NexAlloy LSPosed module into a project named
"PhantomCore". Read PhantomCore-Execution-Plan.md fully before doing anything.

Hard rules:
- Follow the plan's phases in order. Stop at every Gate and report evidence
  (build output, device test result, or file diff). Do not continue until I approve.
- Do NOT change the module's logo, launcher icon, theme, layouts or navigation.
- Do NOT rename Kotlin source packages. Do NOT refactor or reformat upstream code.
- Do NOT modify submodule contents before the submodules are forked and .gitmodules
  points at my forks.
- Morphe patches cannot be copied. Port each one to an Xposed hook with DexKit
  fingerprints, following DEVELOPMENT.md and the existing app packages as examples.
- Preserve every LICENSE, NOTICE and copyright header. Add credits for Piko,
  bufferk, dh6k and 2eno where code or logic is derived from them.
- Never claim a patch works unless it was verified on a device on the pinned
  app version. If it cannot be verified, say so.
- If a fingerprint does not match, stop and report. Do not loosen it blindly.
- If a patch needs APK, manifest or resource changes, mark it "Not portable" and skip it.
- Never commit APKs, keystores or tokens.
- One commit per phase on branch phantomcore/main.
- If anything in the plan is marked VERIFY, verify it from the actual source first
  and tell me what you found.
- If a decision in section 8 is unresolved and blocks you, ask me. Do not guess.

Begin with Phase 0 only. Do not modify any files. Output the Gate 0 summary.
```

### 10.2 Phase prompts

**Phase 1**
```text
Execute Phase 1 of PhantomCore-Execution-Plan.md. Fork steps that need my GitHub
account must be given to me as exact commands or UI steps; do not assume access.
Use these decisions: owner=<GITHUB_OWNER>, applicationId=<KEEP|CHANGE to io.github.<you>.phantomcore>.
Before renaming anything, build and install the UNMODIFIED fork and confirm an
existing patch works. Then apply only the minimal rename set. Report Gate 1 evidence.
```

**Phase 2**
```text
Execute Phase 2 (Android 17). Report the current compileSdk/targetSdk, Xposed API
version and DexKit version first. Then make the smallest changes needed to build
against API 37 and run on Android 17. Verify the exact SDK package name in the
installed Android SDK before using it. Report test results per Android version;
if no Android 17 device is available, label it "builds; runtime unverified".
```

**Phase 3**
```text
Execute Phase 3 (Instagram). First report whether Instagram and Threads share any code.
Then remove only the Instagram-specific patch code and verify Threads still builds.
Use the archived 2eno/NexAlloy fork as READ-ONLY reference for Tier A patches and
crimera/piko for intent. Target Instagram 447.0.0.55.81. Port Tier A first, one
patch per commit-sized step, and show me each fingerprint match result before
moving on. Do not start Tier B without my approval. Create/extend a NOTICE file
crediting Piko and the 2eno port. Update the registry, manifest query and arrays.xml.
```

**Phase 4**
```text
Execute Phase 4 (Brave). Read the Brave Origin implementations in bufferk/morphe-patches
and dh6k/morphe-patches, compare them, and tell me which you recommend and why before
implementing. Implement Brave Origin once for com.brave.browser, .browser_beta and
.browser_nightly. Do NOT implement any AMOLED theme; that is blocked on my decision
(plan section 7). Report device verification on Brave 1.92.140.
```

**Phase 5**
```text
Execute Phase 5 (Spotify). Port the 10 patches listed in the plan from
2eno/morphe-patches. Reuse its framework-only extension code where it compiles
unchanged. State the Spotify version you pinned and how you determined it.
There is no Premium unlock; do not add one. Verify each patch on device.
```

**Phases 6 to 8**
```text
Execute Phases 6, 7 and 8. Use the README draft in section 9 of the plan, filling the
Instagram, Brave and Spotify lists ONLY with patches that passed their gates.
Update DEVELOPMENT.md where it is out of date. Run a clean build from a fresh clone
with submodules, run tests, and give me the regression checklist results for every
previously supported app. Do not create a release tag until I approve.
```

---

## 11. Source references

- NexAlloy: https://github.com/NexAlloy/NexAlloy (README, DEVELOPMENT.md, .gitmodules)
- Piko: https://github.com/crimera/piko
- bufferk patches: https://github.com/bufferk/morphe-patches
- dh6k patches: https://github.com/dh6k/morphe-patches
- 2eno patches: https://github.com/2eno/morphe-patches
- 2eno NexAlloy fork (archived, reference only): https://github.com/2eno/NexAlloy
- Community patch index: https://morphe-patches.software/
