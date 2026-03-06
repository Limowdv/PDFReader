package com.limow.pdfreader.domain.usercase;

import com.limow.pdfreader.domain.model.PdfDocument;
import com.limow.pdfreader.domain.repository.PdfRepository;

public class OpenPdfUseCase {

    private final PdfRepository repository;

    public OpenPdfUseCase(PdfRepository repository){
        this.repository = repository;
    }

    public PdfDocument execute(String uri){
        return repository.open(uri);
    }


}
