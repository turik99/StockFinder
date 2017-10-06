package com.ericz.stockfinder;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.ericz.stockfinder.util.IabHelper;
import com.ericz.stockfinder.util.IabResult;
import com.ericz.stockfinder.util.Purchase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private Spinner percentSpinner;
    private Spinner volumeSpinner;
    private Spinner sectorSpinner;
    private Spinner marketcapSpinner;
    private Spinner peratioSpinner;
    private Spinner epsSpinner;
    private Spinner reportSpinner;
    private Spinner priceSpinner;
    private Spinner stockExchangeSpinner;
    private Spinner debtEquitySpinner;
    private ProgressDialog progressDialog;

    private boolean paid;
    private static final String TAG = "inappbilling";
    IabHelper mHelper;
    static final String ITEM_SKU = "android.test.purchased";



    IInAppBillingService mService;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences.Editor editor;
        SharedPreferences sharedPreferences = getSharedPreferences("name", MODE_PRIVATE);
        editor = sharedPreferences.edit();



        sharedPreferences.getBoolean("firstTime", true);

        if (sharedPreferences.getBoolean("firstTime", true))
        {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.changelayout);
            dialog.show();
        }

        editor.putBoolean("firstTime", false);



        Intent intent = new Intent(MainActivity.this, SubscriptionMain.class);
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        percentSpinner = (Spinner) findViewById(R.id.percentSpinner);
        volumeSpinner = (Spinner) findViewById(R.id.volumeSpinner);
        sectorSpinner = (Spinner) findViewById(R.id.sectorSpinner);
        marketcapSpinner = (Spinner) findViewById(R.id.marketcapSpinner);
        peratioSpinner = (Spinner) findViewById(R.id.peratioSpinner);
        epsSpinner = (Spinner) findViewById(R.id.earningsSpinner);
        priceSpinner = (Spinner) findViewById(R.id.priceSpinner);
        stockExchangeSpinner = (Spinner) findViewById(R.id.exchangeSpinner);
        debtEquitySpinner = (Spinner) findViewById(R.id.debtEquitySpinner);

        super.onStart();
        String base64EncodedPublicKey =
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiraN/vkkv3xD9dcqPwYC6wdzaO/WD9A9pHm4X7Tg2v1oEZd+1dWQM9eOjTA++00LW3cOKzlBg/g0Py+YCAi5NVOgzHvDhlsgkPu4a7WIIsNJdygYv0L04+0XLH0+9J4oL9A0JD+CZ58HJCo8rndCo/6XTiUEw9pkX/lPx7VCEIFnvChtXAuPg1VEW7dSTo0JfT3wzpGyiwrAZ51YJ6wNdqNR2t8xaRp+zs90wGO4z/cHI/MUR6zOWX1k0xu+tSOyAN8VuRQDjGwF4mk/nDe7+5LQG1QjJUMFvx7bixodUwMWJlgGkyn/uxdUPemK3IDeBVTVaWZMvL8/9xGa5OSzlwIDAQAB";

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " +
                            result);
                } else {
                    Log.d(TAG, "In-app Billing is set up OK");
                }
            }
        });


    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        mHelper = null;



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        if (resultCode == 0)
        {
            paid = true;
        }
        else {
            paid = false;
        }
        stocks(new View(getApplicationContext()));

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else
        {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }



    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase)
        {
            if (result.isFailure()) {
                // Handle error
                paid = false;

                return;
            }
            else if (purchase.getSku().equals(ITEM_SKU)) {
                Log.v("SUCCESS", "SUCCESS!!!!!!!!!!!");
                paid = true;
            }

        }
    };


    public void stocks(View view)
    {

        try
        {
            mHelper.launchSubscriptionPurchaseFlow(this, ITEM_SKU, 10001,
                    mPurchaseFinishedListener, "mypurchasetoken");
        }


        catch (IabHelper.IabAsyncInProgressException e)
        {
            e.printStackTrace();
        }

        if (paid)
        {
            String percentString = percentSpinner.getSelectedItem().toString();
            String volumeString = volumeSpinner.getSelectedItem().toString();
            int sector = sectorSpinner.getSelectedItemPosition();
            String marketcapString = marketcapSpinner.getSelectedItem().toString();

            String sectorString = "";
            if (percentString.equals("Any") ){
                percentString = "percent_change~gte~-100";}
            if (percentString.equals( "+5%")){
                percentString = "percent_change~gte~5e-2";}
            if (percentString.equals("+10%") ){
                percentString = "percent_change~gte~10e-2";}
            if (percentString.equals( "+15%")){
                percentString = "percent_change~gte~15e-2";}

            if (volumeString.equals("Any") ){
                volumeString = ",adj_volume~gte~0";}
            if (volumeString.equals( "low: 0-100k")){
                volumeString = ",adj_volume~lte~100000";}
            if (volumeString.equals("med 100k-1m") ){
                volumeString = ",adj_volume~gte~100000";}
            if (volumeString.equals("high 1m up") ){
                volumeString = ",adj_volume~gte~1000000";}

            if (marketcapString.equals("Any") ){
                marketcapString = "";}
            if (marketcapString.equals("0 - 100m") ){
                marketcapString = ",marketcap~lte~100e6";}
            if (marketcapString .equals("100m - 1b") ){
                marketcapString = ",marketcap~gte~1e8,marketcap~lt~1e9";}
            if (marketcapString.equals("1b - 10b") ){
                marketcapString = ",marketcap~gte~1e9,marketcap~lt~10e9";}
            if (marketcapString.equals("10b - 100b")){
                marketcapString = ",marketcap~gte~10e9,marketcap~lt~100e9";}
            if (marketcapString.equals("100b and up")){
                marketcapString = ",marketcap~gte~100e9";}

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

            if (sector ==12)
            {

            }
            String price = priceSpinner.getSelectedItem().toString();

            if (price.equals("Any"))
            {
                price = ",close_price~gte~0.00";
            }
            if (price.equals("$0 to $1"))
            {
                price = ",close_price~lte~1";
            }
            if (price.equals("$1 to $5"))
            {
                price = ",close_price~gte~1,close_price~lte~5";
            }

            if (price.equals("$5 to $20") )
            {
                price = ",close_price~gte~5,close_price~lte~20";
            }
            if (price.equals("$20 to $100"))
            {
                price = ",close_price~gte~20.00,close_price~lte~100.00";
            }
            if (price.equals("$100 to $200"))
            {
                price = ",close_price~gte~100.00,close_price~lte~200.00";
            }
            if (price.equals("200$ and up"))
            {
                price = ",close_price~gte~200.00";
            }

            String eps = epsSpinner.getSelectedItem().toString();
            if (eps.equals("Any"))
            {
                eps = "";
            }
            if(eps.equals("0 - 1$"))
            {
                eps = ",cashdividendspershare~gte~1.00";
            }
            if(eps.equals("1 - 5$"))
            {
                eps = ",cashdividendspershare~lte~5.00,cashdividendspershare~gt~1.00";

            }
            if(eps.equals("5$ and up"))
            {
                eps = ",cashdividendspershare~gte~5.00";
            }

            String peratio = peratioSpinner.getSelectedItem().toString();

            if (peratio.equals("Any"))
            {
                peratio = ",pricetoearnings~lte~1000";
            }
            if(peratio.equals("0 - 15"))
            {
                peratio = ",pricetoearnings~gte~0,pricetoearnings~lt~15";
            }
            if(peratio.equals("15 - 30"))
            {
                peratio = ",pricetoearnings~gte~15,pricetoearnings~lte~30";
            }
            if(peratio.equals("30 and up"))
            {
                peratio = ",pricetoearnings~gte~30";
            }

            String debtEquity = debtEquitySpinner.getSelectedItem().toString();
            if (debtEquity.equals("Any"))
            {
                debtEquity = "";
            }
            if (debtEquity.equals("High (>.5)"))
            {
                debtEquity = ",debttoequity~gte~0.5";
            }
            if (debtEquity.equals("Low (<0.1)"))
            {
                debtEquity = ",debttoequity~lte~0.1";
            }

            String exchange = stockExchangeSpinner.getSelectedItem().toString();

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


            String finalString =
                    "https://api.intrinio.com/securities/search?conditions="
                            + percentString + marketcapString + volumeString + sectorString
                            + eps + price + peratio + debtEquity + exchange + ",name~gte~0";


            Log.v("String Test", finalString);



            Getdata getdata = new Getdata(finalString);
            try
            {

                getdata.execute();
            }


            catch (Exception e)
            {
                e.printStackTrace();
            }
        }



    }

    public class Getdata extends AsyncTask<String, String, String>
    {

        private ArrayList<String[]> dataSet = new ArrayList<String[]>();
        private String string;
        private String dataString;
        private String error;
        public Getdata(String string)
        {

            this.string = string;
        }

        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading Stocks");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }
        @Override
        protected String doInBackground(String... strings) {
            String username = "8ce8fe36e93c5bedf609fef9d63050f9";
            String password = "210addd4e3ac39f48c4ca84c50ff81c9";
            String login = username + ":" + password;

//            String encoded = new String (Base64.encode(login.getBytes(), Base64.DEFAULT));
            Log.v("login auth", login);


            try {
                dataString = Jsoup.connect(string)
                        .header("Authorization", "Basic " + Base64.encodeToString(login.getBytes(), Base64.NO_WRAP))
                        .ignoreContentType(true)
                        .execute().body();

                Log.v("data string", dataString);

                error = "none";
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
                error = "noInternet";
            }
            catch (HttpStatusException e)
            {
                e.printStackTrace();
                error = "tooMany";
            } catch (IOException e) {
                e.printStackTrace();
            }

            return dataString;

        }

        protected void onPostExecute(String string)
        {
            if (error.equals("none"))
            {
                try
                {
                    if (string.equals("https://api.intrinio.com/securities/search?conditions=percent_change~gte~-100,adj_volume~gte~0,sic~gte~0000,close_price~gte~0.00,pricetoearnings~lte~1000,name~gte~0"))
                    {
                        Toast.makeText(MainActivity.this, "narrow your search criteria", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if (new JSONObject(dataString).getJSONArray("data").length() == 0)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "No results.. try broadening your search criteria", Toast.LENGTH_LONG).show();

                        }
                        if (dataString == null)
                        {
                            progressDialog.dismiss();
                            Log.v("onPostExec", "jsonarray is null");
                            Toast.makeText(getApplicationContext(), "Problem loading, check your internet connection", Toast.LENGTH_SHORT).show();
                        }
                        if (new JSONObject(dataString).getJSONArray("data").length() >0)
                        {
                            progressDialog.dismiss();
                            Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                            intent.putExtra("data string", dataString);

                            startActivity(intent);
                        }
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, "Connection error - Please try again later.", Toast.LENGTH_LONG).show();
                }

            }
            if (error.equals("tooMany"))
            {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Too many conditions specified! remove some and try again", Toast.LENGTH_LONG).show();

            }
            if (error.equals("noInternet"))
            {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Connection error - Please try again later.", Toast.LENGTH_LONG).show();
            }
        }


    }

}
