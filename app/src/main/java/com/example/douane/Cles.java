package com.example.douane;

// كلاس مساعد لبناء مفاتيح putExtra انطلاقا من رقم بطاقة الطالب
public class Cles {

    // آخر 4 أرقام من بطاقة الطالب
    public static final String MATRICULE = "1709";

    // دالة ثابتة تبني المفتاح: matricule + "_" + nom
    public static String construire(String matricule, String nom) {
        return matricule + "_" + nom;
    }

    // مفاتيح جاهزة (غير مكتوبة بشكل ثابت، بل مبنية بالدالة)
    public static String nomVoyageur()  { return construire(MATRICULE, "NOM_VOYAGEUR"); }
    public static String anneeNaissance(){ return construire(MATRICULE, "ANNEE_NAISSANCE"); }
    public static String anneeDest()    { return construire(MATRICULE, "ANNEE_DESTINATION"); }
    public static String motif()        { return construire(MATRICULE, "MOTIF"); }
    public static String verdict()      { return construire(MATRICULE, "VERDICT"); }
}
