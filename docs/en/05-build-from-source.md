# 5. Build from source

[Documentation index](../README.md) · **English** · [Italiano](../it/05-compilare.md)

## Requirements

- JDK 17 (the one bundled with Android Studio works)
- Android SDK with platform 34 and build tools
- Node.js 18+ (only to regenerate the database)

## Build

```bash
git clone https://github.com/diegobruvhrpiugvjrigjtibjte/ir-remote-tv.git
cd ir-remote-tv
echo "sdk.dir=/path/to/Android/Sdk" > local.properties

./gradlew :app:testDebugUnitTest      # 16 unit tests
./gradlew :app:assembleRelease        # app/build/outputs/apk/release/app-release.apk
```

On Windows use `gradlew.bat`, and if Java is not on the PATH:

```bat
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
gradlew.bat :app:testDebugUnitTest :app:assembleRelease
```

The release build is minified and signed with the **debug key** so it installs
directly. For distribution on a store, configure your own signing key in
`app/build.gradle.kts`.

## Project layout

```
app/src/main/java/app/telecomandoir/
  MainActivity.java   the single remote screen, built in code
  RemoteViews.java    custom key, D-pad/touchpad and rocker views
  Icons.java          vector icons drawn on a 24x24 grid
  VoiceCommands.java  Italian phrase → key sequence
  TvDb.java           database model: Key, Btn, Remote, Brand, power sweep
  IrCodec.java        NEC, NECext, NEC42, Samsung32, SIRC, RC5, RC6 encoders
app/src/main/assets/tvdb.json   the remote database
app/src/test/java/.../IrCodecTest.java   unit tests
tools/gen-db.cjs           builds tvdb.json from Flipper-IRDB
tools/gen-brand-docs.cjs   builds the supported-brands pages
```

| Setting | Value | Why |
|---|---|---|
| `minSdk` | 19 | `ConsumerIrManager` exists since Android 4.4 |
| `targetSdk` | 21 | Android 5.0, the last official version of the Galaxy S4 |
| `compileSdk` | 34 | |

## Tests

`IrCodecTest` checks: the NEC frame layout and bit count, the Hisense NECext
bits, Samsung32 address repetition, Sony's three frames, RC5 length, RC6
leader, that **every** button of **every** remote in the database encodes to a
valid pattern with a plausible carrier, that the Hisense `0xBF00 / 0xF20D`
power code is present, that the sweep has more than 80 distinct power codes and
that YouTube and Netflix keys exist on many remotes. `VoiceCommandsTest` checks power, volume, channel numbers, apps, navigation and playback phrases.

## Regenerate the database

```bash
git clone --depth 1 https://github.com/Lucaslhm/Flipper-IRDB.git
node tools/gen-db.cjs Flipper-IRDB/TVs
node tools/gen-brand-docs.cjs
```

`gen-db.cjs` prints the number of brands, remotes, the file size and how many
remotes have each key. To support a new button name, add it to the matching
regular expression in `mapKey()`. To support a new protocol, add it to
`SUPPORTED`, write the encoder in `IrCodec.java` and the `case` in
`TvDb.Btn.pattern()`, then add a test.

Next: [Troubleshooting and FAQ](06-troubleshooting-faq.md).
