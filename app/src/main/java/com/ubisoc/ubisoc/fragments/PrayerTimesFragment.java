package com.ubisoc.ubisoc.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubisoc.ubisoc.Day;
import com.ubisoc.ubisoc.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Displays times for the month
 */
public class PrayerTimesFragment extends UbisocFragment {

    class MonthTimesAdapter extends BaseAdapter {

        private String[] columnNames = {"Date", "Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha"};

        LayoutInflater inflater;

        public MonthTimesAdapter() {
            this.inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            //the number of day (+1 for header row) * 7 for columns (5 prayers, sunrise, date)
            return (callback.getMonth().size() + 1) * 7;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            TextView vi = new TextView(getContext());

            int column = i % 7;
            int row = (i - column) / 7;

            if (row == 0) {
                vi.setText(columnNames[column]);
            } else {
                Day d = callback.getMonth().get(row - 1);

                String text = String.valueOf(row);//The date
                if (column > 0)
                    text = d.prayerTimeForPrayer(column - 1);

                vi.setText(text);
            }

            vi.setGravity(Gravity.CENTER);
            //Add padding top and bottom. Left most column also has left padding, and rightmost has right padding
            if (column == 0)
                vi.setPadding(16, 12, 0, 12);
            else if (column == 7)
                vi.setPadding(0, 12, 0, 12);
            else
                vi.setPadding(0, 12, 16, 12);
            //Header row is bold
            vi.setTypeface(null, row == 0 ? Typeface.BOLD : Typeface.NORMAL);

            //Every other row is grey
            if (row % 2 == 0)
                vi.setBackgroundColor(Color.parseColor("#F1E2E2"));


            return vi;
        }
    }

    public PrayerTimesFragment() {
    }

    MonthTimesAdapter adapter;

    @BindView(R.id.prayerGrid)
    GridView monthList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.fragment_prayer_times, container, false);
        ButterKnife.bind(this, root);

        adapter = new MonthTimesAdapter();
        monthList.setAdapter(adapter);

        return root;
    }

    @Override
    public void notifyDatasetChanged() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }
}
