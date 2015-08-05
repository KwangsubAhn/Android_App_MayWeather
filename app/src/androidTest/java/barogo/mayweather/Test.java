package barogo.mayweather;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.test.AndroidTestCase;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
          readCity();
//        getInsert();

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

        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                null,
                WeatherContract.LocationEntry.COLUMN_CITY_NAME + " COLLATE NOCASE like '%se%'",
                null,
                WeatherContract.LocationEntry.COLUMN_COUNTRY_CODE + " ASC"
        );

        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getReadableDatabase();
//        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        Cursor c = db.rawQuery("select * from location", null);
//        Cursor c = db.rawQuery("select * from city_list where city_name like '%londoN%'", null);
        c.moveToFirst();
        int cnt = c.getCount();
        do {
            StringBuffer sb = new StringBuffer();
            for (int i=0; i<c.getColumnCount(); i++) {
                sb.append(c.getString(i));
                sb.append(" / ");
            }
            Log.e("QUERY: ", sb.toString());
        } while (c.moveToNext());
        Log.e("QUERY: END", "");
        Log.e("", "");
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


}
