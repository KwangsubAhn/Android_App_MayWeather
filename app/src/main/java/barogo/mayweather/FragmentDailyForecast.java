package barogo.mayweather;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import barogo.mayweather.data.WeatherContract;

/**
 * Created by user on 2015-07-17.
 */
public class FragmentDailyForecast extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = FragmentDailyForecast.class.getSimpleName();

    private static final int FORECAST_LOADER = 1;
    private AdapterDaily mDailyAdapter;//private ArrayAdapter<String> mForecastAdapterDaily;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDailyAdapter = new AdapterDaily(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_daily, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mDailyAdapter);
        return rootView;

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "on Pause Fragment");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                WeatherContract.WeatherEntry.COLUMN_TYPE + " = " + WeatherContract.WEATHER_TYPE_DAILY,
                null,
                WeatherContract.WeatherEntry.COLUMN_DATE + " DESC LIMIT 15");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String[] columns = data.getColumnNames();
        MatrixCursor matrixCursor= new MatrixCursor(columns);

        data.moveToLast();

        if (data.getCount() != 0) {
            do {
                Object[] row = new Object[columns.length];
                row[0] = data.getInt(0);
                row[1] = data.getInt(1);
                row[2] = data.getInt(2);
                row[3] = data.getString(3);
                row[4] = data.getString(4);
                row[5] = data.getString(5);
                row[6] = data.getString(6);
                row[7] = data.getDouble(7);
                row[8] = data.getInt(8);
                row[9] = data.getDouble(9);
                double tempMax = Utility.getTemp(getActivity(), data.getDouble(10));
                row[10] = tempMax;//data.getDouble(10);//max
                double tempMin = Utility.getTemp(getActivity(), data.getDouble(11));
                row[11] = tempMin;//data.getDouble(11);//min
                row[12] = data.getDouble(12);
                row[13] = data.getInt(13);
                row[14] = data.getInt(14);
                row[15] = data.getDouble(15);
                row[16] = data.getDouble(16);
                row[17] = data.getString(17);
                row[18] = data.getString(18);
                matrixCursor.addRow(row);
            } while (data.moveToPrevious());
        }



        mDailyAdapter.swapCursor(matrixCursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDailyAdapter.swapCursor(null);
    }
}
