package app.telecomandoir;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/** Icone vettoriali disegnate a mano su una griglia 24x24, scalate al rettangolo richiesto. */
final class Icons {
    private Icons() {}

    static void draw(Canvas c, String name, float cx, float cy, float size, int color) {
        float s = size / 24f;
        c.save(); c.translate(cx - size / 2f, cy - size / 2f); c.scale(s, s);
        Paint st = new Paint(Paint.ANTI_ALIAS_FLAG); st.setStyle(Paint.Style.STROKE); st.setStrokeWidth(2.2f); st.setStrokeCap(Paint.Cap.ROUND); st.setStrokeJoin(Paint.Join.ROUND); st.setColor(color);
        Paint fi = new Paint(Paint.ANTI_ALIAS_FLAG); fi.setStyle(Paint.Style.FILL); fi.setColor(color);
        Path p = new Path();
        switch (name) {
            case "power": c.drawArc(new RectF(4, 4, 20, 20), -60, 300, false, st); c.drawLine(12, 2.5f, 12, 11, st); break;
            case "mute": p.moveTo(3, 9); p.lineTo(7, 9); p.lineTo(12, 4.5f); p.lineTo(12, 19.5f); p.lineTo(7, 15); p.lineTo(3, 15); p.close(); c.drawPath(p, st); c.drawLine(16, 9, 21, 15, st); c.drawLine(21, 9, 16, 15, st); break;
            case "home": p.moveTo(3, 11); p.lineTo(12, 3.5f); p.lineTo(21, 11); c.drawPath(p, st); p.reset(); p.moveTo(5.5f, 9.5f); p.lineTo(5.5f, 20); p.lineTo(18.5f, 20); p.lineTo(18.5f, 9.5f); c.drawPath(p, st); c.drawLine(10, 20, 10, 14.5f, st); c.drawLine(14, 14.5f, 14, 20, st); c.drawLine(10, 14.5f, 14, 14.5f, st); break;
            case "back": p.moveTo(9, 5); p.lineTo(4, 10); p.lineTo(9, 15); c.drawPath(p, st); p.reset(); p.moveTo(4.5f, 10); p.lineTo(14, 10); p.cubicTo(18, 10, 20, 12.5f, 20, 15); p.cubicTo(20, 17.5f, 18, 20, 14, 20); p.lineTo(10, 20); c.drawPath(p, st); break;
            case "menu": c.drawLine(4, 6, 20, 6, st); c.drawLine(4, 12, 20, 12, st); c.drawLine(4, 18, 20, 18, st); break;
            case "gear": c.drawCircle(12, 12, 3.2f, st); c.drawCircle(12, 12, 7, st);
                for (int i = 0; i < 8; i++) { double a = Math.PI / 4 * i; c.drawLine((float) (12 + 7 * Math.cos(a)), (float) (12 + 7 * Math.sin(a)), (float) (12 + 9.6 * Math.cos(a)), (float) (12 + 9.6 * Math.sin(a)), st); } break;
            case "input": c.drawRoundRect(new RectF(3, 5, 21, 17), 2, 2, st); c.drawLine(8, 21, 16, 21, st); p.moveTo(9, 11); p.lineTo(15, 11); c.drawPath(p, st); p.reset(); p.moveTo(12.5f, 8.5f); p.lineTo(15, 11); p.lineTo(12.5f, 13.5f); c.drawPath(p, st); break;
            case "mic": c.drawRoundRect(new RectF(8.5f, 2.5f, 15.5f, 14.5f), 3.5f, 3.5f, st); c.drawArc(new RectF(5, 6, 19, 18), 0, 180, false, st); c.drawLine(12, 18, 12, 21.5f, st); c.drawLine(8.5f, 21.5f, 15.5f, 21.5f, st); break;
            case "play": p.moveTo(7, 4.5f); p.lineTo(19, 12); p.lineTo(7, 19.5f); p.close(); c.drawPath(p, fi); break;
            case "pause": c.drawRoundRect(new RectF(6, 5, 10, 19), 1, 1, fi); c.drawRoundRect(new RectF(14, 5, 18, 19), 1, 1, fi); break;
            case "stop": c.drawRoundRect(new RectF(6, 6, 18, 18), 2, 2, fi); break;
            case "rew": p.moveTo(12, 6); p.lineTo(3, 12); p.lineTo(12, 18); p.close(); p.moveTo(21, 6); p.lineTo(12, 12); p.lineTo(21, 18); p.close(); c.drawPath(p, fi); break;
            case "ffwd": p.moveTo(12, 6); p.lineTo(21, 12); p.lineTo(12, 18); p.close(); p.moveTo(3, 6); p.lineTo(12, 12); p.lineTo(3, 18); p.close(); c.drawPath(p, fi); break;
            case "info": c.drawCircle(12, 12, 9, st); c.drawLine(12, 11, 12, 17, st); c.drawCircle(12, 7.5f, 1.3f, fi); break;
            case "guide": c.drawRoundRect(new RectF(3.5f, 5, 20.5f, 20), 2, 2, st); c.drawLine(3.5f, 10, 20.5f, 10, st); c.drawLine(8, 3, 8, 7, st); c.drawLine(16, 3, 16, 7, st); c.drawCircle(8.5f, 14.5f, 1.2f, fi); c.drawCircle(12, 14.5f, 1.2f, fi); c.drawCircle(15.5f, 14.5f, 1.2f, fi); break;
            case "list": for (int i = 0; i < 3; i++) { float y = 6 + i * 6; c.drawCircle(5, y, 1.3f, fi); c.drawLine(9, y, 20, y, st); } break;
            case "apps": for (int i = 0; i < 2; i++) for (int j = 0; j < 2; j++) c.drawRoundRect(new RectF(4 + i * 9, 4 + j * 9, 11 + i * 9, 11 + j * 9), 2, 2, st); break;
            case "dots": c.drawCircle(5, 12, 2, fi); c.drawCircle(12, 12, 2, fi); c.drawCircle(19, 12, 2, fi); break;
            case "sub": c.drawRoundRect(new RectF(2.5f, 5, 21.5f, 19), 2.5f, 2.5f, st); c.drawLine(6, 13, 11, 13, st); c.drawLine(13, 13, 18, 13, st); c.drawLine(6, 16, 15, 16, st); break;
            case "exit": c.drawLine(6, 6, 18, 18, st); c.drawLine(18, 6, 6, 18, st); break;
            case "text": c.drawRoundRect(new RectF(3, 4, 21, 20), 2, 2, st); c.drawLine(7, 9, 17, 9, st); c.drawLine(12, 9, 12, 16.5f, st); break;
            case "sleep": p.moveTo(15, 3.5f); p.cubicTo(9, 4, 5, 8.5f, 5, 13); p.cubicTo(5, 18, 9, 21, 13.5f, 21); p.cubicTo(17, 21, 19.5f, 19, 20.5f, 16.5f); p.cubicTo(14, 18, 9.5f, 11, 15, 3.5f); c.drawPath(p, st); break;
            case "up": p.moveTo(5, 15); p.lineTo(12, 8); p.lineTo(19, 15); c.drawPath(p, st); break;
            case "down": p.moveTo(5, 9); p.lineTo(12, 16); p.lineTo(19, 9); c.drawPath(p, st); break;
            case "left": p.moveTo(15, 5); p.lineTo(8, 12); p.lineTo(15, 19); c.drawPath(p, st); break;
            case "right": p.moveTo(9, 5); p.lineTo(16, 12); p.lineTo(9, 19); c.drawPath(p, st); break;
            case "plus": c.drawLine(12, 5, 12, 19, st); c.drawLine(5, 12, 19, 12, st); break;
            case "minus": c.drawLine(5, 12, 19, 12, st); break;
            default: break;
        }
        c.restore();
    }
}
