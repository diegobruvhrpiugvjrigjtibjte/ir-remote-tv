# 4. Come funziona

[Indice della documentazione](../README.md) · [English](../en/04-how-it-works.md) · **Italiano**

## Il percorso di un tasto

```
tocco ──► TvDb.Remote.get(Key) ──► Btn.pattern(toggle) ──► ConsumerIrManager.transmit(portante, pattern)
                                      │
                                      └── IrCodec: nec / necExt / nec42 / samsung / sony / rc5 / rc6, oppure grezzo
```

1. `MainActivity` associa ogni tasto a un `TvDb.Key` (`POWER`, `VOL_UP`, `OK`, `YOUTUBE`...).
2. Il `TvDb.Remote` scelto restituisce un `Btn`: un codice **interpretato**
   (protocollo + indirizzo + comando) o una lista **grezza** di tempi in microsecondi.
3. I codici interpretati diventano tempi grazie a `IrCodec`; quelli grezzi
   vengono inviati così come sono.
4. `ConsumerIrManager.transmit(frequenza, pattern)` pilota il LED. Il pattern
   alterna *mark* (LED che lampeggia alla portante) e *space* (spento), in µs.

## Protocolli supportati

| Protocollo | Portante | Trama | Codificatore |
|---|---|---|---|
| NEC | 38 kHz | guida 9 ms + 4,5 ms, indirizzo 8 bit, ~indirizzo, comando 8 bit, ~comando, LSB per primo | `IrCodec.nec` |
| NECext | 38 kHz | come NEC con indirizzo e comando a 16 bit | `IrCodec.necExt` |
| NEC42 / NEC42ext | 38 kHz | indirizzo 13 bit (o 26), comando 8 bit (o 16) | `IrCodec.nec42` |
| Samsung32 | 38 kHz | guida 4,5 + 4,5 ms, indirizzo ripetuto due volte, comando e inverso | `IrCodec.samsung` |
| Sony SIRC 12 / 15 / 20 | 40 kHz | guida 2,4 ms, bit a larghezza di impulso, inviato 3 volte a 45 ms | `IrCodec.sony` |
| Philips RC5 / RC5X | 36 kHz | Manchester, mezzo bit 889 µs, bit di toggle | `IrCodec.rc5` |
| Philips RC6 (modo 0) | 36 kHz | guida 2,666 ms, Manchester, toggle a doppia larghezza | `IrCodec.rc6` |
| Grezzo | dal file | tempi salvati nel database | — |

Tempi NEC: uno `0` è 560 µs di mark + 560 µs di space, un `1` è 560 µs di mark
+ 1 690 µs di space, con un mark finale di 560 µs.

**Il bit di toggle.** Le TV RC5 e RC6 ignorano una trama ripetuta se un bit di
toggle non cambia fra due pressioni separate. L'app lo inverte a ogni nuovo
tocco e lo mantiene mentre tieni premuto, come fa un vero telecomando Philips.

### Esempio: il codice di accensione Hisense

`NECext`, indirizzo `0xBF00`, comando `0xF20D`. Byte inviati LSB per primo:
`00`, `BF`, `0D`, `F2`. Sono 15 uni e 17 zeri, quindi la trama dura
9 + 4,5 + 32 × 1,12 + 15 × 1,13 + 0,56 ≈ **66,8 ms**.

## Il database

`app/src/main/assets/tvdb.json` (circa 495 KB) è generato da `tools/gen-db.cjs`
a partire dalla cartella TV di [Flipper-IRDB](https://github.com/Lucaslhm/Flipper-IRDB) (CC0):

- ogni file `.ir` è un telecomando; ogni nome di tasto viene associato a un
  `Key` con espressioni regolari (`vol_up`, `VOL+`, `volume_up` → `VOL_UP`...);
- i protocolli che l'app non sa generare (per esempio Kaseikyo, usato da molti
  telecomandi Panasonic) vengono saltati, così come i segnali grezzi oltre 400 valori;
- un telecomando deve avere almeno 4 tasti utilizzabili e accensione o volume;
- i telecomandi identici vengono uniti e il più completo viene per primo;
- le marche principali (Samsung, LG, Sony, Hisense, TCL, Philips...) sono in cima.

Formato:

```json
{ "v": 1, "brands": [ { "n": "Hisense", "r": [ { "n": "Hisense EN3Y39H",
  "b": { "POWER": { "p": "NECext", "a": 48896, "c": 61965 },
         "YOUTUBE": { "r": [9000, 4500, 560, 560], "f": 38000 } } } ] } ] }
```

`p` protocollo, `a` indirizzo, `c` comando (byte little-endian come interi),
`r` tempi grezzi in µs, `f` portante in Hz.

## La ricerca automatica

`TvDb.powerSweep()` raccoglie il codice di accensione di ogni telecomando ed
elimina i doppioni per firma (protocollo + indirizzo + comando, o lunghezza e
campioni di un codice grezzo). La ricerca li invia uno alla volta; fermandola
restano selezionati l'ultima marca e l'ultimo modello inviati.

## Perché l'app è così piccola

Nessuna libreria: l'interfaccia è costruita in codice con le viste standard di
Android, il database è JSON compresso nell'APK e la build di rilascio è ridotta con R8.

Avanti: [Compilare dai sorgenti](05-compilare.md).
