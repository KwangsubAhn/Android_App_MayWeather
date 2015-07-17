package barogo.mayweather;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.concurrent.ExecutionException;

import barogo.mayweather.data.CurrentWeather;


public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    public ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWeatherDataFromDB();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#357bbd")));
        setTitle("Seoul");

        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(new SwipeTabAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.d(LOG_TAG, "onPageScrolled at " + " position " + position + " from " +
                //              positionOffset + " with number of pixels= " + positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                //Log.d(LOG_TAG, "onPageSelected at " + " position " + position);
//                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    Log.d(LOG_TAG, "onPageScrollStateChanged Idle");
                }

                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    Log.d(LOG_TAG, "onPageScrollStateChanged Dragging");
                }

                if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    Log.d(LOG_TAG, "onPageScrollStateChanged Settling");
                }
            }
        });
    }
    @Override
    public void onPause() {
        super.onPause();viewPager.setCurrentItem(0);
        Log.i(LOG_TAG, "on Pause Fragment");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getWeatherDataFromDB() {
        try {
            FetchWeatherTask curWeatherTask = new FetchWeatherTask(this);
            FetchWeatherTask foreWeatherTask = new FetchWeatherTask(this);
            FetchWeatherTask weatherTask = new FetchWeatherTask(this);

            curWeatherTask.execute("http://api.openweathermap.org/data/2.5/weather?q=st.+johns&units=metric", WeatherDataParser.CURRENT_WEATHER).get();
            foreWeatherTask.execute("http://api.openweathermap.org/data/2.5/forecast?q=st.+johns&units=metric&cnt=4", WeatherDataParser.HOURLY_WEATHER).get();
            weatherTask.execute("http://api.openweathermap.org/data/2.5/forecast/daily?q=st.+johns&units=metric&cnt=16", WeatherDataParser.DAILY_WEATHER).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        CurrentWeather.CURRENT = WeatherDataParser.getCurWeatherFromDB(this);
        CurrentWeather.HOURLY_FORECAST = WeatherDataParser.getHourlyWeatherFromDB(this);
    }

    class SwipeTabAdapter extends FragmentStatePagerAdapter {
        private final int amountTabs = 2;

        public SwipeTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            if (position == 0) {
                fragment = new FragmentTodayForecast();
            } else if (position == 1) {
                fragment = new FragmentDailyForecast();
            } else {
                fragment = null;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return amountTabs;
        }
    }

}
