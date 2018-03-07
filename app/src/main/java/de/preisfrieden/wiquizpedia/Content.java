package de.preisfrieden.wiquizpedia;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by peter on 21.02.2018.
 */

public class Content {

    String title = "";
    String msg_orig = "";
    String msg_wo_token = "";
    List<String> msg_querable_sentences = new ArrayList<String>();
    int msg_querable_sentences_total = 0;
    List<ContentQuery> recentQuery = new ArrayList<ContentQuery>();

    public static Tokens token = new Tokens();
    private DownloadCallback mCallback;
    private static Random random = new Random(System.nanoTime());



    static public void updatePicFromData(String data, DownloadCallback callback) {
        // http://wikimedia.7.x6.nabble.com/What-is-the-Full-URL-of-the-images-returned-by-a-wikipedia-query-td1147620.html
        // https://en.wikipedia.org/w/api.php?action=query&titles=Albert%20Einstein&generator=images&gimlimit=5&prop=imageinfo&iiprop=url
        class picRefDown implements DownloadCallback {
            DownloadCallback callback;
            public void getUrl(String title, DownloadCallback callback){
                this.callback = callback;
                DownloadTask2 downloadTask = new DownloadTask2( this );
                downloadTask.execute("https://de.wikipedia.org/w/api.php?action=query&generator=images&gimlimit=5&prop=imageinfo&iiprop=url&format=json&titles=" + title);
            }

            @Override
            public void updateFromDownload(Object result) {
                String jsonRows = (String) result;
                List<String> imageUrls = new ArrayList<String>();
                if (null != jsonRows) {
                    for (String row : jsonRows.split("\n|\\{|\\}")) { // TODO iterate through
                        if (row.contains("\"url\":") && !row.toLowerCase().contains(".svg"))
                            imageUrls.add( row.replaceAll("^.*\"url\":\\s*\"", "").replaceAll("\".*$", ""));
                    }
                    DownloadDrawable downloadTask = new DownloadDrawable(callback);
                    if (!imageUrls.isEmpty()) downloadTask.execute(imageUrls.get(random.nextInt( imageUrls.size())));
                }
            }
        }

        String titleExtr = data.replaceAll("\n","").replaceAll("^.*?\"title\":\"","").replaceAll("\".*","");
        new picRefDown().getUrl( URLEncoder.encode(titleExtr), callback);
    }



    // --------------------------------------------

    public Content(){
    }

