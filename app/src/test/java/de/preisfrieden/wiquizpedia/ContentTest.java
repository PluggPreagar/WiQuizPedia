package de.preisfrieden.wiquizpedia;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Created by peter on 21.02.2018.
 */

public class ContentTest extends Content {


    static public void GetContentData(String query, DownloadCallback callback)   {

    }

    static public void updatePicFromData(String data, DownloadCallback callback) {

    }



    // --------------------------------------------

    public void Content(){
    }


    @Test
    public void parseTitle(){
        // urlStr = "https://de.wikipedia.org/w/api.php?action=query&prop=extracts&exintro=&explaintext=&redirects=&formatversion=2&format=json&titles=Brasilien";
        String msg = "{\"batchcomplete\":true,\"query\":{\"pages\":[{\"pageid\":7771,\"ns\":0,\"title\":\"Brasilien\",\"extract\":\"Brasilien (portugiesisch Brasil, gemäß Lautung des brasilianischen Portugiesisch [bɾaˈziu̯] ) ist der flächen- und bevölkerungsmäßig fünftgrößte Staat der Erde. Es ist das größte und mit über 200 Millionen Einwohnern auch das bevölkerungsreichste Land Südamerikas, von dessen Fläche es 47,3 Prozent einnimmt. Brasilien hat mit jedem südamerikanischen Staat außer Chile und Ecuador eine gemeinsame Grenze.\\nDie ersten Spuren menschlicher Besiedlung durch Paläo-Indianer reichen mehrere tausend Jahre zurück. Nach der Entdeckung Amerikas und der Aufteilung des südamerikanischen Kontinents durch den Vertrag von Tordesillas wurde Brasilien eine portugiesische Kolonie. Diese mehr als drei Jahrhunderte andauernde Kolonialzeit, in der Einwanderer verschiedenster Herkunft (freiwillig oder gezwungenermaßen) nach Brasilien kamen, trug erheblich zur ethnischen Vielfalt des heutigen Staates bei. Nach der im Jahre 1822 erlangten Unabhängigkeit, auf die eine Zeit der konstitutionellen Monarchie folgte, wurde das Land 1889 als Vereinigte Staaten von Brasilien zu einer Republik. Nach der Zeit der Militärdiktatur von 1964 bis 1985 kehrte das Land zur Demokratie mit einem präsidentiellen Regierungssystem zurück.\\nDer Name Brasilien geht auf den portugiesischen Namen pau-brasil des Brasilholz-Baumes (Caesalpinia echinata), der ein wichtiges Ausfuhrprodukt zur Zeit der frühen Kolonisation aus den Wäldern der Atlantikküste war, zurück. Brasa bedeutet im Portugiesischen „Glut“ und „glühende Kohlen“; das Adjektiv brasil („glutartig“) bezieht sich auf die Farbe des Holzes, das, wenn geschnitten, rot leuchtet (Brasilin) und in Europa zum Färben von Stoffen benutzt wurde.\"}]}}";

        assertEquals("Brasilien", parseTitle(msg));
        assertEquals("Brasilien", title);

        /*
        String urlStr = "https://de.wikipedia.org/w/api.php?action=query&prop=extracts&exintro=&explaintext=&redirects=&formatversion=2&format=json&titles=";
        urlStr = "https://de.wikipedia.org/w/api.php?action=query&prop=extracts&exintro=&explaintext=&redirects=&formatversion=2&format=json&titles=Albert+Einstein";
        urlStr = "https://de.wikipedia.org/w/api.php?action=query&prop=extracts&exintro=&explaintext=&redirects=&formatversion=2&format=json&titles=Brasilien";

        String msg = "{\"batchcomplete\":true,\"query\":{\"pages\":[{\"pageid\":7771,\"ns\":0,\"title\":\"Brasilien\",\"extract\":\"Brasilien (portugiesisch Brasil, gemäß Lautung des brasilianischen Portugiesisch [bɾaˈziu̯] ) ist der flächen- und bevölkerungsmäßig fünftgrößte Staat der Erde. Es ist das größte und mit über 200 Millionen Einwohnern auch das bevölkerungsreichste Land Südamerikas, von dessen Fläche es 47,3 Prozent einnimmt. Brasilien hat mit jedem südamerikanischen Staat außer Chile und Ecuador eine gemeinsame Grenze.\\nDie ersten Spuren menschlicher Besiedlung durch Paläo-Indianer reichen mehrere tausend Jahre zurück. Nach der Entdeckung Amerikas und der Aufteilung des südamerikanischen Kontinents durch den Vertrag von Tordesillas wurde Brasilien eine portugiesische Kolonie. Diese mehr als drei Jahrhunderte andauernde Kolonialzeit, in der Einwanderer verschiedenster Herkunft (freiwillig oder gezwungenermaßen) nach Brasilien kamen, trug erheblich zur ethnischen Vielfalt des heutigen Staates bei. Nach der im Jahre 1822 erlangten Unabhängigkeit, auf die eine Zeit der konstitutionellen Monarchie folgte, wurde das Land 1889 als Vereinigte Staaten von Brasilien zu einer Republik. Nach der Zeit der Militärdiktatur von 1964 bis 1985 kehrte das Land zur Demokratie mit einem präsidentiellen Regierungssystem zurück.\\nDer Name Brasilien geht auf den portugiesischen Namen pau-brasil des Brasilholz-Baumes (Caesalpinia echinata), der ein wichtiges Ausfuhrprodukt zur Zeit der frühen Kolonisation aus den Wäldern der Atlantikküste war, zurück. Brasa bedeutet im Portugiesischen „Glut“ und „glühende Kohlen“; das Adjektiv brasil („glutartig“) bezieht sich auf die Farbe des Holzes, das, wenn geschnitten, rot leuchtet (Brasilin) und in Europa zum Färben von Stoffen benutzt wurde.\"}]}}";
        //MainActivity.Content content = new MainActivity.Content( msg );

        String content1 = content.extractContent("");
        */
    }


