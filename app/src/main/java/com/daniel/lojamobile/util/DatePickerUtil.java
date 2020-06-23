package com.daniel.lojamobile.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerUtil extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private Listener listener;

    public static DatePickerUtil getInstance(){
        return new DatePickerUtil();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //TODO criar metodo para formatar data
        String date = year + " " + month + " " + day;

        if(listener != null){
            listener.onDatePicked(date);
        }
    }


    public interface Listener{
        void onDatePicked(String data);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}