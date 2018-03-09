package de.preisfrieden.wiquizpedia;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.List;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity implements DownloadCallback  {


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


     */

    private static Activity gui = null;
    private static Content content = null;
    private static ContentQuery query = null;

    private List<CheckBox> checkBoxes = new ArrayList<CheckBox>();

    GestureDetector gestureDetector;
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    private String picTitle;
    private boolean initHelp2Shop = true;

    OnSwipeTouchListener swipeTouchListener;

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
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setVisibility( View.INVISIBLE);

                setTitleText( "");
            }

            @Override
            public boolean onSingleTapUp2() {
                MainActivity.toast("Tap");
                /*
                TextView textView = (TextView) findViewById(R.id.tv_question);
                MainActivity.toast("Tap: " + textView.getLineCount() + "*" + textView.getLineHeight() , false);
                textView.setHeight( textView.getHeight() + textView.getLineHeight());
                */
                return true;
            }

            @Override
            public void onLongPress2() {
                MainActivity.toast("longPress ... list random pages");
                ContentQuery queryRef = content.createQueryRef();
                if (null != queryRef) {
                    updateFromDownload(queryRef);
                } else {
                    setTitleText( "");
                }
            }

            @Override
            public boolean onDoubleTap2() {
                MainActivity.toast("open external");
                callUrl4Content();
                return true;
            }
        };

        View view = findViewById(R.id.entire_view);
        view.setOnTouchListener( swipeTouchListener);

        AutoCompleteTextView tv_title = setTitleText( null);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // https://turbomanage.wordpress.com/2012/05/02/show-soft-keyboard-automatically-when-edittext-receives-focus/
        // tv_title will get focus right from start - so prevent soft-keyboard to show up the hard way ...
        tv_title.setAdapter( new AutoCompleteAdapter( this, android.R.layout.simple_expandable_list_item_1));
        //tv_title.setAdapter(  new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, Arrays.asList("Jim Knopf","Jim Hanson","Jim Morris","Jimnasium")) );

        initCheckbox();
        setTextView(R.id.et_answer, null);

        if (initHelp2Shop) {
            initHelp2Shop = false;
            showHelp();
            toast("tap to close ... ");
        }

        redraw(); // always redraw - even before loaded ...
        if (null == content) setTitleText("Albert Einstein"); // init but do not change content when screen is just turned ...
    }
    // ------------------------------------------------------------------------------

    /** Called when the user taps the Send button */
    public void processTvTitle(EditText tv_title) {
        if (null == tv_title) tv_title = (EditText) findViewById(R.id.tv_title);
        String curTitle = tv_title.getText().toString();
        boolean forceLoadData = null == query || curTitle.isEmpty() || !query.title.equals(curTitle);
        new ContentTask(this).execute(new ContentTaskParam(content, forceLoadData ? curTitle : null)); // ContentTask will block repeated calls ...
    }

    @Override
    public void updateFromDownload(Object result) {
        if ( null == result) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageResource( R.mipmap.ic_launcher_v4 );
            //imageView.setVisibility( View.INVISIBLE);
        } else if (result instanceof  Drawable) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageDrawable( (Drawable) result);
            imageView.setVisibility( View.VISIBLE);
        } else if (result instanceof  ContentQuery){
            query = (ContentQuery) result;
            content = (null == query) ? null : query.content;
            redraw();
        }
    }

    public void redraw() {
        String msg = null ;
        if (null == query )query = new ContentQueryRef(null , content);

        AutoCompleteTextView titleText = setTitleText(query.title, false);
        String picTitleNew = titleText.getText().toString();
        if (!picTitleNew.equals(picTitle) && ! picTitleNew.isEmpty()) {
            picTitle = picTitleNew;
            updateFromDownload(null);
            if (!query.title.isEmpty()) Content.updatePicFromData( query.title, this);
        }

        msg = query.msg;
        if (null != msg ) {
            if (!query.answer_token_id.isEmpty())  msg = msg.replaceAll( query.answer_token_id , "____");
            msg = msg.replaceAll( "__[a-zA-Z0-9]+__" , " ... ");
            TextView textView = (TextView) findViewById(R.id.tv_question);
            textView.setText( msg );
            textView.setHeight( Math.max(5,textView.getLineCount()+1) * textView.getLineHeight());
        }
        setButton( query);
        setCheckbox( query.answer_token_avail);

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
        if (null != answer) checkBox.setText(answer);
        checkBox.setTextColor(Color.BLACK);
        checkBox.setChecked(false);
        checkBox.setBackgroundColor(Color.TRANSPARENT);
    }

    private void setTextView(int textViewId, String initNullOrValue ){
        final String placeholder_text = "direct answer ...";
        final TextView tv_answer = (TextView) findViewById( textViewId);

        tv_answer.setTextColor(Color.BLACK);
        tv_answer.setBackgroundColor(Color.TRANSPARENT);
        tv_answer.setText(placeholder_text);

        if (null == initNullOrValue) {
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
                        tv_answer.setBackgroundColor( null == text || placeholder_text.equals(text) ? Color.TRANSPARENT : checkAnswer(text) ? Color.GREEN : Color.RED);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private boolean checkAnswer ( String text) {
        boolean nextRef = query instanceof ContentQueryRef;
        if ( query instanceof ContentQueryRef) {
            toast( "next title ... ");
            setTitleText(text);
        } else if ((Settings.mode & Settings.MODE_AUTO_NEXT)>0){
            toast("auto next query ...", true);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    processTvTitle(null);
                }
            }, 1000);
        }
        return query.checkAnswer(text);
    }

    private AutoCompleteTextView setTitleText( String nullOrTitle) {
        return setTitleText(nullOrTitle, true);
    }

    private AutoCompleteTextView setTitleText( String nullOrTitle, boolean runProcess){
        final AutoCompleteTextView tv_title = (AutoCompleteTextView) findViewById( R.id.tv_title);
        if (null == nullOrTitle) {
            tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTitleText("", false);
                }
            });
            tv_title.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        if ( null != handleSpecialCommand(tv_title, MainActivity.this)) processTvTitle(tv_title);
                        return true;
                    }
                    return false;
                }
            });
        } else { // https://stackoverflow.com/questions/5495225/how-to-disable-autocompletetextviews-drop-down-from-showing-up#7320542
            tv_title.setDropDownHeight( 0 );
            tv_title.setText( nullOrTitle );
            tv_title.setDropDownHeight( WindowManager.LayoutParams.WRAP_CONTENT );
            if (runProcess) processTvTitle( tv_title);
        }
        return tv_title;
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
        try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse( content.getURL2View() ));
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request."
                    + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    // ------------------ options menu ---------
    //
    //   http://viralpatel.net/blogs/android-preferences-activity-example/
    //

    private static final int RESULT_SETTINGS = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // http://viralpatel.net/blogs/android-preferences-activity-example/
        getMenuInflater().inflate(R.menu.menu_pref_fragment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.opt_menu_show_help:
                showHelp();
                break;

            case R.id.opt_menu_data_refresh:
                //String url = "https://github.com/PluggPreagar/WiQuizPedia/blob/master/app/release/app-release.apk?raw=true";
                String url = "http://preisfrieden.de/app-release.apk";
                toast("get new version ..", true);
                try {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse( url ));
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "No application can handle this request."
                            + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

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
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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

}
