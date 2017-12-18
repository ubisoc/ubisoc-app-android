package com.ubisoc.ubisoc.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubisoc.ubisoc.Day;
import com.ubisoc.ubisoc.R;
import com.ubisoc.ubisoc.util.Util;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TodayFragment extends UbisocFragment {

    class TodayTimesAdapter extends BaseAdapter {

        private String[] prayers = {"Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha"};

        LayoutInflater inflater;

        public TodayTimesAdapter() {
            this.inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        private class ViewHolder {
            TextView name, prayerTime, jamaahTime;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View vi = view;
            if (vi == null || vi.getTag() == null) {
                vi = inflater.inflate(R.layout.today_list_adapter, null);
                ViewHolder holder = new ViewHolder();

                holder.name = (TextView) vi.findViewById(R.id.prayer);
                holder.prayerTime = (TextView) vi.findViewById(R.id.startTime);
                holder.jamaahTime = (TextView) vi.findViewById(R.id.jamaahTime);

                vi.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) vi.getTag();

            if(i == 0){
                //header
                holder.name.setText("Prayer");
                holder.prayerTime.setText("Start Time");
                holder.jamaahTime.setText("Jama'ah Time");

                holder.name.setTypeface(null, Typeface.BOLD);
                holder.prayerTime.setTypeface(null, Typeface.BOLD);
                holder.jamaahTime.setTypeface(null, Typeface.BOLD);

                return vi;
            }

            int index = i - 1;//Remove 1 for header

            holder.name.setTypeface(null, Typeface.NORMAL);
            holder.prayerTime.setTypeface(null, Typeface.NORMAL);
            holder.jamaahTime.setTypeface(null, Typeface.NORMAL);

            holder.name.setText(prayers[index]);
            if (today != null) {
                holder.prayerTime.setText(today.prayerTimeForPrayer(index));
                holder.jamaahTime.setText(today.jamaahTimeForPrayer(index));
            }

            return vi;
        }
    }

    public TodayFragment() {
    }

    @BindView(R.id.currentDayLabel)
    TextView currentDayLabel;

    @BindView(R.id.todayList)
    ListView todayList;

    TodayTimesAdapter adapter;

    Day today;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.fragment_today, container, false);
        ButterKnife.bind(this, root);

        adapter = new TodayTimesAdapter();
        todayList.setAdapter(adapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        if(currentDayLabel == null)
            return;
        //Set the date to the current date
        currentDayLabel.setText(Util.getHumanDate(new Date()));

        this.today = callback.getToday();
        if (this.adapter != null)
            this.adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyDatasetChanged() {
        refresh();
    }

}
