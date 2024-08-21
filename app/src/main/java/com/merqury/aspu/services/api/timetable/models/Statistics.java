package com.merqury.aspu.services.api.timetable.models;

import com.merqury.aspu.services.api.timetable.enums.TimetableOwner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Statistics {
    private String startTime, endTime, date;
    private int total;
    private Map<TimetableOwner, Map<String, Integer>> statistics;
    public Statistics(){
        this.startTime = getCurrentTime();
        this.endTime = "now";
        date = getCurrentDate();
        statistics = new HashMap<>();
        for(TimetableOwner owner: TimetableOwner.values()){
            statistics.put(owner, new HashMap<>());
        }
    }

    public void endRecording(){
        endTime = getCurrentTime();
    }

    private static String getCurrentTime(){
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }

    private static String getCurrentDate(){
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return date.format(formatter);
    }

    public void addRecord(TimetableOwner owner, String group){
        if(!statistics.get(owner).containsKey(group))
            statistics.get(owner).put(group, 1);
        else
            statistics.get(owner).put(group, statistics.get(owner).get(group)+1);
        total++;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Map<TimetableOwner, Map<String, Integer>> getStatistics() {
        return statistics;
    }

    public void setStatistics(Map<TimetableOwner, Map<String, Integer>> statistics) {
        this.statistics = statistics;
    }
}
