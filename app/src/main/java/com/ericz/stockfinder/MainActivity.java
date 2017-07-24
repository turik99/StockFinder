package com.ericz.stockfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    private Spinner percentSpinner;
    private Spinner volumeSpinner;
    private Spinner sectorSpinner;
    private Spinner marketcapSpinner;
    private Spinner peratioSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        percentSpinner = (Spinner) findViewById(R.id.percentSpinner);
        volumeSpinner = (Spinner) findViewById(R.id.volumeSpinner);
//        sectorSpinner = (Spinner) findViewById(R.id.sectorSpinner);
        marketcapSpinner = (Spinner) findViewById(R.id.marketcapSpinner);
        peratioSpinner = (Spinner) findViewById(R.id.peratioSpinner);
    }

    protected void stocks(View view)
    {
        String percentString = percentSpinner.getSelectedItem().toString();
        String volumeString = volumeSpinner.getSelectedItem().toString();
        String sectorString = sectorSpinner.getSelectedItem().toString();
        String marketcapString = marketcapSpinner.getSelectedItem().toString();
        String peratioString = peratioSpinner.getSelectedItem().toString();

        if (percentString == "Any")
            percentString = "";
        if (percentString == "+5%")
            percentString = "percent_change~gte~5";
        if (percentString == "+10%")
            percentString = "percent_change~gte~10";
        if (percentString == "+15%")
            percentString = "percent_change~gte~15";

        if (volumeString == "Any")
            volumeString = "";
        if (volumeString == "low: 0-100k")
            volumeString = "volume~lte~100000";
        if (volumeString == "med 100k-1m")
            volumeString = "volume~gte~100000";
        if (volumeString == "high 1m up")
            volumeString = "volume~gte~1000000";

        i



    }


}
