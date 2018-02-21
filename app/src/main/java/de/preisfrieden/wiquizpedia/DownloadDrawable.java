package de.preisfrieden.wiquizpedia;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

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

    DownloadDrawable(DownloadCallback<Drawable> callback) {
        setCallback(callback);
    }
    void setCallback(DownloadCallback<Drawable> callback) {
        mCallback = callback;
    }

    @Override
    protected Drawable doInBackground(String... urls) {
        Drawable drawable = null;
        if (null != urls && 0 < urls.length) {
            try { // https://stackoverflow.com/questions/6407324/how-to-display-image-from-url-on-android#6407554
                InputStream iStream = (InputStream) new URL(urls[0]).getContent();
                drawable = Drawable.createFromStream(iStream, "src name");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return drawable;
    }

    // http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html#asynctask
    protected void onPostExecute(Drawable msg) {
        mCallback.updateFromDownload(msg);
    }



}
