package com.udacity.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.udacity.stockhawk.R;

public class StockDetailReport extends AppCompatActivity {

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("StockDetailReport", "On Restart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("StockDetailReport", "On Start");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("StockDetailReport", "On Pause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("StockDetailReport", "On Destroy");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("StockDetailReport", "On Resume");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("StockDetailReport", "On Create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail_report);

        if (savedInstanceState == null){


            StockDetailFragment sdf = new StockDetailFragment();
            sdf.setArguments(getIntent().getExtras());

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_stock_detail_report, sdf)
                    .commit();
        }



//        setContentView(R.layout.activity_stock_detail_report);
//        List<CandleEntry> barEntries = new ArrayList<>();
//        barEntries.add(new CandleEntry(0, 63.650002f,63.139999f,63.5f,63.639999f));
//        barEntries.add(new CandleEntry(1, 65.790001f,62.75f,65.690002f,63.68f));
//        barEntries.add(new CandleEntry(2, 65.910004f,62.57f,62.700001f,65.779999f));
//        barEntries.add(new CandleEntry(3, 62.98f,62.029999f,62.68f,62.740002f));
//        barEntries.add(new CandleEntry(4, 63.400002f,61.950001f,62.759998f,62.700001f));
//        barEntries.add(new CandleEntry(5, 63.150002f,62.029999f,62.790001f,62.84f));
//        barEntries.add(new CandleEntry(6, 64.07f,62.029999f,63.209999f,62.139999f));
//        barEntries.add(new CandleEntry(7, 64.099998f,62.419998f,62.560001f,63.240002f));
//        barEntries.add(new CandleEntry(8, 63.450001f,61.720001f,61.82f,62.299999f));
//        barEntries.add(new CandleEntry(9, 61.990002f,59.560001f,59.700001f,61.970001f));
//        barEntries.add(new CandleEntry(10, 61.41f,58.799999f,60.34f,59.25f));
//        barEntries.add(new CandleEntry(11, 61.259998f,60.130001f,60.5f,60.529999f));
//        barEntries.add(new CandleEntry(12, 61.139999f,57.279999f,59.02f,60.349998f));
//
//        CandleDataSet barSet = new CandleDataSet(barEntries, " Stock Entries");
//        barSet.setIncreasingColor(Color.GREEN);
//        barSet.setDecreasingColor(Color.RED);
//        barSet.setShowCandleBar(true);
//
//        CandleData candleData = new CandleData(barSet);
//        candleData.setValueTextSize(16f);
//
//        CandleStickChart candleChart =  (CandleStickChart) findViewById(R.id.candleStick_chart);
//        candleChart.setData(candleData);
//        candleChart.setBackgroundColor(Color.WHITE);

    }
}
