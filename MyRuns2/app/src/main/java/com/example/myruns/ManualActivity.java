package com.example.myruns;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/** MYRUNS2: MainActivity allows scrolling between two pages, the start and history fragments
 */

public class ManualActivity extends AppCompatActivity {
    // Constants for switch case onclick listener
    private static final int DATE = 1;
    private static final int TIME = 2;
    private static final int DURATION = 3;
    private static final int DISTANCE = 4;
    private static final int CALORIES = 5;
    private static final int HEARTBEAT = 6;
    private static final int COMMENT = 7;

    // Declaring of the listview of the manual activity, it's adapter and arraylist
    private ListView mListView;
    public ArrayList<ManualEntryStructure> mItems;
    private ManualEntryAdapter mManualInputAdapter;

    // Calendar for the Date and Time
    private Calendar cal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        //Set up Calendar
        cal = Calendar.getInstance();

        // set up the list view
        mListView = findViewById(R.id.listView);

        // If this is not the first instance, load the data
        if (savedInstanceState != null){
            mItems = savedInstanceState.getParcelableArrayList("datalist");

        } else {
            mItems = new ArrayList<ManualEntryStructure>();
        }

        // Custom adapter
        mManualInputAdapter = new ManualEntryAdapter(this, R.layout.manual_entry_structure, mItems);
        //mManualInputAdapter.clear();

        // Bind the adapter to the listView and initialize the fields
        mListView.setAdapter(mManualInputAdapter);

        if (savedInstanceState == null) {initializeFields();}

        // Create an onItemClickListener for the list view: Date, time, duration, distance...
        AdapterView.OnItemClickListener mItemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case DATE:
                        manualDate(mListView);
                        break;
                    case TIME:
                        manualTime(mListView);
                        break;
                    case DURATION:
                        manualDuration();
                        break;
                    case DISTANCE:
                        manualDistance();
                        break;
                    case CALORIES:
                        manualCalories();
                        break;
                    case HEARTBEAT:
                        manualHeartbeat();
                        break;
                    case COMMENT:
                        manualComments();
                        break;
                }
            }
        };

        // bind the view to the onClickListeners
        mListView.setOnItemClickListener(mItemListener);


    }

    /* Save the array of objects in saved instances */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("datalist", mItems);
    }

    /**
     * Initializes fields before the user decides to c h a n g e it u p.
     */
    public void initializeFields() {
        mItems.add(new ManualEntryStructure("Activity", getIntent().getStringExtra(StartFragment.ACTIVITY_TYPE)));
        mItems.add(new ManualEntryStructure("Date", "today"));
        mItems.add(new ManualEntryStructure("Time", "now"));
        mItems.add(new ManualEntryStructure("Duration", "0 mins"));
        mItems.add(new ManualEntryStructure("Distance", "0 kms"));
        mItems.add(new ManualEntryStructure("Calories", "0 calories"));
        mItems.add(new ManualEntryStructure("Heartbeat", "0 bpm"));
        mItems.add(new ManualEntryStructure("Comment", ""));
    }

    /** Methods to help edit fields! **/

    /**
     * Manual Date editing
     * @param v
     */
    public void manualDate(View v) {

        // Creates a Date Picker Dialog, adapted and inspired by Campbell.

        DatePickerDialog.OnDateSetListener activityDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
                mItems.get(1).setData(sdf.format(cal.getTime()));
                mManualInputAdapter.notifyDataSetChanged();
            }
        };
        new DatePickerDialog(ManualActivity.this, activityDatePicker, cal
                .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * Manual Time editing
     * @param v
     */
    public void manualTime(View v) {

        // Creates a Time Picker Dialog, adapted from Campbell

        TimePickerDialog activityTimePicker = new TimePickerDialog(ManualActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mItems.get(2).setData(hourOfDay + ":" + minute);
                mManualInputAdapter.notifyDataSetChanged();
            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        activityTimePicker.setTitle("Time Started");
        activityTimePicker.show();
    }

    /**
     *  Changes the duration manually with a MyRunsDialogFragment
     */
    public void manualDuration() {
        MyRunsDialogFragment durationFragment = MyRunsDialogFragment.newInstance(MyRunsDialogFragment.TYPE_DURATION);
        durationFragment.show(getSupportFragmentManager(), getString(R.string.duration));
    }

    /**
     *  Changes the distance manually with a MyRunsDialogFragment
     */
    public void manualDistance() {
        MyRunsDialogFragment distanceFragment = MyRunsDialogFragment.newInstance(MyRunsDialogFragment.TYPE_DISTANCE);
        distanceFragment.show(getSupportFragmentManager(), getString(R.string.distance));
    }

    /**
     *  Changes the calories manually with a MyRunsDialogFragment
     */
    public void manualCalories() {
        MyRunsDialogFragment caloriesFragment = MyRunsDialogFragment.newInstance(MyRunsDialogFragment.TYPE_CALORIES);
        caloriesFragment.show(getSupportFragmentManager(), getString(R.string.calories));
    }

    /**
     *  Changes the heartbeat manually with a MyRunsDialogFragment. <3 I l o v e working with M a r i a
     */
    public void manualHeartbeat() {
        MyRunsDialogFragment heartbeatFragment = MyRunsDialogFragment.newInstance(MyRunsDialogFragment.TYPE_HEARTBEAT);
        heartbeatFragment.show(getSupportFragmentManager(), getString(R.string.heartbeat));
    }

    /**
     *  Changes the comments manually with a MyRunsDialogFragment
     */
    public void manualComments() {
        MyRunsDialogFragment commentsFragment = MyRunsDialogFragment.newInstance(MyRunsDialogFragment.TYPE_COMMENT);
        commentsFragment.show(getSupportFragmentManager(), getString(R.string.comment));
    }

}