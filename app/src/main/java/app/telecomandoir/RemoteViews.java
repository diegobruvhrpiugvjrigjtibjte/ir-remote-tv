package app.telecomandoir;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

import app.telecomandoir.TvDb.Key;

/** Le viste del telecomando: tasto, croce direzionale con touchpad e bilanciere volume/canali. */
final class RemoteViews {
    private RemoteViews() {}

    interface Sender { void press(Key k, boolean fromRepeat); boolean has(Key k); }

    static final int TXT = 0xFFEEF1F7, DIM = 0xFF8E95A6;
    private static final Handler H = new Handler(Looper.getMainLooper());

    static void fitText(Canvas c, String t, float cx, float cy, float maxW, float maxH, Paint p) {
        p.setTextSize(maxH); float w = p.measureText(t); if (w > maxW) p.setTextSize(maxH * maxW / w);
        Paint.FontMetrics fm = p.getFontMetrics(); c.drawText(t, cx, cy - (fm.ascent + fm.descent) / 2f, p);
    }

    /** Ripetizione mentre il dito resta premuto: primo invio subito, poi ogni 160 ms dopo 420 ms. */
    static final class Repeater {
        private final Runnable r; private Key key; private Sender s;
        Repeater() { r = new Runnable() { @Override public void run() { if (key != null) { s.press(key, true); H.postDelayed(this, 160); } } }; }
        void start(Sender s, Key k, boolean repeat) { stop(); this.s = s; key = k; s.press(k, false); if (repeat) H.postDelayed(r, 420); else key = null; }
        void stop() { key = null; H.removeCallbacks(r); }
    }

    // ------------------------------------------------------------------ tasto
    static final class KeyButton extends View {
        final Key key; final String icon, label, style; final boolean round, repeat; final Sender s;
        private boolean down; private final Repeater rep = new Repeater(); private final float d;
        private final Paint bg = new Paint(Paint.ANTI_ALIAS_FLAG), tx = new Paint(Paint.ANTI_ALIAS_FLAG), rim = new Paint(Paint.ANTI_ALIAS_FLAG);

        KeyButton(Context c, Sender s, Key key, String icon, String label, String style, boolean round, boolean repeat) {
            super(c); this.s = s; this.key = key; this.icon = icon; this.label = label; this.style = style; this.round = round; this.repeat = repeat;
            d = c.getResources().getDisplayMetrics().density; tx.setTextAlign(Paint.Align.CENTER); tx.setTypeface(Typeface.DEFAULT_BOLD);
            rim.setStyle(Paint.Style.STROKE); rim.setStrokeWidth(d);
        }

        private int base() {
            switch (style) {
                case "power": return 0xFFD9363E; case "netflix": return 0xFF050505; case "youtube": return 0xFF1A1A1A;
                case "prime": return 0xFF0B1A2E; case "disney": return 0xFF0E2266; case "spotify": return 0xFF1DB954;
                case "red": return 0xFFC0392B; case "green": return 0xFF27AE60; case "yellow": return 0xFFE0B100; case "blue": return 0xFF2D6CDF;
                case "ok": return 0xFF2B4A8C; case "mic": return 0xFF3B4C7A; default: return 0xFF2A2E38;
            }
        }

