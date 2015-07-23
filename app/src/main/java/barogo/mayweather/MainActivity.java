package barogo.mayweather;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import barogo.mayweather.sync.SyncAdapterCurrent;


public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    public ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

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

        SyncAdapterCurrent.initializeSyncAdapter(this);
//        SyncAdapterHourly.initializeSyncAdapter(this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            SyncAdapterCurrent.flagHourly = "";
            SyncAdapterCurrent.flagDaily = "";
            recreate();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), 1);
//            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
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
