package barogo.mayweather.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by user on 2015-07-18.
 */
public class WeatherService extends IntentService {

    public static final String LOCATION_QUERY_EXTRA = "lqe";
    private final String LOG_TAG = WeatherService.class.getSimpleName();

    public WeatherService() {
        super("WeatherService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(LOG_TAG, "##WeatherService Test##");
    }

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent sendIntent = new Intent(context, WeatherService.class);
            sendIntent.putExtra(WeatherService.LOCATION_QUERY_EXTRA, intent.getStringExtra(WeatherService.LOCATION_QUERY_EXTRA));
            context.startService(sendIntent);

        }
    }
}
