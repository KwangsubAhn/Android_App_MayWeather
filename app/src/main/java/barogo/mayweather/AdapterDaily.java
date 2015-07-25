package barogo.mayweather;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

        String dayOfWeek = Utility.getDayOfWeek(simpleDate);

        viewHolder.date.setText(Utility.getReadableDateString(simpleDate)[0]);
//        viewHolder.day.setText(dayOfWeekweek);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final TextView todayView = (TextView)((Activity)context).getWindow().getDecorView().findViewById(R.id.weather_today_date);

        SpannableString spanDayOfWeek = new SpannableString(dayOfWeek);

        Date dtToday = Utility.getDate((""+todayView.getText()).split(", ")[1]);

        Calendar c = Calendar.getInstance();
        c.setTime(dtToday);
        c.add(Calendar.DATE, 1);

        Date dtAddOneDay = c.getTime();

        Date dtDateFromList = Utility.getDate(viewHolder.date.getText().toString());

        if (dtAddOneDay.compareTo(dtDateFromList) == 0) {   //if true, it's tomorrow
            spanDayOfWeek = new SpannableString("Tomorrow");
        }

        if(dayOfWeek.toUpperCase().equals("SUNDAY")) {
            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
            spanDayOfWeek.setSpan(boldSpan, 0, spanDayOfWeek.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanDayOfWeek.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spanDayOfWeek.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        viewHolder.day.setText(spanDayOfWeek);
//        viewHolder.temp.setText("" + (int) Math.floor(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_TEMP_MIN))) + (char) 0x00B0 +
//                " - " + (int) Math.ceil(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_TEMP_MAX))) + (char) 0x00B0);
        viewHolder.tempMin.setText("" + (int) Math.floor(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_TEMP_MIN))) + (char) 0x00B0);

        viewHolder.tempMax.setText("" + (int) Math.ceil(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_TEMP_MAX))) + (char) 0x00B0);

        double rain = cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_RAIN));
        double snow = cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_SNOW));
        if (rain >= 0.05d) {
            rain = Math.round(rain*10);
            rain = rain / 10.0d;
            viewHolder.rainfall.setText(""+rain+"mm");
        } else if (rain > 0d && rain < 0.05d) {
            viewHolder.rainfall.setText("<0.1" + "mm");
        } else {
            viewHolder.rainfall.setText("");
            if (cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_DESC)).contains("rain")) {viewHolder.rainfall.setText("<0.1" + "mm");}
        }

        if (snow >= 0d && snow > rain) {
            if (snow >= 0.05d) {
                snow = Math.round(snow*10);
                snow = snow / 10.0d;
                viewHolder.rainfall.setText(""+snow+"mm");
            } else if (snow > 0d && snow < 0.05d) {
                viewHolder.rainfall.setText("<0.1" + "mm");
            } else {
                viewHolder.rainfall.setText("");
                if (cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_DESC)).contains("snow")) {viewHolder.rainfall.setText("<0.1" + "mm");}
            }
        }

    }

    public static class ViewHolder {
        public final TextView day;
        public final TextView date;
        public final ImageView iconView;
        public final TextView tempMin;
        public final TextView tempMax;
        public final TextView rainfall;

        public ViewHolder (View view) {
            day = (TextView) view.findViewById(R.id.weather_daily_day);
            date = (TextView) view.findViewById(R.id.weather_daily_date);
            iconView = (ImageView) view.findViewById(R.id.img_daily);
            tempMin = (TextView) view.findViewById(R.id.weather_daily_temp);
            tempMax = (TextView) view.findViewById(R.id.weather_daily_temp2);
            rainfall = (TextView) view.findViewById(R.id.weather_daily_rainfall);
        }

    }

}
