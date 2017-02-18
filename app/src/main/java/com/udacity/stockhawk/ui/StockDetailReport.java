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
    }
}
