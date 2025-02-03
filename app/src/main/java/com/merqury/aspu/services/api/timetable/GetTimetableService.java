package com.merqury.aspu.services.api.timetable;

import static com.merqury.aspu.ui.DebugKt.printlog;

import android.annotation.SuppressLint;

import com.merqury.aspu.services.api.timetable.models.Discipline;
import com.merqury.aspu.services.api.timetable.models.TimetableDay;
import com.merqury.aspu.services.api.timetable.enums.DisciplineType;
import com.merqury.aspu.services.api.timetable.enums.TimetableOwner;
import com.merqury.aspu.ui.Debug;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class GetTimetableService {
    public GetTimetableService(GetSearchIdService getSearchIdService) {
        this.getSearchIdService = getSearchIdService;
    }

    private final static GetTimetableService instance;

    static {
        instance = new GetTimetableService(GetSearchIdService.getInstance());
    }

    public static GetTimetableService getInstance() {
        return instance;
    }

    private final GetSearchIdService getSearchIdService;

    public List<TimetableDay> getDisciplines(
            String id, TimetableOwner owner, String startDate, String endDate
    ) throws IOException {
        List<TimetableDay> result = new ArrayList<>();
        for (String date : getDatesBetween(startDate, endDate))
            result.add(getTimetableDayFromSite(id, date, owner));
        return result;
    }

    public TimetableDay getTimetableDayFromSite(
            String id, String date, TimetableOwner owner
    ) throws IOException {
        TimetableDay[] week = getTimetableWeek(id, date, owner);
        return getFromArrayByDate(week, date);
    }

    public TimetableDay[] getTimetableWeek(String id, String date, TimetableOwner owner) throws IOException {
        long weekId = WeekIdService.Companion.weekIdByDate(date);
        String html = getHtmlFromPage(id, getSearchIdService.getSearchId(id, owner), owner, weekId);
        TimetableDay[] week = parseHtml(html, owner);

        Arrays.stream(week).forEach(day ->
                day.getDisciplines().removeIf(discipline -> discipline.getName() == null)
        );

        return week;
    }

    private TimetableDay getFromArrayByDate(TimetableDay[] arr, String date) {
        return Arrays.stream(arr)
                .filter(el -> el.getDate().equals(date))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }


    private String getHtmlFromPage(
            String searchText, int searchId, TimetableOwner owner, long weekId
    ) throws IOException {
        String ownerString = capitalize(owner.name().toLowerCase());
        @SuppressLint("DefaultLocale") URL url1 = new URL(
                String.format(
                        "http://it-institut.ru/Raspisanie/SearchedRaspisanie?OwnerId=118&SearchId=%d&SearchString=%s&Type=%s&WeekId=%d",
                        searchId,
                        URLEncoder.encode(searchText, "UTF-8"),
                        ownerString,
                        weekId
                )
        );

        HttpURLConnection conn = (HttpURLConnection) url1.openConnection();

        conn.setConnectTimeout(5000);
        conn.setReadTimeout(15000);
        InputStream is = conn.getInputStream();


        StringBuilder html = new StringBuilder();

        Scanner sc = new Scanner(is);

        String cur = "";
        while (!cur.equals("</html>")) {
            cur = sc.nextLine();
            html.append(cur);
        }

        return html.toString();
    }

    private String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private TimetableDay[] parseHtml(String html, TimetableOwner for_who) {
        TimetableDay[] week = new TimetableDay[7];
        Document doc = Jsoup.parse(html);

        Elements elements = doc
                .getElementsByClass("table")
                .first()
                .getElementsByTag("tbody")
                .first()
                .getElementsByTag("tr");

        Elements times = doc
                .getElementsByClass("thead-light")
                .first()
                .getElementsByTag("tr")
                .first()
                .getElementsByTag("th");

        List<Element> dayCells = doc
                .getElementsByClass("table")
                .first()
                .getElementsByTag("tbody")
                .first()
                .getElementsByTag("tr")
                .stream().map(element -> element
                        .getElementsByTag("th")
                        .first())
                .collect(Collectors.toList());


        List<String> dates = dayCells.stream().map(element ->
                element.html().split("<br>")[1].trim()
        ).collect(Collectors.toList());

        String id = doc
                .getElementsByClass("input-group")
                .first()
                .getElementsByTag("input")
                .first()
                .attr("value");

        for (int i = 0; i < week.length; i++) {
            week[i] = TimetableDay.builder()
                    .date(dates.get(i))
                    .disciplines(new ArrayList<>())
                    .id(id)
                    .build();
        }

        Integer[] col = parseCol(times);

        for (int i = 0; i < 7; i++)
            parseDay(elements.get(i), week[i], col);

        for (TimetableDay timetableDay : week) {
            timetableDay.setOwner(for_who);
        }

        return week;
    }

    public static List<String> getDatesBetween(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        List<String> dates = new ArrayList<>();
        LocalDate dateCursor = start;

        while (!dateCursor.isAfter(end)) {
            dates.add(dateCursor.format(formatter));
            dateCursor = dateCursor.plusDays(1);
        }

        return dates;
    }

    private long countDays(String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate dt = LocalDate.parse("28.08.2023", formatter);
        long mappingWeek = dt.toEpochDay();

        dt = LocalDate.parse(endDate, formatter);
        long currentWeek = dt.toEpochDay();

        return (currentWeek - mappingWeek);
    }

    private void assignTimeOfDisciplines(List<Discipline> result, Integer[] col) {
        Integer[] pairs = new Integer[result.size()];
        for (int i = 0; i < pairs.length; i++) {
            if (result.get(i) == null) {
                pairs[i] = col[i];
                continue;
            }
            pairs[i] = result.get(i).getColspan();
        }

        Integer[] res = res(col, pairs);

        for (int i = 0; i < result.size(); i++) {
            if (result.get(i) == null)
                continue;
            result.get(i).setTime(timeByIndex(res[i]));
        }
    }

    private Integer[] parseCol(Elements elements) {
        List<Integer> res = new ArrayList<>();
        for (Element el : elements)
            if (el.hasAttr("colspan"))
                res.add(Integer.parseInt(el.attr("colspan")));
        return res.toArray(new Integer[0]);
    }

    private void parseDay(Element el, TimetableDay day, Integer[] col) { // сюда приходит тег tr
        Element nameOfClass = el.getElementsByTag("th").first();
        Elements disciplines = el.getElementsByTag("td");
        for (int i = 0; i < 8; i++) {
            assert nameOfClass != null;
            parseDiscipline(disciplines.get(i), day, nameOfClass.html().split("\n")[1]);
        }
        assignTimeOfDisciplines(day.getDisciplines(), col);
    }

    private void parseDiscipline(Element el, TimetableDay day, String date) { // сюда приходит тег td
        Discipline result = new Discipline();
        result.setDistant(el.text().toLowerCase().contains("дист".toLowerCase()));
        Elements spans = el.getElementsByTag("span");
        if (spans.isEmpty()) {
            result.setDate(date);
            result.setColspan(Integer.parseInt(el.attr("colspan")));
            day.getDisciplines().add(result);
            return;
        }
        String name = spans.get(0).text();
        List<Character> characters = List.of(',', '.');
        if (characters.contains(name.charAt(name.length() - 1)))
            name = name.substring(0, name.length() - 1);
        result.setName(name);
        String[] prepodAndAudience = spans.get(1).text().split(",");
        if (prepodAndAudience.length >= 3) {
            result.setTeacherName(prepodAndAudience[0].trim());
            result.setAudienceId(prepodAndAudience[prepodAndAudience.length - 1].trim());
        } else if (prepodAndAudience.length == 2) {
            result.setTeacherName(prepodAndAudience[0].trim());
            result.setAudienceId(prepodAndAudience[1].trim());
        }
        if (spans.size() < 4)
            result.setSubgroup(0);
        else
            result.setSubgroup(spans.get(3).text().contains("1") ? 1 : 2);
        result.setDate(date);
        result.setGroupName(spans.get(2).text().replace("(", "").replace(")", ""));
        result.setColspan(Integer.parseInt(el.attr("colspan")));
        assignTypeAndRenameDiscipline(result);
        day.getDisciplines().add(result);
    }

    private void assignTypeAndRenameDiscipline(Discipline discipline) {
        Map<String, DisciplineType> mapBetweenNameAndType = Map.of(
                "конс", DisciplineType.cons,
                "лек", DisciplineType.lec,
                "фэпо", DisciplineType.fepo,
                "зач", DisciplineType.cred,
                "выходной", DisciplineType.hol,
                "каникулы", DisciplineType.hol,
                "лаб", DisciplineType.lab,
                "экз", DisciplineType.exam,
                "прак", DisciplineType.prac,
                "курсов", DisciplineType.cours
        );

        for (Map.Entry<String, DisciplineType> entry : mapBetweenNameAndType.entrySet()) {
            if (discipline.getName().toLowerCase().contains("," + entry.getKey())) {
                discipline.setName(discipline.getName().replace("," + entry.getKey(), ""));
                discipline.setType(entry.getValue());
                return;
            }
        }
        discipline.setType(DisciplineType.none);
    }

    private String timeByIndex(int index) {
        switch (index) {
            case 0:
                return "8:00-9:30";
            case 1:
                return "9:40-11:10";
            case 2:
                return "11:40-13:10";
            case 3:
                return "13:30-15:00";
            case 4:
                return "15:10-16:40";
            case 5:
                return "16:50-18:20";
            case 6:
                return "18:30-20:00";
            default:
                return "-----------";
        }
    }

    private Integer[] res(Integer[] col, Integer[] pairs) {
        ArrayList<Integer> res = new ArrayList<>(pairs.length);
        int currentIndex = 0;
        int remainder = col[currentIndex];
        for (int i = 0; i < pairs.length; i++) {
            res.add(currentIndex);
            remainder -= pairs[i];
            if (remainder <= 0 && ++currentIndex < col.length) {
                remainder = col[currentIndex];
            }
        }
        return res.toArray(new Integer[0]);
    }
}
