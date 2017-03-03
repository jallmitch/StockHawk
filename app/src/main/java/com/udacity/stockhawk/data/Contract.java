package com.udacity.stockhawk.data;


import android.net.Uri;
import android.provider.BaseColumns;

import com.google.common.collect.ImmutableList;

public final class Contract {

    static final String AUTHORITY = "com.udacity.stockhawk";
    static final String PATH_QUOTE = "quote";
    static final String PATH_QUOTE_WITH_SYMBOL = "quote/*";
    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private Contract() {
    }

    @SuppressWarnings("unused")
    public static final class Quote implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_QUOTE).build();
        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_PREVIOUS_CLOSE = "previous_close";
        public static final String COLUMN_OPEN = "open";
        public static final String COLUMN_HIGH = "high";
        public static final String COLUMN_LOW = "low";
        public static final String COLUMN_ASK = "ask";
        public static final String COLUMN_ASK_SIZE = "ask_size";
        public static final String COLUMN_BID = "bid";
        public static final String COLUMN_BID_SIZE = "bid_size";
        public static final String COLUMN_ABSOLUTE_CHANGE = "absolute_change";
        public static final String COLUMN_PERCENTAGE_CHANGE = "percentage_change";
        public static final String COLUMN_HISTORY = "history";
        public static final String COLUMN_PRICE_MODIFIED = "price_modified";
        public static final String COLUMN_HISTORY_MODIFIED = "history_modified";
        public static final int POSITION_ID = 0;
        public static final int POSITION_SYMBOL = 1;
        public static final int POSITION_NAME = 2;
        public static final int POSITION_PRICE = 3;
        public static final int POSITION_PREVIOUS_CLOSE = 4;
        public static final int POSITION_OPEN = 5;
        public static final int POSITION_HIGH = 6;
        public static final int POSITION_LOW = 7;
        public static final int POSITION_ASK = 8;
        public static final int POSITION_ASK_SIZE = 9;
        public static final int POSITION_BID = 10;
        public static final int POSITION_BID_SIZE = 11;
        public static final int POSITION_ABSOLUTE_CHANGE = 12;
        public static final int POSITION_PERCENTAGE_CHANGE = 13;
        public static final int POSITION_HISTORY = 14;
        public static final int POSITION_PRICE_MODIFIED = 15;
        public static final int POSITION_HISTORY_MODIFIED = 16;
        public static final ImmutableList<String> QUOTE_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_SYMBOL,
                COLUMN_NAME,
                COLUMN_PRICE,
                COLUMN_PREVIOUS_CLOSE,
                COLUMN_OPEN,
                COLUMN_HIGH,
                COLUMN_LOW,
                COLUMN_ASK,
                COLUMN_ASK_SIZE,
                COLUMN_BID,
                COLUMN_BID_SIZE,
                COLUMN_ABSOLUTE_CHANGE,
                COLUMN_PERCENTAGE_CHANGE,
                COLUMN_HISTORY,
                COLUMN_PRICE_MODIFIED,
                COLUMN_HISTORY_MODIFIED
        );
        static final String TABLE_NAME = "quotes";

        public static Uri makeUriForStock(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        static String getStockFromUri(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }
    }

}
