package barogo.mayweather.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by user on 2015-07-19.
 */
public class SyncServiceWeatherHourly extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static SyncAdapterHourly sSyncAdapterHourly = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapterHourly == null) {
                sSyncAdapterHourly = new SyncAdapterHourly(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapterHourly.getSyncAdapterBinder();
    }

}
