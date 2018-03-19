package de.preisfrieden.wiquizpedia;

import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by peter on 08.03.2018.
 */

public class ContentQueryRef extends ContentQuery {

    public static String loadingTitle = "  L o a d i n g . .  ";

    public ContentQueryRef(String nextTitle, Content content ) {
        this(null, null, content);
        if (null != nextTitle) this.title = nextTitle;
    }

    public ContentQueryRef(List<String> potentialTitles, Content content ) {
        this(potentialTitles, null, content);
    }

    public ContentQueryRef(List<String> potentialTitles, Tokens token, Content content ) {
        this.content = content;
        this.title = null == potentialTitles ? loadingTitle : null == content ? "" : content.title;
        this.msg_query_id = -1;
        this.msg = null == potentialTitles ? "\n \t\t loading ... "
                        : ((null == content || 0 == content.msg_querable_sentences.size() ? " \tsorry no (more) queries found \n"  : "" )
                                    + "\n \t\t\t please insert new title \n\n \t\t\t\t or select from below");
        this.answer_token_id = "";
        this.answer_token = "";
        this.answer_token_avail.clear();
        if (null != potentialTitles) {
            for (int i = 0; i < potentialTitles.size() ; i++) {
                this.answer_token_avail.add( potentialTitles.get( i ) );
            }
        }
        Collections.shuffle(answer_token_avail, random);
    }

    public boolean checkAnswer(String answer) {
        return true;
    }

    public ContentQuery mergeWithNextSentence() {
        return this;
    }
}
