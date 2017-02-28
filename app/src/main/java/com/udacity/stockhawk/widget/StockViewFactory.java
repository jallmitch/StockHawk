package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.github.mikephil.charting.charts.LineChart;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by jesse.mitchell on 2/24/2017.
 */

public class StockViewFactory implements RemoteViewsService.RemoteViewsFactory
{

    private Context context;
    private int appWidgetId;
    private ArrayList<WidgetStockQuote> stockQuotes;

    public StockViewFactory(Context context, Intent intent)
    {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                         AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        Cursor cursor = context.getContentResolver().query(Contract.Quote.URI, null, null, null, null);
        stockQuotes = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        Calendar cal = Calendar.getInstance();
        String date = sdf.format(cal.getTime());

        while(cursor.moveToNext())
        {
            WidgetStockQuote quote = new WidgetStockQuote();
            quote.stockSymbol = cursor.getString(Contract.Quote.POSITION_SYMBOL);
            quote.price = cursor.getFloat(Contract.Quote.POSITION_PRICE);
            quote.prevClose = cursor.getFloat(Contract.Quote.POSITION_PREVIOUS_CLOSE);
            quote.openPrice = cursor.getFloat(Contract.Quote.POSITION_OPEN);
            quote.highPrice = cursor.getFloat(Contract.Quote.POSITION_HIGH);
            quote.lowPrice = cursor.getFloat(Contract.Quote.POSITION_LOW);
            quote.askPrice = cursor.getFloat(Contract.Quote.POSITION_ASK);
            quote.askSize = cursor.getLong(Contract.Quote.POSITION_ASK_SIZE);
            quote.bidPrice = cursor.getFloat(Contract.Quote.POSITION_BID);
            quote.bidSize = cursor.getLong(Contract.Quote.POSITION_BID_SIZE);
            quote.date = date;

            stockQuotes.add(quote);
        }
        cursor.close();
    }

    @Override
    public RemoteViews getViewAt(int postion)
    {
        WidgetStockQuote stockQuote = stockQuotes.get(postion);
        RemoteViews stockRow = new RemoteViews(context.getPackageName(),
                                               R.layout.widget_list_quote);

        Intent intent = new Intent();
        Bundle extras = new Bundle();

        LineChart chart = new LineChart(context);
        chart.measure(View.MeasureSpec.makeMeasureSpec(300,View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(500,View.MeasureSpec.EXACTLY));
        chart.layout(0, 0, chart.getMeasuredWidth(), chart.getMeasuredHeight());

        extras.putString(StockQuoteWidgetProvider.STOCK_QUOTE, stockQuote.stockSymbol);
        intent.putExtras(extras);

        stockRow.setTextViewText(R.id.stock_symbol, stockQuote.stockSymbol);
        stockRow.setTextViewText(R.id.stock_date, stockQuote.date);
        stockRow.setTextViewText(R.id.stock_price, stockQuote.price.toString());
        stockRow.setTextViewText(R.id.stock_prev_close, stockQuote.prevClose.toString());
        stockRow.setTextViewText(R.id.stock_open, stockQuote.openPrice.toString());
        stockRow.setTextViewText(R.id.stock_day_high, stockQuote.highPrice.toString());
        stockRow.setTextViewText(R.id.stock_day_low, stockQuote.lowPrice.toString());
        stockRow.setTextViewText(R.id.stock_ask, "Ask(" + stockQuote.askSize + "):" + stockQuote.askPrice.toString() );
        stockRow.setTextViewText(R.id.stock_bid, "Bid(" + stockQuote.bidSize + "):" + stockQuote.bidPrice.toString() );

        stockRow.setImageViewBitmap(R.id.widget_line_chart, chart.getChartBitmap());
        return stockRow;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public void onDataSetChanged() {
        Cursor cursor = context.getContentResolver().query(Contract.Quote.URI, null, null, null, null);
        stockQuotes = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        Calendar cal = Calendar.getInstance();
        String date = sdf.format(cal.getTime());

        while(cursor.moveToNext())
        {
            WidgetStockQuote quote = new WidgetStockQuote();
            quote.stockSymbol = cursor.getString(Contract.Quote.POSITION_SYMBOL);
            quote.price = cursor.getFloat(Contract.Quote.POSITION_PRICE);
            quote.prevClose = cursor.getFloat(Contract.Quote.POSITION_PREVIOUS_CLOSE);
            quote.openPrice = cursor.getFloat(Contract.Quote.POSITION_OPEN);
            quote.highPrice = cursor.getFloat(Contract.Quote.POSITION_HIGH);
            quote.lowPrice = cursor.getFloat(Contract.Quote.POSITION_LOW);
            quote.askPrice = cursor.getFloat(Contract.Quote.POSITION_ASK);
            quote.askSize = cursor.getLong(Contract.Quote.POSITION_ASK_SIZE);
            quote.bidPrice = cursor.getFloat(Contract.Quote.POSITION_BID);
            quote.bidSize = cursor.getLong(Contract.Quote.POSITION_BID_SIZE);
            quote.date = date;

            stockQuotes.add(quote);
        }
        cursor.close();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onDestroy() {

    }

}
