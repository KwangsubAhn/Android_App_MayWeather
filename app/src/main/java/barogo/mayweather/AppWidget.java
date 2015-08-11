package barogo.mayweather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import barogo.mayweather.data.CurrentWeatherVo;
import barogo.mayweather.sync.SyncAdapterCurrent;


/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {
    CurrentWeatherVo weatherVo = null;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();
        if (action.equals("WIDGET")) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                ComponentName name = new ComponentName(context, AppWidget.class);
                int [] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(name);
                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    weatherVo = intent.getParcelableExtra("CURRENT");
                    this.onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
                }
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        LocalBroadcastManager.getInstance(context).registerReceiver((this),
                new IntentFilter("WIDGET"));

        weatherVo = SyncAdapterCurrent.weatherVoCurrent;

        ComponentName name = new ComponentName(context, AppWidget.class);
        int [] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(name);
        if (appWidgetIds != null && appWidgetIds.length > 0) {
            this.onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
        }
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        context.unregisterReceiver(this);
    }

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        if (weatherVo != null) {
            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
            views.setImageViewResource(R.id.img_widget, Utility.findWeatherConditionImg(weatherVo.icon, weatherVo.rain, false));

            double tempCur = Utility.getTemp(context, weatherVo.temp);
            views.setTextViewText(R.id.text_widget, "" + Math.round(tempCur) + (char) 0x00B0);

            Intent openApp = new Intent(context, MainActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, openApp, 0);

            views.setOnClickPendingIntent(R.id.layout_widget, pIntent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }
}

