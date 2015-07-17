package barogo.mayweather;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import barogo.mayweather.data.CurrentWeather;
import barogo.mayweather.data.CurrentWeatherVo;


/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentTodayForecast extends Fragment {

    private final String LOG_TAG = FragmentTodayForecast.class.getSimpleName();

    public FragmentTodayForecast() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchWeatherTask curWeatherTask = new FetchWeatherTask(getActivity());
            curWeatherTask.execute("http://api.openweathermap.org/data/2.5/weather?q=st.+johns&units=metric", WeatherDataParser.CURRENT_WEATHER);

            FetchWeatherTask foreWeatherTask = new FetchWeatherTask(getActivity());
            foreWeatherTask.execute("http://api.openweathermap.org/data/2.5/forecast?q=st.+johns&units=metric&cnt=4", WeatherDataParser.HOURLY_WEATHER);

            FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
            weatherTask.execute("http://api.openweathermap.org/data/2.5/forecast/daily?q=st.+johns&units=metric&cnt=16", WeatherDataParser.DAILY_WEATHER);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_today_item, container, false);

        //Current
        CurrentWeatherVo curInfo = CurrentWeather.CURRENT;
        ArrayList<CurrentWeatherVo> hourlyInfo = CurrentWeather.HOURLY_FORECAST;
//        ArrayList<CurrentWeatherVo> dailyInfo = CurrentWeather.DAILY_FORECAST;

        TextView weather_today_data = (TextView) rootView.findViewById(R.id.weather_today_date);
        weather_today_data.setText("Today - " + WeatherDataParser.getDayOfWeek(curInfo.date) + ", "
                + WeatherDataParser.getReadableDateString(curInfo.date)[0]);

        TextView weather_today_temp = (TextView) rootView.findViewById(R.id.weather_today_temp);
        weather_today_temp.setText(""+Math.round(curInfo.temp)+(char) 0x00B0);

        TextView weather_today_temp_bounds = (TextView) rootView.findViewById(R.id.weather_today_temp_bounds);
        weather_today_temp_bounds.setText("fix"/*+Math.round(dailyInfo.get(0).temp_min)*/ +(char) 0x00B0 + " - "
                + "fix"/*Math.round(dailyInfo.get(0).temp_max)*/ + (char) 0x00B0);

        ImageView img_today = (ImageView) rootView.findViewById(R.id.img_today);
        img_today.setImageResource(WeatherDataParser.findWeatherConditionImg(curInfo.icon));

        TextView weather_today_desc = (TextView) rootView.findViewById(R.id.weather_today_desc);
        weather_today_desc.setText(curInfo.desc.toUpperCase());

        TextView weather_today_humidity = (TextView) rootView.findViewById(R.id.weather_today_humidity);
        weather_today_humidity.setText(curInfo.humidity + "%");

        TextView weather_today_wind = (TextView) rootView.findViewById(R.id.weather_today_wind);
        weather_today_wind.setText(""+curInfo.wind_speed + "m/s");
        //End Current

        //Hourly
        TextView weather_hourly_time_1 = (TextView) rootView.findViewById(R.id.weather_hourly_time_1);
        weather_hourly_time_1.setText(WeatherDataParser.getReadableDateString(hourlyInfo.get(0).date)[1]);

        TextView weather_hourly_temp_1 = (TextView) rootView.findViewById(R.id.weather_hourly_temp_1);
        weather_hourly_temp_1.setText("" + Math.round(hourlyInfo.get(0).temp) + (char) 0x00B0);

        ImageView img_hourly_1 = (ImageView) rootView.findViewById(R.id.img_hourly_1);
        img_hourly_1.setImageResource(WeatherDataParser.findWeatherConditionImg(hourlyInfo.get(0).icon));


        TextView weather_hourly_time_2 = (TextView) rootView.findViewById(R.id.weather_hourly_time_2);
        weather_hourly_time_2.setText(WeatherDataParser.getReadableDateString(hourlyInfo.get(1).date)[1]);

        TextView weather_hourly_temp_2 = (TextView) rootView.findViewById(R.id.weather_hourly_temp_2);
        weather_hourly_temp_2.setText("" + Math.round(hourlyInfo.get(1).temp) + (char) 0x00B0);

        ImageView img_hourly_2 = (ImageView) rootView.findViewById(R.id.img_hourly_2);
        img_hourly_2.setImageResource(WeatherDataParser.findWeatherConditionImg(hourlyInfo.get(1).icon));


        TextView weather_hourly_time_3 = (TextView) rootView.findViewById(R.id.weather_hourly_time_3);
        weather_hourly_time_3.setText(WeatherDataParser.getReadableDateString(hourlyInfo.get(2).date)[1]);

        TextView weather_hourly_temp_3 = (TextView) rootView.findViewById(R.id.weather_hourly_temp_3);
        weather_hourly_temp_3.setText("" + Math.round(hourlyInfo.get(2).temp) + (char) 0x00B0);

        ImageView img_hourly_3 = (ImageView) rootView.findViewById(R.id.img_hourly_3);
        img_hourly_3.setImageResource(WeatherDataParser.findWeatherConditionImg(hourlyInfo.get(2).icon));


        TextView weather_hourly_time_4 = (TextView) rootView.findViewById(R.id.weather_hourly_time_4);
        weather_hourly_time_4.setText(WeatherDataParser.getReadableDateString(hourlyInfo.get(3).date)[1]);

        TextView weather_hourly_temp_4 = (TextView) rootView.findViewById(R.id.weather_hourly_temp_4);
        weather_hourly_temp_4.setText("" + Math.round(hourlyInfo.get(3).temp) + (char) 0x00B0);

        ImageView img_hourly_4 = (ImageView) rootView.findViewById(R.id.img_hourly_4);
        img_hourly_4.setImageResource(WeatherDataParser.findWeatherConditionImg(hourlyInfo.get(3).icon));


        return rootView;
    }
}
