package xyz.maksimenko.smssearch;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by smaksimenko on 17.02.2016.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private int type;
    private MainActivity ma;

    public void setType(int type){
        this.type = type;
    }

    public void setMainActivity(MainActivity ma) {
        this.ma = ma;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        long time;
        if(type == 0){
            time = ma.getDateFrom();
            if(time == 0){
                Calendar temp = Calendar.getInstance();
                temp.set(Calendar.YEAR, temp.get(Calendar.YEAR) - 1);
                time = temp.getTimeInMillis();
            }
        } else {
            time = ma.getDateTo();
            if(time == Long.MAX_VALUE){
                Calendar temp = Calendar.getInstance();
                temp.set(Calendar.YEAR, temp.get(Calendar.YEAR) + 1);
                time = temp.getTimeInMillis();
            }
        }
        c.setTimeInMillis(time);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        if(type == 0) {
             ma.setDateFrom(c.getTimeInMillis());
        } else {
            ma.setDateTo(c.getTimeInMillis());
        }
    }
}
