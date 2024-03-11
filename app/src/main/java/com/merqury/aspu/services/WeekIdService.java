package com.merqury.aspu.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WeekIdService {

    public static long weekIdByDate(String date){
        int mappingWeekId = 3655;

        return mappingWeekId + countDays(date) / 7;
    }
    private static long countDays(String endDate) { 
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate dt = LocalDate.parse("28.08.2023", formatter);
        long mappingWeek = dt.toEpochDay();

        dt = LocalDate.parse(endDate, formatter);
        long currentWeek = dt.toEpochDay();

        return (currentWeek - mappingWeek);
    }
}
