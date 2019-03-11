package io.github.hamzaikine.loginex;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {

    OnSelectedDate callback;

    public void setOnSelectedDateListener(Activity activity){
        callback = (OnSelectedDate) activity;
    }
    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        CalendarView simpleCalendarView = (CalendarView) view.findViewById(R.id.calendarView); // get the reference of CalendarView
// perform setOnDateChangeListener event on CalendarView
        simpleCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
             // add code here
                Toast.makeText(getContext(), (month+1) + "/" + dayOfMonth + "/" + year, Toast.LENGTH_LONG).show();
                LocalDate date = null;
                    date = LocalDate.of(year,month+1,dayOfMonth);

                callback.sendDate(date);
            }
        });

        return view;
    }

    public interface OnSelectedDate{
        public void sendDate(LocalDate date);
    }


}
