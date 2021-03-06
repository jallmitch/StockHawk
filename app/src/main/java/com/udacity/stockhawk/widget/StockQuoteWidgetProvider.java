package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteIntentService;
import com.udacity.stockhawk.sync.QuoteSyncJob;

/**
 * Created by jesse.mitchell on 2/18/2017.
 */

public class StockQuoteWidgetProvider extends AppWidgetProvider {

    public static String WIDGET_STOCK_QUOTE = "widget.stock.quote";
    public static String EXTRA_STRING_ID = "widget.stock.quote_symbol";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WIDGET_STOCK_QUOTE))
        {
            String symbol = intent.getExtras().getString(EXTRA_STRING_ID);
            Intent sync = new Intent(context, QuoteIntentService.class);
            sync.putExtra(context.getString(R.string.STOCK_QUOTE), symbol);
            context.startService(sync);
        }

        if(intent.getAction().equalsIgnoreCase(QuoteSyncJob.ACTION_DATA_UPDATED))
        {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName compName = new ComponentName(context, StockQuoteWidgetProvider.class);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(compName), R.id.stock_widget);
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stock_widget);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int i = 0; i < appWidgetIds.length; i++) {
            RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.stock_widget);

            Intent serviceIntent = new Intent(context, StockWidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
            widgetView.setRemoteAdapter(R.id.stock_widget, serviceIntent);
            widgetView.setEmptyView(R.id.stock_widget, R.id.empty_view);

            Intent onItemClick = new Intent(context, StockQuoteWidgetProvider.class);
            onItemClick.setAction(WIDGET_STOCK_QUOTE);
            onItemClick.setData(Uri.parse(onItemClick.toUri(Intent.URI_INTENT_SCHEME)));

            PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0, onItemClick, PendingIntent.FLAG_UPDATE_CURRENT);

            widgetView.setPendingIntentTemplate(R.id.stock_widget, onClickPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds[i], widgetView);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stock_widget);
    }
}
