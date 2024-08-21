package com.merqury.aspu.services.api.timetable.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.merqury.aspu.services.api.timetable.enums.TimetableOwner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TimetableDay {
    String date;
    String id;
    TimetableOwner owner;
    List<Discipline> disciplines;

    @JsonIgnore
    public boolean isSynthetic;

    @JsonIgnore
    public boolean isEmpty(){
        return disciplines.isEmpty();
    }

    public TimetableDay deleteHolidays(){
        disciplines = disciplines.stream()
                .filter(discipline -> !discipline.getName().equals("HOLIDAY"))
                .collect(Collectors.toList());
        return this;
    }

    public static TimetableDay builder() {
        return new TimetableDay();
    }

    public TimetableDay build() {
        return this;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public TimetableOwner getOwner() {
        return owner;
    }

    public void setOwner(TimetableOwner owner) {
        this.owner = owner;
    }

    public List<Discipline> getDisciplines() {
        return disciplines;
    }

    public boolean isSynthetic() {
        return isSynthetic;
    }

    public TimetableDay synthetic(boolean synthetic) {
        isSynthetic = synthetic;
        return this;
    }

    public TimetableDay disciplines(List<Discipline> disciplines) {
        this.disciplines = disciplines;
        return this;
    }

    public TimetableDay owner(TimetableOwner owner) {
        this.owner = owner;
        return this;
    }

    public TimetableDay id(String id) {
        this.id = id;
        return this;
    }

    public TimetableDay date(String date) {
        this.date = date;
        return this;
    }


    public TimetableDay setId(String id) {
        this.id = id;
        return this;
    }

    public TimetableDay setDate(String date) {
        this.date = date;
        return this;
    }

    public TimetableDay proxy(){
        List<Discipline> proxyList = new ArrayList<>();

        for (Discipline disc: disciplines) {
            Discipline proxyDisc;
            try {
                proxyDisc = disc.proxy();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            proxyList.add(proxyDisc);
        }

        return TimetableDay.builder()
                .date(date)
                .id(id)
                .owner(owner)
                .disciplines(proxyList)
                .build();
    }
}
