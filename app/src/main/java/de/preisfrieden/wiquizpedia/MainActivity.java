package de.preisfrieden.wiquizpedia;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.preisfrieden.wiquizpedia.gui.SettingsActivity;
import de.preisfrieden.wiquizpedia.trf.Download;
import de.preisfrieden.wiquizpedia.trf.DownloadCallback;
import de.preisfrieden.wiquizpedia.trf.DownloadTask2;
import de.preisfrieden.wiquizpedia.util.AutoCompleteAdapter;
import de.preisfrieden.wiquizpedia.util.CustomExceptionHandler;
import de.preisfrieden.wiquizpedia.util.OnSwipeTouchListener;
import de.preisfrieden.wiquizpedia.util.Settings;
import de.preisfrieden.wiquizpedia.util.Util;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity implements DownloadCallback {


    /*
    https://stackoverflow.com/questions/17271147/android-get-content-from-url
    https://developer.android.com/reference/javax/net/ssl/HttpsURLConnection.html

    https://de.wikipedia.org/w/api.php?action=query&prop=extracts&titles=Albert%20Einstein&exintro=&explaintext=&redirects=&formatversion=2
    https://en.wikipedia.org/w/api.php

    https://stackoverflow.com/questions/4460921/extract-the-first-paragraph-from-a-wikipedia-article-python
    https://stackoverflow.com/questions/627594/is-there-a-wikipedia-api



    TODO - use wiki-style - extract refs ...
    TODO - use ref to offer alternatives to go one ...
    TODO - use svg - https://www.codeproject.com/articles/136239/android-imageview-and-drawable-with-svg-support

    TODO - autocomplete - http://easyautocomplete.com/example/duckduckgo-ajax-api ,,,
                http://api.duckduckgo.com/?q=jim+morris&format=json

    TODO - Chat
    TODO - Cache file ..
                https://developer.android.com/training/data-storage/files.html#WriteExternalStorage

     */

    private static Activity gui = null;
    private static Content content = null;
    private static ContentQuery query = null;

    private List<CheckBox> checkBoxes = new ArrayList<CheckBox>();

    GestureDetector gestureDetector;
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    private static String picTitle;
    private static String curTitle;
    private static boolean firstRun = true;
    private static Drawable curImage ;

    OnSwipeTouchListener swipeTouchListener;
    private String initTitle = "Albert Einstein";
    private String initContent = "{\"batchcomplete\":true,\"query\":{\"normalized\":[{\"fromencoded\":false,\"from\":\"Albert_Einstein\",\"to\":\"Albert Einstein\"}],\"pages\":[{\"pageid\":1278360,\"ns\":0,\"title\":\"Albert Einstein\",\"extract\":\"Albert Einstein (* 14. März 1879 in Ulm, Württemberg, Deutsches Reich; † 18. April 1955 in Princeton, New Jersey, Vereinigte Staaten) gilt als einer der bedeutendsten theoretischen Physiker der Wissenschaftsgeschichte. Seine Forschungen zur Struktur von Materie, Raum und Zeit sowie zum Wesen der Gravitation veränderten maßgeblich das zuvor geltende newtonsche Weltbild.\\nEinsteins Hauptwerk, die Relativitätstheorie, machte ihn weltberühmt. Im Jahr 1905 erschien seine Arbeit mit dem Titel Zur Elektrodynamik bewegter Körper, deren Inhalt heute als spezielle Relativitätstheorie bezeichnet wird. 1915 publizierte er die allgemeine Relativitätstheorie. Auch zur Quantenphysik leistete er wesentliche Beiträge. „Für seine Verdienste um die theoretische Physik, besonders für seine Entdeckung des Gesetzes des photoelektrischen Effekts“, erhielt er den Nobelpreis des Jahres 1921, der ihm 1922 überreicht wurde. Seine theoretischen Arbeiten spielten – im Gegensatz zur verbreiteten Meinung – beim Bau der Atombombe und der Entwicklung der Kernenergie nur eine indirekte Rolle.Albert Einstein gilt als Inbegriff des Forschers und Genies. Er nutzte seine außerordentliche Bekanntheit auch außerhalb der naturwissenschaftlichen Fachwelt bei seinem Einsatz für Völkerverständigung und Frieden. In diesem Zusammenhang verstand er sich selbst als Pazifist, Sozialist und Zionist.\\nIm Laufe seines Lebens war Einstein Staatsbürger mehrerer Länder: Durch Geburt besaß er die württembergische Staatsbürgerschaft. Von 1896 bis 1901 staatenlos, ab 1901 bis zu seinem Tode Staatsbürger der Schweiz, war er 1911/12 in Österreich-Ungarn auch Bürger Österreichs. Von 1914 bis 1932 lebte Einstein in Berlin und war als Bürger Preußens erneut Staatsangehöriger im Deutschen Reich. Mit der Machtergreifung Hitlers gab er 1933 den deutschen Pass endgültig ab und wurde 1934 vom Deutschen Reich strafausgebürgert. Zusätzlich zu seinem seit 1901 geltenden Schweizer Bürgerrecht erwarb er 1940 noch die amerikanische Staatsbürgerschaft.\"}]}}";

    private static String version = "";

    public static String getErrorInfo () {
     return getErrorInfo(true);
    }

    public static String getErrorInfo (boolean longVersion) {
        //(EditText) MainActivity.findViewById(R.id.tv_title);
        return (version.replaceAll("[0-9]*$", "")
                + (null == query ? "" : " " + query.title)
                + (longVersion ? " -- " + (null == curTitle ? "-" : curTitle)
                               + " -- " + (null == content || null == content.recentQuery ? "" : content.recentQuery.size())
                               + " -- " + (null == query || null == query.msg ? "" : Util.shortenString(query.msg))
                            : ""
                    )).replace("\n", " ").replace("\r", " ").replaceAll("\\s+", " ")
        ;
    }

    public String getVersionInfo(Context context) {
        //http://devdeeds.com/auto-increment-build-number-using-gradle-in-android/
        try {
            version = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            CustomExceptionHandler.uncaughtException(e);
            e.printStackTrace();
        }
        version = null == version ? "" : version;
        return version;
    }


    // https://proandroiddev.com/building-an-autocompleting-edittext-using-rxjava-f69c5c3f5a40
    // Using RxRelay's implementation of publish subject - https://github.com/JakeWharton/RxRelay

    public static void toast(String text, boolean toast_long ){
        Toast.makeText(gui, text, toast_long ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public static void toast(String text){
        toast(text, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // https://www.intertech.com/Blog/android-handling-the-unexpected/
        // https://stackoverflow.com/questions/601503/how-do-i-obtain-crash-data-from-my-android-application#2855736
        CustomExceptionHandler.register(this);
        getVersionInfo(this);

        setContentView(R.layout.activity_main);
        // setHasOptionsMenu(true); // http://www.programmierenlernenhq.de/tutorial-android-options-menu-in-action-bar/
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        Settings.readPreferences( this );
        gui = this;

        swipeTouchListener = new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
                MainActivity.toast("merge with next sentence");
                updateFromDownload( query.mergeWithNextSentence());
            }

            public void onSwipeRight() {
                MainActivity.toast( ("last query"));
                updateFromDownload( content.getLastQuery());
            }

            public void onSwipeLeft() {
                MainActivity.toast( ("next query"));
                processTvTitle(null);
            }

            public void onSwipeBottom() {
                MainActivity.toast("random page");
                setTitleText( "");
                query = null;
                redraw();
            }

            @Override
            public boolean onSingleTapUp2() {
                MainActivity.toast("comment");
                showComment();
                return true;
            }

            @Override
            public void onLongPress2() {
                MainActivity.toast("longPress ... open external");
                callUrl4Content();
            }

            @Override
            public boolean onDoubleTap2() {
                MainActivity.toast("double tap ... list random pages");
                ContentQuery queryRef = content.createQueryRef();
                if (null != queryRef) {
                    updateFromDownload(queryRef);
                } else {
                    setTitleText( "");
                    query = null;
                    redraw();
                }
                return true;
            }
        };

        View view = findViewById(R.id.main_activity);
        view.setOnTouchListener( swipeTouchListener);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // https://turbomanage.wordpress.com/2012/05/02/show-soft-keyboard-automatically-when-edittext-receives-focus/

        initCheckbox();
        setTextView( null );
        setImageView( null );

        if (firstRun) {
            firstRun = false;
            showHelp();
            toast("tap to close ... ");
            Download.inject( Content.getUrl(initTitle),initContent);
            // preload randoms..
            // new ContentTask(null).execute(new ContentTaskParam(content, ""));
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    new ContentTask(null).execute(new ContentTaskParam(content, ""));
                }
            }, 2000);
        }

        redraw(); // always redraw - even before loaded ...
        if (null == content) setTitleText(null); // init but do not change content when screen is just turned ...
    }
    // ------------------------------------------------------------------------------

    /** Called when the user taps the Send button */
    public void processTvTitle(EditText tv_title) {
        if (null == tv_title) tv_title = (EditText) findViewById(R.id.tv_title);
        String curTitle = tv_title.getText().toString();
        boolean forceLoadData = null == query || query instanceof ContentQueryRef || curTitle.isEmpty() || !query.title.equals(curTitle);
        if (forceLoadData) redraw();
        new ContentTask(this).execute(new ContentTaskParam(content, forceLoadData ? curTitle : null)); // ContentTask will block repeated calls ...
    }

    @Override
    public void updateFromDownload(Object result) {
        if ( null == result || result instanceof  Drawable) {
            setImageView( (Drawable) result);
        } else if (result instanceof  ContentQuery){
            query = (ContentQuery) result;
            content = (null == query) ? null : query.content;
            redraw();
        }
    }

    public void redraw() {
        String msg = null ;
        if (null == query )query = new ContentQueryRef(( List<String>) null , content);

        AutoCompleteTextView titleText = setTitleText(query.title, false);
        String picTitleNew = titleText.getText().toString();
        picTitleNew = picTitleNew.replaceAll(ContentQueryRef.loadingTitle,"");
        if (!picTitleNew.equals(picTitle) && ! picTitleNew.isEmpty()) {
            picTitle = picTitleNew;
            setImageView( (Drawable) null);
            if (!query.title.isEmpty()) Content.updatePicFromData( query.title, this);
        } else {
            setImageView( curImage);
        }

        msg = query.msg;
        if (null != msg ) {
            if (!query.answer_token_id.isEmpty()) msg = msg.replaceAll( query.answer_token_id , "____");
            msg = msg.replaceAll( "__[a-zA-Z0-9]+__" , " ... ");
            TextView textView = (TextView) findViewById(R.id.tv_question);
            textView.setText( msg );
            textView.setHeight( Math.max(5,textView.getLineCount()+1) * textView.getLineHeight());
        }
        setButton( query);
        setCheckbox( query.answer_token_avail);
        setTextView( query.answer_token);

    }

    private void setButton(ContentQuery query) {
        Button button = (Button) findViewById( R.id.button);
        if (null != query && null !=  query.content) {
            int msg_querable_sentences_count = query.content.msg_querable_sentences.size();
            button.setText((query.content.msg_querable_sentences_total - msg_querable_sentences_count) + "/ " + query.content.msg_querable_sentences_total);
        }
    }

    private void initCheckbox( ){
        checkBoxes.add(initCheckbox( R.id.cb_answer_1));
        checkBoxes.add(initCheckbox( R.id.cb_answer_2));
        checkBoxes.add(initCheckbox( R.id.cb_answer_3));
    }

    private CheckBox initCheckbox( int checkBoxId){
        CheckBox checkBox = (CheckBox) findViewById( checkBoxId);
        checkBox.setVisibility( View.INVISIBLE );
        checkBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                   @Override
                   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                       buttonView.setBackgroundColor(!isChecked ? Color.TRANSPARENT : checkAnswer(isChecked ? buttonView.getText().toString() : "") == isChecked ? Color.GREEN : Color.RED );
                       setButton( query);
                   }
               }
        );
        return checkBox;
    }

    private void setCheckbox( List<String> answers){
        for (int i = 0 ; i < checkBoxes.size() ; i++) {
            setCheckbox( i, answers);
        }
    }

    private void setCheckbox( int checkBoxId, List<String> answers){
        CheckBox checkBox = checkBoxes.get(checkBoxId) ; // (CheckBox) findViewById( checkBoxId);
        String answer = null != answers && checkBoxId < answers.size() && answers.size() > 1 ? answers.get(checkBoxId) : null ;

        /* API 23
            cb1.setCompoundDrawableTintList(colorStateListNormal);
            cb1.setForegroundTintList(colorStateListNormal);
        */

        checkBox.setVisibility( null == answer ? View.INVISIBLE : View.VISIBLE );
        if (null != answer) {
            checkBox.setText(answer);
            checkBox.setTextColor(Color.BLACK);
            checkBox.setChecked(false);
            checkBox.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void setTextView( String initNullOrValue ){
        final String placeholder_text = "direct answer ...";
        final TextView tv_answer = (TextView) findViewById( R.id.et_answer);

        if (null == initNullOrValue) {
            tv_answer.setVisibility( View.INVISIBLE);
            tv_answer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tv_answer.setText("");
                }
            });
            tv_answer.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        String text = handleSpecialCommand( tv_answer, MainActivity.this );
                        tv_answer.setBackgroundColor( null == text || text.isEmpty() ? Color.TRANSPARENT : checkAnswer(text) ? Color.GREEN : Color.RED);
                        return true;
                    }
                    return false;
                }
            });
        } else if (initNullOrValue.isEmpty()) {
            tv_answer.setVisibility( View.INVISIBLE); // hide for ContentQueryRef ....
        } else {
            tv_answer.setVisibility( View.VISIBLE);
            tv_answer.setTextColor(Color.BLACK);
            tv_answer.setBackgroundColor(Color.TRANSPARENT);
            tv_answer.setText("");
        }
    }

    protected void setImageView( Drawable drawable){
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        curImage = drawable;
        if (null == drawable) {
            imageView.setImageResource( R.mipmap.ic_launcher_foreground_v0a1 );
        } else {
            imageView.setImageDrawable( (Drawable) drawable);
        }
    }

    private boolean checkAnswer ( String text) {
        boolean result = true;
        if ( query instanceof ContentQueryRef) {
            toast( "next title ... ");
            setTitleText(text);
        } else if ((Settings.mode & Settings.MODE_AUTO_NEXT)>0){
            result = query.checkAnswer(text);
            if (query.content.msg_querable_sentences.isEmpty()) {
                TextView tv_title = (TextView) findViewById(R.id.tv_title);
                tv_title.setBackgroundColor( Color.GREEN );
                toast("Well DONE !!!", true);
            } else {
                toast("auto next ...", false);
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    processTvTitle(null);
                }
            }, 1000);
        }
        return result;
    }


    public AutoCompleteTextView setTitleText( String nullOrTitle) {
        return setTitleText(nullOrTitle, true);
    }

    private AutoCompleteTextView setTitleText( String nullOrTitle, boolean runProcess){
        final AutoCompleteTextView tv_title = (AutoCompleteTextView) findViewById( R.id.tv_title);
        if (null == nullOrTitle) {
            nullOrTitle = initTitle;
            tv_title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean gainFocus) {
                    if (gainFocus) {
                        setTitleText("", false);
                        tv_title.setDropDownHeight( WindowManager.LayoutParams.WRAP_CONTENT );
                    }
                }
            });
            tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { // or get problem as return type from onKey ist different to onClick - why that ????
                }
            });
            tv_title.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        tv_title.setDropDownHeight( 0 );
                        if ( null != handleSpecialCommand(tv_title, MainActivity.this)) processTvTitle(tv_title);
                        return true;
                    }
                    return false;
                }
            });
            // tv_title will get focus right from start - so prevent soft-keyboard to show up the hard way ...
            tv_title.setAdapter( new AutoCompleteAdapter( this, android.R.layout.simple_expandable_list_item_1));
            tv_title.setDropDownHeight( 0 );
        }
        tv_title.setBackgroundColor( Color.TRANSPARENT );
        if (null!= nullOrTitle) {
            tv_title.setText( nullOrTitle );
            curTitle = nullOrTitle;
        }
        findViewById(R.id.main_activity).requestFocus(); // https://stackoverflow.com/questions/14424654/how-to-clear-focus-for-edittext
        if (runProcess) {
            query = new ContentQueryRef( nullOrTitle , content);
            processTvTitle( tv_title);
        }
        return tv_title;
    }

    public void onClick(View view){
        processTvTitle((AutoCompleteTextView) findViewById( R.id.tv_title));
    }

    public String handleSpecialCommand(TextView view, AppCompatActivity context) {
        String text = view.getText().toString();
        String specialInput = "mode";
        switch (text.toUpperCase()) {
            case "FULL": Settings.mode ^= Settings.MODE_FULL ; content = new Content(); break;
            case "FREE": Settings.mode ^= Settings.MODE_ALLOW_FREE_INPUT ; break;
            case "HARD": Settings.mode ^= Settings.MODE_ALLOW_FREE_INPUT + Settings.MODE_FULL;  content = new Content(); break;
            case "AUTO": Settings.mode ^= Settings.MODE_AUTO_NEXT; break;
            default:    specialInput = null;
        }
        if (null != specialInput) {
            Toast.makeText( context, "Enter special " + specialInput + ": " + text, Toast.LENGTH_LONG).show();
            setTitleText( query.title , false);
        }
        return null == specialInput ? text : null;
    }

    // ------------------------------------------------------
    // https://stackoverflow.com/questions/8430805/clicking-the-back-button-twice-to-exit-an-activity#13578600
    //
    boolean doubleBackToExitPressedOnce = false;
    boolean singleBackToPausePressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            //super.onBackPressed();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            return;
        } else if (singleBackToPausePressedOnce) {
            super.onBackPressed();
            return;
        }


        this.doubleBackToExitPressedOnce = true;
        this.singleBackToPausePressedOnce = true;
        toast( "click BACK again to exit - pause otherwhise");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
                onBackPressed();
            }
        }, 2000);
    }

    // ------------------------------------------------------
    //
    //

    public void callUrl4Content() {
        if (null != content) {
            try {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse( content.getURL2View() ));
                startActivity(myIntent);
            } catch (ActivityNotFoundException e) {
                CustomExceptionHandler.uncaughtException(e);
                Toast.makeText(this, "No application can handle this request."
                        + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }


    // ------------------ options menu ---------
    //
    //   http://viralpatel.net/blogs/android-preferences-activity-example/
    //

    private static final int RESULT_SETTINGS = 1;

    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        // http://viralpatel.net/blogs/android-preferences-activity-example/
        final Menu menu_view = menu;

        getMenuInflater().inflate(R.menu.menu_pref_fragment, menu);

        ((MenuItem) menu.findItem(R.id.opt_menu_version)).setTitle("Version: " + version);
        ((MenuItem) menu.findItem(R.id.opt_menu_data_refresh)).setTitle("     update ... " );

        class VersionDownloader implements DownloadCallback {

            VersionDownloader() {
                DownloadTask2 downloadTask = new DownloadTask2( this );
                downloadTask.execute("http://preisfrieden.de/WiQuizPedia/version.php", DownloadTask2.FORCELOAD);
            }

            @Override
            public void updateFromDownload(Object result) {
                if (null != result && result instanceof String) {
                    String newVersion = (String) result;
                    Long minutesDiff = null;
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmm");
                        Date dateThis = dateFormat.parse(version.replaceAll(".* ",""));
                        Date dateNew = dateFormat.parse(newVersion);
                        minutesDiff = (dateNew.getTime() - dateThis.getTime()) / 1000 / 60;
                    } catch (ParseException e) {
                        CustomExceptionHandler.uncaughtException(e);
                        e.printStackTrace();
                    }
                    if (null == minutesDiff) {
                    } else {
                        if (5 < minutesDiff) toast("New version of app available!");
                        String msg =  5 < minutesDiff ? "     update to " + newVersion : -5 < minutesDiff ? "     already update to date" : "  downgrade to " + newVersion;
                        ((MenuItem) menu_view.findItem(R.id.opt_menu_data_refresh)).setTitle(msg);
                    }

                }
            }
        }
        if ( 0 < (Settings.mode & Settings.SYS_CHECK_UPDATE)) new VersionDownloader();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.opt_menu_show_help:
                showHelp();
                break;

            case R.id.opt_menu_send_comment:
                showComment();
                break;


            case R.id.opt_menu_err_show:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                File file = new File( CustomExceptionHandler.localPath, CustomExceptionHandler.errorFileName);
                if (file.exists()) {
                    String modTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date(file.lastModified()));
                    try {
                        String trace = new Download().readStream(new FileInputStream(file), 5000);
                        showErrorTrace( modTime + ":\n" + trace + "\n\n\t\t\t(click to close)\n\n");
                    } catch (IOException e) {
                        CustomExceptionHandler.uncaughtException(e);
                        e.printStackTrace();
                    }
                    /*
                    Uri uri = Uri.parse("content://com.sample.provider/" + file.getAbsolutePath());
                    //Here com.sample.provider is the authority defined in the manifest.
                    intent.setDataAndType(uri, "text/plain");
                    startActivity(intent);
                    */
                } else {
                    toast("no error files found - jjjiiiipeeeee ;-)");
                }
                break;

            case R.id.opt_menu_data_refresh:
                updateVersion();
                break;

            case R.id.opt_menu_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivityForResult(i, RESULT_SETTINGS);
                /*getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new SettingsFragment())
                        .commit();*/
                break;

        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SETTINGS:
                Settings.readPreferences( this );
                toast("got settings ... mode = " + Settings.mode,true);
                break;

        }

    }

    // -------------------------------------------------------
    //
    //      https://stackoverflow.com/questions/10216937/how-do-i-create-a-help-overlay-like-you-see-in-a-few-android-apps-and-ics
    //

    public void showHelp(){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Drawable helpDoc = getResources().getDrawable(R.mipmap.screenshot_help_dok6_head_backg);
        helpDoc.setAlpha(200);
        dialog.getWindow().setBackgroundDrawable( helpDoc );
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.help_page);
        dialog.setCanceledOnTouchOutside(true);
        //for dismissing anywhere you touch
        View masterView = dialog.findViewById(R.id.hlp_master_view);
        masterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showErrorTrace(String txt){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ColorDrawable drawable = new ColorDrawable(getResources().getColor(R.color.colorPrimaryBackgroundHover));
        //drawable.setAlpha(220);
        dialog.getWindow().setBackgroundDrawable(drawable);
        dialog.setContentView(R.layout.error_trace_page);
        dialog.setCanceledOnTouchOutside(true);
        //for dismissing anywhere you touch
        TextView tv = (TextView) dialog.findViewById(R.id.tv_error_trace);
        txt = txt.replaceAll("at\\s+.*WiQuizPedia","at WQP");
        txt = txt.replaceAll("at\\s+[a-zA-Z.]*\\.","at ");
        tv.setText(txt);
        tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showComment(){

        final MainActivity mainActivity = this;
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ColorDrawable drawable = new ColorDrawable( getResources().getColor(R.color.colorPrimaryBackgroundHover)); // @color/colorPrimaryLight
        //drawable.setAlpha(220);
        dialog.getWindow().setBackgroundDrawable(drawable);
        dialog.setContentView(R.layout.comment_page);
        dialog.setCanceledOnTouchOutside(true);
        //for dismissing anywhere you touch
        final TextView tv_comment = (TextView) dialog.findViewById(R.id.et_comment_page);
        tv_comment.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        sendComment(v);
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        TextView tv_comment_rcv = (TextView) dialog.findViewById(R.id.tv_comment_rcv);
        tv_comment_rcv.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Layout layout = ((TextView) v).getLayout();
                int x = (int)event.getX();
                int y = (int)event.getY();
                if (layout!=null){
                    int line = layout.getLineForVertical(y);
                    int offset = layout.getOffsetForHorizontal(line, x);
                    //toast("index[ " + line + ", " + offset+" ]");
                    String titleCommented = null;
                    TextView tv_comment_rcv = ((TextView) v);
                    // simulate wraping  ...
                    String text = tv_comment_rcv.getText().toString();
                    List<String> textLines = new ArrayList<String>();
                    String[] text2 = tv_comment_rcv.getText().toString().split("\n");
                    // simulate text wrapping // KLUDGE - only more or less accurate ..
                    // https://flowovertop.blogspot.de/2013/08/android-get-chars-to-fit-in-textview.html
                    while(text.length()>0)
                    {
                        int totalCharstoFit= tv_comment_rcv.getPaint().breakText(text,  0, text.length(),
                                true, tv_comment_rcv.getWidth(), null);
                        int newLineAt = text.indexOf("\n");
                        if ( -1 < newLineAt && newLineAt<totalCharstoFit) totalCharstoFit = newLineAt + 1;
                        String textLine=text.substring(0, totalCharstoFit);
                        textLines.add(textLine);
                        text=text.substring(textLine.length(),text.length());

                    }
                    Pattern p = Pattern.compile("^> [0-9][0-9.:-]* (?:[1-9.]+ )?(.*):$");
                    Matcher m = null ;
                    line = Math.min(line+1, textLines.size());
                    while (--line>= 0 && !(m = p.matcher( textLines.get(line))).find()) ;
                    if (0 <= line && null != m ) {
                        titleCommented = m.group(1);
                        mainActivity.setTitleText( titleCommented);
                        //TextView tv_title = (TextView) findViewById(R.id.tv_title);
                        //tv_title.setText(titleCommented);
                        dialog.dismiss();
                    }
                }
                return true;
            }
        });

        class CommentDownloader implements DownloadCallback {

            CommentDownloader() {
                DownloadTask2 downloadTask = new DownloadTask2( this );
                downloadTask.execute("http://preisfrieden.de/WiQuizPedia/recentComment.php", DownloadTask2.FORCELOAD);
            }

            @Override
            public void updateFromDownload(Object result) {
                if (null != result && result instanceof String) {
                    TextView tv_comment_rcv = (TextView) dialog.findViewById(R.id.tv_comment_rcv);
                    String msg = Util.shortenDate((String) result);
                    msg = msg.replaceAll("(.*)([^ ]:) ","> $1$2\n");
                    tv_comment_rcv.setText( msg);
                }
            }
        }

        new CommentDownloader();
        dialog.show();
    }


    public void sendComment(View v) {
        TextView et_comment = (TextView) v.findViewById( R.id.et_comment_page);
        // KLUDGE
        Thread.UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (currentHandler instanceof CustomExceptionHandler) {
            String msg = et_comment.getText().toString();
            ((CustomExceptionHandler) currentHandler).saveBugComment(msg);
        }
    }

    public void updateVersion(){
        toast("get new version ..", true);
        final File apkFile = new File( this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "app-release.apk");

        class DownloadHandlerApk implements DownloadCallback {

            DownloadHandlerApk() {
                DownloadTask2 downloadTask = new DownloadTask2( this );
                // beim download dieses paketes ist ein fehler aufgetreten...
                //downloadTask.execute("http://preisfrieden.de/WiQuizPedia/app", DownloadTask2.FORCELOAD, apkFile.getAbsolutePath() );
                downloadTask.execute("http://preisfrieden.de/WiQuizPedia/app-release.apk", DownloadTask2.FORCELOAD, apkFile.getAbsolutePath() );
            }

            @Override
            public void updateFromDownload(Object result) {
                if (null != result && result instanceof String) {

                    try {
                        //Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse( url ));
                        Intent myIntent = new Intent(Intent.ACTION_VIEW); // application/vnd.android.package-archive
                        // does not work with non-file-URI (have to download first ... )
                        myIntent.setDataAndType(  Uri.fromFile( new File((String) result) ), "application/vnd.android.package-archive");
                        //Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse( url )); // call Webbrowser //  https://stackoverflow.com/questions/10943037/install-apk-without-downloading
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e) {
                        MainActivity.toast( "No application can handle this request." + " Please install a webbrowser");
                        CustomExceptionHandler.uncaughtException(e);
                        e.printStackTrace();
                    }
                }
            }
        }

        new DownloadHandlerApk();
    }

}