    @Test
    public void normaliseContent() {
        String normaliseContent = "";

        // urlStr = "https://de.wikipedia.org/w/api.php?action=query&prop=extracts&exintro=&explaintext=&redirects=&formatversion=2&format=json&titles=Brasilien";
        String msg = "{\"batchcomplete\":true,\"query\":{\"pages\":[{\"pageid\":7771,\"ns\":0,\"title\":\"Brasilien\",\"extract\":\"Brasilien (portugiesisch Brasil, gemäß Lautung des brasilianischen Portugiesisch [bɾaˈziu̯] ) ist der flächen- und bevölkerungsmäßig fünftgrößte Staat der Erde. Es ist das größte und mit über 200 Millionen Einwohnern auch das bevölkerungsreichste Land Südamerikas, von dessen Fläche es 47,3 Prozent einnimmt. Brasilien hat mit jedem südamerikanischen Staat außer Chile und Ecuador eine gemeinsame Grenze.\\nDie ersten Spuren menschlicher Besiedlung durch Paläo-Indianer reichen mehrere tausend Jahre zurück. Nach der Entdeckung Amerikas und der Aufteilung des südamerikanischen Kontinents durch den Vertrag von Tordesillas wurde Brasilien eine portugiesische Kolonie. Diese mehr als drei Jahrhunderte andauernde Kolonialzeit, in der Einwanderer verschiedenster Herkunft (freiwillig oder gezwungenermaßen) nach Brasilien kamen, trug erheblich zur ethnischen Vielfalt des heutigen Staates bei. Nach der im Jahre 1822 erlangten Unabhängigkeit, auf die eine Zeit der konstitutionellen Monarchie folgte, wurde das Land 1889 als Vereinigte Staaten von Brasilien zu einer Republik. Nach der Zeit der Militärdiktatur von 1964 bis 1985 kehrte das Land zur Demokratie mit einem präsidentiellen Regierungssystem zurück.\\nDer Name Brasilien geht auf den portugiesischen Namen pau-brasil des Brasilholz-Baumes (Caesalpinia echinata), der ein wichtiges Ausfuhrprodukt zur Zeit der frühen Kolonisation aus den Wäldern der Atlantikküste war, zurück. Brasa bedeutet im Portugiesischen „Glut“ und „glühende Kohlen“; das Adjektiv brasil („glutartig“) bezieht sich auf die Farbe des Holzes, das, wenn geschnitten, rot leuchtet (Brasilin) und in Europa zum Färben von Stoffen benutzt wurde.\"}]}}";
        String msg2="Brasilien (portugiesisch Brasil, gemäß Lautung des brasilianischen Portugiesisch [bɾaˈziu̯] ) ist der flächen- und bevölkerungsmäßig fünftgrößte Staat der Erde.\n" +
                "Es ist das größte und mit über 200 Millionen Einwohnern auch das bevölkerungsreichste Land Südamerikas, von dessen Fläche es 47,3 Prozent einnimmt.\n" +
                "Brasilien hat mit jedem südamerikanischen Staat außer Chile und Ecuador eine gemeinsame Grenze.\n" +
                "Die ersten Spuren menschlicher Besiedlung durch Paläo-Indianer reichen mehrere tausend Jahre zurück.\n" +
                "Nach der Entdeckung Amerikas und der Aufteilung des südamerikanischen Kontinents durch den Vertrag von Tordesillas wurde Brasilien eine portugiesische Kolonie.\n" +
                "Diese mehr als drei Jahrhunderte andauernde Kolonialzeit, in der Einwanderer verschiedenster Herkunft (freiwillig oder gezwungenermaßen) nach Brasilien kamen, trug erheblich zur ethnischen Vielfalt des heutigen Staates bei.\n" +
                "Nach der im Jahre 1822 erlangten Unabhängigkeit, auf die eine Zeit der konstitutionellen Monarchie folgte, wurde das Land 1889 als Vereinigte Staaten von Brasilien zu einer Republik.\n" +
                "Nach der Zeit der Militärdiktatur von 1964 bis 1985 kehrte das Land zur Demokratie mit einem präsidentiellen Regierungssystem zurück.\n" +
                "Der Name Brasilien geht auf den portugiesischen Namen pau-brasil des Brasilholz-Baumes (Caesalpinia echinata), der ein wichtiges Ausfuhrprodukt zur Zeit der frühen Kolonisation aus den Wäldern der Atlantikküste war, zurück.\n" +
                "Brasa bedeutet im Portugiesischen „Glut“ und „glühende Kohlen“; das Adjektiv brasil („glutartig“) bezieht sich auf die Farbe des Holzes, das, wenn geschnitten, rot leuchtet (Brasilin) und in Europa zum Färben von Stoffen benutzt wurde.";
        normaliseContent = normaliseContent(msg);
        assertEquals(msg2, normaliseContent);

        // https://de.wikipedia.org/w/api.php?action=query&format=json&prop=revisions|images|links|extracts&exintro=&explaintext=&titles=George_Mallia&redirects=1&formatversion=2&rvprop=content&pllimit=50
        msg="{\"batchcomplete\":true,\"query\":{\"normalized\":[{\"fromencoded\":false,\"from\":\"George_Mallia\",\"to\":\"George Mallia\"}],\"pages\":[{\"pageid\":4675984,\"ns\":0,\"title\":\"George Mallia\",\"revisions\":[{\"contentformat\":\"text/x-wiki\",\"contentmodel\":\"wikitext\",\"content\":\"{{Infobox Fußballspieler\\n| kurzname            = George Mallia\\n| bildname            = Mallia, George.jpg\\n| bildbreite          = \\n| bildunterschrift    = George Mallia 2009\\n| langname            = George Mallia\\n| geburtstag          = 10. Oktober 1978\\n| geburtsort          = [[Sliema]]\\n| geburtsland         = [[Malta]]\\n| position            = [[Mittelfeld]]\\n| jugendvereine_tabelle =\\n| vereine_tabelle =\\n{{Team-Station|1995–1998|[[Sliema Wanderers]]|44 (11)}}\\n{{Team-Station|1998–2002|[[FC Floriana]]|84 {{0}}(9)}}\\n{{Team-Station|2002–    |[[FC Birkirkara]]|171 (24)}}\\n| nationalmannschaft_tabelle =\\n{{Team-Station|?    |[[Maltesische Fußballnationalmannschaft (U-21-Männer)|Malta U-21]]|12 {{0}}(3)}}\\n{{Team-Station|1997–|[[Maltesische Fußballnationalmannschaft|Malta]]|60 {{0}}(5)}}\\n| trainer_tabelle =\\n}}\\n'''George Mallia''' (* [[10. Oktober]] [[1978]] in [[Sliema]]) ist ein maltesischer Fußballspieler.\\n\\nMallia begann seine Karriere in der [[Maltese Premier League]] bei [[Sliema Wanderers]]. Dort spielte er vier Jahre und wechselte im Sommer 1998 zu [[Floriana FC]]. Seit Sommer 2002 spielt er bei [[FC Birkirkara]]. Für die [[Maltesische Fußballnationalmannschaft|Nationalmannschaft]] kam er seit 1997 auf 60 Einsätze.\\n\\n== Weblinks ==\\n* [http://www.maltafootball.com/players/georgemallia.html George Mallia bei MaltaFootball.com]\\n* {{NFTPlayer|ID=4222}}\\n\\n{{SORTIERUNG:Mallia, George}}\\n[[Kategorie:Fußballnationalspieler (Malta)]]\\n[[Kategorie:Fußballspieler (FC Birkirkara)]]\\n[[Kategorie:Fußballspieler (FC Floriana)]]\\n[[Kategorie:Fußballspieler (Sliema Wanderers)]]\\n[[Kategorie:Malteser]]\\n[[Kategorie:Geboren 1978]]\\n[[Kategorie:Mann]]\\n\\n{{Personendaten\\n|NAME=Mallia, George\\n|ALTERNATIVNAMEN=\\n|KURZBESCHREIBUNG=maltesischer Fußballspieler\\n|GEBURTSDATUM=10. Oktober 1978\\n|GEBURTSORT=[[Sliema]], [[Malta]]\\n|STERBEDATUM=\\n|STERBEORT=\\n}}\"}],\"images\":[{\"ns\":6,\"title\":\"Datei:Mallia, George.jpg\"}],\"links\":[{\"ns\":0,\"title\":\"10. Oktober\"},{\"ns\":0,\"title\":\"1978\"},{\"ns\":0,\"title\":\"FC Birkirkara\"},{\"ns\":0,\"title\":\"FC Floriana\"},{\"ns\":0,\"title\":\"Floriana FC\"},{\"ns\":0,\"title\":\"Malta\"},{\"ns\":0,\"title\":\"Maltese Premier League\"},{\"ns\":0,\"title\":\"Maltesische Fußballnationalmannschaft\"},{\"ns\":0,\"title\":\"Maltesische Fußballnationalmannschaft (U-21-Männer)\"},{\"ns\":0,\"title\":\"Mittelfeld\"},{\"ns\":0,\"title\":\"Sliema\"},{\"ns\":0,\"title\":\"Sliema Wanderers\"},{\"ns\":12,\"title\":\"Hilfe:Personendaten\"}],\"extract\":\"George Mallia (* 10. Oktober 1978 in Sliema) ist ein maltesischer Fußballspieler.\\nMallia begann seine Karriere in der Maltese Premier League bei Sliema Wanderers. Dort spielte er vier Jahre und wechselte im Sommer 1998 zu Floriana FC. Seit Sommer 2002 spielt er bei FC Birkirkara. Für die Nationalmannschaft kam er seit 1997 auf 60 Einsätze.\"}]}}";
        msg2 ="George Mallia (* 10.Oktober1978 in Sliema) ist ein maltesischer Fußballspieler.\n" +
                "Mallia begann seine Karriere in der Maltese Premier League bei Sliema Wanderers.\n" +
                "Dort spielte er vier Jahre und wechselte im Sommer 1998 zu Floriana FC.\n" +
                "Seit Sommer 2002 spielt er bei FC Birkirkara.\n" +
                "Für die Nationalmannschaft kam er seit 1997 auf 60 Einsätze.";
        normaliseContent = normaliseContent(msg);
        assertEquals(msg2, normaliseContent);

        // [[Sliema Wanderers]]

    }

