# 2. User guide

[Documentation index](../README.md) · **English** · [Italiano](../it/02-guida-utente.md)

The interface is in Italian. This page gives the Italian label of every
control with its meaning.

## The screen, top to bottom

| Element | Italian label | What it does |
|---|---|---|
| Status line | — | Hints, or a warning if the phone has no IR transmitter |
| Brand button | `Samsung ▾` | Opens the brand list with a search box (`Cerca la marca`) |
| Model chips | `1 · ...`, `2 · ...` | The remotes available for the brand. The highlighted one is in use |
| Power | `Accendi / Spegni` | Power toggle |
| Apps | `APP` | YouTube, Netflix, Prime, Disney+, Spotify, App list. Shown only if the remote has them |
| Audio and channels | `AUDIO E PROGRAMMI` | `Vol +`, `Vol –`, `Muto` (mute), `Prog +`, `Prog –`, `Ingresso` (input/source) |
| Navigation | `NAVIGAZIONE` | Arrows, `OK`, `Menu`, `Home`, `Indietro` (back), `Esci` (exit) |
| Touch pad | `Scorri qui` | Drag to send arrows, tap to send OK |
| Extra | | `Info`, `Guida` (EPG), `Impost.` (settings) |
| Playback | `RIPRODUZIONE` | Rewind, play, pause, stop, fast forward |
| Colour keys | | Red, green, yellow, blue (teletext, apps) |
| Numbers | `NUMERI` | 0–9, `Ultimo` (previous channel), `Lista` (channel list) |
| Others | | `Sottotitoli` (subtitles), `Sleep`, `Teletext` |
| Search | `Trova il modello giusto di ...` | Tries the power code of every model of the brand |
| Search all | `Non so la marca: prova tutte le TV` | Tries the power code of every model of every brand |

Keys the selected remote does not have are dimmed and do nothing.

## Pointing

The transmitter is on the **top edge** of the phone. Point that edge at the TV,
within a few metres, with nothing in between. Every sent key gives a short
vibration.

## Holding keys

Volume, channels, arrows, rewind and fast forward **repeat while held**
(about four times per second). Other keys send once per touch.

## Choosing the brand

Tap the brand button, type a few letters in `Cerca la marca`, tap the brand.
The first chip (`1`) is the most complete remote of that brand. Your choice is
remembered.

## Finding the model that works

TVs of the same brand do not all use the same codes. Two ways to find yours:

1. **By hand**: tap the chips one by one and press **Power** after each, until
   the TV reacts.
2. **Automatically**: tap `Trova il modello giusto di <brand>`. The app sends
   the power code of each model, one every **1.5 seconds**, and shows
   `Provo 3 di 13: ...`. As soon as the TV turns off (or on), tap
   `Stop: la TV ha reagito`. That model stays selected.

If you stop a little late, the chip after the right one may be selected: tap
the previous chip and try Power again.

### When you do not know the brand

`Non so la marca: prova tutte le TV` tries every distinct power code in the
database, one every **0.7 seconds**, across all brands. Stop it when the TV
reacts; brand and model are both selected.

## Scrolling and selecting (smart TV menus)

The `Scorri qui` area works like a touchpad:

- **drag right / left / up / down**: sends one arrow every ~34 dp of movement,
  so a long drag scrolls several items;
- **tap without dragging**: sends **OK**.

Use it to move through app grids, YouTube results or settings, then the
`Indietro` key to go back.

## Opening apps

On remotes that have dedicated keys (many Samsung, LG, Hisense, TCL, Philips
and others), the `APP` row opens YouTube, Netflix, Prime Video, Disney+ or
Spotify directly. If your remote has no such keys, use `Home` and the
`Scorri qui` pad to reach the app.

## Example: Hisense 55E8Q

Choose **Hisense**. The first chip, `EN3Y39H`, uses NECext address `0xBF00`,
power command `0xF20D`. Several other Hisense chips share the same power code
with different extra keys. If it does not work, try the other Hisense chips or
the automatic search.

Next: [Supported brands](03-supported-brands.md).
