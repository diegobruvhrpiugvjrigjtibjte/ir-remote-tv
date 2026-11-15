# 2. User guide

[Documentation index](../README.md) · **English** · [Italiano](../it/02-guida-utente.md)

Since version 2.1 the app looks like a physical remote: every key fits on one
screen, nothing scrolls. Labels on the keys are in English, as on most TV
remotes; menus are in Italian.

## The remote, top to bottom

| Area | Keys |
|---|---|
| IR LED | The red bar at the top lights up every time a code is sent |
| Header | Brand chip (`Hisense · 1/14 ▾`), **microphone** (voice commands), **⋯** menu |
| Status line | Hints, voice results, model search progress |
| System row | **Power** (red), SOURCE, MUTE, SETUP |
| Apps | **NETFLIX**, **YouTube**, **prime video**, **Disney+** |
| Navigation | Big D-pad with **OK** in the centre; BACK and MENU on the left, HOME and EXIT on the right |
| Volume and channels | **VOL** rocker, **CH** rocker; between them APP, GUIDE, **123**, INFO, LIST, SUB |
| Playback | Rewind, play, pause, stop, fast forward |
| Colour keys | Red, green, yellow, blue |

Keys the selected remote does not have are dimmed and do nothing.

**123** opens the number pad (0–9, LAST, LIST) with Spotify, TELETEXT and SLEEP.

## Pointing and holding

Point the **top edge** of the phone at the TV. Every key vibrates briefly.
Volume, channels, arrows, rewind and fast forward **repeat while held**.

## The D-pad is also a touchpad

- **Tap** an arrow or OK: sends it (arrows repeat while held).
- **Drag** anywhere on the D-pad: sends one arrow every ~30 dp in the direction
  of the movement, so you can scroll smart-TV menus, YouTube results and app
  grids like on a touchpad.

## Voice commands

Tap the **microphone** and speak in Italian. Examples:

| Say | Sends |
|---|---|
| "accendi", "spegni" | Power |
| "alza il volume", "alza il volume di 5", "abbassa", "muto" | VOL+ ×3 / ×5, VOL− ×3, MUTE |
| "canale 12", "canale successivo", "canale precedente" | 1, 2, OK / CH+ / CH− |
| "Netflix", "YouTube", "Prime", "Disney" | App keys |
| "home", "indietro", "ok", "esci" | Navigation |
| "su", "giù", "destra 3", "sinistra" | Arrows |
| "pausa", "play", "stop", "avanti" | Playback |
| "sorgente", "impostazioni", "guida", "info", "sottotitoli" | Other keys |

The app uses the phone's speech service (the Google app) and asks for offline
recognition when the Italian language pack is downloaded. The full list is in
**⋯ → Comandi vocali**.

## Choosing the brand and the model

- Tap the **brand chip**, search your brand, tap it.
- **Long-press** the chip (or **⋯ → Cambia modello**) to pick another remote of
  the same brand.
- **⋯ → Trova il modello giusto**: sends the power code of each model, one
  every 1.5 s. When the TV reacts, **tap the status line** to stop: that model
  stays selected.
- **⋯ → Non so la marca**: the same across every brand, one code every 0.8 s.

## Example: Hisense 55E8Q (VIDAA)

Choose **Hisense**. Remote 1, `EN3Y39H`, uses NECext address `0xBF00`, power
`0xF20D`, and has SOURCE, SETUP, MENU, HOME, BACK, EXIT, GUIDE, INFO, LIST,
SUB, colour keys, playback, Netflix and YouTube. For Prime Video and Disney+
keys try remote 4 (`32A4HAU`), which has all four app keys.

Next: [Supported brands](03-supported-brands.md).
