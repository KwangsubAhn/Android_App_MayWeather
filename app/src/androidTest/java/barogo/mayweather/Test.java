package barogo.mayweather;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.test.AndroidTestCase;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import barogo.mayweather.data.CurrentWeatherVo;
import barogo.mayweather.data.WeatherContract;
import barogo.mayweather.data.WeatherDbHelper;

/**
 * Created by user on 2015-07-19.
 */
public class Test extends AndroidTestCase {

    public void testWriteCity() throws Throwable {
          readCity2();
//        getInsert();

    }
    private ArrayList<String[]> readFile() {
        ArrayList<String[]> result = new ArrayList<String[]>();
        try {


            AssetManager am = mContext.getAssets();
            InputStream is = null;

            is = am.open("CITY_TIMEZONE.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line = br.readLine();
            while (line != null) {
                String[] arrStr = line.split("\t");
                result.add(arrStr);
                line = br.readLine();
            }

        } catch (Exception e) {
            Log.d("","");
        }
        return result;
    }

    private ArrayList<String[]> deleteMultiTimezone(ArrayList<String[]> city_timezone) {
        ArrayList<String[]> result = new ArrayList<String[]>();

        for (int i=0; i<city_timezone.size(); i++) {
            String[] arrCity = city_timezone.get(i);
            boolean isDup = false;

            if (i != 0 && i != city_timezone.size()-1) {
                String[] arrBeforeCity = city_timezone.get(i-1);
                String[] arrNextCity = city_timezone.get(i+1);
                if (arrCity[0].equals(arrBeforeCity[0]) || arrCity[0].equals(arrNextCity[0])) {
                    isDup = true;
                }
            }

            if (!isDup) {
                result.add(arrCity);
            }
        }

        return result;
    }

    private void getInsert() {
        try {
            WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            AssetManager am = mContext.getAssets();
            InputStream is = null;

            String[] aaa = am.list("");

            is = am.open("city_list.txt");

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                String[] data = line.split("\\t");
                String id = data[0];
                String name = data[1];
                double lat = Math.round(Double.parseDouble(data[2]));
                double lon = Math.round(Double.parseDouble(data[3]));
                String code = data[4];

                ContentValues testValues = new ContentValues();

                testValues.put("location_setting", id);
                testValues.put("city_name", name);
                testValues.put("coord_lat", lat);
                testValues.put("coord_long", lon);
                testValues.put("country_code", code);
                testValues.put("time_zone", "");

                long result = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, testValues);

                line = br.readLine();

//                Cursor c = db.rawQuery("select * from " +
//                                WeatherContract.LocationEntry.TABLE_NAME, null);
//                c.moveToFirst();
//                int cnt = c.getCount();
//                do {
//                    StringBuffer sbs = new StringBuffer();
//                    for (int i=0; i<c.getColumnCount(); i++) {
//                        sbs.append(c.getString(i));
//                        sbs.append(" / ");
//                    }
//                    Log.e("QUERY: ", sbs.toString());
//                } while (c.moveToNext());

            }

            String everything = sb.toString();



        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void readCity() throws Throwable {
        ArrayList<String[]> city_timezone = readFile();
        ArrayList<String[]> deleteMultiTimezoneCountry = deleteMultiTimezone(city_timezone);

        for (int i=0; i<deleteMultiTimezoneCountry.size(); i++) {
            Cursor cursor = mContext.getContentResolver().query(
                    WeatherContract.LocationEntry.CONTENT_URI,
                    null,
                    WeatherContract.LocationEntry.COLUMN_CITY_NAME + " COLLATE NOCASE like '%se%'",
                    null,
                    WeatherContract.LocationEntry.COLUMN_COUNTRY_CODE + " ASC"
            );

            SQLiteDatabase db = new WeatherDbHelper(this.mContext).getReadableDatabase();//  where time_zone = '' and country_code = '" + deleteMultiTimezoneCountry.get(i)[0] + "'"
            Cursor c = db.rawQuery("select * from location where time_zone = '' and country_code = '" + deleteMultiTimezoneCountry.get(i)[0] + "'", null);
            String timezone = deleteMultiTimezoneCountry.get(i)[1];
            c.moveToFirst();
            int cnt = c.getCount();
            if (cnt==0) {continue;}
            do {
                StringBuffer sb = new StringBuffer();
                int _id;
                String location_setting;
                String city_name;
                double coord_lat;
                double coord_long;
                String country_code;
                String time_zone;
                _id = c.getInt(0);
                location_setting = c.getString(1);
                city_name = c.getString(2);
                coord_lat = c.getDouble(3);
                coord_long = c.getDouble(4);
                country_code = c.getString(5);
                time_zone = c.getString(6);


                ContentValues testValues = new ContentValues();

                testValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, location_setting);
                testValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, city_name);
                testValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, coord_lat);
                testValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, coord_long);
                testValues.put(WeatherContract.LocationEntry.COLUMN_COUNTRY_CODE, country_code);
                testValues.put(WeatherContract.LocationEntry.COLUMN_TIME_ZONE, timezone);

