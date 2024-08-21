package com.merqury.aspu.services.api.news.models;

public class PreviewArticle extends Article{
    private String previewImage;

    public String getPreviewImage() {
        return previewImage;
    }

    public void setPreviewImage(String previewImage) {
        this.previewImage = previewImage;
    }
}