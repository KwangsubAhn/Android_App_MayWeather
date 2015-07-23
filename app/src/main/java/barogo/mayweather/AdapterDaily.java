package barogo.mayweather;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import barogo.mayweather.data.WeatherContract.WeatherEntry;
/**
 * Created by user on 2015-07-17.
 */
public class AdapterDaily extends CursorAdapter {

    public AdapterDaily(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_daily_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.iconView.setImageResource(Utility.findWeatherConditionImg(
                cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_ICON))));

        String simpleDate = cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_DATE));

        String date = simpleDate.split(" ")[0];
        String dayOfMonth = date.split("-")[2];

        String dayOfWeekweek = Utility.getDayOfWeek(simpleDate);

        viewHolder.date.setText(Utility.getReadableDateString(simpleDate)[0]);
        viewHolder.day.setText(dayOfWeekweek);
        viewHolder.temp.setText("" + (int) Math.floor(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_TEMP_MIN))) + (char) 0x00B0 +
                " - " + (int) Math.ceil(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_TEMP_MAX))) + (char) 0x00B0);

        double rain = cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_RAIN));
        double snow = cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_SNOW));
        if (rain >= 0.5d) {
            rain = Math.round(rain);
            viewHolder.rainfall.setText(""+(int)rain+"mm");
        } else if (rain > 0d && rain < 0.5d) {
            viewHolder.rainfall.setText("<1" + "mm");
        } else {
            viewHolder.rainfall.setText("");
        }

        if (snow >= 0d && snow > rain) {
            if (snow >= 0.5d) {
                snow = Math.round(rain);
                viewHolder.rainfall.setText(""+(int)snow+"mm");
            } else if (snow > 0d && snow < 0.5d) {
                viewHolder.rainfall.setText("<1" + "mm");
            } else {
                viewHolder.rainfall.setText("");
            }
        }

    }

    public static class ViewHolder {
        public final TextView day;
        public final TextView date;
        public final ImageView iconView;
        public final TextView temp;
        public final TextView rainfall;

        public ViewHolder (View view) {
            day = (TextView) view.findViewById(R.id.weather_daily_day);
            date = (TextView) view.findViewById(R.id.weather_daily_date);
            iconView = (ImageView) view.findViewById(R.id.img_daily);
            temp = (TextView) view.findViewById(R.id.weather_daily_temp);
            rainfall = (TextView) view.findViewById(R.id.weather_daily_rainfall);
        }

    }

}
