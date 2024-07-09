package com.merqury.aspu.ui.navfragments.marks;

public enum ControlType {
    exam("Экзамены", "Экзамен"),
    cred("Зачёты", "Зачет"),
    cours("Курсовая работа", "Курсовая работа"),
    prac("Практика", "Практика"),
    none("N/A", "N/A");

    public final String label;
    public final String controlForm;

    ControlType(String label, String controlForm) {
        this.label = label;
        this.controlForm = controlForm;
    }
}
