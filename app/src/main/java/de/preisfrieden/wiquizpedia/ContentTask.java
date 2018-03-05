package de.preisfrieden.wiquizpedia;

import android.app.Activity;
import android.os.AsyncTask;

/**
 * Created by peter on 05.03.2018.
 */

public class ContentTask extends AsyncTask<ContentTaskParam, Void, ContentQuery> implements DownloadCallback{

    private Content content = null;

    private static DownloadCallback<ContentQuery> mCallback;

    ContentTask(DownloadCallback<ContentQuery> callback) {
        setCallback(callback);
    }
    void setCallback(DownloadCallback<ContentQuery> callback) {
        mCallback = callback;
    }

    @Override
    protected ContentQuery doInBackground(ContentTaskParam... contentTaskParams) {
        ContentQuery contentQuery = null;
        if (null != contentTaskParams && contentTaskParams.length > 0 || null != contentTaskParams[0]) {
            ContentTaskParam contentTask = contentTaskParams[0];
            String title = contentTask.getNewTitle();
            content = contentTask.getContent();

            if (null != title) {
                content.readContentData( title, this ); // cont. on updateFromDownload-Callback
            } else {
                contentQuery = content.createQuery();
            }

        }
        return contentQuery;
    }

    // http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html#asynctask
    @Override
    protected void onPostExecute(ContentQuery contentQuery) {
        if (null != contentQuery) mCallback.updateFromDownload( contentQuery); // get query or wait for download ---
    }

    @Override
    public void updateFromDownload(Object result) {
        if (result instanceof Content) {
            Content content = (Content) result;
            ContentQuery contentQuery = content.createQuery();
            if (null == contentQuery) { // risk rekursion - o just try next random title ...
                content.readContentData( "", this );
            } else {
                mCallback.updateFromDownload(content.createQuery());
            }
        }
    }

}
