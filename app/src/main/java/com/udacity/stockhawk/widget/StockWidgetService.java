package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by jesse.mitchell on 2/25/2017.
 */

public class StockWidgetService extends RemoteViewsService
{
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockViewFactory(this.getApplicationContext(), intent);
    }
}
