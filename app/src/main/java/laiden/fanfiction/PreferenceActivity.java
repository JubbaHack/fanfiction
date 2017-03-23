package laiden.fanfiction;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;

import com.jrummyapps.android.colorpicker.ColorPreference;


public class PreferenceActivity extends Activity {

    private static SharedPreferences data;

    private static PreferenceScreen   pref_content;
    private static Preference         pref_background;
    private static EditTextPreference pref_text;
    private static ColorPreference    pref_text_color;
    private static Preference         pref_border;
    private static Preference         pref_text_disabled;


    @Override
    protected void onDestroy(){
        super.onDestroy();
        data = null;
        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = null;
        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();


    }
    private static void disable_content_preferences(){
        pref_content.removePreference(pref_text);
        pref_content.removePreference(pref_text_color);
        pref_content.removePreference(pref_border);
        pref_content.removePreference(pref_text_disabled);

        pref_content.addPreference(pref_text_disabled);
    }
    private static void enable_content_preferences(){
        pref_content.removePreference(pref_text);
        pref_content.removePreference(pref_text_color);
        pref_content.removePreference(pref_border);
        pref_content.removePreference(pref_text_disabled);

        pref_content.addPreference(pref_text);
        pref_content.addPreference(pref_text_color);
        pref_content.addPreference(pref_border);
    }
    /* Injects Thing properties to all preferences. */
    private static void load(){
        if(StoryView.thing.isImage()){
            disable_content_preferences();
            pref_background.setSummary("Resource '" + StoryView.thing.getBackground() + "'");
        }
        else{
            enable_content_preferences();
            pref_background.setSummary("Color " + StoryView.thing.getBackground());
        }
        pref_text.setSummary(Utils.ellipsize(StoryView.thing.getText(), 35));
        pref_text.setText(StoryView.thing.getText());
        pref_text_color.setSummary("Color " + StoryView.thing.getTextColor());
        pref_text_color.saveValue(Utils.intColor(StoryView.thing.getTextColor()));
    }

    /* Fetches data from preferences, applies it to selected Thing. */
    private static void save(){
        StoryView.thing.setTextColor(Utils.hexColor(data.getInt("element-properties content text-color", Utils.intColor(StoryView.thing.getTextColor()))));

        load();
    }
    public static class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
                pref_content       = (PreferenceScreen) findPreference("element-properties content");
                pref_background    = findPreference("element-properties content background");
                pref_text          = (EditTextPreference) findPreference("element-properties content text");
                pref_text_color    = (ColorPreference) findPreference("element-properties content text-color");
                pref_border        = findPreference("element-properties content border");
                pref_text_disabled = findPreference("element-properties content disabled");
            load();
        }
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            data = sharedPreferences;
            Log.d("PreferenceActivity", "Preferences updated: " + StoryManager.gson.toJson(data.getAll()));
            save();
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
