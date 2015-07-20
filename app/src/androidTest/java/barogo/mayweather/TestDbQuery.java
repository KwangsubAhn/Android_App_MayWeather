package barogo.mayweather;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import barogo.mayweather.data.WeatherDbHelper;

/**
 * Created by user on 2015-07-18.
 */
public class TestDbQuery extends AndroidTestCase {

    public void testQueryDb() throws Throwable {// where weather_type = 2 order by date desc limit 4
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getReadableDatabase();
        Cursor c = db.rawQuery("select * from weather", null);
        c.moveToFirst();
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
}