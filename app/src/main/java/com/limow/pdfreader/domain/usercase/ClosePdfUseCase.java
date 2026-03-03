package com.limow.pdfreader.domain.usercase;

import com.limow.pdfreader.domain.repository.PdfRepository;

public class ClosePdfUseCase {

    private PdfRepository repository;

    public ClosePdfUseCase(PdfRepository repository) {
        this.repository = repository;
    }

    public void execute(){
        repository.close();
    }
}
