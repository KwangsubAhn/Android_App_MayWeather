package barogo.mayweather.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncServiceWeatherCurrent extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static SyncAdapterCurrent sSyncAdapterCurrent = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapterCurrent == null) {
                sSyncAdapterCurrent = new SyncAdapterCurrent(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapterCurrent.getSyncAdapterBinder();
    }

}
