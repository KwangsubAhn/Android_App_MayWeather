package barogo.mayweather;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import barogo.mayweather.data.CurrentWeatherVo;
import barogo.mayweather.data.WeatherContract;
import barogo.mayweather.data.WeatherContract.WeatherEntry;

/**
 * Created by user on 2015-07-17.
 */
public class Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();

//    public static final int CURRENT_WEATHER = 0;
//    public static final int HOURLY_WEATHER = 1;
//    public static final int DAILY_WEATHER = 2;

    public static int findWeatherConditionImg(String condition) {

        switch (condition) {
            case "01d": return R.drawable.art_clear;        case "01n": return R.drawable.art_clear;
            case "02d": return R.drawable.art_light_clouds; case "02n": return R.drawable.art_light_clouds;
            case "03d": return R.drawable.art_clouds;       case "03n": return R.drawable.art_clouds;
            case "04d": return R.drawable.art_clouds;       case "04n": return R.drawable.art_clouds;
            case "09d": return R.drawable.art_rain;         case "09n": return R.drawable.art_rain;
            case "10d": return R.drawable.art_shower_rain;  case "10n": return R.drawable.art_shower_rain;
            case "11d": return R.drawable.art_storm;        case "11n": return R.drawable.art_storm;
            case "13d": return R.drawable.art_snow;         case "13n": return R.drawable.art_snow;
            case "50d": return R.drawable.art_fog;          case "50n": return R.drawable.art_fog;
            default: return R.drawable.error;

        }

    }

    public static String[] getReadableDateString(String simpleDate){   //2015-07-13 15:05:36
        String date = simpleDate.split(" ")[0];
        String time = simpleDate.split(" ")[1];

        String year = date.split("-")[0];
        String month = date.split("-")[1];
        String day = date.split("-")[2];

        String hour = time.split(":")[0];
        String min = time.split(":")[1];
        String sec = time.split(":")[2];

        switch (Integer.parseInt(month)) {
            case 1: month = "January";      break;
            case 2: month = "February";     break;
            case 3: month = "March";        break;
            case 4: month = "April";        break;
            case 5: month = "May";          break;
            case 6: month = "June";         break;
            case 7: month = "July";         break;
            case 8: month = "August";       break;
            case 9: month = "September";    break;
            case 10: month = "October";     break;
            case 11: month = "November";    break;
            case 12: month = "December";    break;
        }

        return new String[]{month + " " + day, hour+":"+min};
    }

    public static String getCurTime(String simpleDate){
        String time = simpleDate.split(" ")[1];
        String hour = time.split(":")[0];
        String min = time.split(":")[1];
        return hour + ":" + min;
    }

    public static String getDayOfWeek(String simpleDate){   //2015-07-13 15:05:36
        String result = "";
        SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date dt1=format1.parse(simpleDate);
            DateFormat format2=new SimpleDateFormat("EEEE");
            result = format2.format(dt1);
        } catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        return result;
    }

    private static String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    public static int getWeatherDataFromJsonSaveDB
            (Context context, String forecastJsonStr, int forecastType) throws JSONException {

        if (forecastType ==  WeatherContract.WEATHER_TYPE_CURRENT) {
            //long date; String description, icon;
            //double temp, temp_max, temp_min, humidity, rain;
            //double speed, deg;
            //int sunrise, sunset;

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray arrWeather = forecastJson.getJSONArray("weather");
            JSONObject elemWeather = arrWeather.getJSONObject(arrWeather.length()-1);

            JSONObject elemMain = forecastJson.getJSONObject("main");

            JSONObject elemWind = forecastJson.getJSONObject("wind");

            JSONObject elemCloud = forecastJson.getJSONObject("clouds");

            double rain = 0.0d;
            if (forecastJson.has("rain")) {
                JSONObject elemRain = forecastJson.getJSONObject("rain");
                rain = elemRain.getDouble("1h");
            }

            double snow = 0.0d;
            if (forecastJson.has("snow")) {
                JSONObject elemSnow = forecastJson.getJSONObject("snow");
                snow = elemSnow.getDouble("1h");
            }

            long unixCurTime = forecastJson.getLong("dt");
            Date curDate = new Date(unixCurTime*1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            sdf.setTimeZone(TimeZone.getTimeZone("Canada/Newfoundland"));
            String strCurDate = sdf.format(curDate);

            JSONObject elemSys = forecastJson.getJSONObject("sys");
            long unixSunriseTime = elemSys.getLong("sunrise");
            long unixSunsetTime = elemSys.getLong("sunset");
            Date sunriseDate = new Date(unixSunriseTime*1000L);
            Date sunsetDate = new Date(unixSunsetTime*1000L);
            String strSunrise = sdf.format(sunriseDate);
            String strSunset = sdf.format(sunsetDate);

            ContentValues values = new ContentValues();

            values.put(WeatherEntry.COLUMN_LOC_KEY, 1);
            values.put(WeatherEntry.COLUMN_TYPE, WeatherContract.WEATHER_TYPE_CURRENT);
            values.put(WeatherEntry.COLUMN_DATE, strCurDate);
            values.put(WeatherEntry.COLUMN_MAIN, elemWeather.getString("main"));
            values.put(WeatherEntry.COLUMN_DESC, elemWeather.getString("description"));
            values.put(WeatherEntry.COLUMN_ICON, elemWeather.getString("icon"));
            values.put(WeatherEntry.COLUMN_PRESSURE, elemMain.getDouble("pressure"));
            values.put(WeatherEntry.COLUMN_HUMIDITY, elemMain.getInt("humidity"));
            values.put(WeatherEntry.COLUMN_TEMP, elemMain.getDouble("temp"));
            values.put(WeatherEntry.COLUMN_TEMP_MAX, elemMain.getDouble("temp_max"));
            values.put(WeatherEntry.COLUMN_TEMP_MIN, elemMain.getDouble("temp_min"));
            values.put(WeatherEntry.COLUMN_WIND_SPEED, elemWind.getDouble("speed"));
            values.put(WeatherEntry.COLUMN_WIND_DEGREE, elemWind.getInt("deg"));
            values.put(WeatherEntry.COLUMN_CLOUDS, elemCloud.getInt("all"));
            values.put(WeatherEntry.COLUMN_RAIN, rain);
            values.put(WeatherEntry.COLUMN_SNOW, snow);
            values.put(WeatherEntry.COLUMN_SUNRISE, strSunrise);
            values.put(WeatherEntry.COLUMN_SUNSET, strSunset);

            ContentValues[] forecastValues = {values};

            int deleted = context.getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI,
                    WeatherEntry.COLUMN_TYPE + " == " + WeatherContract.WEATHER_TYPE_CURRENT, null);
            context.getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, values);

            return 1;

        } else if (forecastType ==  WeatherContract.WEATHER_TYPE_HOURLY) {
            //long date;
            //double temp;
            //String description, icon;
            //double rain;

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray arrList = forecastJson.getJSONArray("list");

            ContentValues[] forecastValues = new ContentValues[arrList.length()];

            for(int i = 0; i < arrList.length(); i++) {

                long unixCurTime = arrList.getJSONObject(i).getLong("dt");
                Date curDate = new Date(unixCurTime*1000L);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                sdf.setTimeZone(TimeZone.getTimeZone("Canada/Newfoundland"));
                String strCurDate = sdf.format(curDate);

                JSONObject elemMain = arrList.getJSONObject(i).getJSONObject("main");

                JSONArray arrWeather = arrList.getJSONObject(i).getJSONArray("weather");

                JSONObject elemCloud = arrList.getJSONObject(i).getJSONObject("clouds");
                JSONObject elemWind = arrList.getJSONObject(i).getJSONObject("wind");

                double rain = 0.0d;
                if (arrList.getJSONObject(i).has("rain")) {
                    JSONObject elemRain = arrList.getJSONObject(i).getJSONObject("rain");
                    rain = elemRain.getDouble("3h");
                }

                double snow = 0.0d;
                if (forecastJson.has("snow")) {
                    JSONObject elemSnow = arrList.getJSONObject(i).getJSONObject("snow");
                    snow = elemSnow.getDouble("3h");
                }


                ContentValues weatherValues_Hourly = new ContentValues();

                weatherValues_Hourly.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, 1);
                weatherValues_Hourly.put(WeatherContract.WeatherEntry.COLUMN_TYPE, WeatherContract.WEATHER_TYPE_HOURLY);
                weatherValues_Hourly.put(WeatherContract.WeatherEntry.COLUMN_DATE, strCurDate);
                weatherValues_Hourly.put(WeatherContract.WeatherEntry.COLUMN_MAIN, arrWeather.getJSONObject(arrWeather.length()-1).getString("main"));
                weatherValues_Hourly.put(WeatherContract.WeatherEntry.COLUMN_DESC, arrWeather.getJSONObject(arrWeather.length()-1).getString("description"));
                weatherValues_Hourly.put(WeatherContract.WeatherEntry.COLUMN_ICON, arrWeather.getJSONObject(arrWeather.length()-1).getString("icon"));
                weatherValues_Hourly.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, elemMain.getDouble("pressure"));
                weatherValues_Hourly.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, elemMain.getInt("humidity"));
                weatherValues_Hourly.put(WeatherContract.WeatherEntry.COLUMN_TEMP, elemMain.getDouble("temp"));
                weatherValues_Hourly.put(WeatherContract.WeatherEntry.COLUMN_TEMP_MAX, elemMain.getDouble("temp_max"));
                weatherValues_Hourly.put(WeatherContract.WeatherEntry.COLUMN_TEMP_MIN, elemMain.getDouble("temp_min"));//
                weatherValues_Hourly.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, elemWind.getDouble("speed"));
                weatherValues_Hourly.put(WeatherContract.WeatherEntry.COLUMN_WIND_DEGREE, elemWind.getInt("deg"));
                weatherValues_Hourly.put(WeatherContract.WeatherEntry.COLUMN_CLOUDS, elemCloud.getInt("all"));
                weatherValues_Hourly.put(WeatherContract.WeatherEntry.COLUMN_RAIN, rain);
                weatherValues_Hourly.put(WeatherContract.WeatherEntry.COLUMN_SNOW, snow);
                weatherValues_Hourly.put(WeatherContract.WeatherEntry.COLUMN_SUNRISE, "");
                weatherValues_Hourly.put(WeatherContract.WeatherEntry.COLUMN_SUNSET, "");
                forecastValues[i] = weatherValues_Hourly;
            }



            ArrayList<CurrentWeatherVo> listHourlyData = new ArrayList<CurrentWeatherVo>();

            for (int i=0; i<forecastValues.length; i++) {
                listHourlyData.add(convertContentValues(forecastValues[i]));
            }
