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

    public ContentQueryRef(List<String> potentialTitles, Tokens token, Content content ) {
        this.content = content;
        this.msg_query_id = -1;
        this.msg = "please insert new title or select below";
        this.answer_token_id = "";
        this.answer_token = "";
        this.answer_token_avail.clear();
        for (int i = 0; i < potentialTitles.size() ; i++) {
            this.answer_token_avail.add( potentialTitles.get( i ) );
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
