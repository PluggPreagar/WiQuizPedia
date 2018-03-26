package de.preisfrieden.wiquizpedia.token;

import android.media.session.MediaSession;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by peter on 23.03.2018.
 */

public class TokenParser {

    private Pattern pattern;
    private Matcher matcher;
    private int start;
    private List<TokenParser> childs;
    private String category;
    private int percent2keep;

    //public class TokenAlternatives extends ArrayList<Token>{};
    public class TokenStructure extends ArrayList<TokenStructure>{

        private String seperator  = " / " ;

        String value;
        TokenParser parser;

        public TokenStructure addParsedToken(String value, TokenParser parser) {
            TokenStructure newToken = add( value);
            newToken.parser = parser;
            return newToken;
        }

        public TokenStructure add(String value) {
            TokenStructure newToken = new TokenStructure();
            newToken.value = value;
            add( newToken);
            return newToken;
        }

        public void toString(StringBuffer sb) {
            //if (null != seperator ) sb.append(seperator );
            if (null != value) {
                sb.append( " <" );
                sb.append( value);
                sb.append( ">" );
            }
            else {
                sb.append( " [" );
                for( TokenStructure myTokens : this){
                    //if (null != seperator ) sb.append(seperator );
                    myTokens.toString(sb);
                }
                sb.append( "]" );
            }
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            toString( sb) ;
            //if (null != seperator) sb.delete(0, seperator.length() );
            sb.delete(0, 1 );
            return  sb.toString();
        }
    };

    public TokenParser(String category, String pattern, int percent2keep) {
        this.pattern = Pattern.compile( pattern) ;
        this.category = category;
        this.percent2keep = percent2keep;
    }

    public TokenParser(String pattern) {
        this.pattern = Pattern.compile( pattern) ;
    }

    public Matcher matcher(CharSequence msg) {
        matcher = pattern.matcher( msg);
        return matcher;
    }

    public TokenParser add( TokenParser childTokenParser) {
        if (null == childs) childs = new ArrayList<TokenParser>();
        childs.add(childTokenParser);
        return this;
    }

    public TokenStructure parse(String msg, Integer start, Integer end) {
        TokenStructure token = new TokenStructure();
        matcher(msg);
        Integer lastEnd = 0;
        while (matcher.find()) {
            int curStart = matcher.start();
            for (TokenParser child : childs) {
                token.add(child.parse( msg, lastEnd, curStart ));
            }
            token.add( msg.substring(lastEnd, curStart) );
            lastEnd = matcher.end();
            token.addParsedToken( msg.substring( curStart , lastEnd) , this);
        }
        if (lastEnd < msg.length()) token.add( msg.substring( lastEnd));
        return token;
    }

    public TokenStructure parse(String msg) {
        TokenStructure token = new TokenStructure();
        int len = msg.length();
        start = 0;
        for (TokenParser child : childs) {
            child.matcher = child.matcher( msg );
            child.start = -1 ;
        }
        while (start < len ) { // search up to end of string
            TokenParser nextMatchingChild = null;
            for (TokenParser child : childs) { //  search nearest matching regexp / skipp use order as prio
                while (child.start < start) child.start = child.matcher.find() ? child.matcher.start() : len;
                if ( null == nextMatchingChild || child.start < nextMatchingChild.start)  nextMatchingChild = child;
            }
            token.add( msg.substring(start, (start = nextMatchingChild.start)) );
            if (start < len ) token.addParsedToken( msg.substring(start, (start = nextMatchingChild.matcher.end())), nextMatchingChild);
        } ;
        return token;
    }


}
