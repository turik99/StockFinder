package com.ericz.stockfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONObject;

public class ResultsActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);


        String dataString = getIntent().getStringExtra("data string");
        ListView listView = (ListView) findViewById(R.id.listView);

        listView.bringToFront();
        AdView adView = (AdView) findViewById(R.id.resultAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(getApplicationContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-4430034146252858/3454610969");
        mInterstitialAd.loadAd( new AdRequest.Builder().build());


        try
        {
            JSONObject jsonObject = new JSONObject(dataString);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            StockList stockList = new StockList(this, jsonArray);

            listView.setAdapter(stockList);

            mInterstitialAd.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

}