    @Test
    public void parseRef() {
        // https://de.wikipedia.org/w/api.php?action=query&prop=revisions|images&rvprop=content&redirects=&formatversion=2&format=json&titles=Dainik+Jagran
        // https://de.wikipedia.org/w/api.php?action=query&format=json&prop=revisions|images|links&redirects=1&formatversion=2&rvprop=content&pllimit=50&titles=George_Mallia
        String msg = "{\"batchcomplete\":true,\"query\":{\"pages\":[{\"pageid\":9363338,\"ns\":0,\"title\":\"Dainik Jagran\",\"revisions\":[{\"contentformat\":\"text/x-wiki\",\"contentmodel\":\"wikitext\",\"content\":\"{{Infobox Publikation\\n| titel              = Dainik Jagran\\n| bild               = \\n| beschreibung       = indische Tageszeitung\\n| fachgebiet         = \\n| sprache            = Hindi\\n| verlag             = ''Jagran Prakashan Limited'', [[Kanpur]]\\n| land               = Indien\\n| erstausgabe_tag    = \\n| erstausgabe_jahr   = 1942\\n| einstellung_tag    = \\n| einstellung_jahr   = \\n| erscheint          = täglich\\n| auflage_zahl       = etwa 3.112.560 (Dezember 2013)\\n| auflage_quelle     = \\n| verbreitung_quelle = \\n| verbreitung_zahl   = \\n| reichweite_zahl    = \\n| reichweite_quelle  = \\n| chefred            = \\n| herausgeber        = \\n| geschäftsführer    = \\n| weblink            = [http://www.jagran.com/ jagran.com]\\n| archiv             =\\n| issn               = \\n| zdb                = \\n| CODEN              = \\n}}\\n'''Dainik Jagran''' ({{hiS|दैनिक जागरण}}) ist eine [[Indien|indische]] [[Tageszeitung]] auf [[Hindi]].\\n\\nDer Sitz der Zeitung befindet sich im ''Jagran Building 2'', Sarvodya Nagar in [[Kanpur]]. Die Zeitung wurde 1942 in der Stadt [[Jhansi]]  von [[Puran Chandra Gupta]] erstmals publiziert. Nach Angaben der Organisation [[World Association of Newspapers]] ist die Dainik Jagran gegenwärtig eine der am häufigsten gelesenen Tageszeitungen der Welt, sowie die Zeitung mit den meisten Lesern in Indien (Stand: 2016)<ref>[http://www.wan-ifra.org/events/study-tour-success-made-in-india Wan-Ifra.org: Study]</ref> Sie ist auch die auflagenstärkste Zeitung in Hindi (Stand: 2013).<ref>{{Internetquelle|url=http://www.auditbureau.org/news/view/17|titel=AUDIT BUREAU OF CIRCULATIONS|format=PDF|datum=2013|zugriff=2016-02-02|sprache=en}}</ref> Die Zeitung gehört dem indischen Medienunternehmen ''Jagran Prakashan Limited'', das an der [[Bombay Stock Exchange]] und an der [[National Stock Exchange of India]] gelistet ist. Das Unternehmen ''Jagran Prakashan Limited'' erwarb 2010 die indische Zeitung ''Mid Day'' und 2012 die Zeitung ''Naiduniya''.\\n\\n== Weblinks ==\\n* [http://www.jagran.com/ Offizielle Webseite von Jagran.com]\\n\\n== Einzelnachweise ==\\n<references/>\\n\\n[[Kategorie:Tageszeitung]]\\n[[Kategorie:Zeitung (Indien)]]\\n[[Kategorie:Ersterscheinung 1942]]\\n[[Kategorie:Kanpur]]\"}]}]}}";
        msg = "{\"batchcomplete\":true,\"query\":{\"normalized\":[{\"fromencoded\":false,\"from\":\"George_Mallia\",\"to\":\"George Mallia\"}],\"pages\":[{\"pageid\":4675984,\"ns\":0,\"title\":\"George Mallia\",\"revisions\":[{\"contentformat\":\"text/x-wiki\",\"contentmodel\":\"wikitext\",\"content\":\"{{Infobox Fußballspieler\\n| kurzname            = George Mallia\\n| bildname            = Mallia, George.jpg\\n| bildbreite          = \\n| bildunterschrift    = George Mallia 2009\\n| langname            = George Mallia\\n| geburtstag          = 10. Oktober 1978\\n| geburtsort          = [[Sliema]]\\n| geburtsland         = [[Malta]]\\n| position            = [[Mittelfeld]]\\n| jugendvereine_tabelle =\\n| vereine_tabelle =\\n{{Team-Station|1995–1998|[[Sliema Wanderers]]|44 (11)}}\\n{{Team-Station|1998–2002|[[FC Floriana]]|84 {{0}}(9)}}\\n{{Team-Station|2002–    |[[FC Birkirkara]]|171 (24)}}\\n| nationalmannschaft_tabelle =\\n{{Team-Station|?    |[[Maltesische Fußballnationalmannschaft (U-21-Männer)|Malta U-21]]|12 {{0}}(3)}}\\n{{Team-Station|1997–|[[Maltesische Fußballnationalmannschaft|Malta]]|60 {{0}}(5)}}\\n| trainer_tabelle =\\n}}\\n'''George Mallia''' (* [[10. Oktober]] [[1978]] in [[Sliema]]) ist ein maltesischer Fußballspieler.\\n\\nMallia begann seine Karriere in der [[Maltese Premier League]] bei [[Sliema Wanderers]]. Dort spielte er vier Jahre und wechselte im Sommer 1998 zu [[Floriana FC]]. Seit Sommer 2002 spielt er bei [[FC Birkirkara]]. Für die [[Maltesische Fußballnationalmannschaft|Nationalmannschaft]] kam er seit 1997 auf 60 Einsätze.\\n\\n== Weblinks ==\\n* [http://www.maltafootball.com/players/georgemallia.html George Mallia bei MaltaFootball.com]\\n* {{NFTPlayer|ID=4222}}\\n\\n{{SORTIERUNG:Mallia, George}}\\n[[Kategorie:Fußballnationalspieler (Malta)]]\\n[[Kategorie:Fußballspieler (FC Birkirkara)]]\\n[[Kategorie:Fußballspieler (FC Floriana)]]\\n[[Kategorie:Fußballspieler (Sliema Wanderers)]]\\n[[Kategorie:Malteser]]\\n[[Kategorie:Geboren 1978]]\\n[[Kategorie:Mann]]\\n\\n{{Personendaten\\n|NAME=Mallia, George\\n|ALTERNATIVNAMEN=\\n|KURZBESCHREIBUNG=maltesischer Fußballspieler\\n|GEBURTSDATUM=10. Oktober 1978\\n|GEBURTSORT=[[Sliema]], [[Malta]]\\n|STERBEDATUM=\\n|STERBEORT=\\n}}\"}],\"images\":[{\"ns\":6,\"title\":\"Datei:Mallia, George.jpg\"}],\"links\":[{\"ns\":0,\"title\":\"10. Oktober\"},{\"ns\":0,\"title\":\"1978\"},{\"ns\":0,\"title\":\"FC Birkirkara\"},{\"ns\":0,\"title\":\"FC Floriana\"},{\"ns\":0,\"title\":\"Floriana FC\"},{\"ns\":0,\"title\":\"Malta\"},{\"ns\":0,\"title\":\"Maltese Premier League\"},{\"ns\":0,\"title\":\"Maltesische Fußballnationalmannschaft\"},{\"ns\":0,\"title\":\"Maltesische Fußballnationalmannschaft (U-21-Männer)\"},{\"ns\":0,\"title\":\"Mittelfeld\"},{\"ns\":0,\"title\":\"Sliema\"},{\"ns\":0,\"title\":\"Sliema Wanderers\"},{\"ns\":12,\"title\":\"Hilfe:Personendaten\"}]}]}}";
/*
        content = new Content();
        content.token.clear();
        content.parseRef("");
        assertEquals("{Ref={}, REF={}}", content.token.toString());

        content.token.clear();
        content.parseRef("[[FC Birkirkara]]");
        assertEquals("", content.token.toString());

        content.token.clear();
        content.parseRef("[[Maltesische Fußballnationalmannschaft (U-21-Männer)|Malta U-21]]");
        assertEquals("", content.token.toString());

        // assertEquals("Fußballklub", content.extractRef("[[FC Birkirkara]]")); // aus infobox von FC Birkirkara
        // assertEquals("U21-Fußballnationalmannschaft", content.extractRef("[[[[Maltesische Fußballnationalmannschaft (U-21-Männer)|Malta U-21]]]]"));
        // oder Fußballklub
*/
    }


