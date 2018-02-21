package de.preisfrieden.wiquizpedia;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.TokenWatcher;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.ArraySet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
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

    public class Content {
        String title = "";
        String msg_orig ="";
        String msg_wo_token = "";
        List<String> msg_querable_sentences = new ArrayList<String>();

        class TokenValueMap extends HashMap<String,String> {}  // { __Date1__ => 1.5.1987, __Date2__ => 1.9.1990}
        class TokenMap extends HashMap<String,TokenValueMap> {} // [ Date => { __Date1__ => 1.5.1987, __Date2__ => 1.9.1990}, Year => {...}
        TokenMap token = new TokenMap() ;
        Query2 query;

        class Query2 {
            String msg = "";
            int msg_id ;
            String answer_ok = "";
            List<String> answers = new ArrayList<String>();
            boolean first_try = true;
            String answer_token = "";
            Content content;

            public boolean checkAnswer(String answer) {
                return content.checkAnswer(answer);
            }
        }

        public Content(String msg) {
            this.msg_orig = msg;
            this.msg_wo_token = extractContent(msg);

            parseTitle(msg);
            extractValues();
            extractSentences();

        }

        public String parseTitle(String msg){
            title = msg.replaceAll("^.*?\"title\":\"","").replaceAll("\".*","");
            return title;
        }

        public Content parseRef(String msg){
            Pattern p = Pattern.compile("[[(.*?)(?:\\|(.*?))?]]" , Pattern.MULTILINE + Pattern.DOTALL);
            Matcher m = p.matcher(msg);

            TokenValueMap tokenValueMapRef = token.containsKey("Ref") ? token.get("Ref") : new TokenValueMap();
            TokenValueMap tokenValueMapREF = token.containsKey("REF") ? token.get("REF") : new TokenValueMap();
            while (m.find()) {
                tokenValueMapRef.put("__Ref" + tokenValueMapRef.size() + "__" , m.group(1));
                tokenValueMapRef.put("__REF" + tokenValueMapREF.size() + "__" , null != m.group(2) ? m.group(2) : m.group(1));
            }
            token.put("Ref", tokenValueMapRef);
            token.put("REF", tokenValueMapREF);

            return this;
        }


        public String extractRef(String msg){
            TokenValueMap tokenValueMap = token.get("REF");
            if (null!=tokenValueMap) {
                ArrayList<String> listOfKeys = new ArrayList<String>(tokenValueMap.keySet());
                Collections.sort(listOfKeys, Collections.reverseOrder()); // long first
                for (String value : listOfKeys) {
                    extractValues("REF", "(" + Pattern.quote(value) + ")", 1);
                }
            }
            return msg_wo_token;
        }

        public String extractContent(String msg){
            // https://cloud.google.com/natural-language/docs/basics#syntactic_analysis
            // msg = msg.replaceFirst("^.*?extract\":\"","").replaceAll("\"}\\]}}?","");

            //title =

            String msg2 = msg.replaceFirst("^.*?extract\":\"","");
            msg2 = msg2.replaceAll("[^.]+$","");
            msg2 = msg2.replaceAll("\\\\n","\n");
            msg2 = msg2.replaceAll("(\\d{2}\\.)\\s+(Januar|Februar|März|April|Mai|Juni|Juli|August|September|Oktober|November|Dezember)\\s*(\\d{2,4})","$1$2$3"); // 14. März 1879 -> 14.März.1879 sep Date from End of Sentencte
            msg2 = msg2.replaceAll("(?<!\\d)\\.\\s",".\n");
            return msg2;
        }

        // -- values


        private void extractValues(){
            extractValues("Date", "(\\d{2}\\.)\\s*(Januar|Februar|März|April|Mai|Juni|Juli|August|September|Oktober|November|Dezember|\\d{2,4}\\.)\\s*(\\d{2,4})",3);
            extractValues("Year","\\b([0-9]{4})\\b",1);
            extractValues("Number","\\b([0-9,]+)\\b",1);

            // TODO remove groups wo option

        }

        private void extractValues(String tokenType,String pattern, int patternCount){
            String nonMatch = "" ;
            int matched = 0;
            Pattern p = Pattern.compile("(.*?)" + pattern, Pattern.MULTILINE + Pattern.DOTALL);
            Matcher m = p.matcher(msg_wo_token);
            TokenValueMap tokenValueMap = token.get(tokenType);
            if (null == tokenValueMap) tokenValueMap = new TokenValueMap();
            while (m.find()) {
                String value ="";
                for (int i = 2 ; i<=  m.groupCount() ; i++) value += m.group(i);

                String key =  "__" + tokenType + tokenValueMap.size() + "__" ;
                tokenValueMap.put( key,value );
                matched += m.group(0).length();
                nonMatch += m.group(1) + key;
            }
            token.put(tokenType, tokenValueMap);
            if (0!=matched) msg_wo_token = nonMatch + msg_wo_token.substring(matched);
        }

        // -- sentences

        private void extractSentences() {
            String[] sentences = msg_wo_token.split("\n");
            for (String sentence : sentences) {
                if (sentence.contains("__")) msg_querable_sentences.add(sentence);
            }
        }

        // -- query

        public Query2 getQuery () {
            if (!msg_querable_sentences.isEmpty()){
                int msg_query_id = new Random().nextInt(msg_querable_sentences.size());
                String msg_query = msg_querable_sentences.get(msg_query_id); // getRandom(msg_querable_sentences);

                Pattern p = Pattern.compile("(__([A-Za-z]+)[0-9]+__)", Pattern.MULTILINE + Pattern.DOTALL);
                Matcher m = p.matcher(msg_query);
                List<String> tokenType = new ArrayList<String>();
                List<String> keys = new ArrayList<String>();
                Map<String, String> tokenType2key = new HashMap<String, String>();
                while (m.find()) {
                    keys.add(m.group(1));
                    tokenType.add(m.group(2));
                    tokenType2key.put(m.group(1), m.group(2));
                }

                String key_choosen = getRandom(keys);
                TokenValueMap tokenTypeMap_choosen = token.get(tokenType2key.get(key_choosen));
                for (String key : keys)
                    if (!key.equals(key_choosen))
                        msg_query = msg_query.replace(key, token.get(tokenType2key.get(key)).get(key));


                //List<String> tokenValues = new ArrayList<String>(Arrays.asList( tokenTypeMap_choosen.keySet().toArray(new String[0])));
                //HashSet<String> tokenValues = new HashSet<>(tokenTypeMap_choosen.keySet());
                ArrayList<String> tokenValues = new ArrayList<String>(new HashSet<>(tokenTypeMap_choosen.keySet())); // uniq values

                List<String> values_answer = new ArrayList<String>();
                if (tokenValues.size() > 1) {
                    values_answer.add(tokenTypeMap_choosen.get(key_choosen));
                    tokenValues.remove(key_choosen);
                    values_answer.add(tokenTypeMap_choosen.get(getRandom(tokenValues)));
                    tokenValues.remove(values_answer.get(0));
                    values_answer.add(tokenTypeMap_choosen.get(getRandom(tokenValues)));
                }

                Collections.shuffle(values_answer, new Random(System.nanoTime()));

                query = new Query2();
                query.msg = msg_query;
                query.msg_id = msg_query_id;
                query.answer_ok = tokenTypeMap_choosen.get(key_choosen);
                query.answers = values_answer;
                query.answer_token = key_choosen;
                query.content = this;
            }
            return query;
        }

        // --
        public boolean checkAnswer(String answer){
            boolean correct = query.answer_ok.toLowerCase().equals(answer.toLowerCase()) ;
            if (correct && query.first_try) {
                //for ( int i=0 ; i < msg_querable_sentences.size() ; i++ ) {
                    int i = query.msg_id;
                    String msg = msg_querable_sentences.get(i);
                    //if (msg.equals(query.msg)) {
                        msg = msg.replace(query.answer_token, query.answer_ok);
                        msg_querable_sentences.set(i, msg);
                        if (!msg.contains("__")) {
                            msg_querable_sentences.remove(i);
                            Toast.makeText(MainActivity.this, "query is solved", Toast.LENGTH_LONG).show();
                        }
                    //}
                //}
            }
            query.first_try = false;
            return correct;
        }

        // -- helper

        public String getRandom(String[] array) {
            int rnd = new Random().nextInt(array.length);
            return array[rnd];
        }

        public String getRandom(List<String> array) {
            int rnd = new Random().nextInt(array.size());
            return array.get(rnd);
        }

        public String getRandom(Set<String> array) {
            int rnd = new Random().nextInt(array.size());
            return array.toArray(new String[0])[rnd];
        }


    }

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    DownloadTask2 dwnlTask;
    public static Content.Query2 query;

    public static final ColorStateList colorStateListNormal = new ColorStateList(new int[][]{
            new int[]{android.R.attr.state_checked}
            , new int[]{-android.R.attr.state_checked}
    }
            , new int[]{Color.BLUE, Color.YELLOW}
    );
    public static final ColorStateList colorStateListRight = new ColorStateList(new int[][]{
            new int[]{android.R.attr.state_checked}
            , new int[]{-android.R.attr.state_checked}
    }
            , new int[]{Color.RED, Color.GREEN}
    );
    public static final ColorStateList colorStateListWrong = new ColorStateList(new int[][]{
            new int[]{android.R.attr.state_checked}
            , new int[]{-android.R.attr.state_checked}
    }
            , new int[]{Color.GREEN, Color.RED}
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.tv_question)).setMovementMethod(new ScrollingMovementMethod());
        EditText urlEt = (EditText) findViewById(R.id.url_et);
        urlEt.setText("Albert Einstein");
        setURL(null);
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
        String prop ="";

        // https://en.wikipedia.org/wiki/Special:ApiSandbox#action=query&format=json&prop=categories%7Cimages%7Clinks%7Cextlinks%7Clinkshere&titles=Albert%20Einstein
        // /w/api.php?action=query&format=json&prop=categories%7Cimages%7Clinks%7Cextlinks%7Clinkshere&titles=Albert%20Einstein

        // how to download
        // https://stackoverflow.com/questions/3028306/download-a-file-with-android-and-showing-the-progress-in-a-progressdialog

        // new DownloadManager.Request();
        updateFromDownload((String) null);
        dwnlTask = new DownloadTask2(MainActivity.this);
        //dwnlTask.cancel(false);
        EditText editText = (EditText) findViewById(R.id.url_et);
        if (editText.getText().toString().isEmpty()) {
            // https://stackoverflow.com/questions/33614492/wikipedia-api-get-random-pages
            // --> https://en.wikipedia.org/w/api.php?format=json&action=query&generator=random&grnnamespace=0&prop=revisions|images&rvprop=content&grnlimit=10
            urlStr += "&generator=random";
            prop = DownloadTask2.NOCACHE;
        } else {
            try {
                urlStr += URLEncoder.encode(String.valueOf(editText.getText()), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        dwnlTask.execute(urlStr, prop);
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
    public void updateFromDownload2(Drawable result) {
    }
    public void updateFromDownload2(String result) {

    }

    public void updatePicFromData(String data) {
        // http://wikimedia.7.x6.nabble.com/What-is-the-Full-URL-of-the-images-returned-by-a-wikipedia-query-td1147620.html
        // https://en.wikipedia.org/w/api.php?action=query&titles=Albert%20Einstein&generator=images&gimlimit=5&prop=imageinfo&iiprop=url
        class picRefDown implements DownloadCallback {
            public void getUrl(String title){
                DownloadTask2 downloadTask = new DownloadTask2( this );
                downloadTask.execute("https://de.wikipedia.org/w/api.php?action=query&generator=images&gimlimit=5&prop=imageinfo&iiprop=url&format=json&titles=" + title);
            }

            @Override
            public void updateFromDownload(Object result) {
                String jsonRows = (String) result;
                String imageUrl = "";
                if (null != jsonRows) {
                    for (String row : jsonRows.split("\n")) { // TODO iterate through
                        if (row.contains("\"url\":"))
                            imageUrl = row.replaceAll("^.*\"url\":\"", "").replaceAll("\".*$", "");
                    }
                    DownloadDrawable downloadTask = new DownloadDrawable(MainActivity.this);
                    downloadTask.execute(imageUrl);
                }
            }
        }

        String titleExtr = data.replaceAll("\n","").replaceAll("^.*?\"title\":\"","").replaceAll("\".*","");
        new picRefDown().getUrl( URLEncoder.encode(titleExtr));
    }


    @Override
    public void updateFromDownload(Object result) {
        TextView editText = (TextView) findViewById(R.id.tv_question);
        String msg = null ;
        if ( null == result) {
        } else if (result instanceof  Drawable) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageDrawable( (Drawable) result);
        } else if (result instanceof  String){
            //msg = extractContent(msg);
            msg = "loading ...";
            String msgLoaded = (String) result;
            Content content = (null == query || !query.content.msg_orig.equals(msgLoaded)) ?  new Content(msgLoaded) : query.content;
            query = content.getQuery();
            CheckBox cb1 = (CheckBox) findViewById(R.id.cb_answer_1);
            CheckBox cb2 = (CheckBox) findViewById(R.id.cb_answer_2);
            CheckBox cb3 = (CheckBox) findViewById(R.id.cb_answer_3);
            if (null == query ){
                msg = "no query found - please try different search term or without term for random page .. enjoy ;-) (here the result of your term:\n" + content.msg_orig+")";
                cb1.setVisibility( View.INVISIBLE);
                cb2.setVisibility( View.INVISIBLE);
                cb3.setVisibility( View.INVISIBLE);
            } else {
                // query.first_try = true; // TODO should be done by getQuery
                //if (query.answer_fail.size()>1) {
                // LoadImageFromWebOperations("https://de.wikipedia.org/wiki/Datei:Einstein_1921_portrait2.jpg");
                //DownloadDrawable drawableDownload = new DownloadDrawable(MainActivity.this);
                //drawableDownload.execute("https://upload.wikimedia.org/wikipedia/commons/thumb/f/f5/Einstein_1921_portrait2.jpg/192px-Einstein_1921_portrait2.jpg");
                updatePicFromData( query.content.msg_orig);
                editText.setText(query.content.title);
                if ("" != query.answer_ok) {
                    cb1.setVisibility(query.answers.isEmpty() ? View.INVISIBLE : View.VISIBLE);
                    cb2.setVisibility(query.answers.isEmpty() ? View.INVISIBLE : View.VISIBLE);
                    cb3.setVisibility(query.answers.isEmpty() || query.answers.size() < 3 ? View.INVISIBLE : View.VISIBLE);
                    if (!query.answers.isEmpty()) {
                        cb1.setText(query.answers.get(0));
                        cb2.setText(query.answers.get(1));
                        if (query.answers.size() > 2) cb3.setText(query.answers.get(2));
                        /*
                        cb1.setSelected(query.answer_ok.equals( query.answers.get(0)));
                        cb2.setSelected(query.answer_ok.equals( query.answers.get(1)));
                        cb3.setSelected(query.answer_ok.equals( query.answers.get(2)));
                        */
                        /*
                        cb1.setTextColor( query.answer_ok.equals( query.answers.get(0)) ? Color.GREEN : Color.BLACK );
                        cb2.setTextColor( query.answer_ok.equals( query.answers.get(1)) ? Color.GREEN : Color.BLACK );
                        cb3.setTextColor( query.answer_ok.equals( query.answers.get(2)) ? Color.GREEN : Color.BLACK );
                        */
                        cb1.setTextColor(Color.BLACK);
                        cb2.setTextColor(Color.BLACK);
                        cb3.setTextColor(Color.BLACK);
                        cb1.setChecked(false);
                        cb2.setChecked(false);
                        cb3.setChecked(false);
                        cb1.setBackgroundColor(Color.TRANSPARENT);
                        cb2.setBackgroundColor(Color.TRANSPARENT);
                        cb3.setBackgroundColor(Color.TRANSPARENT);
                        /* API 23
                        cb1.setCompoundDrawableTintList(colorStateListNormal);
                        cb1.setForegroundTintList(colorStateListNormal);
                        */
                        cb1.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                                                           @Override
                                                           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                               // buttonView.setTextColor(MainActivity.query.answer_ok.equals(buttonView.getText().toString()) ? Color.GREEN : Color.RED);
                                                               // buttonView.setBackgroundColor( MainActivity.query.checkAnswer( isChecked ? buttonView.getText().toString() : "" ) == isChecked ? Color.GREEN : isChecked ? Color.RED : Color.TRANSPARENT);
                                                               if (isChecked)
                                                                   buttonView.setBackgroundColor(MainActivity.query.checkAnswer(isChecked ? buttonView.getText().toString() : "") == isChecked ? Color.GREEN : isChecked ? Color.RED : Color.TRANSPARENT);
                                                           }
                                                       }
                        );
                        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                           @Override
                                                           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                               //buttonView.setTextColor(MainActivity.query.answer_ok.equals(buttonView.getText().toString()) ? Color.GREEN : Color.RED);
                                                               if (isChecked)
                                                                   buttonView.setBackgroundColor(MainActivity.query.checkAnswer(isChecked ? buttonView.getText().toString() : "") == isChecked ? Color.GREEN : isChecked ? Color.RED : Color.TRANSPARENT);
                                                           }
                                                       }
                        );
                        cb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                           @Override
                                                           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                               //buttonView.setTextColor(MainActivity.query.answer_ok.equals(buttonView.getText().toString()) ? Color.GREEN : Color.RED);
                                                               if (isChecked)
                                                                   buttonView.setBackgroundColor(MainActivity.query.checkAnswer(isChecked ? buttonView.getText().toString() : "") == isChecked ? Color.GREEN : isChecked ? Color.RED : Color.TRANSPARENT);
                                                           }
                                                       }
                        );


                    }
                    TextView answer = (TextView) findViewById(R.id.et_answer);
                    answer.setTextColor(Color.BLACK);
                    answer.setBackgroundColor(Color.TRANSPARENT);
                    answer.setText("direct answer ...");
                    answer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView answer = (TextView) findViewById(R.id.et_answer);
                            answer.setText("");
                        }
                    });
                    answer.setOnKeyListener(new OnKeyListener() {
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                                TextView answer = (TextView) findViewById(R.id.et_answer);
                                answer.setBackgroundColor(MainActivity.query.checkAnswer(answer.getText().toString()) ? Color.GREEN : Color.RED);
                                return true;
                            }
                            return false;
                        }
                    });

                    msg = query.msg;
                }
            }
            /*
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
            */
        }

        if (null != msg ) editText.setText( msg );
    }


    public static String getRandom(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    public static String getRandom(List<String> array) {
        int rnd = new Random().nextInt(array.size());
        return array.get(rnd);
    }



    private List<String> extractValues(String msg){
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





}
