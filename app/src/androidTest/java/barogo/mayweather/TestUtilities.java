package barogo.mayweather;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import barogo.mayweather.data.WeatherContract;
import barogo.mayweather.data.WeatherDbHelper;

/**
 * Created by user on 2015-07-17.
 */
public class TestUtilities extends AndroidTestCase {

    static final String TEST_LOCATION = "6324733";
    //    static final long TEST_DATE = 1419033600L;
    static final String TEST_DATE = "2015-07-06 19:38:42";

    static ContentValues createLocationValues() {
        ContentValues testValues = new ContentValues();

        testValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, TEST_LOCATION);
        testValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, "St. John's");
        testValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, 47.56);
        testValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, -52.71);
        testValues.put(WeatherContract.LocationEntry.COLUMN_COUNTRY_CODE, "CA");

        return testValues;

    }

    static ContentValues createWeatherValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_TYPE, WeatherContract.WEATHER_TYPE_CURRENT);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, TEST_DATE);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAIN, "Rain");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DESC, "light rain");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_ICON, "10n");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, 1026.15);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, 98);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_TEMP, 0);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_TEMP_MAX, 8.52);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_TEMP_MIN, 6.18);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, 6.27);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_DEGREE, 3.50);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_CLOUDS, 80);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_RAIN, 0.01);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SNOW, 0);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SUNRISE, "05:11");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SUNSET, "21:00");

        return weatherValues;
    }

    static long insertSTJOHNSLocationValues(Context context) {
        // insert our test records into the database
        WeatherDbHelper dbHelper = new WeatherDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createLocationValues();

        long locationRowId;
        locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        return locationRowId;
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
//            new PollingCheck(5000) {
//                @Override
//                protected boolean check() {
//                    return mContentChanged;
//                }
//            }.run();
//            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

}
