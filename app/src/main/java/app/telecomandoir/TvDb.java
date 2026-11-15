package app.telecomandoir;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Database dei telecomandi (assets/tvdb.json, generato dal database pubblico Flipper-IRDB): marche -> modelli -> tasti. */
public final class TvDb {
    private TvDb() {}

    public enum Key {
        POWER, MUTE, VOL_UP, VOL_DOWN, CH_UP, CH_DOWN, UP, DOWN, LEFT, RIGHT, OK, BACK, MENU, HOME, INPUT, EXIT, INFO, GUIDE, SETTINGS,
        PLAY, PAUSE, STOP, REW, FFWD, RED, GREEN, YELLOW, BLUE, YOUTUBE, NETFLIX, PRIME, DISNEY, SPOTIFY, APPS, SUBTITLE, SLEEP, TEXT, CHLIST, LAST,
        D0, D1, D2, D3, D4, D5, D6, D7, D8, D9
    }

    /** Un tasto: o un comando "parsed" (protocollo + indirizzo + comando) o una sequenza grezza. */
    public static final class Btn {
        final String proto; final long a, c; final int[] raw; final int freq;
        Btn(String proto, long a, long c, int[] raw, int freq) { this.proto = proto; this.a = a; this.c = c; this.raw = raw; this.freq = freq; }
        public int carrier() {
            if (raw != null) return freq;
            if (proto.startsWith("SIRC")) return IrCodec.HZ_SONY;
            if (proto.startsWith("RC")) return IrCodec.HZ_RC5;
            return IrCodec.HZ_NEC;
        }
        /** toggle: i protocolli Philips (RC5/RC6) lo alternano a ogni pressione. */
        public int[] pattern(boolean toggle) {
            if (raw != null) return raw.clone();
            switch (proto) {
                case "NEC": return IrCodec.nec((int) (a & 0xFF), (int) (c & 0xFF));
                case "NECext": return IrCodec.necExt(a, c);
                case "NEC42": return IrCodec.nec42(a & 0x1FFF, c & 0xFF, false);
                case "NEC42ext": return IrCodec.nec42(a & 0x3FFFFFFL, c & 0xFFFF, true);
                case "Samsung32": return IrCodec.samsung((int) (a & 0xFF), (int) (c & 0xFF));
                case "RC5": case "RC5X": return IrCodec.rc5((int) (a & 0x1F), (int) (c & 0x7F), toggle);
                case "RC6": return IrCodec.rc6((int) (a & 0xFF), (int) (c & 0xFF), toggle);
                case "SIRC": return IrCodec.sony((int) (a & 0x1F), (int) (c & 0x7F), 12);
                case "SIRC15": return IrCodec.sony((int) (a & 0xFF), (int) (c & 0x7F), 15);
                case "SIRC20": return IrCodec.sony((int) (a & 0x1FFF), (int) (c & 0x7F), 20);
                default: return null;
            }
        }
        String signature() { return raw != null ? "r" + raw.length + ":" + raw[0] + ":" + raw[raw.length / 2] : proto + ":" + a + ":" + c; }
    }

    public static final class Remote {
        public final String name; public final Map<Key, Btn> keys = new LinkedHashMap<>();
        Remote(String name) { this.name = name; }
        public boolean has(Key k) { return keys.containsKey(k); }
        public Btn get(Key k) { return keys.get(k); }
    }

    public static final class Brand {
        public final String name; public final List<Remote> remotes = new ArrayList<>();
        Brand(String name) { this.name = name; }
    }

    public static final List<Brand> BRANDS = new ArrayList<>();

    /** Carica il database dal JSON (marche "n", modelli "r", tasti "b"). */
    public static void load(String json) throws Exception {
        BRANDS.clear(); JSONObject root = new JSONObject(json); JSONArray bs = root.getJSONArray("brands");
        for (int i = 0; i < bs.length(); i++) {
            JSONObject bo = bs.getJSONObject(i); Brand b = new Brand(bo.getString("n")); JSONArray rs = bo.getJSONArray("r");
            for (int j = 0; j < rs.length(); j++) {
                JSONObject ro = rs.getJSONObject(j); Remote r = new Remote(ro.getString("n")); JSONObject kb = ro.getJSONObject("b");
                Iterator<String> it = kb.keys();
                while (it.hasNext()) {
                    String kn = it.next(); Key k; try { k = Key.valueOf(kn); } catch (IllegalArgumentException e) { continue; }
                    JSONObject o = kb.getJSONObject(kn);
                    if (o.has("r")) { JSONArray ra = o.getJSONArray("r"); int[] d = new int[ra.length()]; for (int x = 0; x < d.length; x++) d[x] = ra.getInt(x); r.keys.put(k, new Btn(null, 0, 0, d, o.optInt("f", 38000))); }
                    else r.keys.put(k, new Btn(o.getString("p"), o.getLong("a"), o.getLong("c"), null, 0));
                }
                b.remotes.add(r);
            }
            BRANDS.add(b);
        }
    }

    /** Tutte le coppie (marca, modello) con il loro codice di accensione, senza doppioni: per la ricerca automatica. */
    public static final class Entry { public final Brand brand; public final Remote remote; public final Btn power; Entry(Brand b, Remote r, Btn p) { brand = b; remote = r; power = p; } }
    public static List<Entry> powerSweep() {
        List<Entry> l = new ArrayList<>(); Set<String> seen = new LinkedHashSet<>();
        for (Brand b : BRANDS) for (Remote r : b.remotes) { Btn p = r.get(Key.POWER); if (p != null && seen.add(p.signature())) l.add(new Entry(b, r, p)); }
        return l;
    }
}
