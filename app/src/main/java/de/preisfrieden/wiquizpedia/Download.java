package de.preisfrieden.wiquizpedia;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by peter on 05.03.2018.
 */

public class Download {

    public static String FORCELOAD = "FORCELOAD";
    private static Map<String,String> cache = new HashMap<String,String>();

    public static void inject (String url, String data) {
        cache.put(url, data);
    }

    public String downloadUrl(String... urls) {
        String result = null;
        String fileName = urls.length>2 ? urls[2] : null;
        if ( urls != null && urls.length > 0) {
            String urlString = urls[0];
            result = urls.length>1 && urls[1].contains(FORCELOAD) ? null : cache.get( urlString) ;
            if (null == result ) {
                try {
                    URL url = new URL(urlString);
                    result = downloadUrl(url, fileName);
                    cache.put(urlString, result);
                    // if (resultString == null) throw new IOException("No response received.");
                } catch (Exception e) {
                    e.printStackTrace();
                    //throw new IOException(e);
                }
            }
        }
        return result;
    }

    private String downloadUrl(URL url, String fileName) throws IOException {
        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection ) url.openConnection();
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            if (!url.getFile().endsWith(".apk")) connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            //publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0);
            if (stream != null) {
                result = null == fileName ? readStream(stream, 50000) : readStream(stream, fileName);// Converts Stream to String with max length of 500.
            }
        } catch(Exception e) {
            e.printStackTrace();
            //throw new IOException(e);
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    /**
     * Converts the contents of an InputStream to a String.
     */
    public String readStream(InputStream stream, int maxReadSize)
            throws IOException, UnsupportedEncodingException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] rawBuffer = new char[maxReadSize];
        int readSize;
        StringBuffer buffer = new StringBuffer();
        while (((readSize = reader.read(rawBuffer)) != -1) && maxReadSize > 0) {
            if (readSize > maxReadSize) {
                readSize = maxReadSize;
            }
            buffer.append(rawBuffer, 0, readSize);
            maxReadSize -= readSize;
        }
        return buffer.toString();
    }

    public String readStream(InputStream instream, String fileName)
            throws IOException, UnsupportedEncodingException {

        File file = new File(fileName);
        file.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(file);

        byte[] buffer = new byte[1024];
        int len1 = 0;
        while ((len1 = instream.read(buffer)) != -1) {
            fos.write(buffer, 0, len1);
        }
        fos.close();
        instream.close();// till here, it works fine - .apk is download to my sdcard in download file

        return fileName;
    }


}
