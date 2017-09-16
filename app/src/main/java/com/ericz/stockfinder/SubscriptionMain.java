package com.ericz.stockfinder;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

        String percent = null;
        String volume = null;
        int sector;
        String marketCap = null;
        String peRatio;
        String debtEquity = null;
        String exchange;
        String price;
        String dividend;


        if (!TextUtils.isEmpty(percentChange1.getText())&& !TextUtils.isEmpty(percentChange2.getText()) && Float.valueOf(percentChange1.getText().toString())<Float.valueOf(percentChange2.getText().toString()))
        {
            percent = ",percent_change~gte~" + percentChange1.getText().toString() + ",percent_change~lte~" + percentChange2.getText().toString();
        }
        else
        {


        }

        if (!TextUtils.isEmpty(volumeText1.getText())&& !TextUtils.isEmpty(volumeText2.getText()) && Float.valueOf(volumeText1.getText().toString())<Float.valueOf(volumeText2.getText().toString()))
        {
            volume = ",adj_volume~gte~" + volumeText1.getText().toString() + ",adj_volume~lte~" + volumeText2.getText().toString();
        }

        if(!TextUtils.isEmpty(marketCap1.getText())&& !TextUtils.isEmpty(marketCapt2.getText()) &&Float.valueOf(marketCap1.getText().toString())<Float.valueOf(marketCapt2.getText().toString())){
            marketCap = ",marketcap~gte~" + marketCap1.getText().toString() + ",marketcap~lte~" + marketCapt2.getText().toString();

        }
        else
        {

        }

        if (!TextUtils.isEmpty(peRatio1.getText())&& !TextUtils.isEmpty(peRatio2.getText()) &&Float.valueOf(peRatio1.getText().toString())<Float.valueOf(peRatio2.getText().toString()))
        {
            peRatio = ",pricetoearnings~gte~" + peRatio1.getText().toString() + ",pricetoearnings~lte~" + peRatio2.getText().toString();
        }
        else
        {

        }

        if (!TextUtils.isEmpty(debtEquity1.getText())&& !TextUtils.isEmpty(debtEquity2.getText()) &&Float.valueOf(debtEquity1.getText().toString())<Float.valueOf(debtEquity2.getText().toString()))
        {
            debtEquity = ",debttoequity~gte~" + debtEquity1.getText().toString() + ",debttoequity~lte~" + debtEquity1.getText().toString();
        }
        else
        {

        }

        if (!TextUtils.isEmpty(dividendShare1.getText())&& !TextUtils.isEmpty(dividendShare2.getText()) &&Float.valueOf(dividendShare1.getText().toString())<Float.valueOf(dividendShare2.getText().toString()))
        {
            dividend = ",cashdividendpershare~gte~" + dividendShare1.getText().toString() + ",cashdividendshare~lte~" + dividendShare2.getText().toString();
        }
        else
        {

        }

        if (!TextUtils.isEmpty(priceText1.getText())&& !TextUtils.isEmpty(priceText2.getText()) &&Float.valueOf(priceText1.getText().toString())<Float.valueOf(priceText2.getText().toString()))
        {
            price = ",price~gte~"  + priceText1.getText().toString() + ",price~lte~" + priceText2.getText().toString();
        }


        exchange = exchangeSpinner.getSelectedItem().toString();

        if (exchange.equals("Any"))
        {
            exchange = "";
        }
        if (exchange.equals("NYSE"))
        {
            exchange = ",stock_exchange~contains~NYSE";
        }
        if (exchange.equals("NASDAQ"))
        {
            exchange = ",stock_exchange~contains~NASDAQ";
        }


        sector = sectorSpinner.getSelectedItemPosition();

        String sectorString = null;
        if (sector == 0)
        {
            sectorString = ",sic~gte~0000";
        }
        if(sector == 1)
        {
            sectorString = ",sic~gte~0100,sic~lte~0999";
        }
        if (sector == 2)
        {
            sectorString = ",sic~gte~1000,sic~lte~1499";
        }
        if (sector == 3){
            sectorString = ",sic~gte~1500,sic~lte~1799";
        }
        if (sector ==4)
        {
            sectorString= ",sic~gte~2000,sic~lte~3999";
        }

        if(sector == 5)
        {
            sectorString = ",sic~gte~4000,sic~lte~4999";
        }

        if (sector == 6)
        {
            sectorString = ",sic~gte~5000,sic~lte~5199";
        }
        if(sector == 7)
        {
            sectorString = ",sic~gte~5200,sic~lte~5999";
        }

        if (sector == 8)
        {
            sectorString = ",sic~gte~6000,sic~lte~6799";
        }
        if (sector == 9)
        {
            sectorString = ",sic~gte~7000,sic~lte~8999";
        }
        if (sector == 10)
        {
            sectorString = ",sic~eq~3674";
        }
        if (sector == 11)
        {
            sectorString = ",sic~gte~9900,sic~lte~9999";
        }



        Log.v("String test", percent + marketCap + volume + sectorString + debtEquity + exchange );
//        String finalString =
//                "https://api.intrinio.com/securities/search?conditions="
//                        + percent + marketCap + volume + sectorString
//                        + peRatio + price + peratio + debtEquity + exchange + ",name~gte~0";
//
//
//



    }







}
