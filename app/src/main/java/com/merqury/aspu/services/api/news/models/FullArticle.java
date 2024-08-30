package com.merqury.aspu.services.api.news.models;

import java.util.ArrayList;
import java.util.List;

public class FullArticle extends Article{
    List<String> images;

    public FullArticle(){
        images = new ArrayList<>();
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
