package de.preisfrieden.wiquizpedia;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by peter on 15.02.2018.
 */

public class DownloadDrawable extends AsyncTask<String, Void, Drawable> {

    private DownloadCallback<Drawable> mCallback;
    private static boolean already_running = false;

    DownloadDrawable(DownloadCallback<Drawable> callback) {
        setCallback(callback);
    }
    void setCallback(DownloadCallback<Drawable> callback) {
        mCallback = callback;
    }

    @Override
    protected Drawable doInBackground(String... urls) {
        Drawable drawable = null;
        if (null != urls && 0 < urls.length && !already_running) {
            already_running = true;
             Log.i("info","Try loading from URL: " + urls[0]);
            try { // https://stackoverflow.com/questions/6407324/how-to-display-image-from-url-on-android#6407554
                InputStream iStream = (InputStream) new URL(urls[0]).getContent();
                // drawable = decodeSampleBitmapFromResource( Drawable.createFromStream(iStream, "src name"), 10, 10);
                //drawable = Drawable.createFromStream(iStream, "src name");
                Bitmap bitmap = BitmapFactory.decodeStream(iStream);
                Log.i("info","Try loading from URL - scale:  " + bitmap.getWidth() + " x " + bitmap.getHeight());
                bitmap = getResizedBitmap( bitmap, 300, 300);
                drawable = new BitmapDrawable( ((MainActivity) mCallback).getResources(), bitmap);;
                // drawable = decodeSampleBitmapFromResource( Drawable.createFromStream(iStream, "src name"), 10, 10);
            } catch (IOException e) {
                Log.e("error","Error loading from URL: " + urls[0]);
                e.printStackTrace();
            } catch (Exception e) {
                Log.e("error","Error creating image ..: " + urls[0]);
                e.printStackTrace();
            } finally {
                already_running = false;
            }
        }
        return drawable;
    }

    // http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html#asynctask
    protected void onPostExecute(Drawable msg) {
        mCallback.updateFromDownload(msg);
    }

    // https://stackoverflow.com/questions/19558713/android-java-lang-outofmemoryerror#32425319
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        scaleWidth = scaleWidth  < scaleHeight ? scaleWidth : scaleHeight;
        scaleHeight = scaleWidth;

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