                db.update("location", testValues, "_id = '" + _id + "'", null);
                Log.e("QUERY: ", sb.toString());

            } while (c.moveToNext());
        }

        Log.e("QUERY: END", "");
        Log.e("", "");
    }
    private void readCity2() throws Throwable {
        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                null,
                WeatherContract.LocationEntry.COLUMN_CITY_NAME + " COLLATE NOCASE like '%se%'",
                null,
                WeatherContract.LocationEntry.COLUMN_COUNTRY_CODE + " ASC"
        );

        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getReadableDatabase();
        Cursor c = db.rawQuery("select * from location where time_zone = ''", null);

        c.moveToFirst();
        int cnt = c.getCount();

        do {
            StringBuffer sb = new StringBuffer();
            int _id;
            String location_setting;
            String city_name;
            double coord_lat;
            double coord_long;
            String country_code;
            String time_zone;
            _id = c.getInt(0);
            location_setting = c.getString(1);
            city_name = c.getString(2);
            coord_lat = c.getDouble(3);
            coord_long = c.getDouble(4);
            country_code = c.getString(5);
            time_zone = c.getString(6);

            String strJson = getTimeZoneJson(coord_lat, coord_long, 1438732800);
            String timezone = getTimeZone(strJson);
            for (int i=0; i<c.getColumnCount(); i++) {
                sb.append(c.getString(i));
                sb.append(" / ");
            }

            ContentValues testValues = new ContentValues();

            testValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, location_setting);
            testValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, city_name);
            testValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, coord_lat);
            testValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, coord_long);
            testValues.put(WeatherContract.LocationEntry.COLUMN_COUNTRY_CODE, country_code);
            testValues.put(WeatherContract.LocationEntry.COLUMN_TIME_ZONE, timezone);

            db.update("location", testValues, "_id = '" + _id + "'", null);
            Log.e("QUERY: ", sb.toString());


//            Thread.sleep(2000);
        } while (c.moveToNext());

    }



    /*@TargetApi(Build.VERSION_CODES.KITKAT)
    public void testCreateCityTable() throws Throwable {// where weather_type = 2 order by date desc limit 4
        String createQuery = "CREATE TABLE city_list (id INTEGER primary key , " +
                                                    "city_name TEXT NOT NULL, " +
                                                    "lat INTEGER NOT NULL, " +
                                                    "lon INTEGER NOT NULL, " +
                                                    "code TEXT NOT NULL);";

        SQLiteDatabase db = new WeatherDbHelper(
                this.mContext).getWritableDatabase();

        assertEquals(true, db.isOpen());

        db.execSQL("drop table city_list");
        db.execSQL(createQuery);

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            Log.d("dddd",c.getString(0));
        } while( c.moveToNext() );
        AssetManager am = mContext.getAssets();
        InputStream is = am.open("city_list.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                String[] data = line.split("\\t");
                int id = Integer.parseInt(data[0]);
                String name = data[1];
                int lat = (int)Math.round(Double.parseDouble(data[2]));
                int lon = (int)Math.round(Double.parseDouble(data[3]));
                String code = data[4];

                ContentValues testValues = new ContentValues();

                testValues.put("id", id);
                testValues.put("city_name", name);
                testValues.put("lat", lat);
                testValues.put("lon", lon);
                testValues.put("code", code);

                db.insert("city_list", null, testValues);

                line = br.readLine();
            }

            String everything = sb.toString();
            Log.d("result",everything);
        } catch (Exception e) {
            Log.d("ffff", e.getMessage());
        }
    }*/
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

    private String getTimeZone(String strJson) {

        String strTimezone = "a";

        try {
            JSONObject timezoneJson = null;
            timezoneJson = new JSONObject(strJson);
            strTimezone = timezoneJson.getString("timezoneId");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return strTimezone;
    }
}