    @Test
    public void testExtractRefs() {
        // https://de.wikipedia.org/w/api.php?action=query&prop=revisions|images&rvprop=content&redirects=&formatversion=2&format=json&titles=Dainik+Jagran
        // https://de.wikipedia.org/w/api.php?action=query&format=json&prop=revisions|images|links&redirects=1&formatversion=2&rvprop=content&pllimit=50&titles=George_Mallia
        String msg = "{\"batchcomplete\":true,\"query\":{\"pages\":[{\"pageid\":9363338,\"ns\":0,\"title\":\"Dainik Jagran\",\"revisions\":[{\"contentformat\":\"text/x-wiki\",\"contentmodel\":\"wikitext\",\"content\":\"{{Infobox Publikation\\n| titel              = Dainik Jagran\\n| bild               = \\n| beschreibung       = indische Tageszeitung\\n| fachgebiet         = \\n| sprache            = Hindi\\n| verlag             = ''Jagran Prakashan Limited'', [[Kanpur]]\\n| land               = Indien\\n| erstausgabe_tag    = \\n| erstausgabe_jahr   = 1942\\n| einstellung_tag    = \\n| einstellung_jahr   = \\n| erscheint          = täglich\\n| auflage_zahl       = etwa 3.112.560 (Dezember 2013)\\n| auflage_quelle     = \\n| verbreitung_quelle = \\n| verbreitung_zahl   = \\n| reichweite_zahl    = \\n| reichweite_quelle  = \\n| chefred            = \\n| herausgeber        = \\n| geschäftsführer    = \\n| weblink            = [http://www.jagran.com/ jagran.com]\\n| archiv             =\\n| issn               = \\n| zdb                = \\n| CODEN              = \\n}}\\n'''Dainik Jagran''' ({{hiS|दैनिक जागरण}}) ist eine [[Indien|indische]] [[Tageszeitung]] auf [[Hindi]].\\n\\nDer Sitz der Zeitung befindet sich im ''Jagran Building 2'', Sarvodya Nagar in [[Kanpur]]. Die Zeitung wurde 1942 in der Stadt [[Jhansi]]  von [[Puran Chandra Gupta]] erstmals publiziert. Nach Angaben der Organisation [[World Association of Newspapers]] ist die Dainik Jagran gegenwärtig eine der am häufigsten gelesenen Tageszeitungen der Welt, sowie die Zeitung mit den meisten Lesern in Indien (Stand: 2016)<ref>[http://www.wan-ifra.org/events/study-tour-success-made-in-india Wan-Ifra.org: Study]</ref> Sie ist auch die auflagenstärkste Zeitung in Hindi (Stand: 2013).<ref>{{Internetquelle|url=http://www.auditbureau.org/news/view/17|titel=AUDIT BUREAU OF CIRCULATIONS|format=PDF|datum=2013|zugriff=2016-02-02|sprache=en}}</ref> Die Zeitung gehört dem indischen Medienunternehmen ''Jagran Prakashan Limited'', das an der [[Bombay Stock Exchange]] und an der [[National Stock Exchange of India]] gelistet ist. Das Unternehmen ''Jagran Prakashan Limited'' erwarb 2010 die indische Zeitung ''Mid Day'' und 2012 die Zeitung ''Naiduniya''.\\n\\n== Weblinks ==\\n* [http://www.jagran.com/ Offizielle Webseite von Jagran.com]\\n\\n== Einzelnachweise ==\\n<references/>\\n\\n[[Kategorie:Tageszeitung]]\\n[[Kategorie:Zeitung (Indien)]]\\n[[Kategorie:Ersterscheinung 1942]]\\n[[Kategorie:Kanpur]]\"}]}]}}";
        msg = "{\"batchcomplete\":true,\"query\":{\"normalized\":[{\"fromencoded\":false,\"from\":\"George_Mallia\",\"to\":\"George Mallia\"}],\"pages\":[{\"pageid\":4675984,\"ns\":0,\"title\":\"George Mallia\",\"revisions\":[{\"contentformat\":\"text/x-wiki\",\"contentmodel\":\"wikitext\",\"content\":\"{{Infobox Fußballspieler\\n| kurzname            = George Mallia\\n| bildname            = Mallia, George.jpg\\n| bildbreite          = \\n| bildunterschrift    = George Mallia 2009\\n| langname            = George Mallia\\n| geburtstag          = 10. Oktober 1978\\n| geburtsort          = [[Sliema]]\\n| geburtsland         = [[Malta]]\\n| position            = [[Mittelfeld]]\\n| jugendvereine_tabelle =\\n| vereine_tabelle =\\n{{Team-Station|1995–1998|[[Sliema Wanderers]]|44 (11)}}\\n{{Team-Station|1998–2002|[[FC Floriana]]|84 {{0}}(9)}}\\n{{Team-Station|2002–    |[[FC Birkirkara]]|171 (24)}}\\n| nationalmannschaft_tabelle =\\n{{Team-Station|?    |[[Maltesische Fußballnationalmannschaft (U-21-Männer)|Malta U-21]]|12 {{0}}(3)}}\\n{{Team-Station|1997–|[[Maltesische Fußballnationalmannschaft|Malta]]|60 {{0}}(5)}}\\n| trainer_tabelle =\\n}}\\n'''George Mallia''' (* [[10. Oktober]] [[1978]] in [[Sliema]]) ist ein maltesischer Fußballspieler.\\n\\nMallia begann seine Karriere in der [[Maltese Premier League]] bei [[Sliema Wanderers]]. Dort spielte er vier Jahre und wechselte im Sommer 1998 zu [[Floriana FC]]. Seit Sommer 2002 spielt er bei [[FC Birkirkara]]. Für die [[Maltesische Fußballnationalmannschaft|Nationalmannschaft]] kam er seit 1997 auf 60 Einsätze.\\n\\n== Weblinks ==\\n* [http://www.maltafootball.com/players/georgemallia.html George Mallia bei MaltaFootball.com]\\n* {{NFTPlayer|ID=4222}}\\n\\n{{SORTIERUNG:Mallia, George}}\\n[[Kategorie:Fußballnationalspieler (Malta)]]\\n[[Kategorie:Fußballspieler (FC Birkirkara)]]\\n[[Kategorie:Fußballspieler (FC Floriana)]]\\n[[Kategorie:Fußballspieler (Sliema Wanderers)]]\\n[[Kategorie:Malteser]]\\n[[Kategorie:Geboren 1978]]\\n[[Kategorie:Mann]]\\n\\n{{Personendaten\\n|NAME=Mallia, George\\n|ALTERNATIVNAMEN=\\n|KURZBESCHREIBUNG=maltesischer Fußballspieler\\n|GEBURTSDATUM=10. Oktober 1978\\n|GEBURTSORT=[[Sliema]], [[Malta]]\\n|STERBEDATUM=\\n|STERBEORT=\\n}}\"}],\"images\":[{\"ns\":6,\"title\":\"Datei:Mallia, George.jpg\"}],\"links\":[{\"ns\":0,\"title\":\"10. Oktober\"},{\"ns\":0,\"title\":\"1978\"},{\"ns\":0,\"title\":\"FC Birkirkara\"},{\"ns\":0,\"title\":\"FC Floriana\"},{\"ns\":0,\"title\":\"Floriana FC\"},{\"ns\":0,\"title\":\"Malta\"},{\"ns\":0,\"title\":\"Maltese Premier League\"},{\"ns\":0,\"title\":\"Maltesische Fußballnationalmannschaft\"},{\"ns\":0,\"title\":\"Maltesische Fußballnationalmannschaft (U-21-Männer)\"},{\"ns\":0,\"title\":\"Mittelfeld\"},{\"ns\":0,\"title\":\"Sliema\"},{\"ns\":0,\"title\":\"Sliema Wanderers\"},{\"ns\":12,\"title\":\"Hilfe:Personendaten\"}]}]}}";
/*
        content = new Content();
        assertEquals("__REF__1", content.extractAndReplaceRef("[[FC Birkirkara]]"));
        assertEquals("__REF__2", content.extractAndReplaceRef("[[Maltesische Fußballnationalmannschaft (U-21-Männer)|Malta U-21]]"));
*/

        // assertEquals("Fußballklub", content.extractRef("[[FC Birkirkara]]")); // aus infobox von FC Birkirkara
        // assertEquals("U21-Fußballnationalmannschaft", content.extractRef("[[[[Maltesische Fußballnationalmannschaft (U-21-Männer)|Malta U-21]]]]"));
        // oder Fußballklub

    }

