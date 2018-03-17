package de.preisfrieden.wiquizpedia;

/**
 * Created by peter on 17.03.2018.
 */

public class Util {

    public static String shortenString(String msg) {
        return null == msg ? "<null>" : msg.length() < 30 ? msg : msg.replaceAll("^(.{0,20}).*?(.{0,20})$", "$1 ... $2");
    }
}
