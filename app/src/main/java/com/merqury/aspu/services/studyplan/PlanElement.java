package com.merqury.aspu.services.studyplan;

import com.merqury.aspu.ui.navfragments.marks.ControlType;

public class PlanElement {
    public String name;
    public int hours;
    public int semester;
    public int lecCount;
    public int pracCount;
    public int labCount;
    public ControlType control;

    @Override
    public String toString() {
        return "PlanElement{" +
                "name='" + name + '\'' +
                ", hours=" + hours +
                ", semester=" + semester +
                ", lecCount=" + lecCount +
                ", pracCount=" + pracCount +
                ", labCount=" + labCount +
                ", control=" + control +
                '}';
    }

    public enum ControlType {
        exam("Экзамен", com.merqury.aspu.ui.navfragments.marks.ControlType.exam),
        exam_kr("Экзамен (КР)", com.merqury.aspu.ui.navfragments.marks.ControlType.exam),
        cred("Зачёт", com.merqury.aspu.ui.navfragments.marks.ControlType.cred),
        dif_cred("Дифференцированный зачёт", com.merqury.aspu.ui.navfragments.marks.ControlType.cred),
        undefined("Не будет в сессии", com.merqury.aspu.ui.navfragments.marks.ControlType.none);

        public final String label;
        public final com.merqury.aspu.ui.navfragments.marks.ControlType type;

        ControlType(String label, com.merqury.aspu.ui.navfragments.marks.ControlType type) {
            this.label = label;
            this.type = type;
        }
    }
}
