package barogo.mayweather;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by user on 2015-07-17.
 */
public class FetchWeatherTask extends AsyncTask<Object, Void, Integer> {

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
    private Context mContext;

    public FetchWeatherTask (Context context){
        mContext = context;
    }

    @Override
    protected Integer doInBackground(Object... params) {

        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String forecastJsonStr = null;

        try {
            URL url = new URL((String)params[0]);

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

            int result = WeatherDataParser.getWeatherDataFromJsonSaveDB(mContext, forecastJsonStr, (int) params[1]);

//            if ((int) params[1] == WeatherDataParser.CURRENT_WEATHER) {
//                CurrentWeather.CURRENT = result;
//            } else if ((int) params[1] ==  WeatherDataParser.HOURLY_WEATHER) {
//                CurrentWeather.HOULY_FORECAST = result;
//            } else if ((int) params[1] ==  WeatherDataParser.DAILY_WEATHER) {
//                CurrentWeather.DAILY_FORECAST = result;
//            } else {
//                Log.e(LOG_TAG, "WeatherDate Type is incorrect");
//            }

            return result;

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            forecastJsonStr = null;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
            e.printStackTrace();
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
        }

        return null;
    }
}
