package de.preisfrieden.wiquizpedia;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by peter on 21.02.2018.
 */

public class ContentQuery {

    Content content;

    boolean first_try = true;
    int msg_query_id;
    String msg = "";
    String answer_token_id = "";
    String answer_token = "";
    List<String> answer_token_avail = new ArrayList<String>();

    public ContentQuery( List<String> msg_querable_sentences, Tokens token, Content content ) {
        this.content = content;
        if (!msg_querable_sentences.isEmpty()) {
            Random random = new Random(System.nanoTime());

            msg_query_id = random.nextInt(msg_querable_sentences.size());
            msg = msg_querable_sentences.get(msg_query_id); // getRandom(msg_querable_sentences);

            List<String> tokenIdFound = new ArrayList<String>();
            Pattern pTokenId = Pattern.compile("__([A-Za-z]+)[0-9]+__", Pattern.MULTILINE + Pattern.DOTALL);
            Matcher matchTokenId = pTokenId.matcher(msg);
            while (matchTokenId.find()) tokenIdFound.add(matchTokenId.group());

            answer_token_id = tokenIdFound.remove( random.nextInt(tokenIdFound.size()));
            answer_token = token.getToken4Id(answer_token_id);
            List<String> values4CategoryOfToken = new ArrayList<String>(token.getValues4CategoryOfToken(answer_token_id)); // TODO - do not copy ... just make random pick smarter

            int max_queries_per_sentence = Settings.max_queries_per_sentence;
            if (0< max_queries_per_sentence && tokenIdFound.size() + 1 > max_queries_per_sentence) {
                ArrayList<String> tokenIdFoundAndFix = new ArrayList<String>(tokenIdFound);
                Collections.shuffle(tokenIdFoundAndFix, random);
                if (0 != (Settings.mode & Settings.MODE_TOKEN_FILL_ALL_SENTENCE)){
                    for(String tokenId2repl : tokenIdFound.subList(0,max_queries_per_sentence)) {
                        String token2repl = token.getToken4Id( tokenId2repl);
                        if (0 == (Settings.mode & Settings.MODE_TOKEN_FILL_OTHER_TYPE_SENTENCE) || !values4CategoryOfToken.contains(token2repl) )
                            msg = msg.replaceAll( tokenId2repl, token.getToken4Id( tokenId2repl));
                    }
                }
                content.msg_querable_sentences.set(msg_query_id, msg);
            }

            if (0 != (Settings.mode & Settings.MODE_TOKEN_FILL_ALL_SENTENCE)) {
                for (String tokenId2repl : tokenIdFound) {
                    String token2repl = token.getToken4Id(tokenId2repl);
                    if (0 == (Settings.mode & Settings.MODE_TOKEN_FILL_OTHER_TYPE_SENTENCE) || !values4CategoryOfToken.contains(token2repl) ) {
                        while (values4CategoryOfToken.remove(token2repl)) ;
                        msg = msg.replaceAll(tokenId2repl, token2repl);
                    }
                }
            }

            answer_token_avail = new ArrayList<String>( Arrays.asList(answer_token));
            if ( values4CategoryOfToken.size() < 2 && answer_token_id.startsWith("__Date") ) { // if no further dates to select - just go with year ...
                values4CategoryOfToken = new ArrayList<String>(token.getValues4CategoryOfToken("__Year1__"));
                answer_token_avail.set(0,answer_token_avail.get(0).replaceAll("^.*([0-9]{4}).*$", "$1"));
            }
            while( answer_token_avail.size() < 3 && ! values4CategoryOfToken.isEmpty() ) {
                while (values4CategoryOfToken.remove(answer_token_avail.get(answer_token_avail.size() - 1))); // might have item multiple times ...
                if (!values4CategoryOfToken.isEmpty()) answer_token_avail.add(values4CategoryOfToken.remove(new Random().nextInt(values4CategoryOfToken.size())));
            }

            Collections.shuffle(answer_token_avail, random);

        }
    }

    public boolean checkAnswer(String answer) {
        boolean correct = answer_token.toLowerCase().equals(answer.toLowerCase());
        boolean correctAlmost = answer_token.toLowerCase().endsWith(answer.toLowerCase());
        if ((correct || correctAlmost)&& first_try) {
            msg = content.msg_querable_sentences.get(msg_query_id).replace(answer_token_id, answer_token);
            if (msg.contains("__")) {
                content.msg_querable_sentences.set(msg_query_id, msg);
                if (!correct && correctAlmost) MainActivity.toast( "answer is "+ (!correct && correctAlmost ? "more or less " : "") + "right", false);
            } else {
                content.msg_querable_sentences.remove(msg_query_id);
                MainActivity.toast( "query is "+ (!correct && correctAlmost ? "more or less " : "") + "solved", true );
            }
        }
        first_try = false;
        return correctAlmost;
    }

}


