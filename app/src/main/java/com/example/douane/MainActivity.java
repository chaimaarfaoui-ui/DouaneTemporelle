package com.example.douane;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends Activity {

    private EditText etNom, etNaissance, etDestination;
    private Spinner spMotif;
    private Button btnInspecter;

    // آلة الزمن: عجلة السنوات (من 1000 إلى 3000)
    private static final int ANNEE_MIN = 1000;
    private static final int ANNEE_MAX = 3000;
    private RecyclerView rvAnnees;
    private LinearLayoutManager lm;
    // علم لمنع الحلقة بين تمرير العجلة وتعديل خانة النص
    private boolean miseAJourInterne = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ربط عناصر الواجهة
        etNom = findViewById(R.id.et_nom_ca);
        etNaissance = findViewById(R.id.et_naissance_ca);
        etDestination = findViewById(R.id.et_destination_ca);
        spMotif = findViewById(R.id.sp_motif_ca);
        btnInspecter = findViewById(R.id.btn_inspecter_ca);

        // تعبئة القائمة المنسدلة بالأسباب الثلاثة
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.motifs, R.layout.spinner_item_ca);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_ca);
        spMotif.setAdapter(adapter);

        btnInspecter.setOnClickListener(v -> inspecter());

        initialiserMachine();

        // تدوير الترسين بشكل مستمر وفي اتجاهين متعاكسين (حركة جمالية)
        animerEngrenage(findViewById(R.id.iv_engrenage1_ca), 360f, 20000);
        animerEngrenage(findViewById(R.id.iv_engrenage2_ca), -360f, 12000);
    }

    // تهيئة عجلة السنوات التي تتحرك وتتوقف على السنة المطلوبة
    private void initialiserMachine() {
        rvAnnees = findViewById(R.id.rv_annees_ca);
        lm = new LinearLayoutManager(this);
        rvAnnees.setLayoutManager(lm);
        final float dp = getResources().getDisplayMetrics().density;
        final int hItem = (int) (60 * dp);
        // حشوة علوية وسفلية حتى يتوسط العنصر الأول والأخير العجلة
        int pad = (int) (60 * dp);
        rvAnnees.setPadding(0, pad, 0, pad);
        rvAnnees.setAdapter(new AnneesAdapter(hItem));
        // التقاط العجلة على السنة الأقرب إلى الوسط
        new LinearSnapHelper().attachToRecyclerView(rvAnnees);

        // الموضع الابتدائي: سنة 2050
        lm.scrollToPositionWithOffset(2050 - ANNEE_MIN, 0);

        rvAnnees.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
                // تكبير السنة الوسطى وتصغير البقية
                effetProfondeur();
            }

            @Override
            public void onScrollStateChanged(RecyclerView rv, int etat) {
                // عند التوقف نكتب السنة المختارة في خانة النص
                if (etat == RecyclerView.SCROLL_STATE_IDLE) {
                    int annee = anneeCentrale();
                    if (annee > 0) {
                        miseAJourInterne = true;
                        etDestination.setText(String.valueOf(annee));
                        miseAJourInterne = false;
                    }
                }
            }
        });

        // عند كتابة سنة في الخانة تتحرك العجلة بسلاسة نحوها
        etDestination.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            public void onTextChanged(CharSequence s, int a, int b, int c) {}
            public void afterTextChanged(Editable e) {
                if (miseAJourInterne || e.length() != 4) return;
                int annee = Integer.parseInt(e.toString());
                if (annee >= ANNEE_MIN && annee <= ANNEE_MAX) {
                    allerVers(annee);
                }
            }
        });

        rvAnnees.post(() -> {
            effetProfondeur();
            miseAJourInterne = true;
            etDestination.setText("2050");
            miseAJourInterne = false;
        });
    }

    // تمرير سلس نحو سنة معينة
    private void allerVers(int annee) {
        int pos = annee - ANNEE_MIN;
        int actuel = lm.findFirstVisibleItemPosition();
        // إذا كانت المسافة بعيدة نقفز قريبا من الهدف أولا ثم نكمل بحركة سلسة
        if (Math.abs(pos - actuel) > 60) {
            int proche = pos > actuel ? pos - 40 : pos + 40;
            lm.scrollToPositionWithOffset(proche, 0);
        }
        rvAnnees.smoothScrollToPosition(pos);
    }

    // السنة الموجودة في وسط العجلة
    private int anneeCentrale() {
        int centre = rvAnnees.getHeight() / 2;
        for (int i = 0; i < rvAnnees.getChildCount(); i++) {
            View c = rvAnnees.getChildAt(i);
            if (c.getTop() <= centre && c.getBottom() >= centre) {
                return ANNEE_MIN + rvAnnees.getChildAdapterPosition(c);
            }
        }
        return -1;
    }

    // تأثير العمق: كلما ابتعدت السنة عن الوسط صغرت وخفتت
    private void effetProfondeur() {
        float centre = rvAnnees.getHeight() / 2f;
        for (int i = 0; i < rvAnnees.getChildCount(); i++) {
            View c = rvAnnees.getChildAt(i);
            float dist = Math.abs((c.getTop() + c.getBottom()) / 2f - centre);
            float f = Math.min(1f, dist / (rvAnnees.getHeight() / 2f));
            c.setScaleX(1f - 0.45f * f);
            c.setScaleY(1f - 0.45f * f);
            c.setAlpha(1f - 0.65f * f);
        }
    }

    // محول يعرض كل السنوات كنصوص
    private static class AnneesAdapter extends RecyclerView.Adapter<AnneesAdapter.VH> {
        private final int hauteur;
        AnneesAdapter(int hauteur) { this.hauteur = hauteur; }

        static class VH extends RecyclerView.ViewHolder {
            TextView tv;
            VH(TextView t) { super(t); tv = t; }
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int type) {
            TextView t = new TextView(parent.getContext());
            t.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, hauteur));
            t.setGravity(Gravity.CENTER);
            t.setTextSize(32);
            t.setTypeface(android.graphics.Typeface.SERIF, android.graphics.Typeface.BOLD);
            t.setTextColor(0xFFE3B65A);
            return new VH(t);
        }

        @Override
        public void onBindViewHolder(VH h, int position) {
            h.tv.setText(String.valueOf(ANNEE_MIN + position));
        }

        @Override
        public int getItemCount() { return ANNEE_MAX - ANNEE_MIN + 1; }
    }

    // عرض سجل المسافرين كلما رجعنا إلى هذه الشاشة
    @Override
    protected void onResume() {
        super.onResume();
        ((android.widget.TextView) findViewById(R.id.tv_historique_ca))
                .setText(Historique.dernier());
    }

    // دالة تدوير ترس: الزاوية النهائية والمدة بالميلي ثانية
    private void animerEngrenage(android.view.View v, float angle, long duree) {
        android.animation.ObjectAnimator rot =
                android.animation.ObjectAnimator.ofFloat(v, "rotation", 0f, angle);
        rot.setDuration(duree);
        rot.setRepeatCount(android.animation.ObjectAnimator.INFINITE);
        rot.setInterpolator(new android.view.animation.LinearInterpolator());
        rot.start();
    }

    private void inspecter() {
        String nom = etNom.getText().toString().trim();
        String naiss = etNaissance.getText().toString().trim();
        String dest = etDestination.getText().toString().trim();

        // التحقق من أن كل الحقول مملوءة
        if (nom.isEmpty() || naiss.length() != 4 || dest.length() != 4) {
            Toast.makeText(this, "Veuillez remplir tous les champs (années à 4 chiffres)",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // تحقق إضافي: السنوات بين 1000 و 3000 وسنة الوجهة تختلف عن سنة الميلاد
        int n = Integer.parseInt(naiss);
        int d = Integer.parseInt(dest);
        if (n < 1000 || n > 3000 || d < 1000 || d > 3000) {
            etNaissance.setError("Année entre 1000 et 3000");
            return;
        }
        if (n == d) {
            etDestination.setError("Destination identique à la naissance");
            return;
        }

        // Intent صريح نحو النشاط 2 مع المفاتيح المبنية بالدالة الثابتة
        Intent intent = new Intent(this, VerificationActivity.class);
        intent.putExtra(Cles.nomVoyageur(), nom);
        intent.putExtra(Cles.anneeNaissance(), Integer.parseInt(naiss));
        intent.putExtra(Cles.anneeDest(), Integer.parseInt(dest));
        intent.putExtra(Cles.motif(), spMotif.getSelectedItem().toString());
        startActivity(intent);
    }
}
