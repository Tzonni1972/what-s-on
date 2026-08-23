# What's on

John's personal Greece desk for Android TV / Google TV.

A sideload TV app that reads a weekly recommendation feed — the same printed card Grok Bot keeps current — and puts it on the living-room screen. Cream paper, Athens stone, gold italics. Not a Netflix clone. No Play Store listing, no accounts, no ads.

`applicationId`: `com.kotsaftis.whatson`

## Look (locked)

- Cream / linen `#F7F1E6`, charcoal serif `#1C1814`, gold italics `#9E702A`
- Thin gold-to-red rule under the **What's *on*** wordmark
- Printed-card-on-a-TV, large type, D-pad gold focus rings
- Landscape 1920×1080

## Screens

1. **Home rows** — THIS WEEK (series), MOVIES, JUST IN, PANATHINAIKOS · EUROLEAGUE. Header shows What's *on*, ATHENS, and the week dates.
2. **Detail overlay** (OK / D-pad center) — why, service, when, IMDb, open link. Reacher always shows an **ALREADY ON IT** badge and is never treated as a new recommendation.
3. **Settings** — feed URL + refresh (also the remote MENU key).

IMDb numbers are never invented. Missing ratings render as `—`.

## Build

Needs JDK 17+ and the Android SDK (compileSdk 35).

```bash
# Point Gradle at your SDK
echo "sdk.dir=$ANDROID_HOME" > local.properties

./gradlew :app:assembleDebug
```

Debug APK:

`app/build/outputs/apk/debug/app-debug.apk`

Unit tests (feed parser, bundled titles, Reacher badge):

```bash
./gradlew :app:testDebugUnitTest
```

## Sideload (USB or adb)

The app declares `android.software.leanback` required, plus a 320×180 banner, so it appears on the Android TV / Google TV launcher. It will not install on a phone.

### On the TV

1. Settings → System → About → click **Build** seven times to enable Developer options.
2. Settings → System → Developer options → **USB debugging** (and **Network debugging** if you use Wi-Fi adb).
3. Allow the debugging prompt when the computer connects.

### USB

```bash
adb devices
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Wi-Fi / network adb

```bash
adb connect 192.168.x.x:5555
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Open **What's on** from the Apps row. D-pad: left/right in a row, up/down between rows, OK for the detail card, Back to dismiss, MENU for Settings.

If a title link is Netflix, Prime, Max, Disney+, or Apple TV+, the app tries the installed living-room app first, then the browser. Cosmote and everything else fall through the same way.

## Feed JSON

The app polls a remote JSON URL on launch and from Settings → Refresh. Default URL is this repo's raw file:

`https://raw.githubusercontent.com/Tzonni1972/what-s-on/main/feed.json`

If the network copy is missing or fails, the bundled asset `app/src/main/assets/feed.json` is used.

Two copies are kept in sync on purpose:

| Path | Role |
| --- | --- |
| [`feed.json`](feed.json) | Live feed. Push to `main` and Google TV picks it up on the next refresh. |
| [`app/src/main/assets/feed.json`](app/src/main/assets/feed.json) | Offline fallback baked into the APK. |

### How the feed is updated

1. Edit `feed.json` (or let Grok Bot rewrite the weekly card into this shape).
2. Copy the same file over the bundled fallback:

   ```bash
   cp feed.json app/src/main/assets/feed.json
   ```

3. Commit and push to `main`. The sideloaded app keeps using the GitHub raw URL — no new APK unless you also changed the app.
4. On the TV: Settings → Refresh (or reopen the app).

Unknown fields are ignored. Item fields: `id`, `title`, `kind` (`series` \| `movie` \| `live` \| `sport`), `why`, `service`, `when`, `imdb` (string or `—`), `link` (url or `null`), `alreadyWatching`, `weekAhead`, `recap`.

Do not invent IMDb numbers. Do not add Flex X Cop or Scream 7. Reacher stays `alreadyWatching: true`.

## Project

Kotlin + Jetpack Compose for TV (`androidx.tv`). Personal sideload only.
