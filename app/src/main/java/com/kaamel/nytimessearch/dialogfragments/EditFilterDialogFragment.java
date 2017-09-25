package com.kaamel.nytimessearch.dialogfragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.kaamel.nytimessearch.utils.Utils;
import com.kaamel.nytimessearch.R;
import com.kaamel.nytimessearch.activities.SearchActivity;
import com.kaamel.nytimessearch.adapters.NewsDeskArrayAdapter;
import com.kaamel.nytimessearch.data.NYTSearchFilter;

import java.util.Calendar;

import static com.kaamel.nytimessearch.data.NYTSearchFilter.FILTER;

/**
 * Created by kaamel on 9/21/17.
 */

public class EditFilterDialogFragment extends AppCompatDialogFragment implements DatePickerDialog.OnDateSetListener {
    // TODO: Rename parameter arguments, choose names that match

    private NYTSearchFilter filter;

    private EditText etBeginDate;

    public EditFilterDialogFragment() {
        // Required empty public constructor
    }

    public static EditFilterDialogFragment newInstance(NYTSearchFilter filter) {
        EditFilterDialogFragment fragment = new EditFilterDialogFragment();
        Bundle filterBundle = new Bundle();
        filterBundle.putBundle(FILTER, NYTSearchFilter.getBundle(filter));
        fragment.setArguments(filterBundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filter = NYTSearchFilter.getFilter(getArguments().getBundle(FILTER));
        }
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().setTitle("Filter Articles");
        View v = inflater.inflate(R.layout.dialog_edit_filter, container, false);

        final Spinner newsDesks = v.findViewById(R.id.spNewsDesk);
        final NewsDeskArrayAdapter newsDeskList = new NewsDeskArrayAdapter(getContext(), 0, getResources().getStringArray(R.array.news_desks_array), filter.getCheckboxes());
        newsDesks.setAdapter(newsDeskList);

        Button actionButton = v.findViewById(R.id.btnSave);
        Button cancelButton = v.findViewById(R.id.btnCancel);
        ImageButton ibClearDate = v.findViewById(R.id.ibClearDate);
        final Spinner spSortOrder = v.findViewById(R.id.spSortOrder);
        //ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.sort_order_array, R.layout.spinner_sort_order_item);
        //ArrayAdapter adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_sort_order_item, SearchFilter.SortOrder.getTitles());
        spSortOrder.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_sort_order_item, NYTSearchFilter.SortOrder.getTitles()));
        //spinner.setAdapter(adapter);

        if (filter.getSortorder() == NYTSearchFilter.SortOrder.NEWEST)
            spSortOrder.setSelection(1);
        else if (filter.getSortorder() == NYTSearchFilter.SortOrder.OLDEST)
            spSortOrder.setSelection(2);

        final Calendar c = Calendar.getInstance();

        actionButton.setOnClickListener(v1 -> {
            // When button is clicked, call up to owning activity.
            filter.setBeginDate(0);
            if (!etBeginDate.getText().toString().equals(""))
                filter.setBeginDate(Utils.dateToLong(etBeginDate.getText().toString()));
            filter.setCategories(newsDeskList.getNewsDesks());
            String name = (String) spSortOrder.getSelectedItem();
            if (name == null || name.equals("Articles Not Sorted"))
                filter.setSortOrder(NYTSearchFilter.SortOrder.NONE);
            else if (name.equals("Latest First"))
                filter.setSortOrder(NYTSearchFilter.SortOrder.NEWEST);
            else
                filter.setSortOrder(NYTSearchFilter.SortOrder.OLDEST);
            filter.setCheckboxes(newsDeskList.getCheckboxes());
            ((SearchActivity) getActivity()).filterUpdated(filter);
            dismiss();
        });

        cancelButton.setOnClickListener(view -> {
            Toast.makeText(getContext(), "cancelled ...", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        ibClearDate.setOnClickListener(view -> {
            etBeginDate.setText("");
        });

        etBeginDate = v.findViewById(R.id.etBeginTime);
        etBeginDate.setText(Utils.longToDateString(filter.getBeginDate()==0?Utils.getTodayLong():filter.getBeginDate()));

        long time = Utils.dateToLong(etBeginDate.getText().toString());
        int startYear = Utils.longToYear(time);
        int starthMonth = Utils.longToMonth(time);
        int startDay = Utils.longToDay(time);
        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), this, startYear, starthMonth-1, startDay);
        datePickerDialog.getDatePicker().setCalendarViewShown(true);
        datePickerDialog.getDatePicker().setSpinnersShown(false);

        etBeginDate.setOnFocusChangeListener((view, b) -> {
            if (b) {
                datePickerDialog.show();
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
        editText.postDelayed(() -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }, 500);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        etBeginDate.setText(Utils.longToDateString(calendar.getTime().getTime()));
        etBeginDate.clearFocus();
    }
}
