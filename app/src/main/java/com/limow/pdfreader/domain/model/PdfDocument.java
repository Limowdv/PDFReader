package com.limow.pdfreader.domain.model;

public class PdfDocument {

    private Integer totalPages;

    public PdfDocument(Integer totalPages){
        this.totalPages = totalPages;
    }

    public Integer getTotalPages(){
        return this.totalPages;
    }
}
