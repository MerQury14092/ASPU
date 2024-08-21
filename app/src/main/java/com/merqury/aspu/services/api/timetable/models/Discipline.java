package com.merqury.aspu.services.api.timetable.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.merqury.aspu.services.api.timetable.enums.DisciplineType;

public class Discipline {
    @JsonIgnore
    private String date;
    private String time;
    private String name;
    private String teacherName;
    private String audienceId;
    private int subgroup;
    private DisciplineType type;
    private String groupName;
    private boolean isDistant;
    @JsonIgnore
    private int colspan;

    public Discipline() {
    }

    public static Discipline holiday(){
        Discipline res = new Discipline();
        res.setName("HOLIDAY");
        return res;
    }

    public Discipline(String date, String time, String name, String teacherName, String audienceId, int subgroup, DisciplineType type, String groupName, boolean isDistant, int colspan) {
        this.date = date;
        this.time = time;
        this.name = name;
        this.teacherName = teacherName;
        this.audienceId = audienceId;
        this.subgroup = subgroup;
        this.type = type;
        this.groupName = groupName;
        this.isDistant = isDistant;
        this.colspan = colspan;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getAudienceId() {
        return audienceId;
    }

    public void setAudienceId(String audienceId) {
        this.audienceId = audienceId;
    }

    public int getSubgroup() {
        return subgroup;
    }

    public void setSubgroup(int subgroup) {
        this.subgroup = subgroup;
    }

    public DisciplineType getType() {
        return type;
    }

    public void setType(DisciplineType type) {
        this.type = type;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isDistant() {
        return isDistant;
    }

    public void setDistant(boolean distant) {
        isDistant = distant;
    }

    public int getColspan() {
        return colspan;
    }

    public void setColspan(int colspan) {
        this.colspan = colspan;
    }

    public Discipline proxy() throws CloneNotSupportedException {
        return new Discipline(
                date,
                time,
                name,
                teacherName,
                audienceId,
                subgroup,
                type,
                groupName,
                isDistant,
                colspan
        );
    }
}
