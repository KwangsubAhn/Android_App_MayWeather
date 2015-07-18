package barogo.mayweather.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by user on 2015-07-18.
 */
public class WeatherAuthenticatorService extends Service {

    private WeatherAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new WeatherAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
