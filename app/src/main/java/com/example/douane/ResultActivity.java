package com.example.douane;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        boolean approuve = getIntent().getBooleanExtra(Cles.verdict(), false);
        String nom = getIntent().getStringExtra(Cles.nomVoyageur());

        int naissance = getIntent().getIntExtra(Cles.anneeNaissance(), 0);
        int destination = getIntent().getIntExtra(Cles.anneeDest(), 0);
        android.widget.ImageView photo = findViewById(R.id.iv_photo_ca);
        View ticket = findViewById(R.id.fl_ticket_ca);

        View root = findViewById(R.id.root_result_ca);
        TextView tvMessage = findViewById(R.id.tv_message_ca);

        // تغيير لون الخلفية حسب الحكم: أخضر للموافقة وأحمر للرفض
        if (approuve) {
            root.setBackgroundResource(R.drawable.bg_verdict_ok);
            tvMessage.setText("Bon voyage " + nom + " !");
            // صورة المسافر وتذكرة السفر عند الموافقة
            photo.setImageResource(R.drawable.img_astronaute);
            ((TextView) findViewById(R.id.tv_ticket_infos_ca))
                    .setText(nom + "\n" + naissance + " → " + destination);
            // حركة طفو خفيفة لصورة المسافر
            photo.animate().translationY(-18f).setDuration(1200).withEndAction(() ->
                    photo.animate().translationY(0f).setDuration(1200).start()).start();
        } else {
            root.setBackgroundResource(R.drawable.bg_verdict_ko);
            tvMessage.setText(nom + " est en état d'arrestation temporelle");
            // الساعة الذائبة عند الرفض وإخفاء التذكرة
            photo.setImageResource(R.drawable.img_horloge_fondue);
            ticket.setVisibility(View.GONE);
        }

        // حفظ الحكم في سجل المسافرين
        Historique.ajouter(nom, approuve);

        // ختم يظهر بحركة تكبير؛ ويهتز الرسالة عند الرفض
        TextView tampon = findViewById(R.id.tv_tampon_ca);
        tampon.setText(approuve ? "APPROUVÉ" : "REFUSÉ");
        tampon.setScaleX(3f);
        tampon.setScaleY(3f);
        tampon.setAlpha(0f);
        tampon.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(600).start();
        if (!approuve) {
            tvMessage.animate().translationXBy(25f).setDuration(80).withEndAction(() ->
                    tvMessage.animate().translationX(0f).setDuration(200).start()).start();
        }

        // الرجوع إلى MainActivity مع غلق كل النشاطات السابقة
        findViewById(R.id.btn_nouveau_ca).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
