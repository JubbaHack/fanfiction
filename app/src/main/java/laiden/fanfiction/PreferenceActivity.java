package laiden.fanfiction;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;


public class PreferenceActivity extends Activity {

    private static SharedPreferences data;

    private static SwitchPreference pref_type;
    private static Preference    pref_content;


    @Override
    protected void onDestroy(){
        super.onDestroy();
        data = null;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = null;
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();


    }
    /* Injects Thing properties to all preferences. */
    private static void load(){
        if(StoryView.thing.isImage()){
            pref_type.setChecked(true);
            pref_type.setSummary("Image block, ignoring text and colors.");
            pref_content.setSummary("Select a background.");
        }
        else{
            pref_type.setChecked(false);
            pref_type.setSummary("Text block, ignoring background image.");
            pref_content.setSummary("Edit the text, choose colors.");
        }
    }

    /* Fetches data from preferences, applies it to selected Thing. */
    private static void save(){

    }
    public static class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
                pref_type = (SwitchPreference) findPreference("elementproperties_type");
                pref_content = findPreference("elementproperties_content");
            load();
        }
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            data = sharedPreferences;
            Log.d("Preferences updated", StoryManager.gson.toJson(data.getAll()));
            if (key.equals("username")) {
                Preference pref = findPreference(key);
                pref.setSummary(sharedPreferences.getString(key, ""));
            }
        }
        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }
    }


}
