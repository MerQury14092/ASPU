package com.merqury.aspu.enums;

public enum NewsCategoryEnum {
    GENERAL("Общие"),
    IPIMiF("ИПИМиФ"),
    IRiIF("ИРИиФ"),
    IstFak("ИстФак"),
    SPF("СПФ"),
    FDiNO("ФДиНО"),
    FTEiD("ФТЭиД");
    public final String localizedName;
    NewsCategoryEnum(String name){
        localizedName = name;
    }
}
