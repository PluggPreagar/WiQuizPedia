package de.preisfrieden.wiquizpedia;

import android.os.AsyncTask;

import de.preisfrieden.wiquizpedia.trf.DownloadCallback;

/**
 * Created by peter on 05.03.2018.
 */

public class ContentTask extends AsyncTask<ContentTaskParam, Void, ContentQuery> {

    private Content content = null;
    private static boolean running = false;

    private DownloadCallback<ContentQuery> mCallback;

    ContentTask(DownloadCallback<ContentQuery> callback) {
        setCallback(callback);
    }
    void setCallback(DownloadCallback<ContentQuery> callback) {
        mCallback = callback;
    }

    @Override
    protected ContentQuery doInBackground(ContentTaskParam... contentTaskParams) {
        ContentQuery contentQuery = null;
        if (!running && (  null != contentTaskParams && (contentTaskParams.length > 0 || null != contentTaskParams[0]))) {
            try {
                running = true;
                ContentTaskParam contentTask = contentTaskParams[0];
                String title = contentTask.getNewTitle();
                content = contentTask.getContent();
                if (null == content) content = new Content();
                contentQuery = null != title ? content.parseUrl(title) : content.createQuery(); // cont. on updateFromDownload-Callback
            } finally {
                running = false;
            }
        }
        return contentQuery;
    }

    // http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html#asynctask
    @Override
    protected void onPostExecute(ContentQuery contentQuery) { // allow mCallback zu be null for preloading ...
        if (null != contentQuery && null != mCallback ) mCallback.updateFromDownload( contentQuery); // get query or wait for download ---
    }


}
