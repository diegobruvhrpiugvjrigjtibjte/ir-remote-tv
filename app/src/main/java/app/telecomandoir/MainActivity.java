package app.telecomandoir;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import app.telecomandoir.RemoteViews.DPad;
import app.telecomandoir.RemoteViews.KeyButton;
import app.telecomandoir.RemoteViews.Rocker;
import app.telecomandoir.TvDb.Key;

/** Telecomando universale a infrarossi: una sola schermata senza scorrimento, disegnata come un telecomando vero. */
public class MainActivity extends Activity implements RemoteViews.Sender {
    private static final int VOICE = 7;
    private ConsumerIrManager ir; private Vibrator vib; private float d; private SharedPreferences prefs;
    private TvDb.Brand brand; private TvDb.Remote remote; private boolean toggle, sweeping;
    private final Handler h = new Handler(Looper.getMainLooper());
    private final List<View> all = new ArrayList<>();
    private TextView brandChip, status; private View led;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        if (android.os.Build.VERSION.SDK_INT >= 21) { Window w = getWindow(); w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); w.setStatusBarColor(0xFF0A0C14); w.setNavigationBarColor(0xFF0A0C14); }
        d = getResources().getDisplayMetrics().density; prefs = getSharedPreferences("p", 0);
        ir = (ConsumerIrManager) getSystemService(Context.CONSUMER_IR_SERVICE); vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        try { InputStream in = getAssets().open("tvdb.json"); ByteArrayOutputStream o = new ByteArrayOutputStream(); byte[] buf = new byte[8192]; int n; while ((n = in.read(buf)) > 0) o.write(buf, 0, n); in.close(); TvDb.load(o.toString("UTF-8")); } catch (Exception e) { /* gestito sotto */ }
        if (!TvDb.BRANDS.isEmpty()) {
            String bn = prefs.getString("brand", "Hisense"); brand = TvDb.BRANDS.get(0); for (TvDb.Brand x : TvDb.BRANDS) if (x.name.equals(bn)) brand = x;
            remote = brand.remotes.get(Math.max(0, Math.min(prefs.getInt("remote", 0), brand.remotes.size() - 1)));
        }
        setContentView(build());
        refresh();
        if (TvDb.BRANDS.isEmpty()) say("Database dei telecomandi non trovato.", true);
        else if (!canSend()) say("Questo telefono non ha il trasmettitore a infrarossi.", true);
        else say("Punta la cima verso la TV · trascina sulla croce per scorrere", false);
    }

    // ------------------------------------------------------------------ invio
    private boolean canSend() { return ir != null && ir.hasIrEmitter(); }
    @Override public boolean has(Key k) { return remote != null && remote.has(k); }
    @Override public void press(Key k, boolean fromRepeat) {
        if (!fromRepeat) toggle = !toggle;
        if (remote == null || !canSend()) return; TvDb.Btn b = remote.get(k); if (b != null) transmit(b);
    }
    private void transmit(TvDb.Btn b) {
        int[] p = b.pattern(toggle); if (p == null) return;
        try { ir.transmit(b.carrier(), p); } catch (Exception ignored) { }
        if (vib != null) try { vib.vibrate(12); } catch (Exception ignored) { }
        led.setAlpha(1f); led.animate().alpha(0.15f).setDuration(260).start();
    }

    // ------------------------------------------------------------------ layout
    private int px(float v) { return (int) (v * d + .5f); }
    private LinearLayout row(float weight, View... vs) {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL); l.setGravity(Gravity.CENTER);
        for (View v : vs) { if (v.getLayoutParams() == null) v.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)); l.addView(v); }
        l.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, weight)); return l;
    }
    private LinearLayout col(float weight, View... vs) {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL);
        for (View v : vs) { v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)); l.addView(v); }
        l.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight)); return l;
    }
    private KeyButton k(Key key, String icon, String label) { return k(key, icon, label, "dark", false, false); }
    private KeyButton k(Key key, String icon, String label, String style, boolean round, boolean repeat) { KeyButton b = new KeyButton(this, this, key, icon, label, style, round, repeat); all.add(b); return b; }
    private KeyButton action(String icon, String label, String style, boolean round, View.OnClickListener l) { KeyButton b = k(null, icon, label, style, round, false); b.setOnClickListener(l); return b; }
    private View w(View v, float weight) { v.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight)); return v; }

    private View build() {
        FrameLayout root = new FrameLayout(this);
        root.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{0xFF141B33, 0xFF0A0C14, 0xFF1E1230}));

        LinearLayout body = new LinearLayout(this); body.setOrientation(LinearLayout.VERTICAL); body.setPadding(px(12), px(8), px(12), px(14));
        GradientDrawable bd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{0xFF262A34, 0xFF17191F, 0xFF101217}); bd.setCornerRadius(px(40)); bd.setStroke(px(1), 0xFF353A46); body.setBackground(bd);
        FrameLayout.LayoutParams blp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT); blp.setMargins(px(14), px(10), px(14), px(10)); root.addView(body, blp);

        // LED a infrarossi in cima: si accende a ogni invio
        LinearLayout ledRow = new LinearLayout(this); ledRow.setGravity(Gravity.CENTER);
        led = new View(this); GradientDrawable lg = new GradientDrawable(); lg.setColor(0xFFFF4D4D); lg.setCornerRadius(px(4)); led.setBackground(lg); led.setAlpha(0.15f);
        ledRow.addView(led, new LinearLayout.LayoutParams(px(26), px(6)));
        body.addView(ledRow, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, px(12)));

        // testata: marca, microfono, menu
        brandChip = new TextView(this); brandChip.setTextColor(RemoteViews.TXT); brandChip.setTextSize(14); brandChip.setTypeface(Typeface.DEFAULT_BOLD); brandChip.setGravity(Gravity.CENTER_VERTICAL); brandChip.setPadding(px(14), 0, px(10), 0); brandChip.setSingleLine(true); brandChip.setEllipsize(TextUtils.TruncateAt.END);
        GradientDrawable cb = new GradientDrawable(); cb.setColor(0xFF2A2E38); cb.setCornerRadius(px(20)); brandChip.setBackground(cb);
        brandChip.setOnClickListener(v -> pickBrand());
        brandChip.setOnLongClickListener(v -> { pickModel(); return true; });
        LinearLayout head = new LinearLayout(this); head.setGravity(Gravity.CENTER_VERTICAL);
        head.addView(brandChip, new LinearLayout.LayoutParams(0, px(40), 1f));
        head.addView(action("mic", null, "mic", true, v -> voice()), new LinearLayout.LayoutParams(px(50), px(50)));
        head.addView(action("dots", null, "dark", true, v -> menu()), new LinearLayout.LayoutParams(px(46), px(46)));
        body.addView(head, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, px(52)));

        status = new TextView(this); status.setTextSize(11); status.setTextColor(RemoteViews.DIM); status.setGravity(Gravity.CENTER); status.setSingleLine(true); status.setEllipsize(TextUtils.TruncateAt.END);
        status.setOnClickListener(v -> stopSweep());
        body.addView(status, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, px(22)));

        body.addView(row(1.15f, w(k(Key.POWER, "power", null, "power", true, false), 1.2f), k(Key.INPUT, "input", "SOURCE"), k(Key.MUTE, "mute", "MUTE"), k(Key.SETTINGS, "gear", "SETUP")));
        body.addView(row(1.0f, k(Key.NETFLIX, null, null, "netflix", false, false), k(Key.YOUTUBE, null, null, "youtube", false, false), k(Key.PRIME, null, null, "prime", false, false), k(Key.DISNEY, null, null, "disney", false, false)));
        DPad pad = new DPad(this, this); all.add(pad);
        body.addView(row(3.7f, col(0.8f, k(Key.BACK, "back", "BACK"), k(Key.MENU, "menu", "MENU")), w(pad, 2.4f), col(0.8f, k(Key.HOME, "home", "HOME"), k(Key.EXIT, "exit", "EXIT"))));
        Rocker vol = new Rocker(this, this, Key.VOL_UP, Key.VOL_DOWN, "VOL"), ch = new Rocker(this, this, Key.CH_UP, Key.CH_DOWN, "CH"); all.add(vol); all.add(ch);
        body.addView(row(2.5f, w(vol, 1f), col(1.25f, k(Key.APPS, "apps", "APP"), k(Key.GUIDE, "guide", "GUIDE"), action(null, "123", "dark", false, v -> numpad())), col(1.25f, k(Key.INFO, "info", "INFO"), k(Key.CHLIST, "list", "LIST"), k(Key.SUBTITLE, "sub", "SUB")), w(ch, 1f)));
        body.addView(row(0.85f, k(Key.REW, "rew", null, "dark", false, true), k(Key.PLAY, "play", null), k(Key.PAUSE, "pause", null), k(Key.STOP, "stop", null), k(Key.FFWD, "ffwd", null, "dark", false, true)));
        body.addView(row(0.5f, k(Key.RED, null, null, "red", false, false), k(Key.GREEN, null, null, "green", false, false), k(Key.YELLOW, null, null, "yellow", false, false), k(Key.BLUE, null, null, "blue", false, false)));
        return root;
    }

    private void refresh() {
        if (brand != null) brandChip.setText(brand.name + "  ·  " + (brand.remotes.indexOf(remote) + 1) + "/" + brand.remotes.size() + "  ▾");
        for (View v : all) { if (v instanceof KeyButton) ((KeyButton) v).refresh(); else v.invalidate(); }
    }
    private void say(String t, boolean warn) { status.setText(t); status.setTextColor(warn ? 0xFFFF8A80 : RemoteViews.DIM); }
    private void select(TvDb.Brand b, TvDb.Remote r) { brand = b; remote = r; prefs.edit().putString("brand", b.name).putInt("remote", b.remotes.indexOf(r)).apply(); refresh(); }

    // ------------------------------------------------------------------ dialoghi
    private void pickBrand() {
        if (TvDb.BRANDS.isEmpty()) return;
        final List<String> names = new ArrayList<>(); for (TvDb.Brand x : TvDb.BRANDS) names.add(x.name);
        LinearLayout box = new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(px(16), px(8), px(16), 0);
        final EditText search = new EditText(this); search.setHint("Cerca la marca"); search.setSingleLine(true); box.addView(search);
        final ListView lv = new ListView(this); final ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(names)); lv.setAdapter(ad); box.addView(lv, new LinearLayout.LayoutParams(-1, px(340)));
        final AlertDialog dlg = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK).setTitle("Marca della TV (" + names.size() + ")").setView(box).setNegativeButton("Chiudi", null).create();
        search.addTextChangedListener(new TextWatcher() { public void beforeTextChanged(CharSequence s, int a, int b, int c) { } public void onTextChanged(CharSequence s, int a, int b, int c) { ad.getFilter().filter(s); } public void afterTextChanged(Editable s) { } });
        lv.setOnItemClickListener((p, v, pos, id) -> { String n = ad.getItem(pos); for (TvDb.Brand x : TvDb.BRANDS) if (x.name.equals(n)) { sweeping = false; select(x, x.remotes.get(0)); say(x.name + ": premi accensione. Non reagisce? Menu ⋯ → Trova il modello", false); } dlg.dismiss(); });
        dlg.show();
    }

    private void pickModel() {
        if (brand == null) return;
        String[] items = new String[brand.remotes.size()];
        for (int i = 0; i < items.length; i++) items[i] = (i + 1) + ".  " + brand.remotes.get(i).name + "  (" + brand.remotes.get(i).keys.size() + " tasti)";
        new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK).setTitle("Telecomando " + brand.name).setSingleChoiceItems(items, brand.remotes.indexOf(remote), (dl, i) -> { select(brand, brand.remotes.get(i)); dl.dismiss(); }).setNegativeButton("Chiudi", null).show();
    }

    private void menu() {
        if (TvDb.BRANDS.isEmpty()) return;
        String[] items = {"Cambia marca (" + brand.name + ")", "Cambia modello di telecomando", "Trova il modello giusto di " + brand.name, "Non so la marca: prova tutte le TV", "Tastierino numerico e altri tasti", "Comandi vocali: esempi"};
        new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK).setItems(items, (dl, i) -> {
            switch (i) { case 0: pickBrand(); break; case 1: pickModel(); break; case 2: sweep(false); break; case 3: sweep(true); break; case 4: numpad(); break; default: voiceHelp(); }
        }).show();
    }

    private void numpad() {
        LinearLayout box = new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(px(12), px(12), px(12), px(4)); box.setBackgroundColor(0xFF17191F);
        String[][] rows = {{"1", "2", "3"}, {"4", "5", "6"}, {"7", "8", "9"}};
        for (String[] r : rows) box.addView(row(1f, k(Key.valueOf("D" + r[0]), null, r[0]), k(Key.valueOf("D" + r[1]), null, r[1]), k(Key.valueOf("D" + r[2]), null, r[2])));
        box.addView(row(1f, k(Key.LAST, null, "ULTIMO"), k(Key.D0, null, "0"), k(Key.CHLIST, "list", "LIST")));
        box.addView(row(1f, k(Key.SPOTIFY, null, null, "spotify", false, false), k(Key.TEXT, "text", "TELETEXT"), k(Key.SLEEP, "sleep", "SLEEP")));
        for (int i = 0; i < box.getChildCount(); i++) box.getChildAt(i).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, px(64)));
        for (View v : all) if (v instanceof KeyButton) ((KeyButton) v).refresh();
        new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK).setView(box).setPositiveButton("Fatto", null).show();
    }

    // ------------------------------------------------------------------ ricerca del modello
    private void stopSweep() { if (sweeping) { sweeping = false; say("Selezionato: " + brand.name + " " + remote.name, false); refresh(); } }
    private void sweep(final boolean everyBrand) {
        if (!canSend()) { say("Serve un telefono con trasmettitore a infrarossi.", true); return; }
        final List<TvDb.Entry> list = new ArrayList<>(); for (TvDb.Entry e : TvDb.powerSweep()) if (everyBrand || e.brand == brand) list.add(e);
        if (list.isEmpty()) return; sweeping = true;
        h.post(new Runnable() { int i = 0; @Override public void run() {
            if (!sweeping) return;
            if (i >= list.size()) { sweeping = false; say("Provati tutti: il telecomando di questa TV non è nel database.", true); return; }
            TvDb.Entry e = list.get(i++); brand = e.brand; remote = e.remote; toggle = !toggle; transmit(e.power);
            prefs.edit().putString("brand", brand.name).putInt("remote", brand.remotes.indexOf(remote)).apply(); refresh();
            status.setTextColor(0xFFFFD27A); status.setText("TOCCA QUI per fermare · " + i + "/" + list.size() + " · " + e.remote.name);
            h.postDelayed(this, everyBrand ? 800 : 1500); } });
    }

    // ------------------------------------------------------------------ voce
    private void voice() {
        Intent it = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        it.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        it.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "it-IT"); it.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        it.putExtra("android.speech.extra.PREFER_OFFLINE", true);
        it.putExtra(RecognizerIntent.EXTRA_PROMPT, "Es. \"alza il volume\", \"canale 5\", \"Netflix\"");
        try { startActivityForResult(it, VOICE); } catch (ActivityNotFoundException e) { say("Riconoscimento vocale non disponibile: installa o aggiorna l'app Google.", true); }
    }
    @Override protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req != VOICE || res != RESULT_OK || data == null) return;
        ArrayList<String> said = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS); if (said == null || said.isEmpty()) return;
        for (String s : said) {
            final List<Key> keys = VoiceCommands.parse(s);
            if (keys.isEmpty()) continue;
            if (!has(keys.get(0))) { say("\"" + s + "\": questo telecomando non ha quel tasto.", true); return; }
            say("\"" + s + "\" → " + label(keys.get(0)) + (keys.size() > 1 ? " ×" + keys.size() : ""), false);
            for (int i = 0; i < keys.size(); i++) { final Key k = keys.get(i); h.postDelayed(() -> press(k, false), 380L * i); }
            return;
        }
        say("Non ho capito \"" + said.get(0) + "\". Menu ⋯ → Comandi vocali.", true);
    }
    private static String label(Key k) { String n = k.name(); return n.length() == 2 && n.charAt(0) == 'D' ? n.substring(1) : n.replace('_', ' ').toLowerCase(); }
    private void voiceHelp() {
        new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK).setTitle("Comandi vocali")
            .setMessage("Premi il microfono e di', per esempio:\n\n• accendi / spegni\n• alza il volume (di 5) · abbassa · muto\n• canale 5 · canale successivo\n• Netflix · YouTube · Prime · Disney\n• home · indietro · ok · esci\n• su · giù · destra 3 · sinistra\n• pausa · play · stop · avanti\n• sorgente · impostazioni · guida · info\n\nIl riconoscimento usa il servizio vocale del telefono e preferisce quello offline, se la lingua italiana è scaricata.")
            .setPositiveButton("Ok", null).show();
    }
}
