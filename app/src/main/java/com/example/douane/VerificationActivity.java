package com.example.douane;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class VerificationActivity extends Activity {

    private String nom;
    private int naissance, destination;
    private final Fx fx = new Fx();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        // استرجاع البيانات باستعمال نفس المفاتيح المبنية بالدالة الثابتة
        Intent in = getIntent();
        nom = in.getStringExtra(Cles.nomVoyageur());
        naissance = in.getIntExtra(Cles.anneeNaissance(), 0);
        destination = in.getIntExtra(Cles.anneeDest(), 0);
        String motif = in.getStringExtra(Cles.motif());

        // العمر الزمني = سنة الوجهة - سنة الميلاد
        int ageTemporel = destination - naissance;

        ((TextView) findViewById(R.id.tv_nom_ca)).setText("Nom : " + nom);
        ((TextView) findViewById(R.id.tv_age_ca)).setText("Âge temporel : " + ageTemporel + " ans");
        ((TextView) findViewById(R.id.tv_annees_ca)).setText("Trajet : " + naissance + " ➜ " + destination);
        ((TextView) findViewById(R.id.tv_dest_ca)).setText("Année de destination : " + destination);
        ((TextView) findViewById(R.id.tv_motif_ca)).setText("Motif : " + motif);

        // رقم تفتيش مشتق من البيانات (لمسة جمالية)
        int numero = Math.abs((nom + naissance + destination).hashCode()) % 10000;
        ((TextView) findViewById(R.id.tv_numero_ca))
                .setText(String.format("INSPECTION No. %04d", numero));

        // درجة خطر المفارقة (0 إلى 100) حسب السبب وحجم القفزة
        int risque = calculerRisque(motif, ageTemporel);
        ((TextView) findViewById(R.id.tv_risque_ca)).setText("⚠ Paradox risk: " + risque + "%");
        ProgressBar pb = findViewById(R.id.pb_risque_ca);
        pb.setProgress(risque);
        // لون الشريط: أخضر ثم برتقالي ثم أحمر حسب الخطر
        int couleur = risque < 35 ? 0xFF2F6B4F : (risque < 70 ? 0xFFE8A93A : 0xFFD9482B);
        pb.setProgressTintList(android.content.res.ColorStateList.valueOf(couleur));

        Button btnApprouver = findViewById(R.id.btn_approuver_ca);
        Button btnRefuser = findViewById(R.id.btn_refuser_ca);
        TextView statut = findViewById(R.id.tv_statut_ca);

        // القيد: إذا كان السبب "Modification du passé" والفرق عدد أولي => تعطيل زر الموافقة
        boolean bloque = "Modification du passé".equals(motif) && estPremier(Math.abs(ageTemporel));
        if (bloque) {
            btnApprouver.setEnabled(false);
        }

        btnApprouver.setOnClickListener(v -> lancerVerdict(true));
        btnRefuser.setOnClickListener(v -> lancerVerdict(false));

        // حركة الختم "سري" عند فتح الملف
        View tampon = findViewById(R.id.tv_classifie_ca);
        tampon.setScaleX(3f);
        tampon.setScaleY(3f);
        tampon.setAlpha(0f);
        tampon.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(500).setStartDelay(250).start();

        // طفو خفيف لصورة الضابط
        fx.boucle(findViewById(R.id.img_passeport_ca), "translationY", 0f, -12f, 1400, true);

        // الماسح الضوئي: شريط يمر فوق الوثيقة مرتين ثم تظهر النتيجة
        statut.setText("SCANNING TIMELINE…");
        final View rl = findViewById(R.id.rl_dossier_ca);
        final View scan = findViewById(R.id.v_scan_ca);
        rl.post(() -> {
            ObjectAnimator a = ObjectAnimator.ofFloat(scan, "translationY",
                    0f, rl.getHeight() - scan.getHeight());
            a.setDuration(900);
            a.setRepeatCount(3);
            a.setRepeatMode(ObjectAnimator.REVERSE);
            a.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    scan.setVisibility(View.GONE);
                    statut.setText(bloque ? "⛔ PARADOX LOCK: approval disabled"
                            : (risque >= 70 ? "⚠ HIGH RISK — decide wisely" : "✔ SCAN COMPLETE — awaiting verdict"));
                    // نبض لزر الموافقة إذا كان مفعلا
                    if (!bloque) fx.boucle(btnApprouver, "scaleX", 1f, 1.04f, 700, true);
                }
            });
            a.start();
        });
    }

    @Override
    protected void onDestroy() {
        fx.arreter();
        super.onDestroy();
    }

    // درجة الخطر: قيمة أساسية حسب السبب + جزء من حجم القفزة (بحد أقصى 100)
    private int calculerRisque(String motif, int diff) {
        int base = 10;
        if ("Modification du passé".equals(motif)) base = 60;
        else if ("Fuite".equals(motif)) base = 40;
        return Math.min(100, base + Math.min(40, Math.abs(diff) / 5));
    }

    // دالة التحقق من العدد الأولي
    private boolean estPremier(int n) {
        if (n < 2) return false;
        for (int i = 2; (long) i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    // إطلاق النشاط 3 مع الحكم (boolean) واسم المسافر
    private void lancerVerdict(boolean approuve) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(Cles.verdict(), approuve);
        intent.putExtra(Cles.nomVoyageur(), nom);
        // السنوات لعرضها في بطاقة السفر
        intent.putExtra(Cles.anneeNaissance(), naissance);
        intent.putExtra(Cles.anneeDest(), destination);
        startActivity(intent);
        overridePendingTransition(R.anim.portal_in, R.anim.portal_out);
    }
}
