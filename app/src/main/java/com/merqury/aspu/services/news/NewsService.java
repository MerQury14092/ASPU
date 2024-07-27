package com.merqury.aspu.services.news;

import android.annotation.SuppressLint;

import androidx.annotation.OptIn;
import androidx.compose.foundation.ExperimentalFoundationApi;

import com.merqury.aspu.enums.NewsCategoryEnum;
import com.merqury.aspu.services.appconfig.AppConfig;
import com.merqury.aspu.ui.navfragments.news.NewsStates;

import java.util.List;

public class NewsService {
    private static final String faculty_header = "faculties/";
    private static final String urlForEverything = "agpu.net/struktura-vuza/%s/news/news.php?PAGEN_1=%d";
    private final static String urlAgpuNews = "agpu.net/news.php";
    private final static List<String> nonStandardCategories;

    static {
        nonStandardCategories = List.of(
                "educationaltechnopark",
                "PedagogicalQuantorium"
        );
    }

    @SuppressLint("DefaultLocale")
    @OptIn(markerClass = ExperimentalFoundationApi.class)
    public static String urlForCurrentFaculty() {
        String faculty = NewsStates.INSTANCE.getSelectedFaculty().name();
        int page = NewsStates.INSTANCE.getPagerState().getCurrentPage();
        if(NewsStates.INSTANCE.getSelectedFaculty() == NewsCategoryEnum.agpu)
            return urlAgpuNews+"?PAGEN_1="+(page+1);
        return String.format(urlForEverything, nonStandardCategories.contains(NewsStates.INSTANCE.getSelectedFaculty().name()) ? faculty : faculty_header + faculty, (page+1));
    }
}
