package app.telecomandoir;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import app.telecomandoir.TvDb.Key;

/** Telecomando universale a infrarossi: una sola schermata scritta in codice, nessuna libreria. */
public class MainActivity extends Activity {
    private static final int BG = 0xFF0B0F1A, CARD = 0xFF171C2B, ACC = 0xFF8AB4FF, ACC2 = 0xFFC39BFF, TXT = 0xFFEEF2FF, DIM = 0xFF8A93AD, RED = 0xFFE5484D;
    private ConsumerIrManager ir; private Vibrator vib; private float d;
    private TvDb.Brand brand; private TvDb.Remote remote; private TextView status, brandBtn; private LinearLayout modelRow, keysBox;
    private final Handler h = new Handler(Looper.getMainLooper()); private boolean toggle = false, sweeping = false; private SharedPreferences prefs;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b); d = getResources().getDisplayMetrics().density;
        ir = (ConsumerIrManager) getSystemService(Context.CONSUMER_IR_SERVICE); vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); prefs = getSharedPreferences("p", 0);
        try { InputStream in = getAssets().open("tvdb.json"); ByteArrayOutputStream o = new ByteArrayOutputStream(); byte[] buf = new byte[8192]; int n; while ((n = in.read(buf)) > 0) o.write(buf, 0, n); in.close(); TvDb.load(o.toString("UTF-8")); } catch (Exception e) { /* senza database: schermata vuota con avviso */ }
        ScrollView sv = new ScrollView(this); sv.setBackgroundColor(BG); sv.setFillViewport(true);
        LinearLayout root = col(); root.setPadding(px(16), px(20), px(16), px(28)); sv.addView(root);
        root.addView(tv("Telecomando IR", 24, TXT, true));
        status = tv("", 13, DIM, false); status.setPadding(0, px(2), 0, px(12)); root.addView(status);
        brandBtn = tv("", 17, TXT, true); brandBtn.setPadding(px(18), px(14), px(18), px(14)); brandBtn.setBackground(shape(CARD, 16)); brandBtn.setGravity(Gravity.CENTER_VERTICAL);
        brandBtn.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { pickBrand(); } }); root.addView(brandBtn);
        HorizontalScrollView hs = new HorizontalScrollView(this); hs.setHorizontalScrollBarEnabled(false); hs.setPadding(0, px(10), 0, 0); modelRow = new LinearLayout(this); hs.addView(modelRow); root.addView(hs);
        keysBox = col(); keysBox.setPadding(0, px(14), 0, 0); root.addView(keysBox);
        setContentView(sv);
        if (TvDb.BRANDS.isEmpty()) { status.setText("Database dei telecomandi non trovato."); return; }
        String bn = prefs.getString("brand", ""); brand = TvDb.BRANDS.get(0); for (TvDb.Brand x : TvDb.BRANDS) if (x.name.equals(bn)) brand = x;
        select(brand, Math.min(prefs.getInt("remote", 0), brand.remotes.size() - 1));
        status.setText(canSend() ? "Punta la cima del telefono verso la TV. Tieni premuto per ripetere." : "Questo telefono non ha il sensore a infrarossi (per esempio i Galaxy S6 e successivi, S20+ compreso). I tasti sono disattivati.");
    }

    private boolean canSend() { return ir != null && ir.hasIrEmitter(); }
    private int px(float v) { return (int) (v * d + .5f); }
    private LinearLayout col() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l; }
    private TextView tv(String t, float sp, int c, boolean bold) { TextView v = new TextView(this); v.setText(t); v.setTextSize(sp); v.setTextColor(c); if (bold) v.setTypeface(Typeface.DEFAULT_BOLD); return v; }
    private GradientDrawable shape(int color, float r) { GradientDrawable g = new GradientDrawable(); g.setColor(color); g.setCornerRadius(r * d); return g; }

    // ---------------------------------------------------------------- scelta marca e modello
    private void pickBrand() {
        final List<String> names = new ArrayList<>(); for (TvDb.Brand x : TvDb.BRANDS) names.add(x.name);
        LinearLayout box = col(); box.setPadding(px(16), px(8), px(16), 0);
        final EditText search = new EditText(this); search.setHint("Cerca la marca"); search.setSingleLine(true); box.addView(search);
        final ListView lv = new ListView(this); final ArrayAdapter<String> ad = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(names)); lv.setAdapter(ad); box.addView(lv, new LinearLayout.LayoutParams(-1, px(360)));
        final AlertDialog dlg = new AlertDialog.Builder(this).setTitle("Marca della TV (" + names.size() + ")").setView(box).setNegativeButton("Chiudi", null).create();
        search.addTextChangedListener(new TextWatcher() { public void beforeTextChanged(CharSequence s, int a, int b, int c) { } public void onTextChanged(CharSequence s, int a, int b, int c) { ad.getFilter().filter(s); } public void afterTextChanged(Editable s) { } });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() { @Override public void onItemClick(AdapterView<?> p, View v, int pos, long id) { String n = ad.getItem(pos); for (TvDb.Brand x : TvDb.BRANDS) if (x.name.equals(n)) { stopSweep(); select(x, 0); } dlg.dismiss(); } });
        dlg.show();
    }

    private void select(TvDb.Brand b, int ri) {
        brand = b; remote = b.remotes.get(ri); prefs.edit().putString("brand", b.name).putInt("remote", ri).apply();
        brandBtn.setText(b.name + "  ▾");
        modelRow.removeAllViews();
        for (int i = 0; i < b.remotes.size(); i++) {
            final int idx = i; TextView c = tv(shortName(b.remotes.get(i).name, i), 13, i == ri ? 0xFF08101F : TXT, true); c.setPadding(px(14), px(8), px(14), px(8)); c.setBackground(shape(i == ri ? ACC : CARD, 16));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2); lp.rightMargin = px(8); c.setLayoutParams(lp);
            c.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { stopSweep(); select(brand, idx); } }); modelRow.addView(c);
        }
        build();
    }
    private String shortName(String n, int i) { String s = n.replaceAll("(?i)^" + brand.name + "\\s*", ""); if (s.length() > 16) s = s.substring(0, 16); return (i + 1) + (s.isEmpty() ? "" : " · " + s); }

    // ---------------------------------------------------------------- tasti
    private View key(String label, final Key k, boolean repeat, int color, int hdp) {
        TextView t = tv(label, 15, TXT, true); t.setGravity(Gravity.CENTER); t.setBackground(shape(color, 16)); t.setSingleLine(true);
        boolean ok = remote.has(k); t.setAlpha(ok && canSend() ? 1f : .3f);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, px(hdp), 1f); lp.setMargins(px(3), px(3), px(3), px(3)); t.setLayoutParams(lp);
        if (ok) attach(t, k, repeat); return t;
    }
    private void attach(final View v, final Key k, final boolean repeat) {
        final Runnable[] loop = new Runnable[1];
        loop[0] = new Runnable() { @Override public void run() { send(k); if (repeat) h.postDelayed(loop[0], 230); } };
        v.setOnTouchListener(new View.OnTouchListener() { @Override public boolean onTouch(View view, MotionEvent e) {
            if (e.getAction() == MotionEvent.ACTION_DOWN) { view.setScaleX(.94f); view.setScaleY(.94f); toggle = !toggle; h.post(loop[0]); }
            else if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_CANCEL) { view.setScaleX(1f); view.setScaleY(1f); h.removeCallbacks(loop[0]); }
            return true; } });
    }
    private LinearLayout line(View... vs) { LinearLayout l = new LinearLayout(this); for (View v : vs) l.addView(v); return l; }
    private View gap(int dp) { View v = new View(this); v.setLayoutParams(new LinearLayout.LayoutParams(1, px(dp))); return v; }
    private View blank() { View v = new View(this); v.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1f)); return v; }
    private boolean anyOf(Key... ks) { for (Key k : ks) if (remote.has(k)) return true; return false; }
    private TextView label(String t) { TextView v = tv(t, 12, DIM, true); v.setPadding(px(4), px(14), 0, px(4)); return v; }

    private void build() {
        keysBox.removeAllViews();
        keysBox.addView(line(key("Accendi / Spegni", Key.POWER, false, RED, 58)));
        if (anyOf(Key.YOUTUBE, Key.NETFLIX, Key.PRIME, Key.DISNEY, Key.SPOTIFY, Key.APPS)) {
            keysBox.addView(label("APP"));
            keysBox.addView(line(key("YouTube", Key.YOUTUBE, false, 0xFFB3262B, 48), key("Netflix", Key.NETFLIX, false, 0xFF8B1A1F, 48), key("Prime", Key.PRIME, false, 0xFF1A5F8F, 48)));
            keysBox.addView(line(key("Disney+", Key.DISNEY, false, 0xFF1D3F7A, 48), key("Spotify", Key.SPOTIFY, false, 0xFF1C7A43, 48), key("App", Key.APPS, false, CARD, 48)));
        }
        keysBox.addView(label("AUDIO E PROGRAMMI"));
        keysBox.addView(line(key("Vol +", Key.VOL_UP, true, CARD, 54), key("Muto", Key.MUTE, false, CARD, 54), key("Prog +", Key.CH_UP, true, CARD, 54)));
        keysBox.addView(line(key("Vol –", Key.VOL_DOWN, true, CARD, 54), key("Ingresso", Key.INPUT, false, CARD, 54), key("Prog –", Key.CH_DOWN, true, CARD, 54)));
        keysBox.addView(label("NAVIGAZIONE"));
        keysBox.addView(line(key("Menu", Key.MENU, false, CARD, 46), key("▲", Key.UP, true, CARD, 46), key("Home", Key.HOME, false, CARD, 46)));
        keysBox.addView(line(key("◀", Key.LEFT, true, CARD, 54), key("OK", Key.OK, false, 0xFF2B4A8C, 54), key("▶", Key.RIGHT, true, CARD, 54)));
        keysBox.addView(line(key("Indietro", Key.BACK, false, CARD, 46), key("▼", Key.DOWN, true, CARD, 46), key("Esci", Key.EXIT, false, CARD, 46)));
        keysBox.addView(gap(6)); keysBox.addView(pad());
        keysBox.addView(line(key("Info", Key.INFO, false, CARD, 42), key("Guida", Key.GUIDE, false, CARD, 42), key("Impost.", Key.SETTINGS, false, CARD, 42)));
        if (anyOf(Key.PLAY, Key.PAUSE, Key.STOP, Key.REW, Key.FFWD)) {
            keysBox.addView(label("RIPRODUZIONE"));
            keysBox.addView(line(key("⏪", Key.REW, true, CARD, 46), key("▶", Key.PLAY, false, CARD, 46), key("❚❚", Key.PAUSE, false, CARD, 46), key("■", Key.STOP, false, CARD, 46), key("⏩", Key.FFWD, true, CARD, 46)));
        }
        if (anyOf(Key.RED, Key.GREEN, Key.YELLOW, Key.BLUE)) keysBox.addView(line(key("", Key.RED, false, 0xFFC0392B, 34), key("", Key.GREEN, false, 0xFF27AE60, 34), key("", Key.YELLOW, false, 0xFFE0B100, 34), key("", Key.BLUE, false, 0xFF2D6CDF, 34)));
        keysBox.addView(label("NUMERI"));
        Key[][] nums = {{Key.D1, Key.D2, Key.D3}, {Key.D4, Key.D5, Key.D6}, {Key.D7, Key.D8, Key.D9}};
        for (Key[] r : nums) keysBox.addView(line(key(num(r[0]), r[0], false, CARD, 44), key(num(r[1]), r[1], false, CARD, 44), key(num(r[2]), r[2], false, CARD, 44)));
        keysBox.addView(line(key("Ultimo", Key.LAST, false, CARD, 44), key("0", Key.D0, false, CARD, 44), key("Lista", Key.CHLIST, false, CARD, 44)));
        keysBox.addView(line(key("Sottotitoli", Key.SUBTITLE, false, CARD, 42), key("Sleep", Key.SLEEP, false, CARD, 42), key("Teletext", Key.TEXT, false, CARD, 42)));
        keysBox.addView(gap(14));
        keysBox.addView(btn(sweeping ? "Stop: la TV ha reagito" : "Trova il modello giusto di " + brand.name, ACC, new View.OnClickListener() { @Override public void onClick(View v) { sweep(false); } }));
        keysBox.addView(gap(8));
        keysBox.addView(btn(sweeping ? " " : "Non so la marca: prova tutte le TV", ACC2, new View.OnClickListener() { @Override public void onClick(View v) { sweep(true); } }));
        TextView note = tv("Database: " + TvDb.BRANDS.size() + " marche, " + totalRemotes() + " telecomandi. Non tutti i modelli rispondono: scegli il modello che funziona. Per 'scorrere' trascina il dito sull'area Scorri; un tocco è OK.", 12, DIM, false); note.setPadding(0, px(14), 0, 0); keysBox.addView(note);
    }
    private int totalRemotes() { int n = 0; for (TvDb.Brand b : TvDb.BRANDS) n += b.remotes.size(); return n; }
    private String num(Key k) { return k.name().substring(1); }
    private TextView btn(String t, int color, View.OnClickListener l) { TextView s = tv(t, 14, 0xFF08101F, true); s.setGravity(Gravity.CENTER); s.setPadding(px(12), px(14), px(12), px(14)); s.setBackground(shape(color, 16)); s.setAlpha(canSend() ? 1f : .35f); if (t.trim().length() > 0) s.setOnClickListener(l); else s.setVisibility(View.GONE); return s; }

    /** Area "Scorri": trascinando si inviano le frecce, un tocco = OK. */
    private View pad() {
        TextView p = tv("Scorri qui\n◀  ▲ ▼  ▶   (tocco = OK)", 13, DIM, false); p.setGravity(Gravity.CENTER); p.setBackground(shape(CARD, 18));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, px(120)); lp.setMargins(px(3), px(6), px(3), px(6)); p.setLayoutParams(lp);
        final float step = 34 * d; final float[] st = new float[3]; // x, y, mosso
        p.setOnTouchListener(new View.OnTouchListener() { @Override public boolean onTouch(View v, MotionEvent e) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN: st[0] = e.getX(); st[1] = e.getY(); st[2] = 0; v.getParent().requestDisallowInterceptTouchEvent(true); return true;
                case MotionEvent.ACTION_MOVE: {
                    float dx = e.getX() - st[0], dy = e.getY() - st[1];
                    if (Math.abs(dx) > step && Math.abs(dx) >= Math.abs(dy)) { toggle = !toggle; send(dx > 0 ? Key.RIGHT : Key.LEFT); st[0] = e.getX(); st[1] = e.getY(); st[2] = 1; }
                    else if (Math.abs(dy) > step) { toggle = !toggle; send(dy > 0 ? Key.DOWN : Key.UP); st[0] = e.getX(); st[1] = e.getY(); st[2] = 1; }
                    return true; }
                case MotionEvent.ACTION_UP: if (st[2] == 0) { toggle = !toggle; send(Key.OK); } return true;
                default: return true;
            } } });
        return p;
    }

    // ---------------------------------------------------------------- invio e ricerca
    private void send(Key k) {
        if (!canSend() || remote == null) return; TvDb.Btn b = remote.get(k); if (b == null) return; transmit(b);
    }
    private void transmit(TvDb.Btn b) {
        int[] p = b.pattern(toggle); if (p == null) return;
        try { ir.transmit(b.carrier(), p); } catch (Exception ignored) { }
        if (vib != null) try { vib.vibrate(10); } catch (Exception ignored) { }
    }

    private void stopSweep() { sweeping = false; }

    /** Prova l'accensione di ogni modello (della marca scelta, o di tutte le marche). Se la TV reagisce basta fermarsi: marca e modello restano selezionati. */
    private void sweep(final boolean all) {
        if (!canSend()) return;
        if (sweeping) { sweeping = false; status.setText("Selezionato: " + brand.name + " " + remote.name); build(); return; }
        final List<TvDb.Entry> list = new ArrayList<>();
        for (TvDb.Entry e : TvDb.powerSweep()) if (all || e.brand == brand) list.add(e);
        if (list.isEmpty()) return;
        sweeping = true; build();
        h.post(new Runnable() { int i = 0; @Override public void run() {
            if (!sweeping) return;
            if (i >= list.size()) { sweeping = false; status.setText("Provati tutti. Se la TV non ha reagito, il suo telecomando non è nel database."); build(); return; }
            TvDb.Entry e = list.get(i); brand = e.brand; remote = e.remote; toggle = !toggle; transmit(e.power);
            brandBtn.setText(e.brand.name + "  ▾"); status.setText("Provo " + (i + 1) + " di " + list.size() + ": " + e.brand.name + " " + e.remote.name + " — tocca Stop quando la TV reagisce");
            prefs.edit().putString("brand", e.brand.name).putInt("remote", Math.max(0, e.brand.remotes.indexOf(e.remote))).apply(); i++; h.postDelayed(this, all ? 700 : 1500); } });
    }
}
