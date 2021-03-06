package barogo.mayweather.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.appwidget.AppWidgetManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import barogo.mayweather.R;
import barogo.mayweather.Utility;
import barogo.mayweather.data.CurrentWeatherVo;
import barogo.mayweather.data.WeatherContract;

/**
 * Created by user on 2015-07-18.
 */
public class SyncAdapterCurrent extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = SyncAdapterCurrent.class.getSimpleName();

    public static final int SYNC_INTERVAL = 60*30;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public static CurrentWeatherVo weatherVoCurrent = null;

    public SyncAdapterCurrent(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        try {

            Log.e(LOG_TAG, "onPerformSync Called.");


            String unit = "metric";
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
            String location = settings.getString(getContext().getString(R.string.pref_location_key),
                    getContext().getString(R.string.pref_location_default));

            String timezone = settings.getString("TIME_ZONE", "UTC");
            String flagHourly = settings.getString("Flag_Hourly", "");
            String flagDaily = settings.getString("Flag_Daily", "");

            //get Current WeatherInfo from cloud
            final String CURRENT_BASE_URL = "http://api.openweathermap.org/data/2.5/weather?";
            final String FORECAST_HOURLY_URL = "http://api.openweathermap.org/data/2.5/forecast?";
            final String FORECAST_DAILY_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
            final String LOCATION_PARAM = "q";
            final String UNITS_PARAM = "units";
            final String DAYS_PARAM = "cnt";

            Uri builtUri = Uri.parse(CURRENT_BASE_URL).buildUpon()
                    .appendQueryParameter(LOCATION_PARAM, location)
                    .appendQueryParameter(UNITS_PARAM, unit)
                    .build();


            String strUrlCurrent = builtUri.toString();
            String data = getWeatherInfo(strUrlCurrent, WeatherContract.WEATHER_TYPE_CURRENT);

            int result = Utility.getWeatherDataFromJsonSaveDB(getContext(),
                        data, WeatherContract.WEATHER_TYPE_CURRENT);

            //sent Current WeatherInfo to UI
            weatherVoCurrent = Utility.getCurWeatherFromDB(getContext());
            Intent intentCurrent = new Intent("TODAY");
            intentCurrent.putExtra("CURRENT", weatherVoCurrent);
            LocalBroadcastManager.getInstance(this.getContext()).sendBroadcast(intentCurrent);

            ///
            ///
            Intent intentWidget = new Intent("WIDGET");
            intentWidget.putExtra("ICON", weatherVoCurrent.icon);
            intentWidget.putExtra("TEMP", weatherVoCurrent.temp);
            intentWidget.putExtra("RAIN", weatherVoCurrent.rain);
                    getContext().sendBroadcast(intentWidget);
//            LocalBroadcastManager.getInstance(this.getContext()).sendBroadcast(intentWidget);
            //

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));
            Date date = calendar.getTime();

            int hour = calendar.get(Calendar.HOUR);
            int day = calendar.get(Calendar.DATE);

            //get Hourly WeatherInfo from cloud (every 80min)
//            if (!flagHourly.equals(Integer.toString(hour))) {
            settings.getString("Flag_Hourly", Integer.toString(hour));

            builtUri = Uri.parse(FORECAST_HOURLY_URL).buildUpon()
                    .appendQueryParameter(LOCATION_PARAM, location)
                    .appendQueryParameter(UNITS_PARAM, unit)
                    .appendQueryParameter(DAYS_PARAM, "8")
                    .build();

            String strUrlHourly = builtUri.toString();
            data = getWeatherInfo(strUrlHourly, WeatherContract.WEATHER_TYPE_HOURLY);

            result = Utility.getWeatherDataFromJsonSaveDB(getContext(),
                    data, WeatherContract.WEATHER_TYPE_HOURLY);

            List<CurrentWeatherVo> weatherVoHourly = Utility.getHourlyWeatherFromDB(getContext());
            Intent intentHourly = new Intent("HOURLY");
            intentHourly.putExtra("HOURLY0", weatherVoHourly.get(0));
            intentHourly.putExtra("HOURLY1", weatherVoHourly.get(1));
            intentHourly.putExtra("HOURLY2", weatherVoHourly.get(2));
            intentHourly.putExtra("HOURLY3", weatherVoHourly.get(3));

            //Get today's max/min temp
            double[] dMaxMin = Utility.getMaxMin(weatherVoHourly);

            intentHourly.putExtra("MAX", dMaxMin[0]);
            intentHourly.putExtra("MIN", dMaxMin[1]);

            LocalBroadcastManager.getInstance(this.getContext()).sendBroadcast(intentHourly);
//          }

            //get Daily WeatherInfo from cloud (once a day)
            if (!flagDaily.equals(Integer.toString(day))){
                Log.e(LOG_TAG, "Daily - onPerformSync Called.");
                settings.getString("Flag_Daily", Integer.toString(day));

                builtUri = Uri.parse(FORECAST_DAILY_URL).buildUpon()
                        .appendQueryParameter(LOCATION_PARAM, location)
                        .appendQueryParameter(UNITS_PARAM, unit)
                        .appendQueryParameter(DAYS_PARAM, "16")
                        .build();

                String strUrlDaily = builtUri.toString();
                data = getWeatherInfo(strUrlDaily, WeatherContract.WEATHER_TYPE_DAILY);

                result = Utility.getWeatherDataFromJsonSaveDB(getContext(),
                        data, WeatherContract.WEATHER_TYPE_DAILY);

            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (Exception e) {
//            Log.e(LOG_TAG, e.getMessage());
        }

    }




    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }
    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        SyncAdapterCurrent.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    //getWeatherInfo from cloud and Save to DB
    public String getWeatherInfo(String strUrl, int type){

        //
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String forecastJsonStr = null;

        try {
            URL url = new URL(strUrl);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                forecastJsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                forecastJsonStr = null;
            }
            forecastJsonStr = buffer.toString();


        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
            forecastJsonStr = null;

        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch ( IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
            return forecastJsonStr;
        }
    }
}
