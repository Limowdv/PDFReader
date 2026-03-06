package com.limow.pdfreader.presentation.viewmodel;

import com.limow.pdfreader.domain.model.PageData;
import com.limow.pdfreader.domain.model.PdfDocument;
import com.limow.pdfreader.domain.repository.PdfRepository;

public class PdfViewModel {

    private final PdfRepository repository;
    private int totalPages;
    private int currentPage;

    public PdfViewModel(PdfRepository repository) {
        this.repository = repository;
    }

    public void openPdf(String uri){
        PdfDocument document = repository.open(uri);
        totalPages = document.getTotalPages();
        currentPage = 0;
    }

    public PageData renderPage(Integer pageIndex){
        PageData page = repository.renderPage(pageIndex);
        currentPage = pageIndex;
        return page;
    }

    public void closePdf(){
        repository.close();
    }


}
