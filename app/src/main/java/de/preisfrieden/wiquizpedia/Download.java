package de.preisfrieden.wiquizpedia;

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
 * Created by peter on 05.03.2018.
 */

public class Download {

    public static String NOCACHE = "NOCACHE";
    private static Map<String,String> cache = new HashMap<String,String>();

    public static void inject (String url, String data) {
        cache.put(url, data);
    }

    public String downloadUrl(String... urls) {
        String result = null;
        if ( urls != null && urls.length > 0) {
            String urlString = urls[0];
            result = urls.length>1 && urls[1].contains( NOCACHE) ? null : cache.get( urlString) ;
            if (null == result ) {
                try {
                    URL url = new URL(urlString);
                    result = downloadUrl(url);
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

    private String downloadUrl(URL url) throws IOException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            // Open communications link (network traffic occurs here).
            // http://www.evanjbrunner.info/posts/json-requests-with-httpurlconnection-in-android/
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            connection.connect();
            //publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS);
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            //publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0);
            if (stream != null) {
                // Converts Stream to String with max length of 500.
                result = readStream(stream, 50000);
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

}
