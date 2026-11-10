package com.example.douane;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

// فيو مخصص يرسم نجوما صغيرة تتلألأ وتطفو ببطء (خفيف على الأداء)
public class TwinkleView extends View {
    private static final int N = 34;
    private final float[] fx = new float[N], fy = new float[N], taille = new float[N], phase = new float[N];
    private final int[] couleur = new int[N];
    private final Paint pinceau = new Paint(Paint.ANTI_ALIAS_FLAG);

    public TwinkleView(Context c, AttributeSet a) {
        super(c, a);
        Random r = new Random(11);
        int[] palette = {0xFFF3E5C0, 0xFFE8A93A, 0xFFA0C8DC};
        for (int i = 0; i < N; i++) {
            fx[i] = r.nextFloat();
            fy[i] = r.nextFloat();
            taille[i] = 3f + r.nextFloat() * 7f;
            phase[i] = r.nextFloat() * 6.28f;
            couleur[i] = palette[r.nextInt(palette.length)];
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float t = SystemClock.uptimeMillis() / 1000f;
        float d = getResources().getDisplayMetrics().density;
        int w = getWidth(), h = getHeight();
        for (int i = 0; i < N; i++) {
            float lueur = 0.5f + 0.5f * (float) Math.sin(t * 2f + phase[i]);
            // طفو بطيء نحو الأعلى مع العودة من الأسفل
            float y = ((fy[i] - t * 0.01f * (1 + i % 3)) % 1f + 1f) % 1f * h;
            float s = taille[i] * d * (0.6f + 0.6f * lueur);
            pinceau.setColor(couleur[i]);
            pinceau.setAlpha((int) (70 + 185 * lueur));
            // نجمة بأربع رؤوس: مستطيلان رفيعان متقاطعان
            canvas.drawRect(fx[i] * w - s, y - s * 0.16f, fx[i] * w + s, y + s * 0.16f, pinceau);
            canvas.drawRect(fx[i] * w - s * 0.16f, y - s, fx[i] * w + s * 0.16f, y + s, pinceau);
        }
        postInvalidateDelayed(45);
    }
}
