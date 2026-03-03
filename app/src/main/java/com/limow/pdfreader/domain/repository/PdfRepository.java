package com.limow.pdfreader.domain.repository;

import com.limow.pdfreader.domain.model.PageData;
import com.limow.pdfreader.domain.model.PdfDocument;

public interface PdfRepository {

    PdfDocument open(String uri);

    PageData renderPage(Integer pageIndex);

    void close();
}