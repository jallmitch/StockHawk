package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by jesse.mitchell on 2/24/2017.
 * For everything to do with the RemoteViews
 * http://dharmangsoni.blogspot.com/2014/03/collection-widget-with-event-handling.html
 */

public class StockViewFactory implements RemoteViewsService.RemoteViewsFactory
{

    private Context context;
    private int appWidgetId;
    private ArrayList<WidgetStockQuote> stockQuotes;
    private DecimalFormat dollarFormat;

    public StockViewFactory(Context context, Intent intent)
    {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                         AppWidgetManager.INVALID_APPWIDGET_ID);

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
    }

    @Override
    public void onCreate() {
        initiateStockQuotes();
    }

    @Override
    public RemoteViews getViewAt(int position)
    {
        WidgetStockQuote stockQuote = stockQuotes.get(position);
        RemoteViews stockView = new RemoteViews(context.getPackageName(),
                                               R.layout.widget_list_quote);

        stockView.setTextViewText(R.id.stock_symbol, stockQuote.stockName);
        stockView.setTextViewText(R.id.stock_date, stockQuote.date);
        stockView.setTextViewText(R.id.stock_price, dollarFormat.format(stockQuote.price));
        stockView.setTextViewText(R.id.stock_prev_close, dollarFormat.format(stockQuote.prevClose));
        stockView.setTextViewText(R.id.stock_open, dollarFormat.format(stockQuote.openPrice));
        stockView.setTextViewText(R.id.stock_day_high, dollarFormat.format(stockQuote.highPrice));
        stockView.setTextViewText(R.id.stock_day_low, dollarFormat.format(stockQuote.lowPrice));
        stockView.setTextViewText(R.id.ask_size, "Ask(" + stockQuote.askSize + "):");
        stockView.setTextViewText(R.id.stock_ask,  dollarFormat.format(stockQuote.askPrice));
        stockView.setTextViewText(R.id.bid_size, "Bid(" + stockQuote.bidSize + "):");
        stockView.setTextViewText(R.id.stock_bid, dollarFormat.format(stockQuote.bidPrice));

        Bundle bundle = new Bundle();
        bundle.putString(StockQuoteWidgetProvider.EXTRA_STRING_ID, stockQuote.stockSymbol);

        Intent fillInIntent = new Intent();
        fillInIntent.setAction(StockQuoteWidgetProvider.STOCK_QUOTE);
        fillInIntent.putExtras(bundle);

        stockView.setOnClickFillInIntent(R.id.stock_item_widget, fillInIntent);

        return stockView;
    }

    private void initiateStockQuotes()
    {
        Cursor cursor = context.getContentResolver().query(Contract.Quote.URI, null, null, null, null);
        stockQuotes = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        Calendar cal = Calendar.getInstance();
        String date = sdf.format(cal.getTime());

        while(cursor.moveToNext())
        {
            WidgetStockQuote quote = new WidgetStockQuote();
            quote.stockName = cursor.getString(Contract.Quote.POSITION_NAME);
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
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public int getCount() {

        return stockQuotes.size();
    }

    @Override
    public void onDataSetChanged() {
        stockQuotes.clear();
        initiateStockQuotes();
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
        stockQuotes.clear();
    }

}
