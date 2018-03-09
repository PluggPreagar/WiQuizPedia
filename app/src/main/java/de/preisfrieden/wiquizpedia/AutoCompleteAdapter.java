package de.preisfrieden.wiquizpedia;


import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by peter on 09.03.2018.
 */

public class AutoCompleteAdapter extends ArrayAdapter<String> {

    static List<String> suggestions = new ArrayList<String>(Arrays.asList("Jim Knopf","Bud Spencer","Jimbo"));


    public AutoCompleteAdapter(@NonNull Context context, int resource) {
        super(context, resource, suggestions );
        suggestions.add("Jim Morrison");
        suggestions.add("Albert Einstein");
    }



}
