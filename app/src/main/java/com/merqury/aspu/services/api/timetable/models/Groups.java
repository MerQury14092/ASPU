package com.merqury.aspu.services.api.timetable.models;


import java.util.ArrayList;
import java.util.List;

public class Groups {
    private String facultyName;
    private List<String> groups;
    public Groups(){
        groups = new ArrayList<>();
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }
}
