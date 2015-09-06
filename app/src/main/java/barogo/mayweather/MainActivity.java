package barogo.mayweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.actionbar_logo);


        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String location = settings.getString(this.getString(R.string.pref_location_key),
                this.getString(R.string.pref_location_default));
        setTitle(" " + location);

        boolean bFirst = settings.getBoolean("FirstLaunch", true);
        if (bFirst) {
            settings.edit().putBoolean("FirstLaunch", false).commit();
            settings.edit().putString("TIME_ZONE", "UTC");
            settings.edit().putString("Flag_Hourly", "");
            settings.edit().putString("Flag_Daily", "");
            deleteExistDB();
            moveFile();
        }

        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(new SwipeTabAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.d(LOG_TAG, "onPageScrolled at " + " position " + position + " from " +
//                positionOffset + " with number of pixels= " + positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
//                Log.d(LOG_TAG, "onPageSelected at " + " position " + position);
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
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        if (resultCode == RESULT_OK) {
            if (data.hasExtra(this.getString(R.string.pref_units_label))) {
                settings.edit().putString("Flag_Hourly", "");
                settings.edit().putString("Flag_Daily", "");
                this.finish();
                this.startActivity(this.getIntent());
//                recreate();
            } else if (data.hasExtra(this.getString(R.string.pref_location_label))) {
                settings.edit().putString("Flag_Hourly", "");
                settings.edit().putString("Flag_Daily", "");
                this.finish();
                this.startActivity(this.getIntent());
//                recreate();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {

            startActivityForResult(new Intent(this, SettingsActivity.class), 1);
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

    public void deleteExistDB() {
        String filePath = "/data/data/" + "barogo.mayweather" + "/databases/" + "mayweather.db";
        File file = new File(filePath);

        if (file.exists()) {
            file.delete();
        }
    }

    public void moveFile() {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            AssetManager am = this.getApplicationContext().getAssets();//u have get assets path from this ocde
            inputStream = am.open("mayweather.db");

            // write the inputStream to a FileOutputStream
            File file = new File("/data/data/" + "barogo.mayweather" + "/databases/" + "mayweather.db");
            file.getParentFile().mkdirs();
            file.createNewFile();
            boolean aa = file.exists();
            outputStream = new FileOutputStream(file);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            System.out.println("Done!");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

}
