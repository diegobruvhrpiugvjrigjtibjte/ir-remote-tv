# 6. Troubleshooting and FAQ

[Documentation index](../README.md) · **English** · [Italiano](../it/06-problemi-faq.md)

## Troubleshooting

| Symptom | What to do |
|---|---|
| "Questo telefono non ha il sensore a infrarossi" and grey keys | The phone has no IR transmitter (for example Galaxy S6 and later). The app cannot work on it |
| Nothing happens on the TV | Point the **top edge** of the phone at the TV from 1–3 m; try another model chip; use the automatic search |
| Power works but other keys do not | Another remote of the same brand may match better: try the next chips |
| A key is dimmed | The selected remote does not have that key in the database |
| The TV reacts twice to one press | Normal on some Sony TVs (Sony frames are sent three times as the protocol requires) |
| Philips TV ignores repeated presses | Release and press again: RC5/RC6 need a new toggle bit for each press |
| Automatic search ended without a reaction | That TV's remote is not in the database. Try "try every TV" |
| Panasonic TV does not respond | Many Panasonic remotes use the Kaseikyo protocol, not supported yet |
| Cannot install the APK | Allow unknown sources (see [Install](01-install.md)) |

## FAQ

**Does it work with my TV?**
If your brand is in the [supported brands](03-supported-brands.md) list it very
likely does; the remaining question is which model chip matches.

**Does it need internet or an account?**
No. It has no internet permission and stores only your chosen brand and model.

**Does it work with air conditioners, set-top boxes or soundbars?**
This version contains TV remotes only. The encoders are generic, so other
devices could be added to the database.

**Does it work with Wi-Fi or Bluetooth TVs?**
No. It sends infrared only, like the original remote.

**Can I use it on the Galaxy S20+?**
No. The S20+ has no IR transmitter.

**Voice commands do not start**
They use the phone's speech service. Install or update the Google app; for
offline use download the Italian language pack in the Google voice settings.

## Licence and credits

App code: MIT. Remote database: derived from
[Flipper-IRDB](https://github.com/Lucaslhm/Flipper-IRDB), CC0 1.0.

[Back to the documentation index](../README.md)
