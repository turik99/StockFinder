package com.ericz.stockfinder;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;



public class SubscriptionMain extends AppCompatActivity {

    private ProgressDialog progressDialog;


    private EditText percentChange1;
    private EditText percentChange2;
    private EditText volumeText1;
    private EditText volumeText2;
    private Spinner sectorSpinner;
    private EditText marketCap1;
    private EditText marketCapt2;
    private EditText peRatio1;
    private EditText peRatio2;
    private EditText debtEquity1;
    private EditText debtEquity2;
    private Spinner exchangeSpinner;
    private EditText priceText1;
    private EditText priceText2;
    private EditText dividendShare1;
    private EditText dividendShare2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_paid);

        percentChange1 = (EditText) findViewById(R.id.percentChange1Premium);
        percentChange2 = (EditText) findViewById(R.id.percentChange2Premium);

        volumeText1 = (EditText) findViewById(R.id.volumeText1Premium);
        volumeText2 = (EditText) findViewById(R.id.volumeText2Premium);

        sectorSpinner = (Spinner) findViewById(R.id.sectorSpinnerPremium);

        marketCap1 = (EditText) findViewById(R.id.marketCapText1Premium);
        marketCapt2 = (EditText) findViewById(R.id.marketCapText2Premium);

        peRatio1 = (EditText) findViewById(R.id.peRatioText1);
        peRatio2 = (EditText) findViewById(R.id.peRatioText2Premium);

        debtEquity1 = (EditText) findViewById(R.id.debtEquityText1Premium);
        debtEquity2 = (EditText) findViewById(R.id.debtEquity2);

        exchangeSpinner = (Spinner) findViewById(R.id.exchangeSpinnerPremium);

        priceText1 = (EditText) findViewById(R.id.priceText1Premium);
        priceText2 = (EditText) findViewById(R.id.priceText2Premium);

        dividendShare1 = (EditText) findViewById(R.id.dividendText1Premium);
        dividendShare2 = (EditText) findViewById(R.id.dividendText2Premium);





    }

    protected void findStocksPaid(View view)
    {

        String percent;
        String volume;
        String sector;
        String marketCap;
        String peRatio;
        String debtEquity;
        String exchange;
        String price;
        String dividend;


        if (Float.valueOf(percentChange1.getText().toString())<Float.valueOf(percentChange2.getText().toString()))
        {
            //Do nothing
        }
        else
        {
            percent = "percent_change~gte~" + percentChange1.getText().toString()
        }


    }







}
