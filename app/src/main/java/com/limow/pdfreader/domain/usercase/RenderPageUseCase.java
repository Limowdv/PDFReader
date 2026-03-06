package com.limow.pdfreader.domain.usercase;

import com.limow.pdfreader.domain.model.PageData;
import com.limow.pdfreader.domain.repository.PdfRepository;

public class RenderPageUseCase {

    private final PdfRepository repository;

    public RenderPageUseCase(PdfRepository repository) {
        this.repository = repository;
    }

    public PageData execute(Integer pageIndex){
        return repository.renderPage(pageIndex);
    }
}
