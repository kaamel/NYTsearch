package com.kaamel.nytimessearch;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaamel on 9/22/17.
 */

public class NewsDeskArrayAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] newsDeskList;
    private NewsDeskArrayAdapter adapter;
    private boolean[] checked;
    private boolean isFromView = false;

    public NewsDeskArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull String[] newsDeskList, boolean[] checked) {
        super(context, resource, newsDeskList);

        this.context = context;
        this.newsDeskList = newsDeskList;
        if (checked == null)
         this.checked = new boolean[newsDeskList.length];
        else
            this.checked = checked;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView,
                              ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(context);
            convertView = layoutInflator.inflate(R.layout.spinner_news_desk_item, null);
            holder = new ViewHolder();
            holder.checkBox = (CheckBox) convertView
                    .findViewById(R.id.cbNewsDeskItemSel);
            holder.title = convertView.findViewById(R.id.tvNewsDeskSelections);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // To check weather checked event fire from getview() or user input
        isFromView = true;
        holder.checkBox.setChecked(checked[position]);
        isFromView = false;
        holder.checkBox.setText(newsDeskList[position]);
        holder.checkBox.setTag(position);
        holder.title.setText(newsDeskList[position]);

        if ((position == 0)) {
            holder.checkBox.setVisibility(View.INVISIBLE);
            holder.title.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.title.setVisibility(View.INVISIBLE);
        }
        holder.checkBox.setTag(position);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();
                if (!isFromView)
                    checked[getPosition] = isChecked;
            }
        });
        return convertView;
    }

    private class ViewHolder {
        private CheckBox checkBox;
        private TextView title;
    }

    public String[] getNewsDesks() {
        List<String> newsDesks = new ArrayList<>();
        int count = newsDeskList.length-1;
        for (int i=1; i< count; i++) {
            if (checked[i])
                newsDesks.add(newsDeskList[i]);
        }
        String[] strings = new String[newsDesks.size()];
        return newsDesks.toArray(strings);
    }

    public boolean[] getCheckboxes() {
        return checked;
    }
}
