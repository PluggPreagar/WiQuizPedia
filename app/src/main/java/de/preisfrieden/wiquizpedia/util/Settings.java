package de.preisfrieden.wiquizpedia.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Created by peter on 07.03.2018.
 */

public class Settings {


    public static final int MODE_FULL = 1;
    public static final int MODE_ALLOW_FREE_INPUT = 2; // Query wiht only 1 avail answer
    public static final int MODE_AUTO_NEXT = 4;
    public static final int MODE_TOKEN_ACROSS_PAGES = 8;
    public static final int MODE_TOKEN_FILL_OTHER_TYPE_SENTENCE = 16 ;
    public static final int MODE_TOKEN_FILL_ALL_SENTENCE = 32;
    public static final int MODE_TOKEN_NOUN_MARKER = 64;
    public static final int MODE_TOKEN_ALMOST_CORRECT =128;
    public static final int ERR_UPLOAD_ERR = 256;
    public static final int SYS_CHECK_UPDATE = 512;

    public static int mode = MODE_AUTO_NEXT;
    public static int max_queries_per_sentence = 0;
    private String picTitle;


    static void setMode4Preferences(SharedPreferences sp, String name, boolean setIf, int mode_bit) {
        mode = setIf == sp.getBoolean(name , 0 != (mode & mode_bit)) ? mode | mode_bit : mode & ~(mode_bit);
    }

    public static void readPreferences(Activity activity) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences( activity);
        setMode4Preferences( sharedPref,"pref_auto_next", true,  MODE_AUTO_NEXT );
        setMode4Preferences( sharedPref,"pref_only_extract", false,  MODE_FULL );
        setMode4Preferences( sharedPref,"pref_allow_free_input", true,  MODE_ALLOW_FREE_INPUT );
        setMode4Preferences( sharedPref,"pref_token_across_pages", true,  MODE_TOKEN_ACROSS_PAGES );
        setMode4Preferences( sharedPref,"pref_qry_fill_other_type", false,  MODE_TOKEN_FILL_OTHER_TYPE_SENTENCE );
        setMode4Preferences( sharedPref,"pref_qry_fill_all", false,  MODE_TOKEN_FILL_ALL_SENTENCE );
        setMode4Preferences( sharedPref,"pref_token_noun_marker", true,  MODE_TOKEN_NOUN_MARKER );

        setMode4Preferences( sharedPref,"pref_qry_almost_correct", false,  MODE_TOKEN_ALMOST_CORRECT );

        setMode4Preferences( sharedPref,"pref_err_upload_err", true,  ERR_UPLOAD_ERR );
        setMode4Preferences( sharedPref,"pref_sys_check_upd", true,  SYS_CHECK_UPDATE );

        max_queries_per_sentence = Integer.valueOf(sharedPref.getString("pref_qry_max_count_p_sentence", "0"));

    }



}
