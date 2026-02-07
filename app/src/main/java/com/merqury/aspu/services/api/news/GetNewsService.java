package com.merqury.aspu.services.api.news;


import android.annotation.SuppressLint;
import android.util.Log;

import com.merqury.aspu.services.api.news.models.FullArticle;
import com.merqury.aspu.services.api.news.models.NewsResponse;
import com.merqury.aspu.services.api.news.models.PreviewArticle;
import com.merqury.aspu.services.appconfig.AppConfig;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class GetNewsService {
    private static final String hostSite = "https://agpu.net";
    private static final String faculty_header = "faculties/";
    private static final String urlForEverything = hostSite + "/struktura-vuza/%s/news/news.php?PAGEN_1=%d";
    private static final String urlForArticle = hostSite + "/struktura-vuza/%s/news/news.php?ELEMENT_ID=%d";
    private final static String urlAgpuNews = "https://agpu.net/news.php";
    private final static List<String> nonStandardCategories;
    private final static GetNewsService instance;
    static {
        instance = new GetNewsService();
        nonStandardCategories = List.of(
                "educationaltechnopark",
                "PedagogicalQuantorium"
        );
    }

    public static GetNewsService getInstance() {
        return instance;
    }

    @SuppressLint("DefaultLocale")
    public NewsResponse getArticlesByFaculty(String faculty, int page) throws IOException {
        String f = faculty;
        faculty = convertFaculty(faculty);
        List<PreviewArticle> res = new ArrayList<>();
        Document doc;
        try {
            URL url;
            if(faculty.equalsIgnoreCase("agpu"))
                url = new URL(hostSite+"/news.php?PAGEN_1="+page);
            else
                url = new URL(String.format(urlForEverything, nonStandardCategories.contains(faculty) ? faculty : faculty_header + faculty, page));
            doc = Jsoup.parse(url, 5000);
        } catch (HttpStatusException e) {
            NewsResponse err = new NewsResponse();
            err.setCurrentPage(0);
            err.setCountPages(0);
            PreviewArticle errArt = new PreviewArticle();
            errArt.setDate("Unknown faculty: " + faculty);
            errArt.setPreviewImage("Unknown faculty: " + faculty);
            errArt.setDescription("Unknown faculty: " + faculty);
            errArt.setTitle("Unknown faculty: " + faculty);
            errArt.setId(-1);
            err.setArticles(List.of(errArt));
            return err;
        }
        NewsResponse result = getNewsResponse(page, doc, res);
        result.setCategory(f);
        return result;
    }

    public FullArticle getArticleById(String faculty, int id) throws IOException {
        faculty = convertFaculty(faculty);
        String url;
        Document doc;
        if (faculty.equals("agpu")) {
            doc = Jsoup.parse(new URL(urlAgpuNews + "?ELEMENT_ID=" + id), 5000);
            url = urlAgpuNews + "?ELEMENT_ID=" + id;
        } else {
            try {
                doc = Jsoup.parse(new URL(String.format(urlForArticle, nonStandardCategories.contains(faculty) ? faculty : faculty_header + faculty, id)), 5000);
                url = String.format(urlForArticle, nonStandardCategories.contains(faculty) ? faculty : faculty_header + faculty, id);
            } catch (HttpStatusException e) {
                FullArticle err = new FullArticle();
                err.setTitle("Article not found");
                return err;
            }
        }
        try {
            FullArticle result = parseArticlePage(Objects.requireNonNull(doc.getElementsByClass(/*"col-md-9 md-padding main-content"*/"mb-3").first()), id, url);
            result.faculty = faculty;
            return result;
        } catch (Exception e) {
            FullArticle err = new FullArticle();
            err.setTitle("Article not found");
            return err;
        }
    }

    private String convertFaculty(String faculty) {
        switch (faculty) {
            case "ipimif": return "fmf";
            case "iriif": return "filfak";
            case "fteid": return "ftifk";
            case "spf":
            case "fdino":
                return "ppf";
            default: return faculty;
        }
    }

    public NewsResponse getAgpuNews(int page) throws IOException {
        Document doc = Jsoup.parse(new URL(urlAgpuNews + "?PAGEN_1=" + page), 5000);
        List<PreviewArticle> res = new ArrayList<>();
        return getNewsResponse(page, doc, res);
    }

    private NewsResponse getNewsResponse(int page, Document doc, List<PreviewArticle> res) {
        for (Element el : doc.getElementsByTag("article"))
            res.add(parsePreviewArticleElement(el));
        NewsResponse result = new NewsResponse();
        result.setCurrentPage(page);
        result.setArticles(res);
        Elements elsA = doc.getElementsByTag("a");
        List<Element> els = new ArrayList<>();
        for (Element el : elsA) {
            if (el.text().equals("Конец"))
                els.add(el);
        }
        if (els.isEmpty()) {
            result.setCountPages(1);
        }
        else {
            URL uriToEnd;
            try {
                uriToEnd = new URL("http://n/"+els.get(0)
                        .attr("href"));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            Map<String, String> queryParams;
            try {
                queryParams = splitQuery(uriToEnd);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            result.setCountPages(Integer.parseInt(queryParams.get("PAGEN_1")));
        }
        for (Element el : doc.getElementsByTag("font")) {
            if (el.text().contains("След. | Конец")) {
                if (els.isEmpty())
                    result.setCountPages(page);
            }
        }
        if (result.getCurrentPage() > result.getCountPages())
            result.setCurrentPage(1);
        return result;
    }

    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new HashMap<>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(
                    URLDecoder.decode(
                            pair.substring(0, idx),
                            "UTF-8"),
                    URLDecoder.decode(
                            pair.substring(idx + 1),
                            "UTF-8"
                    )
            );
        }
        return query_pairs;
    }

    private FullArticle parseArticlePage(Element element, int id, String url) {
        Element el = element.getElementsByClass("news-detail-body").first();
        FullArticle res = new FullArticle();
        res.url = url;
        assert el != null;
        res.setDate(
                Objects.requireNonNull(element.getElementsByClass("news-detail-date")
                                .first())
                        .text()
        );
        res.setTitle(
                Objects.requireNonNull(el.getElementsByTag("h3")
                        .first()).text()
        );

        res.setId(id);
        Element newsDetailsContent = element.getElementsByClass("news-detail-content").first();
        res.setDescription(recursiveParseText(newsDetailsContent));

        for (Element img : el.getElementsByTag("img"))
            res.getImages().add("http://www.agpu.net" + img.attr("src"));
        return res;
    }

    private String recursiveParseText(Element el) {
        if (
                el
                        .children()
                        .stream()
                        .filter(
                                element -> !element
                                        .tag()
                                        .equals(Tag.valueOf("br"))
                        )
                        .collect(Collectors.toList())
                        .isEmpty()
        )
            return el.text();
        StringBuilder res = new StringBuilder();
        for (Element child : el.children()) {
            res.append(recursiveParseText(child));
        }
        return res.toString();

    }

    private PreviewArticle parsePreviewArticleElement(Element el) {
        PreviewArticle res = new PreviewArticle();
        res.setId(
                Integer.parseInt(
                        Objects.requireNonNull(el.getElementsByTag("a")
                                        .first())
                                .attr("href")
                                .split("ELEMENT_ID=")[1]
                )
        );
        res.setTitle(
                Objects.requireNonNull(Objects.requireNonNull(el.getElementsByTag("h4")
                                        .first())
                                .getAllElements()
                                .first())
                        .text()
        );

        if (!el.getElementsByAttributeValue("style", "text-align: justify;").isEmpty())
            res.setDescription(
                    el.getElementsByAttributeValue("style", "text-align: justify;")
                            .first()
                            .text()
            );
        res.setPreviewImage(
                hostSite
                        +
                        el.getElementsByTag("img")
                                .first()
                                .attr("src")
        );
        res.setDate(
                el.getElementsByTag("li")
                        .get(1)
                        .text()
        );
        return res;
    }
}
