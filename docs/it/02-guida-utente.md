# 2. Guida utente

[Indice della documentazione](../README.md) · [English](../en/02-user-guide.md) · **Italiano**

## La schermata, dall'alto in basso

| Elemento | Scritta | Cosa fa |
|---|---|---|
| Riga di stato | — | Suggerimenti, o un avviso se il telefono non ha il trasmettitore IR |
| Pulsante marca | `Samsung ▾` | Apre l'elenco delle marche con la ricerca (`Cerca la marca`) |
| Chip dei modelli | `1 · ...`, `2 · ...` | I telecomandi disponibili per la marca. Quello evidenziato è in uso |
| Accensione | `Accendi / Spegni` | Accende o spegne |
| App | `APP` | YouTube, Netflix, Prime, Disney+, Spotify, elenco App. Solo se il telecomando li ha |
| Audio e canali | `AUDIO E PROGRAMMI` | `Vol +`, `Vol –`, `Muto`, `Prog +`, `Prog –`, `Ingresso` (sorgente) |
| Navigazione | `NAVIGAZIONE` | Frecce, `OK`, `Menu`, `Home`, `Indietro`, `Esci` |
| Area di scorrimento | `Scorri qui` | Trascina per inviare le frecce, tocca per OK |
| Extra | | `Info`, `Guida` (EPG), `Impost.` (impostazioni) |
| Riproduzione | `RIPRODUZIONE` | Indietro veloce, play, pausa, stop, avanti veloce |
| Tasti colorati | | Rosso, verde, giallo, blu (televideo, app) |
| Numeri | `NUMERI` | 0–9, `Ultimo` (canale precedente), `Lista` (lista canali) |
| Altri | | `Sottotitoli`, `Sleep`, `Teletext` |
| Ricerca | `Trova il modello giusto di ...` | Prova il codice di accensione di ogni modello della marca |
| Ricerca su tutto | `Non so la marca: prova tutte le TV` | Prova il codice di accensione di ogni modello di ogni marca |

I tasti che il telecomando scelto non ha sono attenuati e non fanno niente.

## Come puntare

Il trasmettitore è sul **bordo superiore** del telefono. Punta quel bordo verso
la TV, a pochi metri, senza ostacoli. Ogni tasto inviato dà una breve vibrazione.

## Tenere premuto

Volume, canali, frecce, indietro e avanti veloce **si ripetono se tenuti
premuti** (circa quattro volte al secondo). Gli altri tasti inviano una volta
per tocco.

## Scegliere la marca

Tocca il pulsante della marca, scrivi qualche lettera in `Cerca la marca`,
tocca la marca. Il primo chip (`1`) è il telecomando più completo di quella
marca. La scelta viene ricordata.

## Trovare il modello che funziona

Non tutte le TV della stessa marca usano gli stessi codici. Due modi:

1. **A mano**: tocca i chip uno alla volta e premi **Accendi / Spegni** dopo
   ciascuno, finché la TV reagisce.
2. **In automatico**: tocca `Trova il modello giusto di <marca>`. L'app invia il
   codice di accensione di ogni modello, uno ogni **1,5 secondi**, e mostra
   `Provo 3 di 13: ...`. Appena la TV si spegne (o si accende), tocca
   `Stop: la TV ha reagito`. Quel modello resta selezionato.

Se fermi un po' in ritardo potrebbe restare selezionato il modello successivo:
tocca il chip precedente e riprova l'accensione.

### Quando non sai la marca

`Non so la marca: prova tutte le TV` prova ogni codice di accensione diverso
del database, uno ogni **0,7 secondi**, su tutte le marche. Fermalo quando la
TV reagisce: marca e modello vengono selezionati insieme.

## Scorrere e selezionare (menu delle smart TV)

L'area `Scorri qui` funziona come un touchpad:

- **trascina a destra / sinistra / su / giù**: invia una freccia ogni ~34 dp di
  movimento, quindi un trascinamento lungo scorre più elementi;
- **tocco senza trascinare**: invia **OK**.

Usala per muoverti tra le app, i risultati di YouTube o le impostazioni, poi
`Indietro` per tornare.

## Aprire le app

Sui telecomandi che hanno i tasti dedicati (molti Samsung, LG, Hisense, TCL,
Philips e altri) la riga `APP` apre direttamente YouTube, Netflix, Prime Video,
Disney+ o Spotify. Se il telecomando non li ha, usa `Home` e l'area
`Scorri qui` per arrivare all'app.

## Esempio: Hisense 55E8Q

Scegli **Hisense**. Il primo chip, `EN3Y39H`, usa NECext indirizzo `0xBF00`,
comando di accensione `0xF20D`. Altri chip Hisense hanno lo stesso codice di
accensione con tasti extra diversi. Se non funziona, prova gli altri chip o la
ricerca automatica.

Avanti: [Marche supportate](03-marche-supportate.md).
