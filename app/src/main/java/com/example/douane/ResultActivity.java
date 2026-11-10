package com.example.douane;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class ResultActivity extends Activity {

    private final Fx fx = new Fx();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        boolean approuve = getIntent().getBooleanExtra(Cles.verdict(), false);
        String nom = getIntent().getStringExtra(Cles.nomVoyageur());
        if (nom == null) nom = "";
        int naissance = getIntent().getIntExtra(Cles.anneeNaissance(), 0);
        int destination = getIntent().getIntExtra(Cles.anneeDest(), 0);

        ImageView photo = findViewById(R.id.iv_photo_ca);
        View ticket = findViewById(R.id.fl_ticket_ca);
        View root = findViewById(R.id.root_result_ca);
        TextView tvTitre = findViewById(R.id.tv_titre_verdict_ca);
        TextView tvMessage = findViewById(R.id.tv_message_ca);
        TextView tvSous = findViewById(R.id.tv_sous_ca);
        TextView tampon = findViewById(R.id.tv_tampon_ca);
        View spirale = findViewById(R.id.iv_spirale_ca);
        String grand = nom.toUpperCase(Locale.ROOT);

        // تغيير الخلفية حسب الحكم: مغامرة بطولية عند الموافقة، شرطة الزمن عند الرفض
        if (approuve) {
            root.setBackgroundResource(R.drawable.bg_verdict_ok);
            tvTitre.setText("TIME JUMP AUTHORIZED!");
            tvMessage.setText("Bon voyage " + nom + " !");
            tvSous.setText("YOUR DESTINY AWAITS, " + grand + "!\nTHE TIMELINE IS YOURS TO EXPLORE!");
            photo.setImageResource(R.drawable.art_hero);
            ((TextView) findViewById(R.id.tv_ticket_infos_ca))
                    .setText(nom + "\n" + naissance + " ➜ " + destination);
            tampon.setText("APPROVED");
            tampon.setBackgroundResource(R.drawable.bg_stamp_ok);
            // البطل يطفو أمام البوابة والبوابة تدور
            fx.boucle(photo, "translationY", 0f, -16f, 1200, true);
            fx.boucle(spirale, "rotation", 0f, 360f, 14000, false);
            // انفجار نجوم احتفالي
            root.post(this::exploserEtoiles);
        } else {
            root.setBackgroundResource(R.drawable.bg_verdict_ko);
            tvTitre.setText("TEMPORAL ARREST!");
            tvTitre.setTextColor(0xFFF3E5C0);
            tvMessage.setText(nom + " est en état d'arrestation temporelle");
            tvSous.setText("TIMELINE VIOLATION DETECTED!\n" + grand
                    + " HAS BEEN FLAGGED BY THE TIME POLICE!\nYOUR TIMELINE PRIVILEGES HAVE BEEN SUSPENDED!");
            photo.setImageResource(R.drawable.art_police);
            ticket.setVisibility(View.GONE);
            tampon.setText("REJECTED");
            tampon.setBackgroundResource(R.drawable.bg_stamp_ko);
            // أشرطة تحذير ووميض أحمر واهتزاز الشرطي
            findViewById(R.id.v_ruban1_ca).setVisibility(View.VISIBLE);
            findViewById(R.id.v_ruban2_ca).setVisibility(View.VISIBLE);
            View sirene = findViewById(R.id.v_sirene_ca);
            sirene.setVisibility(View.VISIBLE);
            fx.boucle(sirene, "alpha", 0.1f, 0.9f, 500, true);
            fx.boucle(spirale, "rotation", 0f, -360f, 6000, false);
            fx.boucle(photo, "rotation", -2f, 2f, 160, true);
            tvMessage.animate().translationXBy(25f).setDuration(80).withEndAction(() ->
                    tvMessage.animate().translationX(0f).setDuration(200).start()).start();
        }

        // حفظ الحكم في سجل المسافرين
        Historique.ajouter(nom, approuve);

        // ختم يضرب الوثيقة بحركة تكبير مع ارتداد
        tampon.setScaleX(3f);
        tampon.setScaleY(3f);
        tampon.setAlpha(0f);
        tampon.animate().scaleX(1f).scaleY(1f).alpha(1f).setStartDelay(400)
                .setDuration(500).setInterpolator(new OvershootInterpolator(2f)).start();

        // الرجوع إلى MainActivity مع غلق كل النشاطات السابقة
        findViewById(R.id.btn_nouveau_ca).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.portal_in, R.anim.portal_out);
            finish();
        });
    }

    // انفجار 14 نجمة من وسط الشاشة نحو الخارج
    private void exploserEtoiles() {
        FrameLayout scene = findViewById(R.id.fl_scene_ca);
        float d = getResources().getDisplayMetrics().density;
        for (int i = 0; i < 14; i++) {
            ImageView s = new ImageView(this);
            s.setImageResource(R.drawable.art_star);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) (30 * d), (int) (30 * d));
            lp.gravity = Gravity.CENTER;
            scene.addView(s, lp);
            double ang = i * 2 * Math.PI / 14;
            float dist = (130 + 70 * (i % 3)) * d;
            s.animate().translationX((float) Math.cos(ang) * dist).translationY((float) Math.sin(ang) * dist)
                    .rotation(360f).alpha(0f).setDuration(1300)
                    .withEndAction(() -> scene.removeView(s)).start();
        }
    }

    @Override
    protected void onDestroy() {
        fx.arreter();
        super.onDestroy();
    }
}