//            CurrentWeather.HOURLY_FORECAST = null;
//            CurrentWeather.HOURLY_FORECAST = listHourlyData;
            int deleted = context.getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI,
                    WeatherEntry.COLUMN_TYPE + " == " + WeatherContract.WEATHER_TYPE_HOURLY, null);
            int inserted = context.getContentResolver().bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, forecastValues);
            List<CurrentWeatherVo> weatherVoHourly = Utility.getHourlyWeatherFromDB(context);
            return inserted;
        } else if (forecastType ==  WeatherContract.WEATHER_TYPE_DAILY) {
            //long date;
            //double max, min;
            //String description, icon;
            //double rain;
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray arrList = forecastJson.getJSONArray("list");

            ContentValues[] forecastValues = new ContentValues[arrList.length()];

            for(int i = 0; i < arrList.length(); i++) {
                long unixCurTime = arrList.getJSONObject(i).getLong("dt");
                Date curDate = new Date(unixCurTime*1000L);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                sdf.setTimeZone(TimeZone.getTimeZone("Canada/Newfoundland"));
                String strCurDate = sdf.format(curDate);

                JSONObject elemTemp = arrList.getJSONObject(i).getJSONObject("temp");
                JSONArray arrWeather = arrList.getJSONObject(i).getJSONArray("weather");
//                JSONObject elemWind = arrList.getJSONObject(i).getJSONObject("wind");
//                JSONObject elemClouds = arrList.getJSONObject(i).getJSONObject("clouds");

                double rain = 0.0d;
                if (arrList.getJSONObject(i).has("rain")) {
                    rain = arrList.getJSONObject(i).getDouble("rain");
                }

                double snow = 0.0d;
                if (arrList.getJSONObject(i).has("snow")) {
                    snow = arrList.getJSONObject(i).getDouble("snow");
                }

                ContentValues weatherValues_Daily = new ContentValues();

                weatherValues_Daily.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, 1);
                weatherValues_Daily.put(WeatherContract.WeatherEntry.COLUMN_TYPE, WeatherContract.WEATHER_TYPE_DAILY);//
                weatherValues_Daily.put(WeatherContract.WeatherEntry.COLUMN_DATE, strCurDate);//
                weatherValues_Daily.put(WeatherContract.WeatherEntry.COLUMN_MAIN, arrWeather.getJSONObject(0).getString("main"));//
                weatherValues_Daily.put(WeatherContract.WeatherEntry.COLUMN_DESC, arrWeather.getJSONObject(0).getString("description"));//
                weatherValues_Daily.put(WeatherContract.WeatherEntry.COLUMN_ICON, arrWeather.getJSONObject(0).getString("icon"));//
                weatherValues_Daily.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, arrList.getJSONObject(i).getDouble("pressure"));//
                weatherValues_Daily.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, arrList.getJSONObject(i).getInt("humidity"));//
                weatherValues_Daily.put(WeatherContract.WeatherEntry.COLUMN_TEMP, 0d);
                weatherValues_Daily.put(WeatherContract.WeatherEntry.COLUMN_TEMP_MAX, elemTemp.getDouble("max"));
                weatherValues_Daily.put(WeatherContract.WeatherEntry.COLUMN_TEMP_MIN, elemTemp.getDouble("min"));//
                weatherValues_Daily.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, arrList.getJSONObject(i).getDouble("speed"));
                weatherValues_Daily.put(WeatherContract.WeatherEntry.COLUMN_WIND_DEGREE, arrList.getJSONObject(i).getInt("deg"));
                weatherValues_Daily.put(WeatherContract.WeatherEntry.COLUMN_CLOUDS, arrList.getJSONObject(i).getInt("clouds"));
                weatherValues_Daily.put(WeatherContract.WeatherEntry.COLUMN_RAIN, rain);
                weatherValues_Daily.put(WeatherContract.WeatherEntry.COLUMN_SNOW, snow);
                weatherValues_Daily.put(WeatherContract.WeatherEntry.COLUMN_SUNRISE, "");
                weatherValues_Daily.put(WeatherContract.WeatherEntry.COLUMN_SUNSET, "");
                forecastValues[i] = weatherValues_Daily;

            }

            ArrayList<CurrentWeatherVo> listDailyData = new ArrayList<CurrentWeatherVo>();

            for (int i=0; i<forecastValues.length; i++) {
                listDailyData.add(convertContentValues(forecastValues[i]));
            }
