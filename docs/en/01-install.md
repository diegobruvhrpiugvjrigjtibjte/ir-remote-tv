# 1. Install

[Documentation index](../README.md) · **English** · [Italiano](../it/01-installazione.md)

## Does my phone work?

The app needs a phone with an **infrared transmitter** (Android `ConsumerIrManager`).
Many phones do **not** have one.

| Phone family | IR transmitter |
|---|---|
| Samsung Galaxy S4, S5, Note 3, Note 4 | Yes |
| Samsung Galaxy S6 and later, including S20/S20+ | **No** |
| Many Xiaomi, Redmi and POCO models | Yes (check your model) |
| Some Huawei and Honor models | Yes (check your model) |
| Google Pixel, most others | No |

The app opens on any phone with Android 4.4 or later. Without an emitter it
says so at the top and greys out the keys.

## Install the APK

1. Download `IR-Remote-TV-2.1.apk` from the
   [latest release](https://github.com/diegobruvhrpiugvjrigjtibjte/ir-remote-tv/releases/latest)
   or from the [website](https://ir-remote-tv.pages.dev).
2. Open the file on the phone.
3. Android asks to allow apps from unknown sources:
   - **Android 4.4 – 7** (Galaxy S4 included): *Settings → Security → Unknown sources* → on.
   - **Android 8 and later**: allow the browser or file manager you used when prompted.
4. Tap **Install**, then **Open**.

### Copying it from a computer

Connect the phone with USB in **file transfer (MTP)** mode, copy the APK to the
`Download` folder of the internal storage, then on the phone open
**My Files → Download** and tap the file.

### With adb

```bash
adb install -r IR-Remote-TV-2.1.apk
```

## Updating

A newer version installs over the old one without uninstalling. Your chosen
brand and model are kept.

## Size and permissions

About 105 KB. Permissions: `TRANSMIT_IR` (send infrared) and `VIBRATE` (a short
tick on every key). No internet permission: the app works completely offline
and collects nothing.

Next: [User guide](02-user-guide.md).
