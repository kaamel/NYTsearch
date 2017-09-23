package com.kaamel.nytimessearch;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.kaamel.Utils;

import java.util.Calendar;

import static com.kaamel.nytimessearch.SearchFilter.FILTER;

/**
 * Created by kaamel on 9/21/17.
 */

public class EditFilterDialogFragment extends AppCompatDialogFragment implements DatePickerDialog.OnDateSetListener {
    // TODO: Rename parameter arguments, choose names that match

    private SearchFilter filter;

    private EditText dueDateET;

    public EditFilterDialogFragment() {
        // Required empty public constructor
    }

    public static EditFilterDialogFragment newInstance(SearchFilter filter) {
        EditFilterDialogFragment fragment = new EditFilterDialogFragment();
        Bundle filterBundle = new Bundle();
        filterBundle.putBundle(FILTER, SearchFilter.getBundle(filter));
        fragment.setArguments(filterBundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filter = SearchFilter.getFilter(getArguments().getBundle(FILTER));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_edit_filter, container, false);

        final Spinner newsDesks = v.findViewById(R.id.spNewsDesk);
        final NewsDeskArrayAdapter newsDeskList = new NewsDeskArrayAdapter(getContext(), 0, getResources().getStringArray(R.array.news_desks_array), filter.getCheckboxes());
        newsDesks.setAdapter(newsDeskList);

        Button actionButton = v.findViewById(R.id.btnSave);
        Button cancelButton = v.findViewById(R.id.btnCancel);
        final Spinner spSortOrder = v.findViewById(R.id.spSortOrder);
        //ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.sort_order_array, R.layout.spinner_sort_order_item);
        //ArrayAdapter adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_sort_order_item, SearchFilter.SortOrder.getTitles());
        spSortOrder.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_sort_order_item, SearchFilter.SortOrder.getTitles()));
        //spinner.setAdapter(adapter);

        if (filter.sortOrder == SearchFilter.SortOrder.NEWEST)
            spSortOrder.setSelection(1);
        else if (filter.sortOrder == SearchFilter.SortOrder.OLDEST)
            spSortOrder.setSelection(2);

        final Calendar c = Calendar.getInstance();

        actionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                filter.beginDate = Utils.dateToLong(dueDateET.getText().toString());
                filter.newsDesk = newsDeskList.getNewsDesks();
                String name = (String) spSortOrder.getSelectedItem();
                if (name == null || name.equals("Articles Not Sorted"))
                    filter.sortOrder = SearchFilter.SortOrder.NONE;
                else if (name.equals("Latest First"))
                    filter.sortOrder = SearchFilter.SortOrder.NEWEST;
                else
                    filter.sortOrder = SearchFilter.SortOrder.OLDEST;
                filter.setCheckboxes(newsDeskList.getCheckboxes());
                ((SearchActivity) getActivity()).filterUpdated(filter);
                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "cancelled ...", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        dueDateET = v.findViewById(R.id.etBeginTime);
        dueDateET.setText(Utils.longToDateString(filter.beginDate==0?Utils.getTodayLong():filter.beginDate));

        long time = Utils.dateToLong(dueDateET.getText().toString());
        int startYear = Utils.longToYear(time);
        int starthMonth = Utils.longToMonth(time);
        int startDay = Utils.longToDay(time);
        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), this, startYear, starthMonth-1, startDay);
        datePickerDialog.getDatePicker().setCalendarViewShown(true);
        datePickerDialog.getDatePicker().setSpinnersShown(false);

        dueDateET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    datePickerDialog.show();
                }
            }
        });

        v.clearFocus();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        final Spinner editText = getView().findViewById(R.id.spNewsDesk);
        editText.requestFocus();
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 500);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        dueDateET.setText(Utils.longToDateString(calendar.getTime().getTime()));
        dueDateET.clearFocus();
    }
}
