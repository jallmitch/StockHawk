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
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

        return inflater.inflate(R.layout.fragment_stock_detail, container, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String stock_symbol = args.getString(MainActivity.STOCK_QUOTE);
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

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
            Calendar cal = Calendar.getInstance();
            String date = sdf.format(cal.getTime());

            String symbol = data.getString(Contract.Quote.POSITION_SYMBOL);
            String mName = data.getString(Contract.Quote.POSITION_NAME);
            Float price = data.getFloat(Contract.Quote.POSITION_PRICE);
            Float prevClose = data.getFloat(Contract.Quote.POSITION_PREVIOUS_CLOSE);
            Float open = data.getFloat(Contract.Quote.POSITION_OPEN);
            Float dayHigh = data.getFloat(Contract.Quote.POSITION_HIGH);
            Float dayLow = data.getFloat(Contract.Quote.POSITION_LOW);
            Float askPrice = data.getFloat(Contract.Quote.POSITION_ASK);
            Long askSize = data.getLong(Contract.Quote.POSITION_ASK_SIZE);
            Float bidPrice = data.getFloat(Contract.Quote.POSITION_BID);
            Long bidSize = data.getLong(Contract.Quote.POSITION_BID_SIZE);
            Float absoluteChange = data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
            Float percentChange = data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

            List<CandleEntry> barEntriesCandleEntries = new ArrayList<>();

            Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");
            TextView dateTV = (TextView) getActivity().findViewById(R.id.stock_date);
            TextView stockTV = (TextView) getActivity().findViewById(R.id.stock_symbol);
            TextView priceTV = (TextView) getActivity().findViewById(R.id.stock_price);
            TextView prevCloseTV = (TextView) getActivity().findViewById(R.id.stock_prev_close);
            TextView openTV = (TextView) getActivity().findViewById(R.id.stock_open);
            TextView dayHighTV = (TextView) getActivity().findViewById(R.id.stock_day_high);
            TextView dayLowTV = (TextView) getActivity().findViewById(R.id.stock_day_low);
            TextView askPriceTV = (TextView) getActivity().findViewById(R.id.stock_ask);
            TextView bidPriceTV = (TextView) getActivity().findViewById(R.id.stock_bid);

            stockTV.setText(mName);
            stockTV.setTextSize(20f);
            stockTV.setTypeface(face);
            stockTV.setContentDescription(mName);

            dateTV.setText(date);
            dateTV.setTextSize(20f);

            String priceLabel = dollarFormat.format(price);

            if (absoluteChange > 0)
                dollarFormatWithPlus.setPositivePrefix("+$");
            else if (absoluteChange < 0)
                dollarFormatWithPlus.setPositivePrefix("-$");

            String absChange = dollarFormatWithPlus.format(absoluteChange);
            priceTV.setText(priceLabel + "  (" + absChange + ")");
            priceTV.setTextSize(24f);
            priceTV.setTypeface(face);
            priceTV.setContentDescription(getContext().getString(R.string.stock_list_current_price) + " " + priceLabel);

            if (absoluteChange > 0)
                priceTV.setTextColor(Color.GREEN);
            else
                priceTV.setTextColor(Color.RED) ;

            String closeLabel = dollarFormat.format(prevClose);
            prevCloseTV.setText(getContext().getString(R.string.stock_previous_close_label) + closeLabel);
            prevCloseTV.setTextSize(20f);
            prevCloseTV.setTypeface(face);
            prevCloseTV.setContentDescription("Previous Close" + " " + priceLabel);

            String openLabel = dollarFormat.format(open);
            openTV.setText(getContext().getString(R.string.stock_open_label) + openLabel);
            openTV.setTextSize(20f);
            openTV.setTypeface(face);
            openTV.setContentDescription("Open" + " " + openLabel);

            String highLabel = dollarFormat.format(dayHigh);
            dayHighTV.setText(getContext().getString(R.string.stock_high_label) + highLabel);
            dayHighTV.setTextSize(20f);
            dayHighTV.setTypeface(face);
            dayHighTV.setContentDescription("High" + " " + highLabel);

            String lowLabel = dollarFormat.format(dayLow);
            dayLowTV.setText(getContext().getString(R.string.stock_low_label) + lowLabel);
            dayLowTV.setTextSize(20f);
            dayLowTV.setTypeface(face);
            dayLowTV.setContentDescription("Low" + " " + lowLabel);

            String askLabel = dollarFormat.format(askPrice);
            String askSizeLabel = askSize.toString();
            askPriceTV.setText(getContext().getString(R.string.stock_ask_label) + askLabel + " X " + askSizeLabel);
            askPriceTV.setTextSize(20f);
            askPriceTV.setTypeface(face);
            askPriceTV.setContentDescription("Ask Price" + " " + askLabel + " by " + askSizeLabel);

            String bidLabel = dollarFormat.format(bidPrice);
            String bidSizeLabel = bidSize.toString();
            bidPriceTV.setText(getContext().getString(R.string.stock_bid_label) + bidLabel + " X " + bidSizeLabel);
            bidPriceTV.setTextSize(20f);
            bidPriceTV.setTypeface(face);
            bidPriceTV.setContentDescription("Bid Price" + " " + bidLabel + " by " + bidSizeLabel);

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
