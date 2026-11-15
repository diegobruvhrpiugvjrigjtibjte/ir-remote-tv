package app.telecomandoir;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.telecomandoir.TvDb.Key;

/** Trasforma una frase detta a voce (italiano, con qualche parola inglese) in una sequenza di tasti. */
final class VoiceCommands {
    private VoiceCommands() {}

    private static final String[] NUM_WORDS = {"zero", "uno", "due", "tre", "quattro", "cinque", "sei", "sette", "otto", "nove", "dieci"};

    static String norm(String s) {
        String n = Normalizer.normalize(s.toLowerCase(Locale.ITALIAN), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return " " + n.replaceAll("[^a-z0-9+ ]", " ").replaceAll("\\s+", " ").trim() + " ";
    }

    private static boolean has(String n, String... words) { for (String w : words) if (n.contains(" " + w + " ") || (w.contains(" ") && n.contains(w))) return true; return false; }

    /** Primo numero nella frase (cifre o parole da zero a dieci), -1 se assente. */
    static int number(String n) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(" (\\d{1,4}) ").matcher(n);
        if (m.find()) return Integer.parseInt(m.group(1));
        for (int i = 0; i < NUM_WORDS.length; i++) if (n.contains(" " + NUM_WORDS[i] + " ")) return i;
        return -1;
    }

    private static List<Key> repeat(Key k, int times) { List<Key> l = new ArrayList<>(); for (int i = 0; i < Math.max(1, Math.min(times, 20)); i++) l.add(k); return l; }

    /** Restituisce i tasti da inviare, o una lista vuota se la frase non è riconosciuta. */
    static List<Key> parse(String phrase) {
        String n = norm(phrase);
        List<Key> out = new ArrayList<>();
        int num = number(n);
        // app
        if (has(n, "netflix")) { out.add(Key.NETFLIX); return out; }
        if (has(n, "youtube", "you tube", "iutub")) { out.add(Key.YOUTUBE); return out; }
        if (has(n, "prime", "amazon")) { out.add(Key.PRIME); return out; }
        if (has(n, "disney")) { out.add(Key.DISNEY); return out; }
        if (has(n, "spotify")) { out.add(Key.SPOTIFY); return out; }
        // volume
        boolean vol = has(n, "volume", "audio", "vol");
        if (has(n, "muto", "silenzio", "mute", "togli audio", "zitto")) { out.add(Key.MUTE); return out; }
        if (has(n, "alza", "aumenta", "piu forte", "su il volume") && (vol || !has(n, "canale"))) return repeat(Key.VOL_UP, num > 0 ? num : 3);
        if (has(n, "abbassa", "diminuisci", "piu piano", "meno forte")) return repeat(Key.VOL_DOWN, num > 0 ? num : 3);
        if (vol && has(n, "su", "piu", "+")) return repeat(Key.VOL_UP, num > 0 ? num : 3);
        if (vol && has(n, "giu", "meno")) return repeat(Key.VOL_DOWN, num > 0 ? num : 3);
        // canali
        boolean ch = has(n, "canale", "programma", "rete");
        if (ch && has(n, "successivo", "prossimo", "avanti", "su", "piu")) { out.add(Key.CH_UP); return out; }
        if (ch && has(n, "precedente", "indietro", "giu", "meno")) { out.add(Key.CH_DOWN); return out; }
        if (has(n, "ultimo canale", "canale di prima")) { out.add(Key.LAST); return out; }
        if (ch && num >= 0) { for (char d : String.valueOf(num).toCharArray()) out.add(Key.valueOf("D" + d)); out.add(Key.OK); return out; }
        // accensione
        if (has(n, "accendi", "spegni", "accensione", "power", "on", "off")) { out.add(Key.POWER); return out; }
        // navigazione
        if (has(n, "home", "schermata principale")) { out.add(Key.HOME); return out; }
        if (has(n, "indietro", "torna", "back")) { out.add(Key.BACK); return out; }
        if (has(n, "esci", "chiudi", "exit")) { out.add(Key.EXIT); return out; }
        if (has(n, "su", "sopra", "alto")) return repeat(Key.UP, num > 0 ? num : 1);
        if (has(n, "giu", "sotto", "basso")) return repeat(Key.DOWN, num > 0 ? num : 1);
        if (has(n, "sinistra")) return repeat(Key.LEFT, num > 0 ? num : 1);
        if (has(n, "destra")) return repeat(Key.RIGHT, num > 0 ? num : 1);
        if (has(n, "ok", "conferma", "seleziona", "invio", "vai", "apri")) { out.add(Key.OK); return out; }
        // riproduzione
        if (has(n, "pausa", "pause", "ferma")) { out.add(Key.PAUSE); return out; }
        if (has(n, "play", "riproduci", "riprendi", "continua")) { out.add(Key.PLAY); return out; }
        if (has(n, "stop")) { out.add(Key.STOP); return out; }
        if (has(n, "avanti veloce", "avanza", "avanti")) { out.add(Key.FFWD); return out; }
        if (has(n, "riavvolgi", "indietro veloce")) { out.add(Key.REW); return out; }
        // altro
        if (has(n, "sorgente", "ingresso", "hdmi", "input")) { out.add(Key.INPUT); return out; }
        if (has(n, "impostazioni", "settings")) { out.add(Key.SETTINGS); return out; }
        if (has(n, "menu")) { out.add(Key.MENU); return out; }
        if (has(n, "guida", "epg")) { out.add(Key.GUIDE); return out; }
        if (has(n, "info", "informazioni")) { out.add(Key.INFO); return out; }
        if (has(n, "sottotitoli")) { out.add(Key.SUBTITLE); return out; }
        if (has(n, "televideo", "teletext")) { out.add(Key.TEXT); return out; }
        if (has(n, "lista canali", "lista")) { out.add(Key.CHLIST); return out; }
        if (has(n, "app", "applicazioni")) { out.add(Key.APPS); return out; }
        if (has(n, "rosso")) { out.add(Key.RED); return out; }
        if (has(n, "verde")) { out.add(Key.GREEN); return out; }
        if (has(n, "giallo")) { out.add(Key.YELLOW); return out; }
        if (has(n, "blu")) { out.add(Key.BLUE); return out; }
        if (num >= 0) { for (char d : String.valueOf(num).toCharArray()) out.add(Key.valueOf("D" + d)); return out; }
        return out;
    }
}
