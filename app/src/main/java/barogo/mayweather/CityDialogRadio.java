package barogo.mayweather;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import barogo.mayweather.data.WeatherDbHelper;

/**
 * Created by user on 2015-07-26.
 */
public class CityDialogRadio extends DialogFragment {

    /** Declaring the interface, to invoke a callback function in the implementing activity class */
    AlertPositiveListener alertPositiveListener;

    /** An interface to be implemented in the hosting activity for "OK" button click listener */
    interface AlertPositiveListener {
        public void onPositiveClick(int position);
    }

    /** This is a callback method executed when this fragment is attached to an activity.
     *  This function ensures that, the hosting activity implements the interface AlertPositiveListener
     * */
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        try{
            alertPositiveListener = (AlertPositiveListener) activity;
        }catch(ClassCastException e){
            // The hosting activity does not implemented the interface AlertPositiveListener
            throw new ClassCastException(activity.toString() + " must implement AlertPositiveListener");
        }
    }

    /** This is the OK button listener for the alert dialog,
     *  which in turn invokes the method onPositiveClick(position)
     *  of the hosting activity which is supposed to implement it
     */
    OnClickListener positiveListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AlertDialog alert = (AlertDialog)dialog;
            int position = alert.getListView().getCheckedItemPosition();
            alertPositiveListener.onPositiveClick(position);
        }
    };

    /** This is a callback method which will be executed
     *  on creating this fragment
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        /** Getting the arguments passed to this fragment */
        Bundle bundle = getArguments();
        int position = bundle.getInt("position");
        String strTypedName = bundle.getString("typed_name");
        findCities(strTypedName);

        /** Creating a builder for the alert dialog window */
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());

        /** Setting a title for the window */
        b.setTitle("Choose your version");

        /** Setting items to the alert dialog */
        String[] code = new String[]{
                "Jelly Bean", "Ice Cream Sandwich", "Honeycomb", "gingerbread"
        };

        b.setSingleChoiceItems(code, position, null);

        /** Setting a positive button and its listener */
        b.setPositiveButton("OK",positiveListener);

        /** Setting a positive button and its listener */
        b.setNegativeButton("Cancel", null);

        /** Creating the alert dialog window using the builder class */
        AlertDialog d = b.create();

        /** Return the alert dialog window */
        return d;
    }

    private void findCities(String name) {
        SQLiteDatabase db = new WeatherDbHelper(getActivity()).getReadableDatabase();
        Cursor c = db.rawQuery("select * from city_list where city_name like '%"
                +name+"%'", null);
        c.moveToFirst();
        int cnt = c.getCount();
        do {
            int id = c.getInt(0);   //id
            String cityName = c.getString(1);
            int lat = c.getInt(2);
            int lon = c.getInt(3);
            String code = c.getString(4);
        } while (c.moveToNext());

        Log.d("","");
    }

}
