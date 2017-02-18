package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;

import com.udacity.stockhawk.ui.StockDetailReport;

import timber.log.Timber;

public class QuoteIntentService extends IntentService {

    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("Intent handled");
        String stock_symbol = intent.getStringExtra("STOCK_SYMBOL");

        if (stock_symbol != null) {
            QuoteSyncJob.getQuoteHistory(getApplicationContext(), stock_symbol);
            Intent reportIntent  = new Intent(this, StockDetailReport.class);
            reportIntent.putExtra("STOCK_SYMBOL", stock_symbol);
            reportIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(reportIntent);
        }

        QuoteSyncJob.getCurrentQuotes(getApplicationContext());
    }
}
