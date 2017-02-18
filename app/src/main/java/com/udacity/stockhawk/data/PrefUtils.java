package com.udacity.stockhawk.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.udacity.stockhawk.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public final class PrefUtils {

    private static Set<String> mDbResults = new HashSet<>();
    private PrefUtils() {
    }

    public static Set<String> getStocks(Context context) {
        Log.d("SyncTime", Calendar.getInstance().getTime().toString());
        Cursor dbStocks = context.getContentResolver().query(Contract.Quote.URI, new String[]{"symbol"}, null, null, null);

        if (dbStocks.moveToNext()){
            mDbResults.addAll(getDBStocks(dbStocks));
        }

        Set<String> prefResults = new HashSet<>(getDefaultStocks(context));

        if (mDbResults.size() < 1)
            return prefResults;
        else
            return mDbResults;

    }

    private static Set<String> getDBStocks(Cursor cursor)
    {
        Set<String> dbSymbols = new HashSet<>();
        do {
            dbSymbols.add(cursor.getString(0));
        }while(cursor.moveToNext());
        cursor.close();
        return dbSymbols;
    }

    private static Set<String> getDefaultStocks(Context context)
    {
        String stocksKey = context.getString(R.string.pref_stocks_key);
        String initializedKey = context.getString(R.string.pref_stocks_initialized_key);
        String[] defaultStocksList = context.getResources().getStringArray(R.array.default_stocks);

        HashSet<String> defaultStocks = new HashSet<>(Arrays.asList(defaultStocksList));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean initialized = prefs.getBoolean(initializedKey, false);

        if (!initialized) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(initializedKey, true);
            editor.putStringSet(stocksKey, defaultStocks);
            editor.apply();
            return defaultStocks;
        }
        return prefs.getStringSet(stocksKey, new HashSet<String>());
    }

    private static void editStockPref(Context context, String symbol, Boolean add) {
        String key = context.getString(R.string.pref_stocks_key);
        Set<String> stocks = getStocks(context);

        if (add) {
            mDbResults.add(symbol);
            stocks.add(symbol);
        } else {

            mDbResults.remove(symbol);
            stocks.remove(symbol);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(key, stocks);
        editor.apply();
    }

    public static void addStock(Context context, String symbol) {
        editStockPref(context, symbol, true);
    }

    public static void removeStock(Context context, String symbol) {
        editStockPref(context, symbol, false);
    }

    public static String getDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String defaultValue = context.getString(R.string.pref_display_mode_default);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultValue);
    }

    public static void toggleDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String absoluteKey = context.getString(R.string.pref_display_mode_absolute_key);
        String percentageKey = context.getString(R.string.pref_display_mode_percentage_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String displayMode = getDisplayMode(context);

        SharedPreferences.Editor editor = prefs.edit();

        if (displayMode.equals(absoluteKey)) {
            editor.putString(key, percentageKey);
        } else {
            editor.putString(key, absoluteKey);
        }

        editor.apply();
    }
}
