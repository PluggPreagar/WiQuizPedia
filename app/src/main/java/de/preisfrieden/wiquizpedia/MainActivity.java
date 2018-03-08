package de.preisfrieden.wiquizpedia;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity implements DownloadCallback , GestureDetector.OnGestureListener {


    /*
    https://stackoverflow.com/questions/17271147/android-get-content-from-url
    https://developer.android.com/reference/javax/net/ssl/HttpsURLConnection.html

    https://de.wikipedia.org/w/api.php?action=query&prop=extracts&titles=Albert%20Einstein&exintro=&explaintext=&redirects=&formatversion=2
    https://en.wikipedia.org/w/api.php

    https://stackoverflow.com/questions/4460921/extract-the-first-paragraph-from-a-wikipedia-article-python
    https://stackoverflow.com/questions/627594/is-there-a-wikipedia-api



    TODO - use wiki-style - extract refs ...
    TODO - use ref to offer alternatives to go one ...


     */

    private static Activity gui = null;
    private static Content content = null;
    private static ContentQuery query = null;

    private List<CheckBox> checkBoxes = new ArrayList<CheckBox>();

    GestureDetector gestureDetector;
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    private String picTitle;

    OnSwipeTouchListener swipeTouchListener;


    public static void toast(String text, boolean toast_long ){
        Toast.makeText(gui, text, toast_long ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public static void toast(String text){
        toast(text, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestureDetector = new GestureDetector( this);

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
                setURL(null);
            }

            public void onSwipeBottom() {
                MainActivity.toast("random page");
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setVisibility( View.INVISIBLE);

                ((EditText) findViewById(R.id.url_et)).setText("");
                setURL(null);
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
                    ((EditText) findViewById(R.id.url_et)).setText("");
                    setURL(null);
                }
            }

            @Override
            public boolean onDoubleTap2() {
                MainActivity.toast("open external");
                callUrl4Content();
                return true;
            }
        };

        TextView tv_question = (TextView) findViewById(R.id.tv_question);
        tv_question.setOnTouchListener( swipeTouchListener);
        //tv_question.setMovementMethod(new ScrollingMovementMethod());

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnTouchListener( swipeTouchListener);

        EditText urlEt = (EditText) findViewById(R.id.url_et);
        //urlEt.setOnTouchListener(swipeTouchListener);

        initCheckbox();
        initEditText( R.id.url_et);
        initTextView(R.id.et_answer, true);
        if (null == content) {
            content = new Content();
            //int inputType = urlEt.getInputType(); // https://stackoverflow.com/questions/10636635/disable-keyboard-on-edittext
            //urlEt.setInputType(InputType.TYPE_NULL);
            urlEt.setText("Albert Einstein");
            //urlEt.setInputType( inputType);
            //findViewById(R.id.cb_answer_1).requestFocus(); // https://stackoverflow.com/questions/2403632/android-show-soft-keyboard-automatically-when-focus-is-on-an-edittext#2418314
            //TextView tv = (TextView) findViewById(R.id.tv_question);
            //tv.setText(tv.getText()+" "); // FIXME prevent soft-keyboard to show up
            //tv.setText("\n\t loading query ... "); // FIXME prevent soft-keyboard to show up
            urlEt.setShowSoftInputOnFocus(false); //  FIXME prevent soft-keyboard to show up https://stackoverflow.com/questions/10611833/how-to-disable-keypad-popup-when-on-edittext
            imageView.setImageResource( R.mipmap.ic_launcher_v4 ); // FIXME show preset image from activity_main.xml...
            setURL(null);
            final Handler handler = new Handler(); // FIXME prevent soft-keyboard to show up
            // handler.postDelayed(new Runnable() {  @Override   public void run() { closeSoftKeyboard(null); } }, 200);

            handler.postDelayed(new Runnable() {  @Override   public void run() { closeSoftKeyboard(null); } }, 1000);
        } else {
            redraw();
        }
        //((TextView) findViewById(R.id.tv_question)).setText("\n\t loading query ... "); // FIXME prevent soft-keyboard to show up
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

    // ------------------------------------------------------------------------------

    /** Called when the user taps the Send button */
    public void setURL(View view) {
        updateQuery(false);
        resetInputFocus();
    }

    public void updateQuery(boolean forceLoadData)   {
        EditText editText = (EditText) findViewById(R.id.url_et);
        String curTitle = editText.getText().toString();
        forceLoadData |=  null == content || curTitle.isEmpty() || !content.title.equals( curTitle);
        new ContentTask(this).execute(new ContentTaskParam(content, forceLoadData ? curTitle : null ));
    }

    public void resetInputFocus()   {
        findViewById(R.id.url_et).clearFocus();
        findViewById(R.id.tv_question).clearFocus();
        findViewById(R.id.et_answer).clearFocus();
        closeSoftKeyboard(null);
    }

    public void closeSoftKeyboard(View view) {
        // https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
        // View view = this.getCurrentFocus();
        // view = this.getCurrentFocus();
        if (null == view) view = findViewById(R.id.url_et);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
        if (null == query ){
            msg = "no query found:\n" + content.msg_orig+")";
            Toast.makeText( this, "try empty for random page", Toast.LENGTH_LONG).show();
            setCheckbox( null );
        } else {
            EditText editText = (EditText) findViewById(R.id.url_et);
            editText.clearFocus();
            editText.setText(query.content.title);
            //editText.setHeight( Math.max(1 ,editText.getLineCount()) * editText.getLineHeight());
            setCheckbox( query.answer_token_avail);
            initTextView(R.id.et_answer, false);
            String picTitleNew = editText.getText().toString();
            if (!picTitleNew.equals(picTitle) && ! picTitleNew.isEmpty()) {
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageResource( R.mipmap.ic_launcher_v4 );
                //imageView.setVisibility( View.INVISIBLE);
                picTitle = picTitleNew;
                // Content.updatePicFromData( query.content.msg_orig, this);
                Content.updatePicFromData( query.content.title, this);
            }
            msg = query.msg;
            if (!query.answer_token_id.isEmpty())  msg = msg.replaceAll( query.answer_token_id , "____");
            msg = msg.replaceAll( "__[a-zA-Z0-9]+__" , " ... ");
            setButton();
        }

        TextView textView = (TextView) findViewById(R.id.tv_question);
        if (null != msg ) {
            textView.setText( msg );
            //textView.setHeight( Math.max(1,textView.getLineCount() * textView.getLineHeight()));
            textView.setVisibility( View.VISIBLE);
            textView.setHeight( Math.max(5,textView.getLineCount()+1) * textView.getLineHeight());
            // kludge - getLineCount is not available immediately ...
            /*
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    //toast("setHeight ..." , false);
                    TextView textView = (TextView) findViewById(R.id.tv_question);
                    textView.setHeight( Math.max(5,textView.getLineCount()+1) * textView.getLineHeight());
                    //toast("setHeight ... Done " , true);
                }
            }, 2000);
            */
        }
        closeSoftKeyboard(textView);

    }

    private void setButton() {
        Button button = (Button) findViewById( R.id.button);
        int msg_querable_sentences_count  = query.content.msg_querable_sentences.size();
        button.setText( (query.content.msg_querable_sentences_total - msg_querable_sentences_count) + "/ " + query.content.msg_querable_sentences_total);
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
                       setButton();
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

    private void initTextView( int textViewId, boolean init ){
        final String placeholder_text = "direct answer ...";
        TextView textView = (TextView) findViewById( R.id.et_answer);
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundColor(Color.TRANSPARENT);
        textView.setText(placeholder_text);
        if (init) {
            textView.setVisibility( View.INVISIBLE);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView answer = (TextView) findViewById(R.id.et_answer);
                    answer.setText("");
                }
            });
            textView.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        TextView answer = (TextView) findViewById(R.id.et_answer);
                        String text = MainActivity.checkText( answer, MainActivity.this );
                        answer.setBackgroundColor(placeholder_text.equals(text) ? Color.TRANSPARENT : checkAnswer(text) ? Color.GREEN : Color.RED);
                        // setURL(v);
                        return true;
                    }
                    return false;
                }
            });
        } else {
            textView.setVisibility( View.VISIBLE);
        }
    }

    private boolean checkAnswer ( String text) {
        boolean correct = query.checkAnswer(text);
        if (query instanceof ContentQueryRef) {
            toast( "next title ... ");
            EditText et_url = (EditText) findViewById(R.id.url_et);
            et_url.setText(text);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setURL(null);
                }
            }, 1000);
        } else if ((Settings.mode & Settings.MODE_AUTO_NEXT)>0)  {
            toast( "auto next query ...", true);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setURL(null);
                }
            }, 1000);
        }
        return correct;
    }

    private void initEditText( int editTextId){
        final String placeholder_text = "";
        EditText editText = (EditText) findViewById( R.id.url_et);
        editText.setText(placeholder_text);
        if (true) {
            editText.setVisibility( View.INVISIBLE);
            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText answer = (EditText) findViewById(R.id.url_et);
                    answer.setText("");
                    answer.setShowSoftInputOnFocus(true);
                }
            });
            editText.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        EditText answer = (EditText) findViewById(R.id.url_et);
                        MainActivity.checkText( answer, MainActivity.this );
                        setURL(v);
                        return true;
                    }
                    return false;
                }
            });
        }
        editText.setVisibility( View.VISIBLE);
    }

    static public String checkText(TextView view, AppCompatActivity context) {
        final String placeholder_text = "direct answer ...";
        String text = view.getText().toString();
        String specialInput = "mode";
        //String specialText = placeholder_text;
        String specialText = content.title;
        switch (text.toUpperCase()) {
            case "FULL": Settings.mode ^= Settings.MODE_FULL ; content = new Content(); break;
            case "FREE": Settings.mode ^= Settings.MODE_ALLOW_FREE_INPUT ; break;
            case "HARD": Settings.mode ^= Settings.MODE_ALLOW_FREE_INPUT + Settings.MODE_FULL;  content = new Content(); break;
            case "AUTO": Settings.mode ^= Settings.MODE_AUTO_NEXT; break;
            default:    specialInput = null;
        }
        if (null == specialInput ) {
            specialInput = "input";
            switch (text.toUpperCase()) {
                case "RANDOM":
                case "*":
                    specialText = "";
                    break;
                default:
                    specialInput = null;
            }
        }
        if (null!= specialInput && !specialInput.isEmpty()) {
            Toast.makeText( context, "Enter special " + specialInput + ": " + text, Toast.LENGTH_LONG).show();
            view.setText(specialText);
        }
        view.clearFocus();
        return text;
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


    // ------------------------------------------------------
    //
    //      Swipe Gesture ...
    //
    //      http://mrbool.com/how-to-work-with-swipe-gestures-in-android/28088
    //      https://stackoverflow.com/questions/7919865/detecting-a-long-press-with-android
    //

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        Toast.makeText(getApplicationContext(), (" - down "), Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        Toast.makeText(getApplicationContext(), (" - press "), Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        Toast.makeText(getApplicationContext(), (" - tap "), Toast.LENGTH_SHORT).show();

        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        //Toast.makeText(getApplicationContext(), (" - scroll "), Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        Toast.makeText(getApplicationContext(), (" - long press "), Toast.LENGTH_SHORT).show();


    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        // Toast.makeText(getApplicationContext(), (" - fling "), Toast.LENGTH_SHORT).show();

        return false;
    }
}
