package de.preisfrieden.wiquizpedia;

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

public class DownloadTask2 extends AsyncTask<String, Void, String> {

    public static String NOCACHE = "NOCACHE";

    private static Map<String,String> cache = new HashMap<String,String>();
    private DownloadCallback<String> mCallback;

    DownloadTask2(DownloadCallback<String> callback) {
        setCallback(callback);
    }
    void setCallback(DownloadCallback<String> callback) {
        mCallback = callback;
    }


    @Override
    protected String doInBackground(String... urls) {
        Download download = new Download();
        String result = download.downloadUrl(urls);
        return result;
    }

    // http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html#asynctask
    protected void onPostExecute(String msg) {
        mCallback.updateFromDownload( msg);
    }

}
