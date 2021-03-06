package barogo.mayweather.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import barogo.mayweather.data.WeatherContract.LocationEntry;
import barogo.mayweather.data.WeatherContract.WeatherEntry;

public class WeatherDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "mayweather.db";

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_LOCATION_TABLE =
                "CREATE TABLE " + WeatherContract.LocationEntry.TABLE_NAME + " (" +
                        LocationEntry._ID + " INTEGER PRIMARY KEY," +
                        LocationEntry.COLUMN_LOCATION_SETTING + " TEXT NOT NULL, " +
                        LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                        LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                        LocationEntry.COLUMN_COORD_LONG + " REAL NOT NULL, " +
                        LocationEntry.COLUMN_COUNTRY_CODE + " TEXT NOT NULL, " +
                        LocationEntry.COLUMN_TIME_ZONE + " TEXT NOT NULL);";

        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
                WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WeatherEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                WeatherEntry.COLUMN_TYPE + " INTEGER NOT NULL, " +
                WeatherEntry.COLUMN_DATE + " TEXT NOT NULL, " +            //yyyy-mm-dd hh:mm
                WeatherEntry.COLUMN_MAIN + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_DESC + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_ICON + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_HUMIDITY + " INTEGER NOT NULL, " +
                WeatherEntry.COLUMN_TEMP + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_TEMP_MAX + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_TEMP_MIN + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_WIND_DEGREE + " INTEGER NOT NULL, " +
                WeatherEntry.COLUMN_CLOUDS + " INTEGER NOT NULL, " +
                WeatherEntry.COLUMN_RAIN + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_SNOW + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_SUNRISE + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_SUNSET + " TEXT NOT NULL, " +

                " FOREIGN KEY (" + WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +

                " UNIQUE (" + WeatherEntry.COLUMN_DATE + ", " +
                WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