    /*
    wiki-text
        https://de.wikipedia.org/w/api.php?action=query&format=json&prop=revisions&redirects=1&formatversion=2&rvprop=content&titles=George_Mallia

    wiki-text + extract-Intro
        https://de.wikipedia.org/w/api.php?action=query&format=json&prop=revisions|extracts&exintro=&explaintext=&titles=George_Mallia&redirects=1&formatversion=2&rvprop=content

    wiki-text + extract/full plain-text
        https://de.wikipedia.org/w/api.php?action=query&exlimit=2&format=json&prop=revisions|extracts&explaintext=&titles=Albert_Einstein&redirects=1&formatversion=2&rvprop=content&exlimit=max

    wiki-text + images + links + extract
        https://de.wikipedia.org/w/api.php?action=query&format=json&prop=revisions|images|links|extracts&exintro=&explaintext=&titles=George_Mallia&redirects=1&formatversion=2&rvprop=content&pllimit=50

    */
    //

    @Test
    public void testIntroExtract(){
        String msgIntro = "{\n" +
                "  \"batchcomplete\": true,\n" +
                "  \"query\": {\n" +
                "    \"pages\": [\n" +
                "      {\n" +
                "        \"pageid\": 4675984,\n" +
                "        \"ns\": 0,\n" +
                "        \"title\": \"George Mallia\",\n" +
                "        \"extract\": \"George Mallia (* 10. Oktober 1978 in Sliema) ist ein maltesischer Fußballspieler.\\n" +
                "Mallia begann seine Karriere in der Maltese Premier League bei Sliema Wanderers. Dort spielte er vier Jahre und wechselte im Sommer 1998 zu Floriana FC." +
                " Seit Sommer 2002 spielt er bei FC Birkirkara. Für die Nationalmannschaft kam er seit 1997 auf 60 Einsätze.\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        String msgIntroNorm = normaliseContent( msgIntro);
        String msgIntroNormExpect = "George Mallia (* 10. Oktober 1978 in Sliema) ist ein maltesischer Fußballspieler.\n" +
                "Mallia begann seine Karriere in der Maltese Premier League bei Sliema Wanderers.\n" +
                "Dort spielte er vier Jahre und wechselte im Sommer 1998 zu Floriana FC.\n" +
                "Seit Sommer 2002 spielt er bei FC Birkirkara.\n" +
                "Für die Nationalmannschaft kam er seit 1997 auf 60 Einsätze.\n";
        assertEquals(msgIntroNormExpect, msgIntroNorm);

    }

