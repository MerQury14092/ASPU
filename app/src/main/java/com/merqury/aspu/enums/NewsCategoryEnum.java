package com.merqury.aspu.enums;

import com.merqury.aspu.R;

public enum NewsCategoryEnum {
    agpu("Общие", R.drawable.agpu_logo),
    ipimif("ИПИМиФ", R.drawable.ipimif_logo),
    iriif("ИРИиФ", R.drawable.iriif_logo),
    istfak("ИстФак", R.drawable.istfak_logo),
    spf("СПФ", R.drawable.spf_logo),
    fdino("ФДиНО", R.drawable.fdino_logo),
    educationaltechnopark("Технопарк", R.drawable.technopark_logo),
    PedagogicalQuantorium("Кванториум", R.drawable.quantorium_logo),
    fteid("ФТЭиД", R.drawable.fteid_logo);
    public final String localizedName;
    public final int logo;
    NewsCategoryEnum(String name, int logo){
        localizedName = name;
        this.logo = logo;
    }
}
