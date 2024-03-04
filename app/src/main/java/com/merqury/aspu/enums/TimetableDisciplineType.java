package com.merqury.aspu.enums;

import android.graphics.Color;

public enum TimetableDisciplineType {
    lec("Лекция", "#f7e3e7"),
    prac("Практика", "#e8e582"),
    exam("Экзамен", "#f6574c"),
    lab("Лаб. работа", "#d3e2d0"),
    hol("Выходной"),
    cred("Зачет", "#C7AB93"),
    fepo("ФЭПО", "#ea48d0"),
    cons("Консультация"),
    cours("Курсовая", "#CA90D7"),
    none("");

    public final String localizedName;
    public final String colorHash;

    TimetableDisciplineType(String localizedName, String colorHash) {
        this.localizedName = localizedName;
        this.colorHash = colorHash;
    }
    TimetableDisciplineType(String localizedName) {
        this.localizedName = localizedName;
        this.colorHash = "#edeef0";
    }
    public int getColorInt(){
        return Color.parseColor(colorHash);
    }
}