//            CurrentWeather.DAILY_FORECAST = null;
//            CurrentWeather.DAILY_FORECAST = listDailyData;
            int deleted = context.getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI,
                    WeatherEntry.COLUMN_TYPE + " == " + WeatherContract.WEATHER_TYPE_DAILY, null);
            int inserted = context.getContentResolver().bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, forecastValues);
            return inserted;
        } else {
            return -1;
        }

    }

    public static CurrentWeatherVo getCurWeatherFromDB(Context context) {
        Cursor cursor = context.getContentResolver().query(
                WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                WeatherContract.WeatherEntry.COLUMN_TYPE + " = " + WeatherContract.WEATHER_TYPE_CURRENT,
                null,
                WeatherContract.WeatherEntry.COLUMN_DATE + " DESC LIMIT 1"
        );
        cursor.moveToFirst();

        CurrentWeatherVo curWeather = new CurrentWeatherVo();
        curWeather.location_id = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_LOC_KEY));
        curWeather.weather_type = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TYPE));
        curWeather.date = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE));
        curWeather.main = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAIN));
        curWeather.desc = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DESC));
        curWeather.icon = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_ICON));
        curWeather.pressure = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE));
        curWeather.humidity = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY));
        curWeather.temp = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP));
        curWeather.temp_max = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP_MAX));
        curWeather.temp_min = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP_MIN));
        curWeather.wind_speed = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED));
        curWeather.wind_degree = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_DEGREE));
        curWeather.clouds = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_CLOUDS));
        curWeather.rain = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_RAIN));
        curWeather.snow = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SNOW));
        curWeather.sunrise = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SUNRISE));
        curWeather.sunset = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SUNSET));

        return curWeather;
    }
    public static ArrayList<CurrentWeatherVo> getHourlyWeatherFromDB(Context context) {
        ArrayList<CurrentWeatherVo> hourlyWeather = new ArrayList<CurrentWeatherVo>();

        Cursor cursor = context.getContentResolver().query(
                WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                WeatherContract.WeatherEntry.COLUMN_TYPE + " = " + WeatherContract.WEATHER_TYPE_HOURLY,
                null,
                WeatherContract.WeatherEntry.COLUMN_DATE + " DESC LIMIT 4"
        );

        if (cursor.moveToLast()){
            do{
                CurrentWeatherVo hourly = new CurrentWeatherVo();
                hourly.location_id = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_LOC_KEY));
                hourly.weather_type = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TYPE));
                hourly.date = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE));
                hourly.main = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAIN));
                hourly.desc = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DESC));
                hourly.icon = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_ICON));
                hourly.pressure = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE));
                hourly.humidity = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY));
                hourly.temp = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP));
                hourly.temp_max = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP_MAX));
                hourly.temp_min = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP_MIN));
                hourly.wind_speed = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED));
                hourly.wind_degree = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_DEGREE));
                hourly.clouds = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_CLOUDS));
                hourly.rain = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_RAIN));
                hourly.snow = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SNOW));
                hourly.sunrise = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SUNRISE));
                hourly.sunset = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SUNSET));

                hourlyWeather.add(hourly);
                // do what ever you want here
            }while(cursor.moveToPrevious());
        }

        return hourlyWeather;
    }
    public static ArrayList<CurrentWeatherVo> getDailyWeatherFromDB(Context context) {
        ArrayList<CurrentWeatherVo> dailyWeather = new ArrayList<CurrentWeatherVo>();

        Cursor cursor = context.getContentResolver().query(
                WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                WeatherContract.WeatherEntry.COLUMN_TYPE + " = " + WeatherContract.WEATHER_TYPE_DAILY,
                null,
                WeatherContract.WeatherEntry.COLUMN_DATE + " DESC LIMIT 16"
        );

        if (cursor.moveToLast()){
            do{
                CurrentWeatherVo daily = new CurrentWeatherVo();
                daily.location_id = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_LOC_KEY));
                daily.weather_type = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TYPE));
                daily.date = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE));
                daily.main = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAIN));
                daily.desc = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DESC));
                daily.icon = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_ICON));
                daily.pressure = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE));
                daily.humidity = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY));
                daily.temp = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP));
                daily.temp_max = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP_MAX));
                daily.temp_min = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP_MIN));
                daily.wind_speed = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED));
                daily.wind_degree = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_DEGREE));
                daily.clouds = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_CLOUDS));
                daily.rain = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_RAIN));
                daily.snow = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SNOW));
                daily.sunrise = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SUNRISE));
                daily.sunset = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SUNSET));

                dailyWeather.add(daily);
                // do what ever you want here
            }while(cursor.moveToPrevious());
        }

        return dailyWeather;
    }

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

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_units_key),
                context.getString(R.string.pref_units_metric))
                .equals(context.getString(R.string.pref_units_metric));
    }

    public static double getTemp(Context context, double metric) {
        // Data stored in Celsius by default.  If user prefers to see in Fahrenheit, convert
        // the values here.
        double temperature = metric;

        if (!isMetric(context)) {
            temperature = (temperature * 1.8) + 32;
        }

        // For presentation, assume the user doesn't care about tenths of a degree.
        return temperature;
    }

    public static String convertDegToDirection(int deg) {

        if (22.5 < deg && deg <= 67.5) {            //NE    22.5 - 67.5
            return "NE";
        } else if (67.5 < deg && deg <= 112.5) {    //E     67.5 - 112.5
            return "E";
        } else if (112.5 < deg && deg <= 157.5) {   //SE    112.5 - 157.5
            return "SE";
        } else if (157.5 < deg && deg <= 202.5) {   //S     157.5 - 202.5
            return "S";
        } else if (202.5 < deg && deg <= 247.5) {   //SW    202.5 - 247.5
            return "SW";
        } else if (247.5 < deg && deg <= 292.5) {   //W     247.5 - 292.5
            return "W";
        } else if (292.5 < deg && deg <= 337.5) {   //NW    292.5 - 337.5
            return "NW";
        } else if (337.5 < deg && deg <= 22.5) {    //N     337.5 - 22.5
            return "N";
        } else {
            return "";
        }
    }

}
