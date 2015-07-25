package barogo.mayweather;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import barogo.mayweather.CityDialogRadio.AlertPositiveListener;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import barogo.mayweather.data.CurrentWeatherVo;
import barogo.mayweather.data.WeatherContract;
import barogo.mayweather.data.WeatherDbHelper;
import barogo.mayweather.sync.SyncAdapterCurrent;

/**
 * Created by user on 2015-07-20.
 */
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener, AlertPositiveListener{

    int position = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_units_key)));
    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {

        String stringValue = value.toString();
        CharSequence stringValue2 = preference.getSummary();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }

        //Location
        if (preference.getTitle().equals(this.getString(R.string.pref_location_label))) {
            //restart
            if (stringValue2!=null &&
                    !stringValue.toUpperCase().equals(stringValue2.toString().toUpperCase())) {
                //check if the city name exist
                String location = "st.+johns";

                //get Current WeatherInfo from cloud
                final String CURRENT_BASE_URL = "http://api.openweathermap.org/data/2.5/weather?";
                final String LOCATION_PARAM = "q";
                final String UNITS_PARAM = "units";

                Uri builtUri = Uri.parse(CURRENT_BASE_URL).buildUpon()
                        .appendQueryParameter(LOCATION_PARAM, location)
                        .appendQueryParameter(UNITS_PARAM, "metric")
                        .build();


                String strUrlCurrent = builtUri.toString();

                Intent intent = this.getIntent();
                intent.putExtra(this.getString(R.string.pref_location_label), stringValue);
                this.setResult(RESULT_OK, intent);

                /** Create List of cities with radio buttons */
                FragmentManager manager = getFragmentManager();
                CityDialogRadio choose = new CityDialogRadio();
                Bundle b  = new Bundle();
                b.putInt("position", 0);
                b.putString("typed_name", stringValue);
                choose.setArguments(b);
                choose.show(manager, "alert_dialog_radio");
            }
        }

        //Temperature Unit
        if (preference.getTitle().equals(this.getString(R.string.pref_units_label))) {
            //restart
            if (stringValue2!=null &&
                    !stringValue.toUpperCase().equals(stringValue2.toString().toUpperCase())) {
                Intent intent = this.getIntent();
                intent.putExtra(this.getString(R.string.pref_units_label), stringValue);
                this.setResult(RESULT_OK, intent);
            }
        }

        return true;
    }

    @Override
    public void onPositiveClick(int position) {
        this.position = position;

        Log.d("PositiveClick", "  "+position);
        //tv.setText("Your Choice : " + Android.code[this.position]);
    }
}
