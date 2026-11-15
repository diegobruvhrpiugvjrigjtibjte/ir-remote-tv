package app.telecomandoir;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import app.telecomandoir.TvDb.Key;

public class VoiceCommandsTest {
    private static List<Key> p(String s) { return VoiceCommands.parse(s); }

    @Test public void accensione() { assertEquals(Arrays.asList(Key.POWER), p("Accendi la TV")); assertEquals(Arrays.asList(Key.POWER), p("spegni")); }
    @Test public void volume() {
        assertEquals(3, p("alza il volume").size()); assertEquals(Key.VOL_UP, p("alza il volume").get(0));
        assertEquals(5, p("alza il volume di 5").size());
        assertEquals(Key.VOL_DOWN, p("abbassa").get(0));
        assertEquals(Arrays.asList(Key.MUTE), p("muto"));
    }
    @Test public void canali() {
        assertEquals(Arrays.asList(Key.D1, Key.D2, Key.OK), p("canale 12"));
        assertEquals(Arrays.asList(Key.D5, Key.OK), p("metti il canale cinque"));
        assertEquals(Arrays.asList(Key.CH_UP), p("canale successivo"));
        assertEquals(Arrays.asList(Key.CH_DOWN), p("canale precedente"));
    }
    @Test public void app() {
        assertEquals(Arrays.asList(Key.NETFLIX), p("apri Netflix"));
        assertEquals(Arrays.asList(Key.YOUTUBE), p("YouTube"));
        assertEquals(Arrays.asList(Key.PRIME), p("Prime Video"));
        assertEquals(Arrays.asList(Key.DISNEY), p("Disney plus"));
    }
    @Test public void navigazione() {
        assertEquals(Arrays.asList(Key.HOME), p("home"));
        assertEquals(Arrays.asList(Key.BACK), p("torna indietro"));
        assertEquals(Arrays.asList(Key.RIGHT, Key.RIGHT, Key.RIGHT), p("vai a destra 3"));
        assertEquals(Arrays.asList(Key.DOWN), p("giù"));
        assertEquals(Arrays.asList(Key.OK), p("conferma"));
    }
    @Test public void riproduzioneEAltro() {
        assertEquals(Arrays.asList(Key.PAUSE), p("pausa"));
        assertEquals(Arrays.asList(Key.INPUT), p("cambia sorgente"));
        assertEquals(Arrays.asList(Key.SETTINGS), p("impostazioni"));
        assertTrue(p("bla bla").isEmpty());
    }
}