    @Test
    public void test() {
        String msg = "{\n" +
                "  \"batchcomplete\": true,\n" +
                "  \"query\": {\n" +
                "    \"normalized\": [\n" +
                "      {\n" +
                "        \"fromencoded\": false,\n" +
                "        \"from\": \"George_Mallia\",\n" +
                "        \"to\": \"George Mallia\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"pages\": [\n" +
                "      {\n" +
                "        \"pageid\": 4675984,\n" +
                "        \"ns\": 0,\n" +
                "        \"title\": \"George Mallia\",\n" +
                "        \"revisions\": [\n" +
                "          {\n" +
                "            \"contentformat\": \"text/x-wiki\",\n" +
                "            \"contentmodel\": \"wikitext\",\n" +
                "            \"content\": \"{{Infobox Fußballspieler\\n" +
                "| kurzname            = George Mallia\\n" +
                "| bildname            = Mallia, George.jpg\\n" +
                "| bildbreite          = \\n" +
                "| bildunterschrift    = George Mallia 2009\\n" +
                "| langname            = George Mallia\\n" +
                "| geburtstag          = 10. Oktober 1978\\n" +
                "| geburtsort          = [[Sliema]]\\n" +
                "| geburtsland         = [[Malta]]\\n" +
                "| position            = [[Mittelfeld]]\\n" +
                "| jugendvereine_tabelle =\\n" +
                "| vereine_tabelle =\\n" +
                "{{Team-Station|1995–1998|[[Sliema Wanderers]]|44 (11)}}\\n" +
                "{{Team-Station|1998–2002|[[FC Floriana]]|84 {{0}}(9)}}\\n" +
                "{{Team-Station|2002–    |[[FC Birkirkara]]|171 (24)}}\\n" +
                "| nationalmannschaft_tabelle =\\n" +
                "{{Team-Station|?    |[[Maltesische Fußballnationalmannschaft (U-21-Männer)|Malta U-21]]|12 {{0}}(3)}}\\n" +
                "{{Team-Station|1997–|[[Maltesische Fußballnationalmannschaft|Malta]]|60 {{0}}(5)}}\\n" +
                "| trainer_tabelle =\\n" +
                "}}\\n" +
                "'''George Mallia''' (* [[10. Oktober]] [[1978]] in [[Sliema]]) ist ein maltesischer Fußballspieler.\\n" +
                "\\n" +
                "Mallia begann seine Karriere in der [[Maltese Premier League]] bei [[Sliema Wanderers]]. Dort spielte er vier Jahre und wechselte im Sommer 1998 zu [[Floriana FC]]. " +
                "Seit Sommer 2002 spielt er bei [[FC Birkirkara]]. Für die [[Maltesische Fußballnationalmannschaft|Nationalmannschaft]] kam er seit 1997 auf 60 Einsätze.\\n" +
                "\\n" +
                "== Weblinks ==\\n" +
                "* [http://www.maltafootball.com/players/georgemallia.html George Mallia bei MaltaFootball.com]\\n" +
                "* {{NFTPlayer|ID=4222}}\\n" +
                "\\n" +
                "{{SORTIERUNG:Mallia, George}}\\n" +
                "[[Kategorie:Fußballnationalspieler (Malta)]]\\n" +
                "[[Kategorie:Fußballspieler (FC Birkirkara)]]\\n" +
                "[[Kategorie:Fußballspieler (FC Floriana)]]\\n" +
                "[[Kategorie:Fußballspieler (Sliema Wanderers)]]\\n" +
                "[[Kategorie:Malteser]]\\n" +
                "[[Kategorie:Geboren 1978]]\\n" +
                "[[Kategorie:Mann]]\\n" +
                "\\n" +
                "{{Personendaten\\n" +
                "|NAME=Mallia, George\\n" +
                "|ALTERNATIVNAMEN=\\n" +
                "|KURZBESCHREIBUNG=maltesischer Fußballspieler\\n" +
                "|GEBURTSDATUM=10. Oktober 1978\\n" +
                "|GEBURTSORT=[[Sliema]], [[Malta]]\\n" +
                "|STERBEDATUM=\\n" +
                "|STERBEORT=\\n" +
                "}}\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        assertEquals("George Mallia",parseTitle(msg));

        token.clear();
        parseRef(msg);
        String msg2 = "Sliema;Malta;Mittelfeld;Sliema Wanderers;FC Floriana;FC Birkirkara;Maltesische Fußballnationalmannschaft (U-21-Männer);Maltesische Fußballnationalmannschaft;10. Oktober;1978;Sliema;Maltese Premier League;Sliema Wanderers;Floriana FC;FC Birkirkara;Maltesische Fußballnationalmannschaft;Kategorie:Fußballnationalspieler (Malta);Kategorie:Fußballspieler (FC Birkirkara);Kategorie:Fußballspieler (FC Floriana);Kategorie:Fußballspieler (Sliema Wanderers);Kategorie:Malteser;Kategorie:Geboren 1978;Kategorie:Mann;Sliema;Malta";
        ArrayList<String> refs = token.getTokens4Category("Ref");
        assertEquals( msg2 , String.join(";", refs));

        int idxNat = refs.indexOf("Maltesische Fußballnationalmannschaft");
        String id = token.getId( "Ref" , 15 /*idxNat*/ );
        //Maltesische Fußballnationalmannschaft|Nationalmannschaft
        assertEquals("Nationalmannschaft", token.getRef4CategoryOfToken(id));

        String msgtst=" as {{ bb {{ sfs }} fh}} sfd";
        String msgtst2 = msgtst.replaceAll("\\{\\{[^\\}]*\\}\\}", ""); // {{..}}

        String normCont = normaliseContent(msg);
        String normaliseContent2 ="'''George Mallia''' (* [[10. Oktober]] [[1978]] in [[Sliema]]) ist ein maltesischer Fußballspieler.\\n" +
                "\\n" +
                "Mallia begann seine Karriere in der [[Maltese Premier League]] bei [[Sliema Wanderers]]. Dort spielte er vier Jahre und wechselte im Sommer 1998 zu [[Floriana FC]]. " +
                "Seit Sommer 2002 spielt er bei [[FC Birkirkara]]. Für die [[Maltesische Fußballnationalmannschaft|Nationalmannschaft]] kam er seit 1997 auf 60 Einsätze.\\n"
                ;
        String normaliseContent ="'''George Mallia''' (* [[__DATE__]] [[__YEAR__]] in [[__ORT__]]) ist ein maltesischer Fußballspieler.\\n" +
                "\\n" +
                "Mallia begann seine Karriere in der [[__REF__]] bei [[__REF__]]. Dort spielte er vier Jahre und wechselte im Sommer __YEAR__ zu [[__REF__]]. " +
                "Seit Sommer __YEAR__ spielt er bei [[__REF__]]. Für die [[__REF__]] kam er seit __YEAR__ auf __NUM__ Einsätze.\\n"
                ;
        assertEquals( normaliseContent, normCont);

    }

    //
    //
    // Read Category ---
    //      https://de.wikipedia.org/wiki/Spezial:ApiSandbox#action=query&format=json&list=categorymembers&generator=links&cmtitle=Kategorie%3ANobelpreistr%C3%A4ger_f%C3%BCr_Physik
    //              cmlimit=max
    //      https://stackoverflow.com/questions/21497323/list-all-wikipedia-articles-in-one-category-and-subcategories
    //

    //  read random pages ... incl content + images
    //
    //      https://en.wikipedia.org/w/api.php?format=json&action=query&generator=random&grnnamespace=0&prop=revisions|images&rvprop=content&grnlimit=10
    //

}
