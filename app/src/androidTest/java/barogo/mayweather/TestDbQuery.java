package barogo.mayweather;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.List;

import barogo.mayweather.data.CurrentWeatherVo;
import barogo.mayweather.data.WeatherContract;
import barogo.mayweather.data.WeatherDbHelper;

/**
 * Created by user on 2015-07-18.
 */
public class TestDbQuery extends AndroidTestCase {

    public void testQueryDb() throws Throwable {// where weather_type = 2 order by date desc limit 4
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getReadableDatabase();
        Cursor c = db.rawQuery("select * from weather order by weather_type", null);
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
        List<CurrentWeatherVo> weatherVoHourly = Utility.getHourlyWeatherFromDB(this.mContext);
        Log.e("QUERY: END", "");
        Log.e("", "");
    }

    private void testQueryDeleteAll() throws Throwable {
        int deleted = mContext.getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI,
                null, null);
        Log.d("", "deleted: "+ deleted);

    }

}
