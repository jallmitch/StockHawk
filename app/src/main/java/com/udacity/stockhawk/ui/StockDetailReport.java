package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.udacity.stockhawk.R;

import java.util.ArrayList;
import java.util.List;

public class StockDetailReport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail_report);
        StockDetailFragment fragment = new StockDetailFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_stock_detail_report, fragment)
                .commit();

    }


    public static class StockDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
    {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);

            Bundle arguments = getArguments();
            String stockSymbol;
            if (arguments != null)
                stockSymbol = arguments.getString("STOCK_SYMBOL", "");



            View rootView = inflater.inflate(R.layout.activity_stock_detail_report, container, false);

            List<BarEntry> barEntries = new ArrayList<>();
            barEntries.add(new BarEntry(63.64f, 0));
            barEntries.add(new BarEntry(63.68f, 1));
            barEntries.add(new BarEntry(65.78f, 2));
            barEntries.add(new BarEntry(62.74f, 3));
            barEntries.add(new BarEntry(62.70f, 4));
            barEntries.add(new BarEntry(62.84f, 5));
            barEntries.add(new BarEntry(62.14f, 6));
            barEntries.add(new BarEntry(63.24f, 7));
            barEntries.add(new BarEntry(62.3f, 8));
            barEntries.add(new BarEntry(61.97f, 9));
            barEntries.add(new BarEntry(59.25f, 10));
            barEntries.add(new BarEntry(60.53f, 11));
            barEntries.add(new BarEntry(60.53f, 12));

            ArrayList<String> labels = new ArrayList<>();
            labels.add("2/6/17");
            labels.add("1/30/17");
            labels.add("1/23/17");
            labels.add("1/17/17");
            labels.add("1/9/17");
            labels.add("12/27/16");
            labels.add("12/19/16");
            labels.add("12/12/16");
            labels.add("12/5/16");
            labels.add("11/28/16");
            labels.add("11/21/16");
            labels.add("11/14/16");
            labels.add("11/7/16");

            BarDataSet barSet = new BarDataSet(barEntries, " Stock Entries");
            BarData barData = new BarData(barSet);
            barData.setBarWidth(.9f);

            BarChart chart =  (BarChart)rootView.findViewById(R.id.stock_chart);
            chart.setData(barData);
            chart.animate();

            return rootView;
        }

        private Cursor getHistoricalData(String symbol)
        {

            return null;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {


            return new CursorLoader(getActivity(), "Uri", "Projection", "Selection", "Selection Args", "sortOrder");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}
