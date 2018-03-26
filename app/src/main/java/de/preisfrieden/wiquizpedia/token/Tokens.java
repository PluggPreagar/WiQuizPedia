package de.preisfrieden.wiquizpedia.token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by peter on 21.02.2018.
 */

public class Tokens {

    static class ValueList extends ArrayList<String> {};
    class TokenVsCategory extends HashMap<String, ValueList> {};

    TokenVsCategory tokenVsCategory = new TokenVsCategory();
    List<String> tokenPreSorted = new ArrayList<String>();
    // { Date => [ 1.5.1987, 1.9.1990 ] , 1.5.1987 => [ Date ] , 1.9.1990 => [ Date* ]    im Falle von Personen auch mehrere Äquivalenzklassen
    //     ... __Date1__ => [ 1.5.1987 ] , __Date2__ => [ 1.9.1990 ] , Year => {...}

    private static final int TOKEN_OR_ID_IDX = 0;
    private static final int REF_IDX = 1;
    private static final int CATEGORY_IDX = 2;

    private ValueList emptyValueListWithElems = createEmptyValueListWithElems();
    private ValueList emptyValueList = new ValueList();

    private static ValueList createEmptyValueListWithElems(){
        ValueList valueList = new ValueList();
        valueList.addAll( Arrays.asList("", "","") );
        return valueList;
    }

    private ValueList get(String key) {
        return tokenVsCategory.get( key);
    }

    private ValueList getOrEmpty(String key) {
        ValueList valueList = get(key);
        return null == valueList ? emptyValueListWithElems: valueList ;
    }

    private void put( String key, ValueList list){
        tokenVsCategory.put( key, list);
    }

    private int addSet(String key, String...values) {
        ValueList valueList = get(key);
        if (null == valueList) valueList = new ValueList();
        valueList.addAll(Arrays.asList(values) );
        put(key,valueList);
        return valueList.size()-1;
    }

    public String add( String token, String category) {
        return add(token, category, null);
    }

    public String add( String token, String category, String link ) {
        int i = addSet( category, token);           //   Date       =>  [ ... 1.1.2001 ]
        String id = getId( category , i);     //
        addSet( id, token, link, category);         //   __Date1__  =>  [ 1.1.2001,  NEUJAHR2001, Date ]
        addSet( token, id, link, category );        //   1.1.2001   =>  [ __Date1__, NEUJAHR2001, Date ]
        //if (null != link) addSet(token, link);    //   1.1.2001   =>  [ ........, NEUJAHR2001 ]
        return id;
    }

    public String getId(String category, int idx) {
        return  "__" + category + idx + "__";
    }
    public int getIdx(String id) {
        return  Integer.valueOf(id.replaceAll("[^0-9]+",""));
    }

    public ArrayList<String> getTokens4Category(String id) {
        //return get( getOrEmpty(id).get( CATEGORY_IDX));
        return getOrEmpty(id);
    }

    public String getToken4Id(String id) {
        return getOrEmpty( id).get(TOKEN_OR_ID_IDX);
    }

    public List<String> getValues4CategoryOfToken(String token) {
        // return new ArrayList<String>(new HashSet<String>(get(getOrEmpty(token).get(CATEGORY_IDX))));  // uniq values ...
        return get(getOrEmpty(token).get(CATEGORY_IDX));
    }

    public List<String> emptyIfNull(List<String> values) {
        return null == values ? emptyValueList : values ;
    }

    public String getRef4CategoryOfToken(String token) {
        // return new ArrayList<String>(new HashSet<String>(get(getOrEmpty(token).get(CATEGORY_IDX))));  // uniq values ...
        return getOrEmpty(token).get(REF_IDX);
    }

    public void clear(){
        tokenVsCategory.clear();
        tokenPreSorted.clear();
    }

}
