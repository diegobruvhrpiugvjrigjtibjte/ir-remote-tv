# 5. Compilare dai sorgenti

[Indice della documentazione](../README.md) · [English](../en/05-build-from-source.md) · **Italiano**

## Requisiti

- JDK 17 (va bene quello incluso in Android Studio)
- Android SDK con la piattaforma 34 e i build tools
- Node.js 18+ (solo per rigenerare il database)

## Compilare

```bash
git clone https://github.com/diegobruvhrpiugvjrigjtibjte/ir-remote-tv.git
cd ir-remote-tv
echo "sdk.dir=/percorso/di/Android/Sdk" > local.properties

./gradlew :app:testDebugUnitTest      # 10 test unitari
./gradlew :app:assembleRelease        # app/build/outputs/apk/release/app-release.apk
```

Su Windows usa `gradlew.bat`, e se Java non è nel PATH:

```bat
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
gradlew.bat :app:testDebugUnitTest :app:assembleRelease
```

La build di rilascio è ridotta e firmata con la **chiave di debug**, così si
installa subito. Per pubblicarla su uno store configura la tua chiave in
`app/build.gradle.kts`.

## Struttura

```
app/src/main/java/app/telecomandoir/
  MainActivity.java   l'unica schermata, costruita in codice
  TvDb.java           modello del database: Key, Btn, Remote, Brand, ricerca
  IrCodec.java        codificatori NEC, NECext, NEC42, Samsung32, SIRC, RC5, RC6
app/src/main/assets/tvdb.json   il database dei telecomandi
app/src/test/java/.../IrCodecTest.java   test unitari
tools/gen-db.cjs           genera tvdb.json da Flipper-IRDB
tools/gen-brand-docs.cjs   genera le pagine delle marche supportate
```

| Impostazione | Valore | Perché |
|---|---|---|
| `minSdk` | 19 | `ConsumerIrManager` esiste da Android 4.4 |
| `targetSdk` | 21 | Android 5.0, l'ultima versione ufficiale del Galaxy S4 |
| `compileSdk` | 34 | |

## Test

`IrCodecTest` controlla: la struttura e il numero di bit della trama NEC, i bit
NECext di Hisense, la ripetizione dell'indirizzo Samsung32, le tre trame Sony,
la lunghezza RC5, la guida RC6, che **ogni** tasto di **ogni** telecomando del
database diventi un pattern valido con una portante plausibile, che il codice
Hisense `0xBF00 / 0xF20D` ci sia, che la ricerca abbia più di 80 codici di
accensione diversi e che i tasti YouTube e Netflix siano presenti su molti
telecomandi.

## Rigenerare il database

```bash
git clone --depth 1 https://github.com/Lucaslhm/Flipper-IRDB.git
node tools/gen-db.cjs Flipper-IRDB/TVs
node tools/gen-brand-docs.cjs
```

`gen-db.cjs` stampa il numero di marche, di telecomandi, la dimensione del file
e quanti telecomandi hanno ciascun tasto. Per riconoscere un nuovo nome di
tasto aggiungilo all'espressione regolare giusta in `mapKey()`. Per un nuovo
protocollo aggiungilo a `SUPPORTED`, scrivi il codificatore in `IrCodec.java` e
il `case` in `TvDb.Btn.pattern()`, poi aggiungi un test.

Avanti: [Problemi e FAQ](06-problemi-faq.md).
