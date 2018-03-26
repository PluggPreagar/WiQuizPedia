package de.preisfrieden.wiquizpedia;

import org.junit.Before;
import org.junit.Test;

import de.preisfrieden.wiquizpedia.token.TokenParser;

import static org.junit.Assert.assertEquals;

/**
 * Created by peter on 23.03.2018.
 */

public class TokenParserTest {


    private TokenParser tokenNewLine;
    private TokenParser tokenYear;
    private TokenParser tokenNum;
    private TokenParser tokenParent;
    private TokenParser tokenParentNewLine;

    @Before
    public void setup(){
        tokenNewLine = new TokenParser("\\.");
        tokenYear = new TokenParser("\\d{1,2}\\.\\d{1,2}\\.\\s*\\d{4}");
        tokenNum = new TokenParser("\\d+");
        // prio is special kind of hierarchy
        tokenNewLine.add(tokenYear.add(tokenNum));

        tokenParent = new TokenParser("");
        tokenParent.add(tokenYear).add(tokenNum).add(tokenNewLine);

        tokenParentNewLine = new TokenParser("");
        tokenParentNewLine.add(tokenNewLine);
    }

    @Test
    public void testSimple(){
        assertEquals( "[]" , tokenParentNewLine.parse("").toString() );
        assertEquals( "[ <a>]" , tokenParentNewLine.parse("a").toString() );
        assertEquals( "[ <> <.>]" , tokenParentNewLine.parse(".").toString() );
        assertEquals( "[ <abc > <.>]" , tokenParentNewLine.parse("abc .").toString() );
        assertEquals( "[ <abc > <.> < def>]" , tokenParentNewLine.parse("abc . def").toString() );
    }

    @Test
    public void testHierarchy1(){
        assertEquals( "[]" , tokenParent.parse("").toString() );
        assertEquals( "[ <a>]" , tokenParent.parse("a").toString() );
        assertEquals( "[ <> <19>]" , tokenParent.parse("19").toString() );
        assertEquals( "[ <> <1986>]" , tokenParent.parse("1986").toString() );
        assertEquals( "[ <a > <1.4. 1998>]" , tokenParent.parse("a 1.4. 1998").toString() );
        assertEquals( "[ <abc > <.> < def>]" , tokenParent.parse("abc . def").toString() );
        assertEquals( "[ <abc > <.> < > <1998>]" , tokenParent.parse("abc . 1998").toString() );
        assertEquals( "[ <abc> <.> <> <1998>]" , tokenParent.parse("abc.1998").toString() );
    }

}
