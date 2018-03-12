package de.preisfrieden.wiquizpedia;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static java.net.Proxy.Type.HTTP;

/**
 * Created by peter on 12.03.2018.
 */

public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

    // https://stackoverflow.com/questions/601503/how-do-i-obtain-crash-data-from-my-android-application#2855736

    private Thread.UncaughtExceptionHandler defaultUEH;
    private String localPath;
    private String url;

    /*
     * if any of the parameters is null, the respective functionality
     * will not be used
     */
    public CustomExceptionHandler(String localPath, String url) {
        this.localPath = localPath;
        this.url = url;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e) {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        String filename = timestamp + ".stacktrace";

        new doitAsync(stacktrace, filename,  t,  e).execute();
        // defaultUEH.uncaughtException(t, e);
    }

    class doitAsync extends AsyncTask<Void, Integer, Integer>
    {
        private final String filename;
        private final String stacktrace;
        private final Throwable e;
        private final Thread t;

        doitAsync(String stacktrace, String filename, Thread t, Throwable e) {
            this.stacktrace = stacktrace;
            this.filename = filename;
            this.t = t;
            this.e = e;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (localPath != null) {
                writeToFile(stacktrace, filename);
            }
            if (url != null) {
                sendToServer(stacktrace, filename);
            }
            defaultUEH.uncaughtException(t, e);
            return null;
        }

        private void writeToFile(String stacktrace, String filename) {
            try {
                BufferedWriter bos = new BufferedWriter(new FileWriter(
                        localPath + "/" + filename));
                bos.write(stacktrace);
                bos.flush();
                bos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void sendToServer(String stacktrace, String filename)  {
            try{
                URL object=new URL(url);
                HttpURLConnection con = (HttpURLConnection) object.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                //con.setRequestProperty("Content-Type", "application/txt");
                //con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");

                // https://stackoverflow.com/questions/9767952/how-to-add-parameters-to-httpurlconnection-using-post#13486223
                OutputStream os = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(  new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery("filename", filename));
                writer.write(getQuery("stacktrace", stacktrace));

                writer.flush();
                writer.close();
                os.close();
                con.connect();

                String response="";
                int responseCode=con.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
                }
                else {
                    response="";

                }

            } catch (IOException e) {
                Log.e( this.getClass().getName(), e.getMessage() );
            }
        /*
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("filename", filename));
        nvps.add(new BasicNameValuePair("stacktrace", stacktrace));
        try {
            httpPost.setEntity(
                    new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        }

        private String getQuery(String name, String value) throws UnsupportedEncodingException
        {
            return URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8")+"&";
        }

    }

    // https://github.com/Pretz/improved-android-remote-stacktrace/blob/master/src/com/nullwire/trace/ExceptionHandler.java
    public static boolean register(Context context) {
        new Thread() {
            @Override
            public void run() {
                UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
                if (currentHandler != null) {
                    Log.d(this.getClass().getName(), "current handler class=" + currentHandler.getClass().getName());
                }
                // don't register again if already registered
                if (!(currentHandler instanceof CustomExceptionHandler)) {
                    // Register default exceptions handler
                    Thread.setDefaultUncaughtExceptionHandler(
                            new CustomExceptionHandler(null, "http://preisfrieden.de/WiQuizPedia/upload.php"));
                }
            }
        }.start();
        return true;
    }
}
