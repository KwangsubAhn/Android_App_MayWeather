package barogo.mayweather.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import barogo.mayweather.data.WeatherContract.WeatherEntry;
/**
 * Created by user on 2015-07-17.
 */
public class CurrentWeatherVo implements Parcelable {
    public int location_id;
    public int weather_type;
    public String date;
    public String main;
    public String desc;
    public String icon;
    public double pressure;
    public int humidity;
    public double temp;
    public double temp_max;
    public double temp_min;
    public double wind_speed;
    public int wind_degree;
    public int clouds;
    public double rain;
    public double snow;
    public String sunrise;
    public String sunset;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.getInt(WeatherEntry.COLUMN_LOC_KEY, location_id);
        bundle.getInt(WeatherEntry.COLUMN_TYPE, weather_type);
        bundle.getString(WeatherEntry.COLUMN_DATE, date);
        bundle.getString(WeatherEntry.COLUMN_MAIN, main);
        bundle.getString(WeatherEntry.COLUMN_DESC, desc);
        bundle.getString(WeatherEntry.COLUMN_ICON, icon);
        bundle.getDouble(WeatherEntry.COLUMN_PRESSURE, pressure);
        bundle.getInt(WeatherEntry.COLUMN_HUMIDITY, humidity);
        bundle.getDouble(WeatherEntry.COLUMN_TEMP, temp);
        bundle.getDouble(WeatherEntry.COLUMN_TEMP_MAX, temp_max);
        bundle.getDouble(WeatherEntry.COLUMN_TEMP_MIN, temp_min);
        bundle.getDouble(WeatherEntry.COLUMN_WIND_SPEED, wind_speed);
        bundle.getInt(WeatherEntry.COLUMN_WIND_DEGREE, wind_degree);
        bundle.getInt(WeatherEntry.COLUMN_CLOUDS, clouds);
        bundle.getDouble(WeatherEntry.COLUMN_RAIN, rain);
        bundle.getDouble(WeatherEntry.COLUMN_SNOW, snow);
        bundle.getString(WeatherEntry.COLUMN_SUNRISE, sunrise);
        bundle.getString(WeatherEntry.COLUMN_SUNSET, sunset);
    }

    /**
     * Creator required for class implementing the parcelable interface.
     */
    public static final Parcelable.Creator<CurrentWeatherVo> CREATOR = new Creator<CurrentWeatherVo>() {

        @Override
        public CurrentWeatherVo createFromParcel(Parcel source) {
            // read the bundle containing key value pairs from the parcel
            Bundle bundle = source.readBundle();

            // instantiate a person using values from the bundle
            CurrentWeatherVo vo = new CurrentWeatherVo();
            vo.location_id = bundle.getInt(WeatherEntry.COLUMN_LOC_KEY);
            vo.weather_type = bundle.getInt(WeatherEntry.COLUMN_TYPE);
            vo.date = bundle.getString(WeatherEntry.COLUMN_DATE);
            vo.main = bundle.getString(WeatherEntry.COLUMN_MAIN);
            vo.desc = bundle.getString(WeatherEntry.COLUMN_DESC);
            vo.icon = bundle.getString(WeatherEntry.COLUMN_ICON);
            vo.pressure = bundle.getDouble(WeatherEntry.COLUMN_PRESSURE);
            vo.humidity = bundle.getInt(WeatherEntry.COLUMN_HUMIDITY);
            vo.temp = bundle.getDouble(WeatherEntry.COLUMN_TEMP);
            vo.temp_max = bundle.getDouble(WeatherEntry.COLUMN_TEMP_MAX);
            vo.temp_min = bundle.getDouble(WeatherEntry.COLUMN_TEMP_MIN);
            vo.wind_speed = bundle.getDouble(WeatherEntry.COLUMN_WIND_SPEED);
            vo.wind_degree = bundle.getInt(WeatherEntry.COLUMN_WIND_DEGREE);
            vo.clouds = bundle.getInt(WeatherEntry.COLUMN_CLOUDS);
            vo.rain = bundle.getDouble(WeatherEntry.COLUMN_RAIN);
            vo.snow = bundle.getDouble(WeatherEntry.COLUMN_SNOW);
            vo.sunrise = bundle.getString(WeatherEntry.COLUMN_SUNRISE);
            vo.sunset = bundle.getString(WeatherEntry.COLUMN_SUNSET);
            return vo;
        }

        @Override
        public CurrentWeatherVo[] newArray(int size) {
            return new CurrentWeatherVo[size];
        }

    };

}
