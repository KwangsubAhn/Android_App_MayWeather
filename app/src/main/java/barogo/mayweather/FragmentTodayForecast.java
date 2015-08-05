package barogo.mayweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import barogo.mayweather.data.CurrentWeatherVo;
import barogo.mayweather.sync.SyncAdapterCurrent;


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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_today_item, container, false);

        CurrentWeatherVo cur = Utility.getCurWeatherFromDB(getActivity());
        ArrayList<CurrentWeatherVo> hourly = Utility.getHourlyWeatherFromDB(getActivity());

        if (cur != null) {updateCurView(rootView, cur);}

        if (hourly != null) {updateHourlyView(rootView, hourly);}

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction() == "TODAY") {
                    CurrentWeatherVo weatherVo = intent.getParcelableExtra("CURRENT");
                    updateCurView(rootView, weatherVo);
                    //End Current

                } else if (intent.getAction() == "HOURLY") {
                    //Set Today's Max/Min temp
                    double dMax = Utility.getTemp(getActivity(), intent.getDoubleExtra("MAX", 0d));
                    double dMin = Utility.getTemp(getActivity(), intent.getDoubleExtra("MIN", 0d));

                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    settings.edit().putString("MAX", "" + intent.getDoubleExtra("MAX", 0d)).commit();
                    settings.edit().putString("MIN", "" + intent.getDoubleExtra("MIN", 0d)).commit();

                    TextView weather_today_temp_bounds = (TextView) rootView.findViewById(R.id.weather_today_temp_bounds);
                    weather_today_temp_bounds.setText("" + (int) Math.floor(dMin) + (char) 0x00B0 + " - "
                            + (int) Math.ceil(dMax) + (char) 0x00B0);
                    //

                    //Set Hourly Forecast
                    CurrentWeatherVo weatherVo0 = intent.getParcelableExtra("HOURLY0");
                    CurrentWeatherVo weatherVo1 = intent.getParcelableExtra("HOURLY1");
                    CurrentWeatherVo weatherVo2 = intent.getParcelableExtra("HOURLY2");
                    CurrentWeatherVo weatherVo3 = intent.getParcelableExtra("HOURLY3");

                    ArrayList<CurrentWeatherVo> listWeatherVo = new ArrayList<CurrentWeatherVo>();
                    listWeatherVo.add(weatherVo0);
                    listWeatherVo.add(weatherVo1);
                    listWeatherVo.add(weatherVo2);
                    listWeatherVo.add(weatherVo3);

                    updateHourlyView(rootView, listWeatherVo);

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

    private void updateCurView(View rootView, CurrentWeatherVo weatherVo){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        settings.edit().putString("CURRENT_TIME", ""+weatherVo.date).commit();

        //Current
        TextView weather_today_data = (TextView) rootView.findViewById(R.id.weather_today_date);
        weather_today_data.setText(Utility.getDayOfWeek(weatherVo.date) + ", "
                + Utility.getReadableDateString(weatherVo.date)[0]);


        TextView weather_today_temp = (TextView) rootView.findViewById(R.id.weather_today_temp);
        double tempCur = Utility.getTemp(getActivity(), weatherVo.temp);
        weather_today_temp.setText("" + Math.round(tempCur) + (char) 0x00B0);

        //MAX BOUND
        String strMax = settings.getString("MAX", "");
        String strMin = settings.getString("MIN", "");

        if (!strMax.equals("") && !strMin.equals("")) {
            double dMax = Utility.getTemp(getActivity(), Double.parseDouble(strMax));
            double dMin = Utility.getTemp(getActivity(), Double.parseDouble(strMin));

            TextView weather_today_temp_bounds = (TextView) rootView.findViewById(R.id.weather_today_temp_bounds);
            weather_today_temp_bounds.setText("" + (int) Math.floor(dMin) + (char) 0x00B0 + " - "
                    + (int) Math.ceil(dMax) + (char) 0x00B0);
        }
        //

        ImageView img_today = (ImageView) rootView.findViewById(R.id.img_today);
        img_today.setImageResource(Utility.findWeatherConditionImg(weatherVo.icon, weatherVo.rain));

        TextView weather_today_desc = (TextView) rootView.findViewById(R.id.weather_today_desc);
        weather_today_desc.setText(weatherVo.desc.toUpperCase());

        if (weatherVo.rain >= 0d && weatherVo.desc.contains("rain")) {
            ImageView img_rainfall = (ImageView) rootView.findViewById(R.id.img_rainfall);
            img_rainfall.setImageResource(R.drawable.umbrella);

            TextView weather_today_rainfall = (TextView) rootView.findViewById(R.id.weather_today_rainfall);
            weather_today_rainfall.setText(""+(int)Math.round(weatherVo.rain*10)/10.0d + "mm");

            if (weatherVo.rain < 0.05d) {
                weather_today_rainfall.setText("<0.1" + "mm");
            }

        } else if (weatherVo.snow >= 0d && weatherVo.snow > weatherVo.rain && weatherVo.desc.contains("snow")) {
            ImageView img_rainfall = (ImageView) rootView.findViewById(R.id.img_rainfall);
            img_rainfall.setImageResource(R.drawable.snow);

            TextView weather_today_rainfall = (TextView) rootView.findViewById(R.id.weather_today_rainfall);
            weather_today_rainfall.setText(""+(int)Math.round(weatherVo.snow*10)/10.0d + "mm");

            if (weatherVo.snow < 0.05d) {
                weather_today_rainfall.setText("<0.1" + "mm");
            }
        } else {
            ImageView img_rainfall = (ImageView) rootView.findViewById(R.id.img_rainfall);
            img_rainfall.setImageResource(0);

            TextView weather_today_rainfall = (TextView) rootView.findViewById(R.id.weather_today_rainfall);
            weather_today_rainfall.setText("");
        }

        TextView weather_today_humidity = (TextView) rootView.findViewById(R.id.weather_today_humidity);
        weather_today_humidity.setText(weatherVo.humidity + "%");

        TextView weather_today_wind = (TextView) rootView.findViewById(R.id.weather_today_wind);
        weather_today_wind.setText("" + weatherVo.wind_speed + "m/s " + Utility.convertDegToDirection(weatherVo.wind_degree));

        //last update
        String curTime = Utility.getReadableDateString(weatherVo.date)[1];
        TextView last_update = (TextView) rootView.findViewById(R.id.last_update);
        last_update.setText("last update:" + curTime);
        //End Current
    }
    private void updateHourlyView(View rootView, ArrayList<CurrentWeatherVo> hourly){
        CurrentWeatherVo weatherVo0 = hourly.get(0);
        CurrentWeatherVo weatherVo1 = hourly.get(1);
        CurrentWeatherVo weatherVo2 = hourly.get(2);
        CurrentWeatherVo weatherVo3 = hourly.get(3);

        TextView weather_hourly_time_1 = (TextView) rootView.findViewById(R.id.weather_hourly_time_1);
        weather_hourly_time_1.setText(Utility.getReadableDateString(weatherVo0.date)[1]);

        TextView weather_hourly_temp_1 = (TextView) rootView.findViewById(R.id.weather_hourly_temp_1);
        double tempHourly1 = Utility.getTemp(getActivity(), weatherVo0.temp);
        weather_hourly_temp_1.setText("" + Math.round(tempHourly1) + (char) 0x00B0);

        ImageView img_hourly_1 = (ImageView) rootView.findViewById(R.id.img_hourly_1);
        img_hourly_1.setImageResource(Utility.findWeatherConditionImg(weatherVo0.icon, weatherVo0.rain));

        if (weatherVo0.rain >= 0d && weatherVo0.desc.contains("rain")) {
//            ImageView img_hourly_rainfall_1 = (ImageView) rootView.findViewById(R.id.img_hourly_rainfall_1);
//            img_hourly_rainfall_1.setImageResource(R.drawable.umbrella);

            TextView weather_hourly_rainfall_1 = (TextView) rootView.findViewById(R.id.weather_hourly_rainfall_1);
            weather_hourly_rainfall_1.setText(""+(int)Math.round(weatherVo0.rain*10)/10.0d + "mm");
            if (weatherVo0.rain < 0.05d) {
                weather_hourly_rainfall_1.setText("<0.1" + "mm");
            }
        }
        if (weatherVo0.snow >= 0d && weatherVo0.snow > weatherVo0.rain && weatherVo0.desc.contains("snow")) {
//            ImageView img_hourly_rainfall_1 = (ImageView) rootView.findViewById(R.id.img_hourly_rainfall_1);
//            img_hourly_rainfall_1.setImageResource(R.drawable.snow);

            TextView weather_hourly_rainfall_1 = (TextView) rootView.findViewById(R.id.weather_hourly_rainfall_1);
            weather_hourly_rainfall_1.setText(""+(int)Math.round(weatherVo0.snow*10)/10.0d + "mm");
            if (weatherVo0.snow < 0.05d) {
                weather_hourly_rainfall_1.setText("<0.1" + "mm");
            }
        }

        TextView weather_hourly_time_2 = (TextView) rootView.findViewById(R.id.weather_hourly_time_2);
        weather_hourly_time_2.setText(Utility.getReadableDateString(weatherVo1.date)[1]);

        TextView weather_hourly_temp_2 = (TextView) rootView.findViewById(R.id.weather_hourly_temp_2);
        double tempHourly2 = Utility.getTemp(getActivity(), weatherVo1.temp);
        weather_hourly_temp_2.setText("" + Math.round(tempHourly2) + (char) 0x00B0);

        ImageView img_hourly_2 = (ImageView) rootView.findViewById(R.id.img_hourly_2);
        img_hourly_2.setImageResource(Utility.findWeatherConditionImg(weatherVo1.icon, weatherVo1.rain));

        if (weatherVo1.rain >= 0d && weatherVo1.desc.contains("rain")) {
//            ImageView img_hourly_rainfall_2 = (ImageView) rootView.findViewById(R.id.img_hourly_rainfall_2);
//            img_hourly_rainfall_2.setImageResource(R.drawable.umbrella);

            TextView weather_hourly_rainfall_2 = (TextView) rootView.findViewById(R.id.weather_hourly_rainfall_2);
            weather_hourly_rainfall_2.setText(""+(int)Math.round(weatherVo1.rain*10)/10.0d + "mm");
            if (weatherVo1.rain < 0.05d) {
                weather_hourly_rainfall_2.setText("<0.1" + "mm");
            }
        }
        if (weatherVo1.snow >= 0d && weatherVo1.snow > weatherVo1.rain && weatherVo1.desc.contains("snow")) {
//            ImageView img_hourly_rainfall_2 = (ImageView) rootView.findViewById(R.id.img_hourly_rainfall_2);
//            img_hourly_rainfall_2.setImageResource(R.drawable.snow);

            TextView weather_hourly_rainfall_2 = (TextView) rootView.findViewById(R.id.weather_hourly_rainfall_2);
            weather_hourly_rainfall_2.setText(""+(int)Math.round(weatherVo1.snow*10)/10.0d + "mm");
            if (weatherVo1.snow < 0.05d) {
                weather_hourly_rainfall_2.setText("<0.1" + "mm");
            }
        }


        TextView weather_hourly_time_3 = (TextView) rootView.findViewById(R.id.weather_hourly_time_3);
        weather_hourly_time_3.setText(Utility.getReadableDateString(weatherVo2.date)[1]);

        TextView weather_hourly_temp_3 = (TextView) rootView.findViewById(R.id.weather_hourly_temp_3);
        double tempHourly3 = Utility.getTemp(getActivity(), weatherVo2.temp);
        weather_hourly_temp_3.setText("" + Math.round(tempHourly3) + (char) 0x00B0);

        ImageView img_hourly_3 = (ImageView) rootView.findViewById(R.id.img_hourly_3);
        img_hourly_3.setImageResource(Utility.findWeatherConditionImg(weatherVo2.icon, weatherVo2.rain));

        if (weatherVo2.rain >= 0d && weatherVo2.desc.contains("rain")) {
//            ImageView img_hourly_rainfall_3 = (ImageView) rootView.findViewById(R.id.img_hourly_rainfall_3);
//            img_hourly_rainfall_3.setImageResource(R.drawable.umbrella);

            TextView weather_hourly_rainfall_3 = (TextView) rootView.findViewById(R.id.weather_hourly_rainfall_3);
            weather_hourly_rainfall_3.setText(""+(int)Math.round(weatherVo2.rain*10)/10.0d + "mm");
            if (weatherVo2.rain < 0.05d) {
                weather_hourly_rainfall_3.setText("<0.1" + "mm");
            }
        }
        if (weatherVo2.snow >= 0d && weatherVo2.snow > weatherVo2.rain && weatherVo2.desc.contains("snow")) {
//            ImageView img_hourly_rainfall_3 = (ImageView) rootView.findViewById(R.id.img_hourly_rainfall_3);
//            img_hourly_rainfall_3.setImageResource(R.drawable.snow);

            TextView weather_hourly_rainfall_3 = (TextView) rootView.findViewById(R.id.weather_hourly_rainfall_3);
            weather_hourly_rainfall_3.setText(""+(int)Math.round(weatherVo2.snow*10)/10.0d + "mm");
            if (weatherVo2.snow < 0.05d) {
                weather_hourly_rainfall_3.setText("<0.1" + "mm");
            }
        }


        TextView weather_hourly_time_4 = (TextView) rootView.findViewById(R.id.weather_hourly_time_4);
        weather_hourly_time_4.setText(Utility.getReadableDateString(weatherVo3.date)[1]);

        TextView weather_hourly_temp_4 = (TextView) rootView.findViewById(R.id.weather_hourly_temp_4);
        double tempHourly4 = Utility.getTemp(getActivity(), weatherVo3.temp);
        weather_hourly_temp_4.setText("" + Math.round(tempHourly4) + (char) 0x00B0);

        ImageView img_hourly_4 = (ImageView) rootView.findViewById(R.id.img_hourly_4);
        img_hourly_4.setImageResource(Utility.findWeatherConditionImg(weatherVo3.icon, weatherVo3.rain));

        if (weatherVo3.rain > 0d && weatherVo3.desc.contains("rain")) {
//            ImageView img_hourly_rainfall_4 = (ImageView) rootView.findViewById(R.id.img_hourly_rainfall_4);
//            img_hourly_rainfall_4.setImageResource(R.drawable.umbrella);

            TextView weather_hourly_rainfall_4 = (TextView) rootView.findViewById(R.id.weather_hourly_rainfall_4);
            weather_hourly_rainfall_4.setText(""+(int)Math.round(weatherVo3.rain*10)/10.0d + "mm");
            if (weatherVo3.rain < 0.05d) {
                weather_hourly_rainfall_4.setText("<0.1" + "mm");
            }
        }
        if (weatherVo3.snow > 0d && weatherVo3.snow > weatherVo3.rain && weatherVo3.desc.contains("snow")) {
//            ImageView img_hourly_rainfall_4 = (ImageView) rootView.findViewById(R.id.img_hourly_rainfall_4);
//            img_hourly_rainfall_4.setImageResource(R.drawable.snow);

            TextView weather_hourly_rainfall_4 = (TextView) rootView.findViewById(R.id.weather_hourly_rainfall_4);
            weather_hourly_rainfall_4.setText("" +(int)Math.round(weatherVo3.snow*10)/10.0d + "mm");
            if (weatherVo3.snow < 0.05d) {
                weather_hourly_rainfall_4.setText("<0.1" + "mm");
            }
        }

    }
    public void updateWeather() {
        //SyncAdapter
        SyncAdapterCurrent.syncImmediately(getActivity());
    }
}
