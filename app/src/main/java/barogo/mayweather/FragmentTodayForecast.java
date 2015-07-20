package barogo.mayweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import barogo.mayweather.data.CurrentWeatherVo;
import barogo.mayweather.sync.SyncAdapterCurrent;


/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentTodayForecast extends Fragment {

    private final String LOG_TAG = FragmentTodayForecast.class.getSimpleName();

    private BroadcastReceiver receiver;

    public FragmentTodayForecast() {
    }

    @Override
    public void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver),
                new IntentFilter("TODAY")
        );
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver),
                new IntentFilter("HOURLY")
        );
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver),
                new IntentFilter("DAILY")
        );
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        updateWeather();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
           updateWeather();
           return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_today_item, container, false);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction() == "TODAY") {
                    CurrentWeatherVo weatherVo = intent.getParcelableExtra("CURRENT");
                    WeatherDataParser.curDate = WeatherDataParser.getReadableDateString(weatherVo.date)[0];

                    //Current
                    TextView weather_today_data = (TextView) rootView.findViewById(R.id.weather_today_date);
                    weather_today_data.setText("Today - " + WeatherDataParser.getDayOfWeek(weatherVo.date) + ", "
                                    + WeatherDataParser.getCurTime(weatherVo.date)
                        /*WeatherDataParser.getReadableDateString(weatherVo.date)[0]*/);


                    TextView weather_today_temp = (TextView) rootView.findViewById(R.id.weather_today_temp);
                    weather_today_temp.setText(""+Math.round(weatherVo.temp)+(char) 0x00B0);

                    TextView weather_today_temp_bounds = (TextView) rootView.findViewById(R.id.weather_today_temp_bounds);
                    weather_today_temp_bounds.setText("fix"/*+Math.round(dailyInfo.get(0).temp_min)*/ +(char) 0x00B0 + " - "
                            + "fix"/*Math.round(dailyInfo.get(0).temp_max)*/ + (char) 0x00B0);

                    ImageView img_today = (ImageView) rootView.findViewById(R.id.img_today);
                    img_today.setImageResource(WeatherDataParser.findWeatherConditionImg(weatherVo.icon));

                    TextView weather_today_desc = (TextView) rootView.findViewById(R.id.weather_today_desc);
                    weather_today_desc.setText(weatherVo.desc.toUpperCase());

                    TextView weather_today_humidity = (TextView) rootView.findViewById(R.id.weather_today_humidity);
                    weather_today_humidity.setText(weatherVo.humidity + "%");

                    TextView weather_today_wind = (TextView) rootView.findViewById(R.id.weather_today_wind);
                    weather_today_wind.setText(""+weatherVo.wind_speed + "m/s");
                    //End Current
                } else if (intent.getAction() == "HOURLY") {
                    CurrentWeatherVo weatherVo0 = intent.getParcelableExtra("HOURLY0");
                    CurrentWeatherVo weatherVo1 = intent.getParcelableExtra("HOURLY1");
                    CurrentWeatherVo weatherVo2 = intent.getParcelableExtra("HOURLY2");
                    CurrentWeatherVo weatherVo3 = intent.getParcelableExtra("HOURLY3");
                    WeatherDataParser.hourlyForecast = WeatherDataParser.getReadableDateString(weatherVo0.date)[0];

                    TextView weather_hourly_time_1 = (TextView) rootView.findViewById(R.id.weather_hourly_time_1);
                    weather_hourly_time_1.setText(WeatherDataParser.getReadableDateString(weatherVo0.date)[1]);

                    TextView weather_hourly_temp_1 = (TextView) rootView.findViewById(R.id.weather_hourly_temp_1);
                    weather_hourly_temp_1.setText("" + Math.round(weatherVo0.temp) + (char) 0x00B0);

                    ImageView img_hourly_1 = (ImageView) rootView.findViewById(R.id.img_hourly_1);
                    img_hourly_1.setImageResource(WeatherDataParser.findWeatherConditionImg(weatherVo0.icon));


                    TextView weather_hourly_time_2 = (TextView) rootView.findViewById(R.id.weather_hourly_time_2);
                    weather_hourly_time_2.setText(WeatherDataParser.getReadableDateString(weatherVo1.date)[1]);

                    TextView weather_hourly_temp_2 = (TextView) rootView.findViewById(R.id.weather_hourly_temp_2);
                    weather_hourly_temp_2.setText("" + Math.round(weatherVo1.temp) + (char) 0x00B0);

                    ImageView img_hourly_2 = (ImageView) rootView.findViewById(R.id.img_hourly_2);
                    img_hourly_2.setImageResource(WeatherDataParser.findWeatherConditionImg(weatherVo1.icon));


                    TextView weather_hourly_time_3 = (TextView) rootView.findViewById(R.id.weather_hourly_time_3);
                    weather_hourly_time_3.setText(WeatherDataParser.getReadableDateString(weatherVo2.date)[1]);

                    TextView weather_hourly_temp_3 = (TextView) rootView.findViewById(R.id.weather_hourly_temp_3);
                    weather_hourly_temp_3.setText("" + Math.round(weatherVo2.temp) + (char) 0x00B0);

                    ImageView img_hourly_3 = (ImageView) rootView.findViewById(R.id.img_hourly_3);
                    img_hourly_3.setImageResource(WeatherDataParser.findWeatherConditionImg(weatherVo2.icon));


                    TextView weather_hourly_time_4 = (TextView) rootView.findViewById(R.id.weather_hourly_time_4);
                    weather_hourly_time_4.setText(WeatherDataParser.getReadableDateString(weatherVo3.date)[1]);

                    TextView weather_hourly_temp_4 = (TextView) rootView.findViewById(R.id.weather_hourly_temp_4);
                    weather_hourly_temp_4.setText("" + Math.round(weatherVo3.temp) + (char) 0x00B0);

                    ImageView img_hourly_4 = (ImageView) rootView.findViewById(R.id.img_hourly_4);
                    img_hourly_4.setImageResource(WeatherDataParser.findWeatherConditionImg(weatherVo3.icon));
                } else if (intent.getAction() == "DAILY") {
                    System.out.print("");
                    System.out.print("");
                    System.out.print("");
                } else {}



                //Hourly

                //End Hourly
            }
        };

        return rootView;
    }

    public void updateWeather() {
        /*try {
            FetchWeatherTask curWeatherTask = new FetchWeatherTask(getActivity());
            FetchWeatherTask foreWeatherTask = new FetchWeatherTask(getActivity());
            FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());

            curWeatherTask.execute("http://api.openweathermap.org/data/2.5/weather?q=st.+johns&units=metric", WeatherDataParser.CURRENT_WEATHER).get();
            foreWeatherTask.execute("http://api.openweathermap.org/data/2.5/forecast?q=st.+johns&units=metric&cnt=4", WeatherDataParser.HOURLY_WEATHER).get();
            weatherTask.execute("http://api.openweathermap.org/data/2.5/forecast/daily?q=st.+johns&units=metric&cnt=16", WeatherDataParser.DAILY_WEATHER).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        CurrentWeather.CURRENT = WeatherDataParser.getCurWeatherFromDB(getActivity());
        CurrentWeather.HOURLY_FORECAST = WeatherDataParser.getHourlyWeatherFromDB(getActivity());*/

        //IntentService
        /*Intent alarmIntent = new Intent(getActivity(), WeatherService.AlarmReceiver.class);
        alarmIntent.putExtra(WeatherService.LOCATION_QUERY_EXTRA, "Seoul");

        //Wrap in a pending intent which only fires once.
        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0,alarmIntent,PendingIntent.FLAG_ONE_SHOT);//getBroadcast(context, 0, i, 0);

        AlarmManager am=(AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);

        //Set the AlarmManager to wake up the system.
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pi);*/

        //SyncAdapter
        SyncAdapterCurrent.syncImmediately(getActivity());
//        SyncAdapterHourly.syncImmediately(getActivity());
    }
}
