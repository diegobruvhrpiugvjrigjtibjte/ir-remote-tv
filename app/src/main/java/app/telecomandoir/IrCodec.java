package app.telecomandoir;

import java.util.ArrayList;
import java.util.List;

/** Codificatori dei protocolli IR piu' usati dalle TV. Restituiscono la sequenza (marca, spazio, marca...) in microsecondi. */
public final class IrCodec {
    private IrCodec() {}

    /** Frequenza della portante per protocollo. */
    public static final int HZ_NEC = 38000, HZ_SAMSUNG = 38000, HZ_SONY = 40000, HZ_RC5 = 36000, HZ_RC6 = 36000;

    private static void byteLsb(List<Integer> p, int v, int mark, int zero, int one) {
        for (int i = 0; i < 8; i++) { p.add(mark); p.add(((v >> i) & 1) == 1 ? one : zero); }
    }

    private static int[] arr(List<Integer> p) { int[] a = new int[p.size()]; for (int i = 0; i < a.length; i++) a[i] = p.get(i); return a; }

    /** NEC: indirizzo a 8 bit (con complemento) o a 16 bit (se > 0xFF), comando a 8 bit con complemento. */
    public static int[] nec(int addr, int cmd) {
        List<Integer> p = new ArrayList<>(); p.add(9000); p.add(4500);
        if (addr > 0xFF) { byteLsb(p, addr & 0xFF, 560, 560, 1690); byteLsb(p, (addr >> 8) & 0xFF, 560, 560, 1690); }
        else { byteLsb(p, addr, 560, 560, 1690); byteLsb(p, ~addr & 0xFF, 560, 560, 1690); }
        byteLsb(p, cmd & 0xFF, 560, 560, 1690); byteLsb(p, ~cmd & 0xFF, 560, 560, 1690);
        p.add(560); return arr(p);
    }


    private static void bits(List<Integer> p, long v, int n) { for (int i = 0; i < n; i++) { p.add(560); p.add(((v >> i) & 1) == 1 ? 1690 : 560); } }

    /** NEC esteso: 4 byte inviati cosi' come sono (indirizzo a 16 bit, comando a 16 bit gia' con il suo complemento). */
    public static int[] necExt(long addr, long cmd) {
        List<Integer> p = new ArrayList<>(); p.add(9000); p.add(4500);
        bits(p, addr & 0xFFFF, 16); bits(p, cmd & 0xFFFF, 16); p.add(560); return arr(p);
    }

    /** NEC42 (indirizzo 13 bit + complemento + comando 8 bit + complemento) e NEC42ext (indirizzo 26 bit + comando 16 bit). */
    public static int[] nec42(long addr, long cmd, boolean ext) {
        List<Integer> p = new ArrayList<>(); p.add(9000); p.add(4500);
        if (ext) { bits(p, addr, 26); bits(p, cmd, 16); } else { bits(p, addr, 13); bits(p, ~addr & 0x1FFF, 13); bits(p, cmd & 0xFF, 8); bits(p, ~cmd & 0xFF, 8); }
        p.add(560); return arr(p);
    }

    /** Samsung32: 4500/4500, indirizzo ripetuto due volte, comando e suo complemento. */
    public static int[] samsung(int addr, int cmd) {
        List<Integer> p = new ArrayList<>(); p.add(4500); p.add(4500);
        byteLsb(p, addr & 0xFF, 560, 560, 1690); byteLsb(p, addr & 0xFF, 560, 560, 1690);
        byteLsb(p, cmd & 0xFF, 560, 560, 1690); byteLsb(p, ~cmd & 0xFF, 560, 560, 1690);
        p.add(560); return arr(p);
    }

    /** Sony SIRC (12, 15 o 20 bit): comando 7 bit + indirizzo, LSB per primo; il fotogramma va ripetuto 3 volte ogni 45 ms. */
    public static int[] sony(int addr, int cmd, int bits) {
        int addrBits = bits - 7; List<Integer> p = new ArrayList<>();
        for (int rep = 0; rep < 3; rep++) {
            int dur = 0; p.add(2400); p.add(600); dur += 3000;
            for (int i = 0; i < 7; i++) { int m = ((cmd >> i) & 1) == 1 ? 1200 : 600; p.add(m); p.add(600); dur += m + 600; }
            for (int i = 0; i < addrBits; i++) { int m = ((addr >> i) & 1) == 1 ? 1200 : 600; p.add(m); p.add(600); dur += m + 600; }
            if (rep < 2) { p.set(p.size() - 1, p.get(p.size() - 1) + Math.max(0, 45000 - dur)); } else p.remove(p.size() - 1);
        }
        return arr(p);
    }

    /** Fonde livelli di mezzo bit (true = portante accesa) in durate alternate, partendo da una marca. */
    private static int[] fromLevels(List<int[]> levels) {
        List<Integer> d = new ArrayList<>(); boolean cur = false; int acc = 0; boolean first = true;
        for (int[] l : levels) { boolean on = l[0] == 1; int t = l[1]; if (first) { cur = on; acc = t; first = false; } else if (on == cur) acc += t; else { d.add(acc); cur = on; acc = t; } }
        d.add(acc);
        if (!levels.isEmpty() && levels.get(0)[0] == 0) d.remove(0); // la sequenza deve cominciare con una marca
        if (d.size() % 2 == 0) d.remove(d.size() - 1); // e finire con una marca
        return arr(d);
    }

    /** RC5 (Philips e molte TV vecchie): 14 bit Manchester, 889 us per mezzo bit. */
    public static int[] rc5(int addr, int cmd, boolean toggle) {
        List<int[]> lv = new ArrayList<>(); List<Integer> bits = new ArrayList<>();
        bits.add(1); bits.add(cmd >= 64 ? 0 : 1); bits.add(toggle ? 1 : 0);
        for (int i = 4; i >= 0; i--) bits.add((addr >> i) & 1);
        for (int i = 5; i >= 0; i--) bits.add((cmd >> i) & 1);
        for (int b : bits) { if (b == 1) { lv.add(new int[]{0, 889}); lv.add(new int[]{1, 889}); } else { lv.add(new int[]{1, 889}); lv.add(new int[]{0, 889}); } }
        return fromLevels(lv);
    }

    /** RC6 modo 0 (TV Philips recenti): guida 2664/889, bit start, 3 bit modo, bit toggle doppio, indirizzo e comando a 8 bit. */
    public static int[] rc6(int addr, int cmd, boolean toggle) {
        List<int[]> lv = new ArrayList<>(); lv.add(new int[]{1, 2664}); lv.add(new int[]{0, 889});
        List<int[]> bits = new ArrayList<>(); // {valore, lunghezza in unita' da 444}
        bits.add(new int[]{1, 1}); bits.add(new int[]{0, 1}); bits.add(new int[]{0, 1}); bits.add(new int[]{0, 1}); bits.add(new int[]{toggle ? 1 : 0, 2});
        for (int i = 7; i >= 0; i--) bits.add(new int[]{(addr >> i) & 1, 1});
        for (int i = 7; i >= 0; i--) bits.add(new int[]{(cmd >> i) & 1, 1});
        for (int[] b : bits) { int t = 444 * b[1]; if (b[0] == 1) { lv.add(new int[]{1, t}); lv.add(new int[]{0, t}); } else { lv.add(new int[]{0, t}); lv.add(new int[]{1, t}); } }
        return fromLevels(lv);
    }
}