    public String getURL2View(){
        String urlString = "";
        try {
            urlString = "https://de.wikipedia.org/wiki/" + URLEncoder.encode(title.replaceAll(" ","_"), "utf-8") ;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return urlString ;
    }


    public ContentQuery parseUrl(String query) {
        String data = readContentData(query);
        parse( data );
        return createQuery();
    }

    public String readContentData(String query)   {
        String prop = "";
        String urlStr = "https://de.wikipedia.org/w/api.php?action=query&prop=extracts&exintro=&explaintext=&redirects=&formatversion=2&format=json";
        if ( 0 != (Settings.mode & Settings.MODE_FULL)) urlStr = urlStr.replace("&exintro=&","&exsentences=50&");
        // urlStr = "https://de.wikipedia.org/w/api.php?action=query&exlimit=2&format=json&prop=extracts&explaintext=&formatversion=2&rvprop=content";
        // if ( 0 == (Content.mode & MODE_FULL)) urlStr += "&exintro=";
        // urlStr  = urlStr.replace("&prop=","&prop=revisions|"); // add wiki text
        // urlStr  = urlStr.replace("&prop=","&prop=revisions|"); // add wiki text to get REFS ..

        // https://de.wikipedia.org/w/api.php?action=query&exlimit=2&format=json&prop=revisions|extracts&explaintext=&titles=Albert_Einstein&redirects=1&formatversion=2&rvprop=content&exlimit=max

        //urlStr += "&rvprop=content";

        // https://en.wikipedia.org/wiki/Special:ApiSandbox#action=query&format=json&prop=categories%7Cimages%7Clinks%7Cextlinks%7Clinkshere&titles=Albert%20Einstein
        // /w/api.php?action=query&format=json&prop=categories%7Cimages%7Clinks%7Cextlinks%7Clinkshere&titles=Albert%20Einstein

        // how to download
        // https://stackoverflow.com/questions/3028306/download-a-file-with-android-and-showing-the-progress-in-a-progressdialog

        if (query.isEmpty()) {
            // https://stackoverflow.com/questions/33614492/wikipedia-api-get-random-pages
            // --> https://en.wikipedia.org/w/api.php?format=json&action=query&generator=random&grnnamespace=0&prop=revisions|images&rvprop=content&grnlimit=10
            urlStr += "&generator=random";
            prop = DownloadTask2.NOCACHE;
        } else {
            try {
                urlStr += "&titles=" + URLEncoder.encode(query, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        Download download = new Download();
        String data = "";
        do {
            data = download.downloadUrl(urlStr, prop);
            prop = DownloadTask2.NOCACHE;
        } while (query.isEmpty() && parseTitle(data).contains(":")); // skipp "Diskussion:" "Benutzer:" ... wikipedia-pseudo sites

        return data;
    }

    protected Content parse(String msg){
        if (null != msg_orig && !msg_orig.equals(msg)){
            msg_orig = msg;
            msg_wo_token = msg;

            if (0 == (Settings.mode & Settings.MODE_TOKEN_ACROSS_PAGES)) token.clear();

            title = parseTitle(msg);
            parseRef(msg);

            msg_wo_token = normaliseContent(msg_wo_token);
            msg_wo_token = extractAndReplaceRef( msg_wo_token);
            msg_wo_token = extractAndReplaceValues(msg_wo_token );

            msg_querable_sentences = extractSentences(msg_wo_token );
            msg_querable_sentences_total = msg_querable_sentences.size();
        }
        return this;
    }

    protected String parseTitle(String msg) {
        title = null == msg ? "" : msg.replaceAll("\\n","").replaceAll("^.*?\"title\":\\s*\"", "").replaceAll("\".*", "");
        return title;
    }

    protected void parseRef(String msg) {
        Pattern p = Pattern.compile("\\[\\[([^\"]*?)(?:\\|([^\"]*?))?\\]\\]", Pattern.MULTILINE + Pattern.DOTALL);
        Matcher m = p.matcher(msg);
        while (m.find()) {
            token.add(m.group(1),"Ref", null != m.group(2) ? m.group(2) : m.group(1));
        }
    }

    protected String normaliseContent(String msg) {
        // https://cloud.google.com/natural-language/docs/basics#syntactic_analysis
        // msg = msg.replaceFirst("^.*?extract\":\"","").replaceAll("\"}\\]}}?","");
        msg = msg.replaceAll("^\\s.*","").replaceAll("\\n","");
        msg = msg.replaceFirst("^.*?extract\":\\s*\"", "");
        msg = msg.replaceFirst("^.*?content\":\\s*\"", "");
        msg = msg.replaceAll("[^.]+$", "");
        Pattern braces = Pattern.compile("\\{\\{[^\\{]*?\\}\\}");
        while( braces.matcher( msg).find() ) {
        //while( msg.matches("\\{\\{[^\\{]*?\\}\\}") ) {
            msg = msg.replaceAll("\\{\\{[^\\{]*?\\}\\}", ""); // {{..}}
        }
        msg = msg.replaceAll("\\\\n", "\n");
        msg = msg.replaceAll("(\\d{2}\\.)\\s+(Januar|Februar|März|April|Mai|Juni|Juli|August|September|Oktober|November|Dezember)\\s*(\\d{2,4})", "$1$2$3"); // 14. März 1879 -> 14.März.1879 sep Date from End of Sentencte
        msg = msg.replaceAll("(?<!\\d)\\.\\s", ".\n");
        return msg;
    }

    protected String extractAndReplaceRef(String msg) {
        ArrayList<String> refsOrig = token.getTokens4Category("Ref");
        if (null != refsOrig) {
            ArrayList<String> refs = (ArrayList<String>) refsOrig.clone();
            Collections.sort(refs, Collections.reverseOrder()); // long first
            for (String value : refs) {
                int idx = refsOrig.indexOf( value);  // TODO kludge loose REF-IDX
                extractAndReplaceTokenByPattern( msg, "__Ref" + idx + "__", "(" + Pattern.quote(value) + ")", -1);
            }
        }
        return msg;
    }

    protected String extractAndReplaceValues(String msg) {
        msg = extractAndReplaceTokenByPattern( msg,"Date", "(\\d{2}\\.)\\s*(Januar|Februar|März|April|Mai|Juni|Juli|August|September|Oktober|November|Dezember|\\d{2,4}\\.)\\s*(\\d{2,4})", -1);
        msg = extractAndReplaceTokenByPattern( msg,"DateYM", "\\b([0-9]{4}/(?:[1-9]|1[012]))\\b", -1);
        msg = extractAndReplaceTokenByPattern( msg,"Year", "\\b([0-9]{4})\\b", -1);
        msg = extractAndReplaceTokenByPattern( msg,"RangAdj", "\\b((?:zweit|dritt|viert|fünft|sechst|siebent|acht|neun|zehnt|elft|zwölft)(?:größte|kleinste|höchste))\\b", -1);
        msg = extractAndReplaceTokenByPattern( msg,"RangNum", "\\b[0-9]+\\.\\b", -1);
        msg = extractAndReplaceTokenByPattern( msg,"BigNum", "\\b([0-9]+[.][0-9]+(?:,[0-9]+)?)\\b", -1);
        msg = extractAndReplaceTokenByPattern( msg,"Real", "\\b([0-9]+)\\b", -1);
        msg = extractAndReplaceTokenByPattern( msg,"Number", "\\b([0-9,]+)\\b", -1);
        msg = extractAndReplaceTokenByPattern( msg,"Ort", "\\b(in\\s+[A-Z][^,. ]+)\\b", 0.10);
        //msg = extractAndReplaceTokenByPattern( msg,"Subst", "\\b(der|die|das)(\\s+[A-Z][^,. ]+)\\b", 0.75);
        if (0 != (Settings.mode & Settings.MODE_TOKEN_NOUN_MARKER)){
            msg = extractAndReplaceTokenByPattern( msg,"der", "\\b(der)(\\s+[A-Z][^,. ]+)\\b", 0.75);
            msg = extractAndReplaceTokenByPattern( msg,"die", "\\b(die)(\\s+[A-Z][^,. ]+)\\b", 0.75);
            msg = extractAndReplaceTokenByPattern( msg,"das", "\\b(das)(\\s+[A-Z][^,. ]+)\\b", 0.75);
        }

        // add dummy years ...

        ArrayList<String> years = new ArrayList<String>(token.emptyIfNull(token.getTokens4Category("Year"))); // clone, as we will sort ..
        if (!years.isEmpty() && years.size() < 5){
            Collections.sort(years);
            Integer minYear = Integer.valueOf(years.get(0));
            Integer maxYear = Integer.valueOf(years.get(years.size()-1));
            if (minYear.equals(maxYear)) maxYear = (int) (( (double) maxYear + 10  ) * 1.05);
            Integer diffYear = maxYear - minYear;
            Integer minminYear = minYear - ((int)(diffYear *  0.2) + 1) ;
            Integer maxmaxYear = maxYear + ((int)(diffYear *  0.2) + 1) ;
            int size = 0;
            while (size<3) {
                String year = String.valueOf(minminYear + random.nextInt( (int) (maxmaxYear-minminYear)));
                size = token.getIdx(token.add(year, "Year"));
            }

        }


        // TODO remove groups wo option
        return msg;
    }

    protected String extractAndReplaceTokenByPattern(String msg, String categoryOrId, String pattern, double percent2keep ) {
        String matched = "";
        int matchLen = 0;
        Pattern p = Pattern.compile("(.*?)" + pattern, Pattern.MULTILINE + Pattern.DOTALL);
        Matcher m = p.matcher(msg);
        while (m.find()) {
            String value = "";
            String key  = "";
            boolean keep = random.nextDouble() <= percent2keep;
            for (int i = 2; i <= m.groupCount(); i++) value += m.group(i);
            if (value.length()>1) {
                key = categoryOrId.startsWith("__") ? categoryOrId : token.add(value, categoryOrId);
            } else {
                keep = true;
            }
            matched += keep ? m.group(0) : m.group(1) + key;
            matchLen += m.group(0).length();
        }
        return 0 == matchLen ? msg : matched + msg.substring(matchLen);
    }

    protected List<String> extractSentences(String msg) {
        String[] sentences = msg.split("\n");
        List<String> query_sentences = new ArrayList<String>();
        for (String sentence : sentences) {
            if (sentence.contains("__")) query_sentences.add(sentence);
        }
        return query_sentences;
    }

    public ContentQuery createQuery(){
        ContentQuery query = null;
        if (null != msg_querable_sentences && !msg_querable_sentences.isEmpty()){
            if (recentQuery.size()>20) recentQuery = recentQuery.subList( recentQuery.size() - 10 , recentQuery.size());
            query = new ContentQuery(msg_querable_sentences,  token, this);
            if (null != query) recentQuery.add(query);
        }
        return query;
    }

    public ContentQuery getLastQuery(){
        return recentQuery.isEmpty() ? createQuery() : recentQuery.remove(recentQuery.size()-1);
    }

}
