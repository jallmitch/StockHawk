package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class StockDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private DecimalFormat dollarFormatWithPlus;
    private DecimalFormat dollarFormat;
    private DecimalFormat percentageFormat;

    public StockDetailFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(0,getArguments(),this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

        return inflater.inflate(R.layout.fragment_stock_detail, container, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String stock_symbol = args.getString("STOCK_SYMBOL");
        Uri uri = Contract.Quote.makeUriForStock(stock_symbol);
        return new CursorLoader(getActivity(),
                               uri,
                               null,
                               null,
                               new String[]{stock_symbol},
                               null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst())
        {
            String history = data.getString(Contract.Quote.POSITION_HISTORY);

            if (history.isEmpty())
                return;

            String symbol = data.getString(Contract.Quote.POSITION_SYMBOL);
            String mName = data.getString(Contract.Quote.POSITION_NAME);
            Float price = data.getFloat(Contract.Quote.POSITION_PRICE);
            Float absoluteChange = data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
            Float percentChange = data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

            List<CandleEntry> barEntriesCandleEntries = new ArrayList<>();

            Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");
            TextView stockTV = (TextView) getActivity().findViewById(R.id.stock_symbol_textview);
            TextView priceTV = (TextView) getActivity().findViewById(R.id.stock_price_textview);
            TextView absoluteTV = (TextView) getActivity().findViewById(R.id.stock_absolute_textview);
            TextView percentTV = (TextView) getActivity().findViewById(R.id.stock_percent_textview);

            stockTV.setText(mName);
            stockTV.setTypeface(face);
            stockTV.setContentDescription(mName);

            String priceLabel = dollarFormat.format(price);
            priceTV.setText(priceLabel);
            priceTV.setTypeface(face);
            priceTV.setContentDescription(getContext().getString(R.string.stock_list_current_price) + " " + priceLabel);

            String absChange = dollarFormatWithPlus.format(absoluteChange);
            absoluteTV.setText(absChange);
            absoluteTV.setTypeface(face);
            absoluteTV.setContentDescription(getContext().getString(R.string.stock_list_absolute_change) + " " + absChange);

            String percentageChange = percentageFormat.format(percentChange);
            percentTV.setText(percentageChange);
            percentTV.setTypeface(face);
            percentTV.setContentDescription(getContext().getString(R.string.stock_list_percentage_change) + " " + percentageChange);

            if (absoluteChange > 0)
                absoluteTV.setTextColor(Color.GREEN);
            else
                absoluteTV.setTextColor(Color.RED) ;

            if (percentChange > 0)
                percentTV.setTextColor(Color.GREEN);
            else
                percentTV.setTextColor(Color.RED) ;


            Map<Long, CandleEntry> quoteMap = new TreeMap<>(parseHistory(history));
            List<String> dates = new ArrayList<>();
            int dateOrder = 0;
            for(Map.Entry<Long, CandleEntry> entry : quoteMap.entrySet())
            {
                dates.add(dateOrder++,addDate(entry.getKey()));
                barEntriesCandleEntries.add(entry.getValue());
            }
            final List<String> dateLabels = new ArrayList<>(dates);

            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return dateLabels.get((int)value);
                }
            };

            CandleDataSet mCandleDataSet = new CandleDataSet(barEntriesCandleEntries, mName);
            mCandleDataSet.setIncreasingColor(Color.GREEN);
            mCandleDataSet.setDecreasingColor(Color.RED);
            mCandleDataSet.setShowCandleBar(true);
            mCandleDataSet.setShadowColor(Color.BLACK);

            CandleData mCandleData = new CandleData(mCandleDataSet);
            mCandleData.setValueTextSize(16f);
            CandleStickChart mCandleChart = (CandleStickChart) getActivity().findViewById(R.id.stock_chart);
            mCandleChart.setData(mCandleData);
            mCandleChart.setBackgroundColor(Color.WHITE);
            Description chartDescript = new Description();
            chartDescript.setEnabled(false);
            mCandleChart.setDescription(chartDescript);
            XAxis mAxis = mCandleChart.getXAxis();
            mAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            mAxis.setValueFormatter(formatter);
            mAxis.setGranularity(1f);
            mAxis.setTypeface(face);
            mCandleChart.invalidate();
        }
    }

    private String addDate(Long dateMils)
    {
        DateFormat formatter = new SimpleDateFormat("MMM dd, yy", Locale.US);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateMils);
        return formatter.format(cal.getTime());
    }

    public static Map<Long, CandleEntry> parseHistory(String history)
    {
        Map<Long, CandleEntry> quoteMap = new TreeMap<>();
        String[] historyVals = history.split(",");
        int quoteIndex = (historyVals.length/5)-1;
        for (int v = 0; v <= (historyVals.length-1); v++)
        {
            Long time = Long.parseLong(historyVals[v++]);
            CandleEntry entry =new CandleEntry(
                    quoteIndex--,
                    Float.parseFloat(historyVals[v++]),
                    Float.parseFloat(historyVals[v++]),
                    Float.parseFloat(historyVals[v++]),
                    Float.parseFloat(historyVals[v]));

            quoteMap.put(time, entry);
        }
        return quoteMap;
    }
}
