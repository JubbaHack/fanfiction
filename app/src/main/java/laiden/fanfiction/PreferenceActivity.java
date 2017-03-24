package laiden.fanfiction;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.jrummyapps.android.colorpicker.ColorPreference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static laiden.fanfiction.Utils.str;


public class PreferenceActivity extends Activity {

    private static SharedPreferences data;

    private static PreferenceScreen   pref_content;
    private static Preference         pref_background;
    private static EditTextPreference pref_text;
    private static ColorPreference    pref_text_color;
    private static Preference         pref_border;
    private static Preference         pref_text_disabled;

    private static PreferenceActivity instance;

    private static File _s;

    private static final int SELECT_FILE_REQUEST_CODE = 1337;

    @Override
    protected void onDestroy(){
        super.onDestroy();
        data = null;
        instance = null;
        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = null;
        instance = this;
        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();


    }
    private static void select_file() {
        Intent intent = new Intent()
                .setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT);

        instance.startActivityForResult(Intent.createChooser(intent, "Select a file"), SELECT_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                File r = StoryManager.addResource(in, StoryView.story.name, "test");
                show_name_dialog();
                Log.d("!!!!", r.toString() + " " + r.exists() + " " + r.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
    private static void show_name_dialog(){
        final EditText editText = new EditText(instance);
        final AlertDialog dialog = new AlertDialog.Builder(instance)
                .setTitle("Add a new task")
                .setView(editText)
                .setCancelable(false)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() { @Override public void onClick(DialogInterface d, int w) {} })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() { @Override public void onClick(DialogInterface d, int w) {} })
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String name = String.valueOf(editText.getText());
                if(name.isEmpty()){
                    editText.setError("Cannot be blank.");

                }
                else{
                    Log.d("!!!", name);
                    dialog.dismiss();
                }

            }
        });
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });
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
            pref_background.setSummary(str("label_resource") + " '" + StoryView.thing.getBackground() + "'");
        }
        else{
            enable_content_preferences();
            pref_background.setSummary(str("label_color") + " " + StoryView.thing.getBackground());
        }
        pref_text.setSummary(Utils.ellipsize(StoryView.thing.getText(), 35));
        pref_text.setText(StoryView.thing.getText());
        pref_text_color.setSummary(str("label_color") + " " + StoryView.thing.getTextColor());
        pref_text_color.saveValue(Utils.intColor(StoryView.thing.getTextColor()));

        if(StoryView.thing.hasBorder()) {
            String offset;
            if(StoryView.thing.hasBorderOffset())
                 offset = " " + str("description_border_offset_by") + " " + StoryView.thing.getBorderDx() + "px, " + StoryView.thing.getBorderDy() + "px";
            else offset = " " + str("description_border_no_offset");
            pref_border.setSummary(
                    StoryView.thing.getBorderSize() +
                    "px " +
                    StoryView.thing.getBorderColor() +
                    offset
            );
        }
        else pref_border.setSummary(str("description_border_none"));
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
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference){
            if(preference.getKey().equals("element-properties content background type-image")){
                select_file();

            }
            return true;
        }
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            data = sharedPreferences;
            Log.d("PreferenceActivity", "Preferences updated: " + StoryManager.gson.toJson(data.getAll()));

            //
            //


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
