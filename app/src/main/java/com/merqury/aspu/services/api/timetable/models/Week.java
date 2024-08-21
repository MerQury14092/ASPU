package com.merqury.aspu.services.api.timetable.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.SortedMap;
import java.util.TreeMap;

public class Week {
    private int id;
    private String from;
    private String to;
    private SortedMap<DateString, String> dayNames;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public SortedMap<DateString, String> getDayNames() {
        return dayNames;
    }

    public void setDayNames(SortedMap<DateString, String> dayNames) {
        this.dayNames = dayNames;
    }

    public void initArray(){
        dayNames = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate from = LocalDate.parse(this.from, formatter);
        dayNames.put(DateString.build(from.format(formatter)), "Понедельник");
        from = from.plusDays(1);
        dayNames.put(DateString.build(from.format(formatter)), "Вторник");
        from = from.plusDays(1);
        dayNames.put(DateString.build(from.format(formatter)), "Среда");
        from = from.plusDays(1);
        dayNames.put(DateString.build(from.format(formatter)), "Четверг");
        from = from.plusDays(1);
        dayNames.put(DateString.build(from.format(formatter)), "Пятница");
        from = from.plusDays(1);
        dayNames.put(DateString.build(from.format(formatter)), "Суббота");
        from = from.plusDays(1);
        dayNames.put(DateString.build(from.format(formatter)), "Воскресенье");
    }
}
