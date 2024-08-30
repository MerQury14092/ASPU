package com.merqury.aspu.services.api.news.models;

import java.util.ArrayList;
import java.util.List;

public class NewsResponse {
    private String category;
    private int currentPage;
    private int countPages;
    private List<PreviewArticle> articles;
    public NewsResponse(){
        articles = new ArrayList<>();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCountPages() {
        return countPages;
    }

    public void setCountPages(int countPages) {
        this.countPages = countPages;
    }

    public List<PreviewArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<PreviewArticle> articles) {
        this.articles = articles;
    }
}
