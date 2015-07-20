package barogo.mayweather;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import barogo.mayweather.data.WeatherDbHelper;

/**
 * Created by user on 2015-07-19.
 */
public class Test extends AndroidTestCase {

    public void test() throws Throwable {// where weather_type = 2 order by date desc limit 4
        WeatherDataParser.curDate = "1";

        String a = "";

        a = WeatherDataParser.curDate;

        WeatherDataParser.curDate = "2";

        System.out.print("");
    }

}
