# 1. Installazione

[Indice della documentazione](../README.md) · [English](../en/01-install.md) · **Italiano**

## Il mio telefono va bene?

Serve un telefono con **trasmettitore a infrarossi** (`ConsumerIrManager` di
Android). Molti telefoni **non** ce l'hanno.

| Famiglia | Trasmettitore IR |
|---|---|
| Samsung Galaxy S4, S5, Note 3, Note 4 | Sì |
| Samsung Galaxy S6 e successivi, S20/S20+ compresi | **No** |
| Molti Xiaomi, Redmi e POCO | Sì (controlla il modello) |
| Alcuni Huawei e Honor | Sì (controlla il modello) |
| Google Pixel e quasi tutti gli altri | No |

L'app si apre su qualsiasi telefono con Android 4.4 o successivo. Senza
trasmettitore lo scrive in alto e disattiva i tasti.

## Installare l'APK

1. Scarica `IR-Remote-TV-2.1.apk` dall'[ultima release](https://github.com/diegobruvhrpiugvjrigjtibjte/ir-remote-tv/releases/latest)
   o dal [sito](https://ir-remote-tv.pages.dev).
2. Apri il file sul telefono.
3. Android chiede di consentire le app da origini sconosciute:
   - **Android 4.4 – 7** (Galaxy S4 compreso): *Impostazioni → Sicurezza → Origini sconosciute* → attiva.
   - **Android 8 e successivi**: consenti il browser o il gestore file che hai usato.
4. Tocca **Installa**, poi **Apri**.

### Copiarlo dal computer

Collega il telefono via USB in modalità **trasferimento file (MTP)**, copia
l'APK nella cartella `Download` della memoria interna, poi sul telefono apri
**I miei file → Download** e tocca il file.

### Con adb

```bash
adb install -r IR-Remote-TV-2.1.apk
```

## Aggiornare

Una versione nuova si installa sopra la vecchia senza disinstallare. Marca e
modello scelti restano salvati.

## Dimensione e permessi

Circa 105 KB. Permessi: `TRANSMIT_IR` (inviare infrarossi) e `VIBRATE` (una
breve vibrazione a ogni tasto). Nessun permesso internet: l'app funziona
completamente offline e non raccoglie niente.

Avanti: [Guida utente](02-guida-utente.md).
