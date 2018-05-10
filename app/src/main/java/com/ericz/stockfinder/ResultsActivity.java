package com.ericz.stockfinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONObject;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);


        String dataString = getIntent().getStringExtra("data string");
        ListView listView = findViewById(R.id.listView);

        listView.bringToFront();


        try
        {
            JSONObject jsonObject = new JSONObject(dataString);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            StockList stockList = new StockList(this, jsonArray);

            listView.setAdapter(stockList);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }




}
