package de.preisfrieden.wiquizpedia.trf;

import android.os.AsyncTask;

/**
 * Created by peter on 15.02.2018.
 */

public class DownloadTask2 extends AsyncTask<String, Void, String> {

    public static String FORCELOAD = "FORCELOAD";

    private DownloadCallback<String> mCallback;

    public DownloadTask2(DownloadCallback<String> callback) {
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
