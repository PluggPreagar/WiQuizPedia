package de.preisfrieden.wiquizpedia;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by peter on 06.03.2018.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

}
