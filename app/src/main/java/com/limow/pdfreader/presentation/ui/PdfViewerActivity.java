package com.limow.pdfreader.presentation.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.limow.pdfreader.R;
import com.limow.pdfreader.data.repository.AndroidPdfRepository;
import com.limow.pdfreader.domain.model.PageData;
import com.limow.pdfreader.presentation.viewmodel.PdfViewModel;

public class PdfViewerActivity extends AppCompatActivity {

    private PdfViewModel viewModel;
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.pdfImageView);

        AndroidPdfRepository repository = new AndroidPdfRepository(getContentResolver());

        viewModel = new PdfViewModel(repository);

        Uri uri = getIntent().getData();
        if(uri == null){
            throw new RuntimeException("Uri no encontrada");
        }

        viewModel.openPdf(String.valueOf(uri));

        PageData pageData = viewModel.renderPage(0);

        if(pageData != null){
            var bitmap = turnBytesToBitmap(pageData);
            imageView.setImageBitmap(bitmap);
        }
    }

    private Bitmap turnBytesToBitmap(PageData p){
        var bytes = p.getContent();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


}
