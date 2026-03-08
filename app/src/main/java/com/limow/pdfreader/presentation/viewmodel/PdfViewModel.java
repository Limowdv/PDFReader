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
        if(uri == null){
            throw new IllegalArgumentException("Uri invalida");
        }

        repository.open(uri);

        totalPages = repository.getTotalPages();

        if(totalPages<=0){
            throw new RuntimeException("El PDF no tiene paginas");
        }
        currentPage = 0;
    }

    public PageData renderPage(int pageIndex){
        if(pageIndex<0){
            return null;
        }

        if(pageIndex >= totalPages){
            return null;
        }

        currentPage = pageIndex;
        return repository.renderPage(pageIndex);
    }

    public void closePdf(){
        repository.close();
    }


}
