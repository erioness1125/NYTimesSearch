package com.codepath.nytimessearch.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.codepath.nytimessearch.R;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsDialogFragment extends DialogFragment implements DatePickerFragment.OnSetBeginDateListener {

    @Bind(R.id.etBeginDateValue) EditText etBeginDateValue;
    @Bind(R.id.spnrNewsDeskValue) Spinner spnrNewsDeskValue;
    @Bind(R.id.spnrSortByValue) Spinner spnrSortByValue;
    @Bind(R.id.tvDone) TextView tvDone;

    private String beginDate;
    private String newsDesk;
    private String sort;

    private static final int SETTINGS_REQUEST_CODE = 10;

    @Override
    public void onSetBeginDate(String beginDate) {
        etBeginDateValue.setText(beginDate);
        this.beginDate = beginDate;
    }

    public interface SettingsDialogListener {
        void onDone (String beginDate, String newsDesk, String sort);
    }

    public SettingsDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static SettingsDialogFragment newInstance() {
        SettingsDialogFragment settingsDialogFragment = new SettingsDialogFragment();
        settingsDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return settingsDialogFragment;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public void setNewsDesk(String newsDesk) {
        this.newsDesk = newsDesk;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_edit_settings, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set spinners
        /****************************** spnrNewsDeskValue ******************************/
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> newsDeskAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.news_desk_values, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        newsDeskAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spnrNewsDeskValue.setAdapter(newsDeskAdapter);
        spnrNewsDeskValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemStr = parent.getItemAtPosition(position).toString();
                if (selectedItemStr.equals(getString(R.string.none)))
                    newsDesk = "";
                else
                    newsDesk = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                newsDesk = "";
            }
        });
        // set the persisted value if available
        String[] arr = getResources().getStringArray(R.array.news_desk_values);
        ArrayList<String> al = new ArrayList<>(Arrays.asList(arr));
        int pos = al.indexOf(newsDesk);
        if (pos != -1)
            spnrNewsDeskValue.setSelection(pos);
        /****************************** end of spnrNewsDeskValue ******************************/

        /****************************** spnrSortByValue ******************************/
        ArrayAdapter<CharSequence> sortByAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.sort_by_values, android.R.layout.simple_spinner_item);
        sortByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrSortByValue.setAdapter(sortByAdapter);
        spnrSortByValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemStr = parent.getItemAtPosition(position).toString();
                if (selectedItemStr.equals(getString(R.string.none)))
                    sort = "";
                else
                    sort = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sort = "";
            }
        });
        // set the persisted value if available
        arr = getResources().getStringArray(R.array.sort_by_values);
        al = new ArrayList<>(Arrays.asList(arr));
        pos = al.indexOf(sort);
        if (pos != -1)
            spnrSortByValue.setSelection(pos);
        /****************************** end of spnrSortByValue ******************************/
    }

    @OnClick(R.id.tvDone)
    void onDoneAction() {
        // Return input text to activity
        SettingsDialogListener listener = (SettingsDialogListener) getActivity();
        listener.onDone(beginDate, newsDesk, sort);
        dismiss();
    }

    @OnClick(R.id.etBeginDateValue)
    void onSelectBeginDate() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setTargetFragment(this, SETTINGS_REQUEST_CODE);
        datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }
}
