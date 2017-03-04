package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.StockDetailReport;

public class QuoteIntentService extends IntentService {

    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String stock_symbol = intent.getStringExtra(getApplicationContext().getString(R.string.STOCK_QUOTE));

        if (stock_symbol != null) {
            QuoteSyncJob.getQuoteHistory(getApplicationContext(), stock_symbol);
            Intent reportIntent  = new Intent(this, StockDetailReport.class);
            reportIntent.putExtra(getApplicationContext().getString(R.string.STOCK_QUOTE), stock_symbol);
            reportIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(reportIntent);
        }

        QuoteSyncJob.getCurrentQuotes(getApplicationContext());
    }
}
