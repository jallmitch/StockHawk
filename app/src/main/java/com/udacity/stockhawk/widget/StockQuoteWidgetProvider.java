package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.StockDetailFragment;

/**
 * Created by jesse.mitchell on 2/18/2017.
 */

public class StockQuoteWidgetProvider extends AppWidgetProvider {

    public static String STOCK_QUOTE = "widget.stock.quote";

    @Override
    public void onReceive(Context context, Intent intent) {
//        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
//        if (intent.getAction().equals(STOCK_QUOTE))
//        {
//            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
//            int viewIndex = intent.getIntExtra("", 0);
//        }
        super.onReceive(context, intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
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
            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.stock_widget);

            Intent serviceIntent = new Intent(context, StockWidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
//            widget.setRemoteAdapter(appWidgetIds[i], R.id.stock_widget, serviceIntent);
            widget.setRemoteAdapter(R.id.stock_widget, serviceIntent);
            widget.setEmptyView(R.id.stock_widget, R.id.empty_view);

            Intent intent = new Intent(context, StockDetailFragment.class);
            intent.setAction(STOCK_QUOTE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            widget.setPendingIntentTemplate(R.id.stock_widget, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
