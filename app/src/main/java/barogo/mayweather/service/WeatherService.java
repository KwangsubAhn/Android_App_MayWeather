package barogo.mayweather.service;

import android.app.IntentService;
import android.content.Intent;

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

    }
}
