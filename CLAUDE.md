# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

An Android app that implements a **Karoo Extension** — a plugin for Hammerhead Karoo bike computers, built on the [karoo-ext](https://github.com/hammerheadnav/karoo-ext) SDK ([docs](https://hammerheadnav.github.io/karoo-ext/index.html)). The repo currently still has the template's placeholder names (`template-id`, `TemplateExtension`, package `io.hammerhead.karooexttemplate`) — this is scaffolding that has not yet been renamed/customized for the actual "climber" extension.

Two things ship in one APK:
- A standalone **Android app UI** (`MainActivity` → Jetpack Compose screens), for configuration/companion use on a phone.
- A **background extension service** (`TemplateExtension`, extends `KarooExtension`) that the Karoo device binds to via an exported service + intent-filter, declared in `AndroidManifest.xml` and described by `app/src/main/res/xml/extension_info.xml`. This is where data types, device support, and ride-page functionality get implemented using the `karoo-ext` API surface.

## Build & run

Standard Gradle/Android Studio project (Kotlin, Jetpack Compose, AGP 8.2.2, Kotlin 2.0.0, compileSdk/targetSdk 34, minSdk 23).

```bash
./gradlew build              # full build
./gradlew assembleDebug      # debug APK
./gradlew installDebug       # install to connected device/emulator
./gradlew lint               # Android lint
```

There are no test source sets or test dependencies configured yet (no `src/test` or `src/androidTest`), so there is no test command to run.

### GitHub Packages dependency

`karoo-ext` is pulled from GitHub Packages (`settings.gradle.kts`), which requires auth credentials even for read access. Provide via gradle properties `gpr.user`/`gpr.key` or env vars `USERNAME`/`TOKEN`. Without these, dependency resolution fails.

## Architecture

- `app/src/main/kotlin/io/hammerhead/karooexttemplate/`
  - `MainActivity.kt` — Compose entry point for the phone-side app UI.
  - `screens/` — Compose screens (currently just `MainScreen`).
  - `theme/` — Compose `AppTheme`/Material3 theming.
  - `extension/TemplateExtension.kt` — the `KarooExtension` subclass that is the actual integration point with the Karoo device. Extension identity (`id`, version) is set in its constructor and must match the `id` in `extension_info.xml`.
- `app/src/main/res/xml/extension_info.xml` — declares the extension's display name, icon, and whether it scans for external devices (`scansDevices`). Data types the extension provides go here as child elements.
- `app/src/main/AndroidManifest.xml` — wires up both the launcher `MainActivity` and the exported `TemplateExtension` service (bound via the `io.hammerhead.karooext.KAROO_EXTENSION` intent-action) that the Karoo firmware discovers and binds to.

When customizing this template for a real extension (per the README's own instructions), the coordinated rename spans: `namespace`/`applicationId` in `app/build.gradle.kts`, the Kotlin package path, `TemplateExtension`'s constructor id, `extension_info.xml`'s `id`, and the `template`/`extension_name` string resources in `strings.xml` — these must all stay consistent with each other.

## Build requirements
- Gradle sync requires gpr.user and gpr.key in local.properties (GitHub PAT, read:packages scope)

## Data fields (v2 — revised, user-configurable)

Architecture: single shared metric-picker component/config screen used by
both fields. Do not implement separate picker logic per field.

### Field 1 (full-width, 3 cells)
- Layout fixed: 3 cells in a horizontal row
- Content per cell: user-selected from shared metric list (see below)
- No default/hardcoded assignment — user must configure on first use
  (or ships with a sensible default selection, TBD)

### Field 2 (half-width, 2x2 grid)
- Layout fixed: 4 cells in a 2x2 grid
- Content per cell: user-selected from same shared metric list
- Two instances supported (side by side in drawer's bottom row)

### Shared metric list (available to both fields' pickers)
- Native streams: Power, HR, Cadence, Speed, Grade, Distance
- Calculated: W/kg (native SDK POWER_TO_WEIGHT), VAM, IF, NP, kJ
- Climb-specific (route-dependent, see Field 1 route-dependency notes below):
  Distance to Top, Elev to Top, Avg Climb Grade

## Route-dependency behavior (applies wherever these metrics are selected)
- Avg Climb Grade: CONFIRMED route-only (OnNavigationState.NavigatingRoute.Climb.grade)
- Distance to Top / Elev to Top: CONFIRMED populate via ambient climb detection,
  independent of route, when Karoo Climber setting is on and a climb is detected
- No-data fallback: "n/a" (single shared constant/function, referenced by all
  cells regardless of which field they're in)
- Unconfirmed edge case: no route AND no climb detected — assumed "n/a", not
  device-verified