package com.merqury.aspu.services.api.timetable

import com.merqury.aspu.services.appconfig.AppConfig
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WeekIdService {
    companion object {
        fun weekIdByDate(date: String): Long {
            val mapping = AppConfig.getWeekIdMappings().firstOrNull {
                dateInRange(it.range.split("-")[0], it.range.split("-")[1], date)
            }
            if(mapping == null)
                return 1
            val mappingWeekId = mapping.id
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            var dt = LocalDate.parse(mapping.range.split("-")[0], formatter)
            val mappingWeek = dt.toEpochDay()

            dt = LocalDate.parse(date, formatter)
            val currentWeek = dt.toEpochDay()

            val countDays = currentWeek - mappingWeek
            return mappingWeekId + countDays / 7
        }

        private fun dateInRange(startDate: String, endDate: String, target: String): Boolean {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val start = LocalDate.parse(startDate, formatter)
            val end = LocalDate.parse(endDate, formatter)

            val dates: MutableList<String> = ArrayList()
            var dateCursor = start

            while (!dateCursor.isAfter(end)) {
                dates.add(dateCursor.format(formatter))
                dateCursor = dateCursor.plusDays(1)
            }

            return dates.any { it == target }
        }
    }
}