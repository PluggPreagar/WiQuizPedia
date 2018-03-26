package de.preisfrieden.wiquizpedia.trf;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.preisfrieden.wiquizpedia.util.CustomExceptionHandler;

/**
 * Created by peter on 11.03.2018.
 */

public class FileCache {


    private static String LOG_TAG  = "FileCache";
    private static File cacheFile = null;
    private static File cacheDir = null;
    private static MessageDigest messageDigest = getHashGenerator();

    static public MessageDigest getHashGenerator() {
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            CustomExceptionHandler.uncaughtException(e);
            e.printStackTrace();
        }
        return messageDigest;
    }

    // https://developer.android.com/training/data-storage/files.html#WriteExternalStorage
    public static File setPrivateStorageDir(Context context, String albumName) {
        String dir = null ; // Environment.DIRECTORY_DOCUMENTS;
        cacheDir = new File(context.getExternalFilesDir(dir), albumName);
        if (!cacheDir.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        } else {
            cacheFile = new File(cacheDir,"wikquizpeda");
        }
        return cacheDir;
    }

    public String getFileName(String id){
        return  new String(messageDigest.digest(id.getBytes()));
    }

    public String get(String id) {
        return "";
    }

    public String set(String id, String data){
        return "";
    }

}
