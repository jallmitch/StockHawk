package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StockDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    public StockDetailFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("StockDetailFragment", "On Create");
        getLoaderManager().initLoader(0,getArguments(),this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_stock_detail, container, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String stock_symbol = args.getString("STOCK_SYMBOL");
        Uri uri = Contract.Quote.makeUriForStock(stock_symbol);
        return new CursorLoader(getActivity(), uri, null, null,  new String[]{stock_symbol}, null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.moveToFirst())
        {
            List<CandleEntry> mBarEntriesCandleEntries = new ArrayList<>();

            String history = data.getString(Contract.Quote.POSITION_HISTORY);
            String symbol = data.getString(Contract.Quote.POSITION_SYMBOL);
            String mName = data.getString(Contract.Quote.POSITION_NAME);
            Float price = data.getFloat(Contract.Quote.POSITION_PRICE);
            Float absoluteChange = data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
            Float percentChange = data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

            TextView stockLabel = (TextView) getActivity().findViewById(R.id.stock_symbol_textview);
            stockLabel.setText(mName + " (" + symbol + ")");


            TextView priceLabel = (TextView) getActivity().findViewById(R.id.stock_price_textview);
            priceLabel.setText("Current Price $" + price);

            TextView absoluteLabel = (TextView) getActivity().findViewById(R.id.stock_absolute_textview);
            absoluteLabel.setText("Absolute Change: " + absoluteChange);

            TextView percentLabel = (TextView) getActivity().findViewById(R.id.stock_percent_textview);
            percentLabel.setText("Percentage Change:" + percentChange);


            Map<Long, CandleEntry> quoteMap = new TreeMap<>(PrefUtils.parseHistory(history));
            List<String> dates = new ArrayList<>();
            int dateOrder = 0;
            for(Map.Entry<Long, CandleEntry> entry : quoteMap.entrySet())
            {
                dates.add(dateOrder++,addDate(entry.getKey()));
                mBarEntriesCandleEntries.add(entry.getValue());
            }
            final List<String> dateLabels = new ArrayList<>(dates);

            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return dateLabels.get((int)value);
                }
            };

            CandleDataSet mCandleDataSet = new CandleDataSet(mBarEntriesCandleEntries, mName);
            mCandleDataSet.setIncreasingColor(Color.GREEN);
            mCandleDataSet.setDecreasingColor(Color.RED);
            mCandleDataSet.setShowCandleBar(true);
            mCandleDataSet.setBarSpace(10f);

            CandleData mCandleData = new CandleData(mCandleDataSet);
            mCandleData.setValueTextSize(16f);
            CandleStickChart mCandleChart = (CandleStickChart) getActivity().findViewById(R.id.stock_chart);
            mCandleChart.setData(mCandleData);
            mCandleChart.setBackgroundColor(Color.WHITE);
            XAxis mAxis = mCandleChart.getXAxis();
            mAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//            mAxis.setLabelRotationAngle(45f);
            mAxis.setValueFormatter(formatter);
            mCandleChart.invalidate();
        }
    }

    private String addDate(Long dateMils)
    {
        DateFormat formatter = new SimpleDateFormat("MMM dd, yy");

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateMils);
        return formatter.format(cal.getTime());
    }
}
