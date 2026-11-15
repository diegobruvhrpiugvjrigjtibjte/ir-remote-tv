# 6. Problemi e FAQ

[Indice della documentazione](../README.md) · [English](../en/06-troubleshooting-faq.md) · **Italiano**

## Risoluzione dei problemi

| Sintomo | Cosa fare |
|---|---|
| "Questo telefono non ha il sensore a infrarossi" e tasti grigi | Il telefono non ha il trasmettitore IR (per esempio Galaxy S6 e successivi). L'app non può funzionare |
| La TV non reagisce | Punta il **bordo superiore** del telefono verso la TV da 1–3 m; prova un altro chip; usa la ricerca automatica |
| L'accensione va ma gli altri tasti no | Un altro telecomando della stessa marca può corrispondere meglio: prova i chip successivi |
| Un tasto è attenuato | Il telecomando scelto non ha quel tasto nel database |
| La TV reagisce due volte a una pressione | Normale su alcune Sony (le trame Sony sono inviate tre volte come vuole il protocollo) |
| La TV Philips ignora le pressioni ripetute | Rilascia e premi di nuovo: RC5/RC6 richiedono un nuovo toggle a ogni pressione |
| La ricerca automatica finisce senza reazioni | Il telecomando di quella TV non è nel database. Prova "prova tutte le TV" |
| La TV Panasonic non risponde | Molti telecomandi Panasonic usano il protocollo Kaseikyo, non ancora supportato |
| Non riesco a installare l'APK | Consenti le origini sconosciute (vedi [Installazione](01-installazione.md)) |

## Domande frequenti

**Funziona con la mia TV?**
Se la marca è nell'elenco delle [marche supportate](03-marche-supportate.md)
è molto probabile; resta da capire quale chip corrisponde.

**Serve internet o un account?**
No. Non ha il permesso internet e salva solo marca e modello scelti.

**Funziona con condizionatori, decoder o soundbar?**
Questa versione contiene solo telecomandi di TV. I codificatori sono generici,
quindi altri dispositivi si possono aggiungere al database.

**Funziona con TV Wi-Fi o Bluetooth?**
No. Invia solo infrarossi, come il telecomando originale.

**Posso usarlo sul Galaxy S20+?**
No. L'S20+ non ha il trasmettitore IR.

**I comandi vocali non partono**
Usano il servizio vocale del telefono. Installa o aggiorna l'app Google; per
usarli offline scarica il pacchetto della lingua italiana nelle impostazioni
vocali di Google.

## Licenza e crediti

Codice dell'app: MIT. Database dei telecomandi: derivato da
[Flipper-IRDB](https://github.com/Lucaslhm/Flipper-IRDB), CC0 1.0.

[Torna all'indice della documentazione](../README.md)