        @Override protected void onDraw(Canvas c) {
            float w = getWidth(), h = getHeight(), m = 2.5f * d;
            boolean on = key == null || s.has(key);
            RectF r = round ? new RectF(w / 2 - Math.min(w, h) / 2 + m, h / 2 - Math.min(w, h) / 2 + m, w / 2 + Math.min(w, h) / 2 - m, h / 2 + Math.min(w, h) / 2 - m) : new RectF(m, m, w - m, h - m);
            float rad = round ? r.width() / 2 : Math.min(14 * d, r.height() / 2);
            int col = base(); if (down) col = lighten(col, 0.18f);
            bg.setShader(new LinearGradient(0, r.top, 0, r.bottom, lighten(col, 0.08f), darken(col, 0.18f), Shader.TileMode.CLAMP));
            c.drawRoundRect(r, rad, rad, bg);
            rim.setColor(down ? 0x55FFFFFF : 0x22FFFFFF); c.drawRoundRect(r, rad, rad, rim);
            float cx = r.centerX(), cy = r.centerY(), ih = Math.min(r.height(), r.width()) * 0.5f;
            switch (style) {
                case "netflix": tx.setColor(0xFFE50914); tx.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD)); fitText(c, "NETFLIX", cx, cy, r.width() * 0.84f, r.height() * 0.42f, tx); return;
                case "youtube": {
                    float bh = r.height() * 0.36f, bw = bh * 1.42f, bx = cx - r.width() * 0.22f;
                    Paint p = new Paint(Paint.ANTI_ALIAS_FLAG); p.setColor(0xFFFF0000); c.drawRoundRect(new RectF(bx - bw / 2, cy - bh / 2, bx + bw / 2, cy + bh / 2), bh * 0.3f, bh * 0.3f, p);
                    Path t = new Path(); t.moveTo(bx - bh * 0.18f, cy - bh * 0.24f); t.lineTo(bx + bh * 0.26f, cy); t.lineTo(bx - bh * 0.18f, cy + bh * 0.24f); t.close(); p.setColor(0xFFFFFFFF); c.drawPath(t, p);
                    tx.setColor(0xFFFFFFFF); tx.setTypeface(Typeface.DEFAULT_BOLD); tx.setTextAlign(Paint.Align.LEFT); fitText(c, "YouTube", bx + bw / 2 + 3 * d, cy, r.right - (bx + bw / 2) - 6 * d, r.height() * 0.3f, tx); tx.setTextAlign(Paint.Align.CENTER); return; }
                case "prime": {
                    tx.setColor(0xFFFFFFFF); tx.setTypeface(Typeface.DEFAULT_BOLD); fitText(c, "prime video", cx, cy - r.height() * 0.06f, r.width() * 0.8f, r.height() * 0.3f, tx);
                    Paint p = new Paint(Paint.ANTI_ALIAS_FLAG); p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(2 * d); p.setStrokeCap(Paint.Cap.ROUND); p.setColor(0xFF1A98FF);
                    c.drawArc(new RectF(cx - r.width() * 0.26f, cy - r.height() * 0.1f, cx + r.width() * 0.1f, cy + r.height() * 0.3f), 30, 110, false, p); return; }
                case "disney": tx.setColor(0xFFFFFFFF); tx.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC)); fitText(c, "Disney+", cx, cy, r.width() * 0.8f, r.height() * 0.42f, tx); return;
                case "spotify": tx.setColor(0xFF0B0B0B); tx.setTypeface(Typeface.DEFAULT_BOLD); fitText(c, "Spotify", cx, cy, r.width() * 0.8f, r.height() * 0.36f, tx); return;
                default: break;
            }
            int fg = style.equals("yellow") ? 0xFF1A1400 : TXT;
            if (icon != null && label != null) { Icons.draw(c, icon, cx, cy - ih * 0.28f, ih * 0.82f, fg); tx.setColor(DIM); tx.setTypeface(Typeface.DEFAULT_BOLD); fitText(c, label, cx, cy + ih * 0.62f, r.width() * 0.86f, ih * 0.36f, tx); }
            else if (icon != null) Icons.draw(c, icon, cx, cy, ih * (round ? 1.0f : 1.05f), fg);
            else if (label != null) { tx.setColor(fg); tx.setTypeface(Typeface.DEFAULT_BOLD); fitText(c, label, cx, cy, r.width() * 0.8f, Math.min(r.height() * 0.42f, 22 * d), tx); }
        }

        @Override public boolean onTouchEvent(MotionEvent e) {
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN: down = true; invalidate(); if (key != null) { if (s.has(key)) rep.start(s, key, repeat); } else performClick(); return true;
                case MotionEvent.ACTION_UP: case MotionEvent.ACTION_CANCEL: down = false; rep.stop(); invalidate(); return true;
                default: return true;
            }
        }
        @Override public boolean performClick() { return super.performClick(); }
        void refresh() { setAlpha(key == null || s.has(key) ? 1f : 0.28f); invalidate(); }
    }

    // ------------------------------------------------------------------ croce direzionale + touchpad
    static final class DPad extends View {
        private final Sender s; private final float d; private int pressed = -1; private float sx, sy; private boolean moved;
        private final Repeater rep = new Repeater();
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG), tx = new Paint(Paint.ANTI_ALIAS_FLAG);
        private static final Key[] DIRS = {Key.UP, Key.RIGHT, Key.DOWN, Key.LEFT};

        DPad(Context c, Sender s) { super(c); this.s = s; d = c.getResources().getDisplayMetrics().density; tx.setTextAlign(Paint.Align.CENTER); tx.setTypeface(Typeface.DEFAULT_BOLD); }

        private float R() { return Math.min(getWidth(), getHeight()) / 2f - 4 * d; }

        @Override protected void onDraw(Canvas c) {
            float cx = getWidth() / 2f, cy = getHeight() / 2f, R = R(), ri = R * 0.4f;
            p.setStyle(Paint.Style.FILL); p.setShader(new RadialGradient(cx, cy - R * 0.3f, R * 1.3f, 0xFF353A46, 0xFF1C1F27, Shader.TileMode.CLAMP)); c.drawCircle(cx, cy, R, p); p.setShader(null);
            if (pressed >= 0 && pressed < 4) { p.setColor(0x33FFFFFF); c.drawArc(new RectF(cx - R, cy - R, cx + R, cy + R), -135 + pressed * 90, 90, true, p); }
            p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(d); p.setColor(0x26FFFFFF); c.drawCircle(cx, cy, R, p);
            String[] ic = {"up", "right", "down", "left"}; float[][] pos = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
            for (int i = 0; i < 4; i++) Icons.draw(c, ic[i], cx + pos[i][0] * R * 0.7f, cy + pos[i][1] * R * 0.7f, R * 0.32f, s.has(DIRS[i]) ? TXT : 0x55FFFFFF);
            p.setStyle(Paint.Style.FILL); p.setShader(new LinearGradient(0, cy - ri, 0, cy + ri, pressed == 4 ? 0xFF4A6FC0 : 0xFF3A5BA6, 0xFF223a73, Shader.TileMode.CLAMP)); c.drawCircle(cx, cy, ri, p); p.setShader(null);
            tx.setColor(TXT); fitText(c, "OK", cx, cy, ri, ri * 0.62f, tx);
        }

        private int sector(float x, float y) {
            float dx = x - getWidth() / 2f, dy = y - getHeight() / 2f; double dist = Math.hypot(dx, dy);
            if (dist < R() * 0.4f) return 4;
            if (Math.abs(dy) >= Math.abs(dx)) return dy < 0 ? 0 : 2; return dx > 0 ? 1 : 3;
        }

        @Override public boolean onTouchEvent(MotionEvent e) {
            float step = 30 * d;
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    getParent().requestDisallowInterceptTouchEvent(true);
                    sx = e.getX(); sy = e.getY(); moved = false; pressed = sector(sx, sy);
                    if (pressed < 4) rep.start(s, DIRS[pressed], true);
                    invalidate(); return true;
                case MotionEvent.ACTION_MOVE: {
                    float dx = e.getX() - sx, dy = e.getY() - sy;
                    if (Math.abs(dx) > step || Math.abs(dy) > step) {
                        if (!moved) { moved = true; rep.stop(); }
                        Key k = Math.abs(dx) >= Math.abs(dy) ? (dx > 0 ? Key.RIGHT : Key.LEFT) : (dy > 0 ? Key.DOWN : Key.UP);
                        s.press(k, false); sx = e.getX(); sy = e.getY();
                        pressed = k == Key.UP ? 0 : k == Key.RIGHT ? 1 : k == Key.DOWN ? 2 : 3; invalidate();
                    }
                    return true; }
                case MotionEvent.ACTION_UP:
                    rep.stop(); if (pressed == 4 && !moved) s.press(Key.OK, false);
                    pressed = -1; invalidate(); return true;
                case MotionEvent.ACTION_CANCEL: rep.stop(); pressed = -1; invalidate(); return true;
                default: return true;
            }
        }
    }

    // ------------------------------------------------------------------ bilanciere volume / canali
    static final class Rocker extends View {
        private final Sender s; private final Key up, dn; private final String label; private final float d; private int pressed = -1;
        private final Repeater rep = new Repeater(); private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG), tx = new Paint(Paint.ANTI_ALIAS_FLAG);

        Rocker(Context c, Sender s, Key up, Key dn, String label) { super(c); this.s = s; this.up = up; this.dn = dn; this.label = label; d = c.getResources().getDisplayMetrics().density; tx.setTextAlign(Paint.Align.CENTER); tx.setTypeface(Typeface.DEFAULT_BOLD); }

        @Override protected void onDraw(Canvas c) {
            float w = getWidth(), h = getHeight(), bw = Math.min(w - 8 * d, h * 0.42f), l = (w - bw) / 2, t = 4 * d, b = h - 4 * d;
            RectF r = new RectF(l, t, l + bw, b);
            p.setStyle(Paint.Style.FILL); p.setShader(new LinearGradient(l, 0, l + bw, 0, 0xFF323744, 0xFF22252e, Shader.TileMode.CLAMP)); c.drawRoundRect(r, bw / 2, bw / 2, p); p.setShader(null);
            if (pressed >= 0) { p.setColor(0x33FFFFFF); RectF hr = pressed == 0 ? new RectF(l, t, l + bw, (t + b) / 2) : new RectF(l, (t + b) / 2, l + bw, b); c.save(); c.clipRect(hr); c.drawRoundRect(r, bw / 2, bw / 2, p); c.restore(); }
            p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(d); p.setColor(0x22FFFFFF); c.drawRoundRect(r, bw / 2, bw / 2, p);
            float is = bw * 0.5f;
            Icons.draw(c, "plus", w / 2, t + bw * 0.62f, is, s.has(up) ? TXT : 0x55FFFFFF);
            Icons.draw(c, "minus", w / 2, b - bw * 0.62f, is, s.has(dn) ? TXT : 0x55FFFFFF);
            tx.setColor(DIM); fitText(c, label, w / 2, h / 2, bw * 0.8f, Math.min(bw * 0.3f, 13 * d), tx);
        }

        @Override public boolean onTouchEvent(MotionEvent e) {
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN: pressed = e.getY() < getHeight() / 2f ? 0 : 1; Key k = pressed == 0 ? up : dn; if (s.has(k)) rep.start(s, k, true); invalidate(); return true;
                case MotionEvent.ACTION_UP: case MotionEvent.ACTION_CANCEL: rep.stop(); pressed = -1; invalidate(); return true;
                default: return true;
            }
        }
    }

    static int lighten(int c, float f) { int a = c >>> 24, r = (c >> 16) & 255, g = (c >> 8) & 255, b = c & 255; return (a << 24) | ((int) (r + (255 - r) * f) << 16) | ((int) (g + (255 - g) * f) << 8) | (int) (b + (255 - b) * f); }
    static int darken(int c, float f) { int a = c >>> 24, r = (c >> 16) & 255, g = (c >> 8) & 255, b = c & 255; return (a << 24) | ((int) (r * (1 - f)) << 16) | ((int) (g * (1 - f)) << 8) | (int) (b * (1 - f)); }
}
