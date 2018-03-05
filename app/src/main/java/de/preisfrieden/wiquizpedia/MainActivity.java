package de.preisfrieden.wiquizpedia;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
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
import java.util.List;


public class MainActivity extends AppCompatActivity implements DownloadCallback {


    /*
    https://stackoverflow.com/questions/17271147/android-get-content-from-url
    https://developer.android.com/reference/javax/net/ssl/HttpsURLConnection.html

    https://de.wikipedia.org/w/api.php?action=query&prop=extracts&titles=Albert%20Einstein&exintro=&explaintext=&redirects=&formatversion=2
    https://en.wikipedia.org/w/api.php

    https://stackoverflow.com/questions/4460921/extract-the-first-paragraph-from-a-wikipedia-article-python
    https://stackoverflow.com/questions/627594/is-there-a-wikipedia-api


     */

    private static Activity gui = null;
    private static Content content = null;
    private static ContentQuery query = null;

    private List<CheckBox> checkBoxes = new ArrayList<CheckBox>();

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    public static final int MODE_FULL = Content.MODE_FULL;
    public static final int MODE_ALLOW_FREE_INPUT = Content.MODE_ALLOW_FREE_INPUT; // Query wiht only 1 avail answer
    public static final int MODE_AUTO_NEXT = 4;
    private static int mode = MODE_AUTO_NEXT;
    private String picTitle;


    public static void toast(String text, boolean toast_long ){
        Toast.makeText(gui, text, toast_long ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gui = this;
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.tv_question)).setMovementMethod(new ScrollingMovementMethod());
        EditText urlEt = (EditText) findViewById(R.id.url_et);
        initCheckbox();
        initEditText( R.id.url_et);
        initTextView(R.id.et_answer, true);
        if (null == content) {
            content = new Content();
            urlEt.setText("Albert Einstein");
            setURL(null);
        } else {
            redraw();
        }
    }

    /** Called when the user taps the Send button */
    public void setURL(View view) {
        resetInputFocus();
        updateQuery(false);
    }

    public void resetInputFocus()   {
        findViewById(R.id.url_et).clearFocus();
        findViewById(R.id.tv_question).clearFocus();
        findViewById(R.id.et_answer).clearFocus();
        closeSoftKeyboard(null);
    }


    public void updateQuery(boolean forceLoadData)   {
        EditText editText = (EditText) findViewById(R.id.url_et);
        String curTitle = editText.getText().toString();
        forceLoadData |=  null == content || curTitle.isEmpty() || !content.title.equals( curTitle);
        new ContentTask(this).doInBackground(new ContentTaskParam(content, forceLoadData ? curTitle : null ));
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
        String msg = null ;
        if ( null == result) {
        } else if (result instanceof  Drawable) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageDrawable( (Drawable) result);
        } else if (result instanceof  ContentQuery){
            msg = "loading ...";
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
            editText.setText(query.content.title);
            //editText.setHeight( Math.max(1 ,editText.getLineCount()) * editText.getLineHeight());
            setCheckbox( query.answer_token_avail);
            initTextView(R.id.et_answer, false);
            String picTitleNew = editText.getText().toString();
            if (!picTitleNew.equals(picTitle) && ! picTitleNew.isEmpty()) {
                picTitle = picTitleNew;
                // Content.updatePicFromData( query.content.msg_orig, this);
                Content.updatePicFromData( query.content.title, this);
            }
            if (!query.answer_token_id.isEmpty())  msg = query.msg.replaceAll( query.answer_token_id , "___");
            setButton();
        }

        TextView textView = (TextView) findViewById(R.id.tv_question);
        if (null != msg ) {
            textView.setText( msg );
            //textView.setHeight( Math.max(1,textView.getLineCount() * textView.getLineHeight()));
            textView.setVisibility( View.VISIBLE);
            textView.setHeight( Math.max(5,textView.getLineCount()) * textView.getLineHeight());

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
        if ((mode & MODE_AUTO_NEXT)>0)  {
            Toast.makeText(this, "auto next ...", Toast.LENGTH_LONG);
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
                }
            });
            editText.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        EditText answer = (EditText) findViewById(R.id.url_et);
                        String text = MainActivity.checkText( answer, MainActivity.this );
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
            case "FULL": mode ^= MODE_FULL ; break;
            case "FREE": mode ^= MODE_ALLOW_FREE_INPUT ; break;
            case "HARD": mode ^= MODE_ALLOW_FREE_INPUT + MODE_FULL; break;
            case "AUTO": mode ^= MODE_AUTO_NEXT; break;
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
            Content.setMode( mode);
            Toast.makeText( context, "Enter special " + specialInput + ": " + text, Toast.LENGTH_LONG).show();
            view.setText(specialText);
        }
        view.clearFocus();
        return text;
    }

}
