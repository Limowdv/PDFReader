package com.limow.pdfreader.domain.model;

public class PageData {

    private final Integer width;
    private final Integer height;
    private final byte[] content;

    public PageData(Integer width, Integer height, byte[]content) {
        this.width = width;
        this.height = height;
        this.content = content;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public byte[] getContent() {
        return content;
    }
}
