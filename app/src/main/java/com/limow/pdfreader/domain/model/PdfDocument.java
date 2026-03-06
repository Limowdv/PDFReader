package com.limow.pdfreader.domain.model;

public class PdfDocument {

    private final Integer totalPages;

    public PdfDocument(Integer totalPages){
        this.totalPages = totalPages;
    }

    public Integer getTotalPages(){
        return this.totalPages;
    }
}
