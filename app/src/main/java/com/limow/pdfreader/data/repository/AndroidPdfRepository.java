package com.limow.pdfreader.data.repository;

import static android.graphics.Bitmap.CompressFormat.PNG;
import static android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.limow.pdfreader.domain.model.PageData;
import com.limow.pdfreader.domain.model.PdfDocument;
import com.limow.pdfreader.domain.repository.PdfRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AndroidPdfRepository implements PdfRepository {

    private ContentResolver contentResolver;
    private ParcelFileDescriptor fileDescriptor;
    private PdfRenderer renderer;
    private Boolean isOpen;

    public AndroidPdfRepository(ContentResolver contentResolver){
        this.contentResolver = contentResolver;
        this.fileDescriptor = null;
        this.renderer = null;
        this.isOpen = false;

    }

    @Override
    public PdfDocument open(String uri) {

        if(renderer !=null){
            throw new IllegalStateException("El PDF ya ha sido abierto");
        }

        try{

            Uri parsedUri = Uri.parse(uri);
            fileDescriptor = contentResolver.openFileDescriptor(parsedUri, "r");

            if(fileDescriptor == null){
                throw new IOException("El descriptor es nulo");
            }

            renderer = new PdfRenderer(fileDescriptor);

            int totalPages = renderer.getPageCount();

            return new PdfDocument(totalPages);

        } catch (IOException e){
            close();
            throw new RuntimeException("Error al abrir el PDF", e);
        }
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap){
        var stream = new ByteArrayOutputStream();
        bitmap.compress(PNG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    public PageData renderPage(Integer pageIndex) {

        if(renderer == null){
            throw new IllegalArgumentException("El PDF no se ha abierto");
        }

        if(pageIndex < 0 || pageIndex >= renderer.getPageCount()){
            throw new IllegalArgumentException("Indice de pagina invalido");
        }

        PdfRenderer.Page page = null;

        try {

             page = renderer.openPage(pageIndex);

             int width = page.getWidth();
             int height = page.getHeight();

             var bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

             page.render(bitmap, null, null, RENDER_MODE_FOR_DISPLAY);

             var byteArray = convertBitmapToByteArray(bitmap);

             return new PageData(width, height, byteArray);

        } catch (Exception e) {
            throw new RuntimeException("Fallo al renderizar la pagina", e);
        } finally {
            if(page!= null){
                page.close();
            }
        }
    }



            /**
        var page = renderer.openPage(pageIndex);
        
        int width = page.getWidth();

        int height = page.getHeight();

        var bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        page.render(bitmap, null, null, RENDER_MODE_FOR_DISPLAY);

        var byteArray = convertBitmapToByteArray(bitmap);

        return new PageData(width, height, byteArray);
             **/


    @Override
    public void close() {

        try{
            if(renderer != null){
                renderer.close();

            }
        } catch (Exception e) {
        } finally{
            renderer = null;
        }

        try{
            if(fileDescriptor != null){
                fileDescriptor.close();
            }
        } catch (IOException e){

        } finally{
            fileDescriptor=null;
        }

    }
}
