package com.example.douane;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

// كلاس مساعد للحركات المتكررة؛ نحتفظ بها لإيقافها عند إغلاق الشاشة
public class Fx {
    private final ArrayList<ObjectAnimator> liste = new ArrayList<>();

    // حركة لا نهائية على خاصية معينة (مثل الدوران أو الطفو)
    public ObjectAnimator boucle(View v, String prop, float de, float vers, long duree, boolean aller_retour) {
        ObjectAnimator a = ObjectAnimator.ofFloat(v, prop, de, vers);
        a.setDuration(duree);
        a.setRepeatCount(ObjectAnimator.INFINITE);
        a.setRepeatMode(aller_retour ? ObjectAnimator.REVERSE : ObjectAnimator.RESTART);
        if (!aller_retour) a.setInterpolator(new LinearInterpolator());
        a.start();
        liste.add(a);
        return a;
    }

    // إيقاف كل الحركات
    public void arreter() {
        for (ObjectAnimator a : liste) a.cancel();
        liste.clear();
    }
}
