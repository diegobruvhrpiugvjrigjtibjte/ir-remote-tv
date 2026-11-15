package app.telecomandoir;

import static org.junit.Assert.*;
import org.junit.Test;

public class IrCodecTest {
    @Test public void necHaIlFormatoGiusto() {
        int[] p = IrCodec.nec(0x04, 0x08); // LG: accensione
        assertEquals(2 + 64 + 1, p.length);
        assertEquals(9000, p[0]); assertEquals(4500, p[1]);
        // indirizzo 0x04, LSB per primo: 0,0,1,0,0,0,0,0 -> il terzo bit e' un "1" (spazio lungo)
        assertEquals(560, p[2 + 2 * 0 + 1]); assertEquals(560, p[2 + 2 * 1 + 1]); assertEquals(1690, p[2 + 2 * 2 + 1]); assertEquals(560, p[2 + 2 * 3 + 1]);
        assertEquals(560, p[p.length - 1]);
        // 0x04 (1 bit) + ~0x04 (7) + 0x08 (1) + ~0x08 (7) = 16 bit a 1
        int ones = 0; for (int i = 3; i < p.length - 1; i += 2) if (p[i] == 1690) ones++;
        assertEquals(16, ones);
    }
    @Test public void hisenseNecEsteso() {
        int[] p = IrCodec.nec(0xBF00, 0x0D); // Hisense: byte 0x00, 0xBF, poi 0x0D e il suo complemento
        assertEquals(2 + 64 + 1, p.length);
        int ones = 0; for (int i = 3; i < p.length - 1; i += 2) if (p[i] == 1690) ones++;
        assertEquals(0 + 7 + 3 + 5, ones);
    }
    @Test public void samsungRipeteLIndirizzo() {
        int[] p = IrCodec.samsung(0x07, 0x02);
        assertEquals(4500, p[0]); assertEquals(4500, p[1]); assertEquals(2 + 64 + 1, p.length);
        for (int i = 0; i < 8; i++) assertEquals(p[2 + 2 * i + 1], p[2 + 2 * (i + 8) + 1]); // secondo byte = primo byte
    }
    @Test public void sonyHa3FotogrammiDa12Bit() {
        int[] p = IrCodec.sony(0x01, 0x15, 12);
        assertEquals(3 * (2 + 24) - 1, p.length); assertEquals(2400, p[0]);
        int total = 0; for (int v : p) total += v; assertTrue(total > 2 * 45000 && total < 3 * 45000);
    }
    @Test public void rc5HaIlNumeroDiBitGiusto() {
        int[] p = IrCodec.rc5(0, 0x0C, false);
        assertTrue(p.length % 2 == 1); assertEquals(889, p[0] % 889 == 0 ? 889 : 0);
        int total = 0; for (int v : p) total += v; assertTrue(total >= 13 * 889 && total <= 14 * 1778);
    }
    @Test public void rc6IniziaConLaGuida() { int[] p = IrCodec.rc6(0, 0x0C, true); assertEquals(2664, p[0]); assertEquals(889, p[1]); assertTrue(p.length % 2 == 1); }
    @Test public void ilDatabaseSiCaricaESiCodificaTutto() throws Exception {
        java.io.File f = new java.io.File("src/main/assets/tvdb.json");
        TvDb.load(new String(java.nio.file.Files.readAllBytes(f.toPath()), "UTF-8"));
        assertTrue("marche", TvDb.BRANDS.size() > 90);
        int remotes = 0, buttons = 0;
        for (TvDb.Brand b : TvDb.BRANDS) for (TvDb.Remote r : b.remotes) { remotes++; for (java.util.Map.Entry<TvDb.Key, TvDb.Btn> e : r.keys.entrySet()) {
            int[] p = e.getValue().pattern(false); assertNotNull(b.name + " " + r.name + " " + e.getKey(), p); assertTrue(p.length > 3);
            for (int v : p) assertTrue(b.name + " " + r.name + " " + e.getKey() + " valore " + v, v > 0); assertTrue(e.getValue().carrier() >= 30000 && e.getValue().carrier() <= 60000); buttons++; } }
        assertTrue(remotes > 300 && buttons > 5000);
    }
    @Test public void hisenseE8HaITastiGiusti() throws Exception {
        TvDb.load(new String(java.nio.file.Files.readAllBytes(new java.io.File("src/main/assets/tvdb.json").toPath()), "UTF-8"));
        TvDb.Brand hi = null; for (TvDb.Brand b : TvDb.BRANDS) if (b.name.equals("Hisense")) hi = b; assertNotNull(hi);
        boolean trovato = false;
        for (TvDb.Remote r : hi.remotes) { TvDb.Btn pw = r.get(TvDb.Key.POWER); if (pw != null && "NECext".equals(pw.proto) && pw.a == 0xBF00 && pw.c == 0xF20D) trovato = true; }
        assertTrue("Hisense NECext 00BF / 0D", trovato);
    }
    @Test public void laRicercaCopreMoltiCodici() throws Exception {
        TvDb.load(new String(java.nio.file.Files.readAllBytes(new java.io.File("src/main/assets/tvdb.json").toPath()), "UTF-8"));
        int n = TvDb.powerSweep().size(); assertTrue("n=" + n, n > 80);
    }
    @Test public void leAppDiStreamingCiSono() throws Exception {
        TvDb.load(new String(java.nio.file.Files.readAllBytes(new java.io.File("src/main/assets/tvdb.json").toPath()), "UTF-8"));
        int yt = 0, nf = 0; for (TvDb.Brand b : TvDb.BRANDS) for (TvDb.Remote r : b.remotes) { if (r.has(TvDb.Key.YOUTUBE)) yt++; if (r.has(TvDb.Key.NETFLIX)) nf++; }
        assertTrue(yt > 10 && nf > 40);
    }
}
