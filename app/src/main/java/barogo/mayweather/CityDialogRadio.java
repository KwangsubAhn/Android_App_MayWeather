package barogo.mayweather;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

import barogo.mayweather.data.LocationVo;
import barogo.mayweather.data.WeatherContract;

public class CityDialogRadio extends DialogFragment {

    ArrayList<LocationVo> listCities;

    AlertPositiveListener alertPositiveListener;

    interface AlertPositiveListener {
        public void onPositiveClick(LocationVo item);
    }

    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        try{
            alertPositiveListener = (AlertPositiveListener) activity;
        }catch(ClassCastException e){
            // The hosting activity does not implemented the interface AlertPositiveListener
            throw new ClassCastException(activity.toString() + " must implement AlertPositiveListener");
        }
    }

    OnClickListener positiveListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AlertDialog alert = (AlertDialog)dialog;
            int position = alert.getListView().getCheckedItemPosition();
            LocationVo vo = listCities.get(position);
            alertPositiveListener.onPositiveClick(vo);
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.setCancelable(false);

        Bundle bundle = getArguments();
        int position = bundle.getInt("position");
        String strTypedName = bundle.getString("typed_name");
        listCities = findCities(strTypedName);
        if (listCities.size()==0) {
            this.dismiss();
            Toast.makeText(getActivity(), "NO CITY HAS BEEN FOUND", Toast.LENGTH_LONG).show();
        } else if (listCities.size() > 100) {
            this.dismiss();
            Toast.makeText(getActivity(), "Too many cities found. Please type more specifically", Toast.LENGTH_LONG).show();
        }
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());

        b.setTitle("Choose your city");

        String[] code = new String[listCities.size()];
        for (int i=0; i<code.length; i++) {
            LocationVo vo = listCities.get(i);
            String lat = "";
            if (vo.coord_lat < 0) {
                lat = "" + (int)Math.abs(vo.coord_lat) + "S";
            } else {
                lat = "" + (int)vo.coord_lat + "N";
            }
            String lon = "";
            if (vo.coord_long < 0) {
                lon = "" + (int)Math.abs(vo.coord_long) + "W";
            } else {
                lon = "" + (int)vo.coord_long + "E";
            }

            code[i] = vo.city_name + ", " + vo.country_code + " (" + lat + " " + lon + ")";
        }

        b.setSingleChoiceItems(code, position, null);
        b.setPositiveButton("OK", positiveListener);
//        b.setNegativeButton("Cancel", null);
        AlertDialog d = b.create();

        return d;
    }

    private ArrayList<LocationVo> findCities(String name) {

        ArrayList<LocationVo> cities = new ArrayList<LocationVo>();

        Cursor cursor = getActivity().getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                null,
                WeatherContract.LocationEntry.COLUMN_CITY_NAME + " COLLATE NOCASE like '%" + name + "%'",
                null,
                WeatherContract.LocationEntry.COLUMN_COUNTRY_CODE + " ASC"
        );

        if (cursor.moveToFirst()){
            do{
                StringBuffer sb = new StringBuffer();
                LocationVo vo = new LocationVo();

                for (int i=0; i<cursor.getColumnCount(); i++) {
                    vo._id = cursor.getInt(0);
                    vo.location_setting = cursor.getString(1);
                    vo.city_name = cursor.getString(2);
                    vo.coord_lat = cursor.getDouble(3);
                    vo.coord_long = cursor.getDouble(4);
                    vo.country_code = cursor.getString(5);
                    vo.time_zone = cursor.getString(6);
                }

                cities.add(vo);
                // do what ever you want here
            }while(cursor.moveToNext());
        }

        return cities;
    }
}
