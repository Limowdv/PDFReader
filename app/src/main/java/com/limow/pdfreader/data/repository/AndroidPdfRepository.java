package com.limow.pdfreader.data.repository;

import static android.graphics.Bitmap.CompressFormat.PNG;
import static android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.LruCache;

import com.limow.pdfreader.domain.model.PageData;
import com.limow.pdfreader.domain.model.PdfDocument;
import com.limow.pdfreader.domain.repository.PdfRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class AndroidPdfRepository implements PdfRepository {

    private final ContentResolver contentResolver;
    private ParcelFileDescriptor fileDescriptor;
    private PdfRenderer renderer;
    private Boolean isOpen;
    private final LruCache<Integer, PageData> pageCache;

    private final static int MAX_CACHE_BYTES = 20 * 1024 * 1024;

    public AndroidPdfRepository(ContentResolver contentResolver){
        this.contentResolver = contentResolver;
        this.fileDescriptor = null;
        this.renderer = null;
        this.isOpen = false;
        pageCache = new LruCache<>(MAX_CACHE_BYTES){
            @Override
            protected int sizeOf(Integer key, PageData value){
                return value.getContent().length;
            }
        };
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

            isOpen = true;

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

        if(!isOpen){
            throw new IllegalArgumentException("El PDF no se ha abierto");
        }

        if(pageIndex < 0 || pageIndex >= renderer.getPageCount()){
            throw new IllegalArgumentException("Indice de pagina invalido");
        }

        if(pageCache.snapshot().containsKey(pageIndex)){
            return pageCache.get(pageIndex);
        }

        PdfRenderer.Page page = null;

        try {

             page = renderer.openPage(pageIndex);

             int width = page.getWidth();
             int height = page.getHeight();

             int maxSize = 2048;

             double scaleWidth;
             double scaleHeight;
             double scale = 1.0;

             if(width > maxSize || height > maxSize){
                 scaleWidth = maxSize / width;
                 scaleHeight = maxSize / height;
                 if (scaleHeight > scaleWidth){
                     scale = scaleWidth;
                 } else if (scaleHeight < scaleWidth) {
                     scale = scaleHeight;
                 }
             }

             int scaledWidth = (int) (width * scale);
             int scaledHeight = (int) (height *scale);

             var bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);

             var matrix = new Matrix();
             matrix.setScale((float) scale, (float) scale);

             page.render(bitmap, null, matrix, RENDER_MODE_FOR_DISPLAY);

             var byteArray = convertBitmapToByteArray(bitmap);

             bitmap.recycle();

             PageData pagina = new PageData(width, height, byteArray);

             pageCache.put(pageIndex, pagina);

             return pagina;

        } catch (Exception e) {
            throw new RuntimeException("Fallo al renderizar la pagina", e);
        } finally {
            if(page!= null){
                page.close();
            }
        }
    }

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

        isOpen = false;

    }
}
