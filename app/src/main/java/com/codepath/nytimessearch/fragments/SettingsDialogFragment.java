package com.codepath.nytimessearch.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.codepath.nytimessearch.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsDialogFragment extends DialogFragment implements DatePickerFragment.OnSetBeginDateListener {

    @Bind(R.id.cbArts) CheckBox cbArts;
    @Bind(R.id.cbFashionStyle) CheckBox cbFashionStyle;
    @Bind(R.id.cbSports) CheckBox cbSports;
    @Bind(R.id.etBeginDateValue) EditText etBeginDateValue;
    @Bind(R.id.spnrSortByValue) Spinner spnrSortByValue;

    private List<String> newsDeskList;
    private String sort;

    private static final int SETTINGS_REQUEST_CODE = 10;

    @Override
    public void onSetBeginDate(String beginDate) {
        etBeginDateValue.setText(beginDate);
    }

    public interface SettingsDialogListener {
        void onDone (String beginDate, List<String> newsDeskList, String sort);
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

    public void setNewsDesk(List<String> newsDeskList) {
        this.newsDeskList = newsDeskList;
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

        /****************************** init CheckBox ******************************/
        if (newsDeskList != null) {
            for (String s : newsDeskList) {
                if (s.equals(getString(R.string.arts)))
                    cbArts.setChecked(true);
                else if (s.equals(getString(R.string.fashion_style)))
                    cbFashionStyle.setChecked(true);
                else if (s.equals(getString(R.string.sports)))
                    cbSports.setChecked(true);
            }
        }
        /****************************** end of CheckBox ******************************/

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
        String[] arr = getResources().getStringArray(R.array.sort_by_values);
        List<String> al = new ArrayList<>(Arrays.asList(arr));
        int pos = al.indexOf(sort);
        if (pos != -1)
            spnrSortByValue.setSelection(pos);
        /****************************** end of spnrSortByValue ******************************/
    }

    @OnClick(R.id.tvDone)
    void onDoneAction() {
        // set newsDesk
        getCheckedNewsDesks();

        // Return input text to activity
        SettingsDialogListener listener = (SettingsDialogListener) getActivity();
        listener.onDone(etBeginDateValue.getText().toString(), newsDeskList, sort);
        dismiss();
    }

    @OnClick(R.id.etBeginDateValue)
    void onSelectBeginDate() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setTargetFragment(this, SETTINGS_REQUEST_CODE);
        datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private void getCheckedNewsDesks() {
        // example => news_desk:("Sports" "Foreign")
        newsDeskList = new ArrayList<>();

        if (cbArts.isChecked())
            newsDeskList.add(getString(R.string.arts));
        if (cbFashionStyle.isChecked())
            newsDeskList.add(getString(R.string.fashion_style));
        if (cbSports.isChecked())
            newsDeskList.add(getString(R.string.sports));
    }
}
