package de.preisfrieden.wiquizpedia;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by peter on 17.03.2018.
 */

public class Util {

    static Pattern shortenDatePattern = null ;

    public static String shortenString(String msg) {
        return null == msg ? "<null>" : msg.length() < 30 ? msg : msg.replaceAll("^(.{0,20}).*?(.{0,20})$", "$1 ... $2");
    }

    public static String shortenDate(String msg){
        if (null == shortenDatePattern) {
            String pattern =   "yyyy-MM-dd ([0-9]+:[0-9]+)(?:[0-9:]*)"
                            + "|dd\\.MM\\.yyyy ([0-9]+:[0-9]+)(?:[0-9:]*)"
                            + "|yyyy-([0-9]+-[0-9]+) [0-9:]+"
                            + "|([0-9]+\\.[0-9]+\\.)YYYY [0-9:]+"
                            + "|([0-9]{4})[0-9-]+ [0-9:]+"
                            + "|[0-9.]+([0-9]{4}) [0-9:]+"
                    ;
            Date date = new Date();
            pattern = pattern.replaceAll("yyyy", new SimpleDateFormat("yyyy").format(date) );
            pattern = pattern.replaceAll("MM", new SimpleDateFormat("MM").format(date) );
            pattern = pattern.replaceAll("dd", new SimpleDateFormat("dd").format(date) );
            shortenDatePattern = Pattern.compile(pattern);
        }
        Matcher matcher = shortenDatePattern.matcher(msg); // not matchning group = "null"
        String msg2 = matcher.replaceAll("$1$2$3$4$5$6").replaceAll("(null)+([0-9])","$2").replaceAll("([0-9])(null)+","$1");
        return msg2;
    }

}
