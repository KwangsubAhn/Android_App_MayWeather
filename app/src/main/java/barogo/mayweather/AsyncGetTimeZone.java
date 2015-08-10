package barogo.mayweather;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import barogo.mayweather.data.WeatherContract;
import barogo.mayweather.data.WeatherDbHelper;
import barogo.mayweather.sync.SyncAdapterCurrent;

/**
 * Created by user on 2015-07-27.
 */
public class AsyncGetTimeZone extends AsyncTask<Object, Void, String> {

    private Context mContext;

    public AsyncGetTimeZone (Context context){
        mContext = context;
    }

    protected String doInBackground(Object... params) {
        try {
            double latitude = (double)params[0];
            double longitude = (double)params[1];
            long unixTime = (long)params[2];

            String strJson = null;//getTimeZoneJson(latitude, longitude, unixTime);
            if (strJson == null) {
                Cursor cursor = mContext.getContentResolver().query(
                        WeatherContract.LocationEntry.CONTENT_URI,
                        null,
                        WeatherContract.LocationEntry.COLUMN_COORD_LAT + " = '" + latitude + "' AND " +
                                WeatherContract.LocationEntry.COLUMN_COORD_LONG + " = '" + longitude + "'",
                        null,
                        WeatherContract.LocationEntry.COLUMN_COUNTRY_CODE + " ASC LIMIT 1"
                );

                cursor.moveToFirst();
                int cnt = cursor.getCount();

                if (cnt == 1) {
                    String timezone;
                    timezone = cursor.getString(6);
                    return timezone;
                } else {
                    String timezone = "UTC";
                    return timezone;
                }
            } else {
                String timezone = getTimeZone(strJson);
                return timezone;
            }
        } catch (Exception e) {
            String timezone = "UTC";
            return timezone;
        }


    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        settings.edit().putString("TIME_ZONE", result).commit();
    }

    private String getTimeZone(String strJson) {

        String strTimezone = "UTC";

        try {
            JSONObject timezoneJson = null;
            timezoneJson = new JSONObject(strJson);
            strTimezone = timezoneJson.getString("timezoneId");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return strTimezone;
    }

    private String getTimeZoneJson(double lat, double lon, long unixTime) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String timezoneJsonStr = null;
        http://api.geonames.org/timezoneJSON?formatted=true&lat=13.0&lng=121.0&username=demo&style=full
        try {
            final String BASE_URL = "http://api.geonames.org/timezoneJSON?";

            final String PARAM1 = "formatted";
            final String PARAM2 = "lat";
            final String PARAM3 = "lng";
            final String PARAM4 = "username";
            final String PARAM5 = "style";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(PARAM1, "true")
                    .appendQueryParameter(PARAM2, ""+lat)
                    .appendQueryParameter(PARAM3, ""+lon)
                    .appendQueryParameter(PARAM4, "demo")
                    .appendQueryParameter(PARAM5, "full")
                    .build();

            /*final String BASE_URL = "https://maps.googleapis.com/maps/api/timezone/json?";

            final String LOCATION_PARAM = "location";
            final String TIME_PARAM = "timestamp";
            final String SENSOR_PARAM = "sensor";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(LOCATION_PARAM, "" + lat + "," + lon)
                    .appendQueryParameter(TIME_PARAM, ""+unixTime)
                    .appendQueryParameter(SENSOR_PARAM, "false")
                    .build();*/

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                timezoneJsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                timezoneJsonStr = null;
            }
            timezoneJsonStr = buffer.toString();


        } catch (IOException e) {
            timezoneJsonStr = null;

        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch ( IOException e) {
                }
            }
        }

        return timezoneJsonStr;
    }
}
