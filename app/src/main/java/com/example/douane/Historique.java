package com.example.douane;

import java.util.ArrayList;

// كلاس لحفظ سجل المسافرين وأحكامهم
public class Historique {
    private static final ArrayList<String> entrees = new ArrayList<>();

    public static void ajouter(String nom, boolean approuve) {
        entrees.add((approuve ? "✔ " : "✖ ") + nom);
    }

    // إرجاع آخر 5 مسافرين كنص
    public static String dernier() {
        if (entrees.isEmpty()) return "No travelers yet — be the first!";
        StringBuilder sb = new StringBuilder();
        int debut = Math.max(0, entrees.size() - 5);
        for (int i = entrees.size() - 1; i >= debut; i--) {
            sb.append(entrees.get(i)).append("\n");
        }
        return sb.toString().trim();
    }
}
