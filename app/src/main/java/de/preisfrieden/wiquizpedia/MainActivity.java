package de.preisfrieden.wiquizpedia;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity implements DownloadCallback {

    /*
    https://stackoverflow.com/questions/17271147/android-get-content-from-url
    https://developer.android.com/reference/javax/net/ssl/HttpsURLConnection.html

    https://de.wikipedia.org/w/api.php?action=query&prop=extracts&titles=Albert%20Einstein&exintro=&explaintext=&redirects=&formatversion=2
    https://en.wikipedia.org/w/api.php

    https://stackoverflow.com/questions/4460921/extract-the-first-paragraph-from-a-wikipedia-article-python
    https://stackoverflow.com/questions/627594/is-there-a-wikipedia-api


     */
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    DownloadTask2 dwnlTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.tv_question)).setMovementMethod(new ScrollingMovementMethod());
        EditText editText = (EditText) findViewById(R.id.url_et);
        editText.setText("Albert Einstein");
    }

    /** Called when the user taps the Send button */
    public void setURL(View view)   {
        // Do something in response to button
        String urlStr = "";
        urlStr = "http://www.android.com/";
        urlStr = "http://stackoverflow.com/feeds/tag?tagnames=android&sort=newest";
        urlStr = "https://de.wikipedia.org/w/api.php?action=query&prop=extracts&titles=Albert%20Einstein&exintro=&explaintext=&redirects=&formatversion=2";
        urlStr += "&format=json";
        urlStr = "https://de.wikipedia.org/w/api.php?action=query&prop=extracts&exintro=&explaintext=&redirects=&formatversion=2&format=json&titles=";
        urlStr += "";

        // how to download
        // https://stackoverflow.com/questions/3028306/download-a-file-with-android-and-showing-the-progress-in-a-progressdialog

        // new DownloadManager.Request();
        updateFromDownload("loading ... ");
        dwnlTask = new DownloadTask2(MainActivity.this);
        //dwnlTask.cancel(false);
        EditText editText = (EditText) findViewById(R.id.url_et);
        try {
            urlStr +=  URLEncoder.encode(String.valueOf(editText.getText()), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        dwnlTask.execute(urlStr);
        //dwnlTask.doInBackground(urlStr);
    }

     /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.url_et);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    // DownloadCallback  --- implements

    @Override
    public void updateFromDownload(Object result) {
        String msg = (String) result;
        msg = extractQuery(msg);
        TextView editText = (TextView) findViewById(R.id.tv_question);

        List<String> dates = extractDates(msg);
        if (dates.size()>1) {
            String msg_wo_dates = dates.remove(dates.size() - 1);
            String msg_query = getRandom(msg_wo_dates.split("\n+"));

            CheckBox cb1 = (CheckBox) findViewById(R.id.cb_answer_1);
            CheckBox cb2 = (CheckBox) findViewById(R.id.cb_answer_2);
            CheckBox cb3 = (CheckBox) findViewById(R.id.cb_answer_3);
            cb1.setText(dates.get(0));
            cb2.setText(dates.get(1));
            cb3.setText(dates.get(2));
            msg = msg_query;
        }

        editText.setText( msg );
    }

    public static String getRandom(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    public static String getRandom(List<String> array) {
        int rnd = new Random().nextInt(array.size());
        return array.get(rnd);
    }

    private String extractQuery(String msg){
        // https://cloud.google.com/natural-language/docs/basics#syntactic_analysis
        // msg = msg.replaceFirst("^.*?extract\":\"","").replaceAll("\"}\\]}}?","");
        String msg2 = msg.replaceFirst("^.*?extract\":\"","");
        //msg2 = msg2.replaceAll("\\\"}]}}*$","");
        msg2 = msg2.replaceAll("\\n","\n\n");
        msg2 = msg2.replaceAll("(\\d{2}\\.)\\s+(Januar|Februar|März|April|Mai|Juni|Juli|August|September|Oktober|November|Dezember)\\s*(\\d{2,4})","$1$2$3"); // 14. März 1879 -> 14.März.1879 sep Date from End of Sentencte
        msg2 = msg2.replaceAll("\\.\\s","\n\n");
        return msg2;
    }

    private List<String> extractDates(String msg){
        List<String> dates = new ArrayList<String>();
        String nonMatch = "" ;
        int matched = 0;
        Pattern p = Pattern.compile("(.*?)((\\d{2}\\.)\\s*(Januar|Februar|März|April|Mai|Juni|Juli|August|September|Oktober|November|Dezember|\\d{2,4}\\.)\\s*(\\d{2,4})|\\b([0-9]{4})\\b)", Pattern.MULTILINE + Pattern.DOTALL);
        Matcher m = p.matcher(msg);
        while (m.find()) {
            dates.add( null == m.group(6) ? m.group(3) + m.group(4) + m.group(5) : m.group(6));
            matched += m.group(0).length();
            nonMatch += m.group(1) + " _" + dates.size()+ "_ ";
        }
        dates.add( nonMatch + msg.substring(matched) );
        return dates;
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        return null;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {

    }

    @Override
    public void finishDownloading() {
        TextView tv = (TextView) findViewById( R.id.tv_question);
        tv.setText( "got message" );
    }


}
