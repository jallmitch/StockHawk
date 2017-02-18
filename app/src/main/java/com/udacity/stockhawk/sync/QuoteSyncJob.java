package com.udacity.stockhawk.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.Toast;

import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;

import static android.os.Looper.getMainLooper;

public final class QuoteSyncJob {

    private static final int ONE_OFF_ID = 2;
    private static final String ACTION_DATA_UPDATED = "com.udacity.stockhawk.ACTION_DATA_UPDATED";
    private static final String ACTION_DATA_NOT_UPDATED = "com.udacity.stockhawk.ACTION_DATA_NOT_UPDATED";
    private static final int PERIOD = 300000;
    private static final int INITIAL_BACKOFF = 10000;
    private static final int PERIODIC_ID = 1;
    private static final int YEARS_OF_HISTORY = 2;

    private QuoteSyncJob() {
    }

    static void getCurrentQuotes(final Context context)
    {
        try {
            Set<String> stockPref = PrefUtils.getStocks(context);
            if (stockPref.size() == 0) {
                return;
            }
            Set<String> newStocks = new HashSet<>();

            Iterator<String> stockPrefIt = stockPref.iterator();
            while (stockPrefIt.hasNext()) {
                String stockSymbol = stockPrefIt.next();
                if (!isStored(context, stockSymbol)) {
                    newStocks.add(stockSymbol);
                }
            }

            Timber.d(newStocks.toString());

            Map<String, Stock> newQuotes = YahooFinance.get(stockPref.toArray(new String[stockPref.size()]));
            Iterator<String> iterator = stockPref.iterator();
            ArrayList<ContentValues> quoteCVs = new ArrayList<>();

            while (iterator.hasNext()) {
                final String symbol = iterator.next();
                Stock stock = newQuotes.get(symbol);
                if  (stock.getName() == null)
                {
                    Handler mHandler = new Handler(getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            PrefUtils.removeStock(context, symbol);
                            Toast.makeText(context, "The symbol:" + symbol + " does not exist or does not have any historical data. Please confirm the symbol is correct.", Toast.LENGTH_SHORT).show();
                            Intent dataUpdatedIntent = new Intent(ACTION_DATA_NOT_UPDATED);
                            context.sendBroadcast(dataUpdatedIntent);
                        }
                    });
                }
                else
                {
                    ContentValues stockCVs = addMinimumStockDetails(stock);
                    quoteCVs.add(stockCVs);

                    if (newStocks.contains(symbol)){
                        context.getContentResolver()
                                .insert(Contract.Quote.URI,
                                        stockCVs);
                    }
                    else{
                        context.getContentResolver()
                                .update(Contract.Quote.URI,
                                        stockCVs,
                                        "symbol=?",
                                        new String[]{symbol});
                    }
                }
            }

            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
            context.sendBroadcast(dataUpdatedIntent);

        }catch (IOException e)
        {
            Timber.e(e, "Error fetching stock quotes");
        }
    }

    public static void getQuoteHistory(final Context context, String stockSymbol) {

        Timber.d("Running sync job");

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -YEARS_OF_HISTORY);

        try {

            Stock stock = YahooFinance.get(stockSymbol);
            Map<String, Object> quoteHistory = hasHistory(context, stock.getSymbol());

            String history = (String)quoteHistory.get("History");
            Long lastModified = (Long)quoteHistory.get("LastModified");
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(lastModified);
            int lastModifiedWeek = cal.get(Calendar.WEEK_OF_YEAR);

            cal.setTime(new Date());
            int thisWeek = cal.get(Calendar.WEEK_OF_YEAR);
            int weeks = thisWeek - lastModifiedWeek;
            if (weeks < 1 && history != null)
                return;

            Timber.d(stock.toString());

            StockQuote quote = stock.getQuote();

            String name = stock.getName();
            float price = quote.getPrice().floatValue();
            float change = quote.getChange().floatValue();
            float percentChange = quote.getChangeInPercent().floatValue();

            List<HistoricalQuote> historicalQuote = stock.getHistory(from, to, Interval.WEEKLY);
            StringBuilder historyBuilder = new StringBuilder();

            for (HistoricalQuote it : historicalQuote) {
                historyBuilder.append(it.getDate().getTimeInMillis());
                historyBuilder.append(", ");
                historyBuilder.append(it.getHigh());
                historyBuilder.append(", ");
                historyBuilder.append(it.getLow());
                historyBuilder.append(", ");
                historyBuilder.append(it.getOpen());
                historyBuilder.append(", ");
                historyBuilder.append(it.getClose());
                historyBuilder.append(",");
            }

            ContentValues quoteCV = new ContentValues();
            quoteCV.put(Contract.Quote.COLUMN_SYMBOL, stockSymbol);
            quoteCV.put(Contract.Quote.COLUMN_NAME, name);
            quoteCV.put(Contract.Quote.COLUMN_PRICE, price);
            quoteCV.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);
            quoteCV.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, change);
            quoteCV.put(Contract.Quote.COLUMN_HISTORY, historyBuilder.toString());
            quoteCV.put(Contract.Quote.COLUMN_PRICE_MODIFIED, Calendar.getInstance().getTimeInMillis());
            quoteCV.put(Contract.Quote.COLUMN_HISTORY_MODIFIED, Calendar.getInstance().getTimeInMillis());

            context.getContentResolver().update(Contract.Quote.URI, quoteCV, "symbol=?",new String[]{stockSymbol});

            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
            context.sendBroadcast(dataUpdatedIntent);

        } catch (IOException exception) {
            Timber.e(exception, "Error fetching stock quotes");
        }
    }

    static boolean isStored(Context context, String symbol)
    {
        return context.getContentResolver()
                .query(Contract.Quote.URI,
                        new String[]{Contract.Quote.COLUMN_SYMBOL},
                        Contract.Quote.COLUMN_SYMBOL +"=?",
                        new String[]{symbol},
                        null)
                .moveToNext();
    }

    static Map<String, Object> hasHistory(Context context, String symbol)
    {
        Cursor query = context.getContentResolver()
                .query(Contract.Quote.URI,
                        new String[]{Contract.Quote.COLUMN_HISTORY,
                                     Contract.Quote.COLUMN_HISTORY_MODIFIED},
                        Contract.Quote.COLUMN_SYMBOL +"=?",
                        new String[]{symbol},
                        null);
        query.moveToNext();

        Map<String, Object> values = new HashMap<>();
        values.put("History", query.getString(0));
        values.put("LastModified", query.getLong(1));
        query.close();
        return values;
    }

    static ContentValues addMinimumStockDetails(Stock stock)
    {
        StockQuote quote = stock.getQuote();
        String symbol = stock.getSymbol();
        String name = stock.getName();
        float price = quote.getPrice().floatValue();
        float change = quote.getChange().floatValue();
        float percentChange = quote.getChangeInPercent().floatValue();

        ContentValues quoteCV = new ContentValues();
        quoteCV.put(Contract.Quote.COLUMN_SYMBOL, symbol);
        quoteCV.put(Contract.Quote.COLUMN_NAME, name);
        quoteCV.put(Contract.Quote.COLUMN_PRICE, price);
        quoteCV.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);
        quoteCV.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, change);
        quoteCV.put(Contract.Quote.COLUMN_PRICE_MODIFIED, Calendar.getInstance().getTimeInMillis());

        return quoteCV;
    }

    private static void schedulePeriodic(Context context) {
        Timber.d("Scheduling a periodic task");


        JobInfo.Builder builder = new JobInfo.Builder(PERIODIC_ID, new ComponentName(context, QuoteJobService.class));


        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(PERIOD)
                .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }


    public static synchronized void initialize(final Context context) {

        schedulePeriodic(context);
        syncImmediately(context);

    }

    public static synchronized void syncImmediately(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Intent nowIntent = new Intent(context, QuoteIntentService.class);
            context.startService(nowIntent);
        } else {

            JobInfo.Builder builder = new JobInfo.Builder(ONE_OFF_ID, new ComponentName(context, QuoteJobService.class));


            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            scheduler.schedule(builder.build());


        }
    }


}
