package com.merqury.aspu.enums;

import com.merqury.aspu.R;

public enum NewsCategoryEnum {
    agpu("Общие", R.drawable.agpu_logo),
    fmf("ФизМат", R.drawable.ipimif_logo),
    filfak("ФилФак", R.drawable.iriif_logo),
    istfak("ИстФак", R.drawable.istfak_logo),
    ppf("ППФ", R.drawable.fdino_logo),
    ftifk("ФТиФК", R.drawable.fteid_logo),
    educationaltechnopark("Технопарк", R.drawable.technopark_logo),
    PedagogicalQuantorium("Кванториум", R.drawable.quantorium_logo);
    public final String localizedName;
    public final int logo;
    NewsCategoryEnum(String name, int logo){
        localizedName = name;
        this.logo = logo;
    }
}
