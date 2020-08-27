package com.example.myruns;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String ACTIVITY_TYPE = "activity";
    private int MANUAL_REQUEST = 1;
    private int GPS_REQUEST = 2;
    private int AUTOMATIC_REQUEST = 3;

    private static final String[] planets_array = {"Mercury, Venus"};
    private Spinner mActivitySpinner, mTypeSpinner;
    private Button mStartButton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartFragment newInstance(String param1, String param2) {
        StartFragment fragment = new StartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }


    private void spinnerHelper() {

        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.input_spinner_list, R.layout.spinner_item);
        adapter1.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mTypeSpinner.setAdapter(adapter1);

        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.input_activity_list, R.layout.spinner_item);
        adapter2.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mActivitySpinner.setAdapter(adapter2);

        mActivitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getChildAt(0) != null) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getChildAt(0) != null) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_start, container, false);
        mActivitySpinner = rootView.findViewById(R.id.activity_spinner);
        mTypeSpinner = rootView.findViewById(R.id.input_spinner);
        mStartButton = rootView.findViewById(R.id.start);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView textView = (TextView) mTypeSpinner.getSelectedView();
                String activity_type = textView.getText().toString();

                TextView textViewActivity = (TextView) mActivitySpinner.getSelectedView();
                String activity = textViewActivity.getText().toString();

                if (activity_type.equals("GPS")) {
                    Intent intent = new Intent(getContext(), GPSActivity.class);
                    startActivityForResult(intent, GPS_REQUEST);
                } else if (activity_type.equals("Manual Entry")) {
                    Intent intent = new Intent(getContext(), ManualActivity.class);
                    intent.putExtra(ACTIVITY_TYPE, activity);
                    startActivityForResult(intent, MANUAL_REQUEST);
                } else {
                    Intent intent = new Intent(getContext(), AutomaticActivity.class);
                    startActivityForResult(intent, AUTOMATIC_REQUEST);
                }
            }
        });
        spinnerHelper();

        return rootView;
    }

}
