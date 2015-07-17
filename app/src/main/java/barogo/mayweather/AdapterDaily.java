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

        viewHolder.iconView.setImageResource(WeatherDataParser.findWeatherConditionImg(
                cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_ICON))));

        String simpleDate = cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_DATE));

        String date = simpleDate.split(" ")[0];
        String dayOfMonth = date.split("-")[2];

        String dayOfWeekweek = WeatherDataParser.getDayOfWeek(simpleDate);

//        SpannableString ss1=  new SpannableString(s);
//        ss1.setSpan(new RelativeSizeSpan(1f), 0, s.length(), 0); // set size
//        SpannableString ss2=  new SpannableString(ss);
//        ss2.setSpan(new RelativeSizeSpan(1f), 0, ss.length(), 0); // set size

        viewHolder.date.setText(TextUtils.concat(dayOfWeekweek.substring(0, 3), " ", dayOfMonth));
        viewHolder.temp.setText(""+(int)Math.floor(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_TEMP_MIN))) + (char) 0x00B0 +
                " - " + (int)Math.ceil(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_TEMP_MAX))) + (char) 0x00B0);
        viewHolder.desc.setText(cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_DESC)));
    }

    public static class ViewHolder {
        public final TextView date;
        public final ImageView iconView;
        public final TextView temp;
        public final TextView desc;

        public ViewHolder (View view) {
            date = (TextView) view.findViewById(R.id.weather_daily_date);
            date.setLineSpacing(-10, 1);
            iconView = (ImageView) view.findViewById(R.id.img_daily);
            temp = (TextView) view.findViewById(R.id.weather_daily_temp);
            desc = (TextView) view.findViewById(R.id.weather_daily_desc);
        }

    }

}
