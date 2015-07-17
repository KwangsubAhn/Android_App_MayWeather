package barogo.mayweather.data;

import android.content.ContentValues;

import java.util.ArrayList;

/**
 * Created by user on 2015-07-17.
 */
public class CurrentWeather {

    public static CurrentWeatherVo CURRENT = null;
    public static ArrayList<CurrentWeatherVo> HOURLY_FORECAST = null;
//    public static ArrayList<CurrentWeatherVo> DAILY_FORECAST = null;

    public static CurrentWeatherVo convertContentValues(ContentValues values){
        CurrentWeatherVo vo = new CurrentWeatherVo();
        vo.location_id = (int) values.get(WeatherContract.WeatherEntry.COLUMN_LOC_KEY);
        vo.weather_type = (int) values.get(WeatherContract.WeatherEntry.COLUMN_TYPE);
        vo.date = (String) values.get(WeatherContract.WeatherEntry.COLUMN_DATE);
        vo.main = (String) values.get(WeatherContract.WeatherEntry.COLUMN_MAIN);
        vo.desc = (String) values.get(WeatherContract.WeatherEntry.COLUMN_DESC);
        vo.icon = (String) values.get(WeatherContract.WeatherEntry.COLUMN_ICON);
        vo.pressure = (double) values.get(WeatherContract.WeatherEntry.COLUMN_PRESSURE);
        vo.humidity = (int) values.get(WeatherContract.WeatherEntry.COLUMN_HUMIDITY);
        vo.temp = (double) values.get(WeatherContract.WeatherEntry.COLUMN_TEMP);
        vo.temp_max = (double) values.get(WeatherContract.WeatherEntry.COLUMN_TEMP_MAX);
        vo.temp_min = (double) values.get(WeatherContract.WeatherEntry.COLUMN_TEMP_MIN);
        vo.wind_speed = (double) values.get(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED);
        vo.wind_degree = (int) values.get(WeatherContract.WeatherEntry.COLUMN_WIND_DEGREE);
        vo.clouds = (int) values.get(WeatherContract.WeatherEntry.COLUMN_CLOUDS);
        vo.rain = (double) values.get(WeatherContract.WeatherEntry.COLUMN_RAIN);
        vo.snow = (double) values.get(WeatherContract.WeatherEntry.COLUMN_SNOW);
        vo.sunrise = (String) values.get(WeatherContract.WeatherEntry.COLUMN_SUNRISE);
        vo.sunset = (String) values.get(WeatherContract.WeatherEntry.COLUMN_SUNSET);
        return vo;
    }

}
