package com.ericz.stockfinder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.ericz.stockfinder.util.Purchase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONObject;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
    private Spinner profitSpinner;
    private Spinner priceToBookSpinner;
    private InterstitialAd mInterstitialAd;

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

            Log.v("mServiceConn", "connected!");
            testPaid(mService);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");

        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);


        Log.v("device number test",
                Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID));


        priceToBookSpinner = (Spinner) findViewById(R.id.pricetobook);
        percentSpinner = (Spinner) findViewById(R.id.percentSpinner);
        volumeSpinner = (Spinner) findViewById(R.id.volumeSpinner);
        sectorSpinner = (Spinner) findViewById(R.id.sectorSpinner);
        marketcapSpinner = (Spinner) findViewById(R.id.marketcapSpinner);
        peratioSpinner = (Spinner) findViewById(R.id.peratioSpinner);
        epsSpinner = (Spinner) findViewById(R.id.earningsSpinner);
        priceSpinner = (Spinner) findViewById(R.id.priceSpinner);
        stockExchangeSpinner = (Spinner) findViewById(R.id.exchangeSpinner);
        debtEquitySpinner = (Spinner) findViewById(R.id.debtEquitySpinner);
        profitSpinner = (Spinner) findViewById(R.id.profitSpinner);
        super.onStart();

        MobileAds.initialize(this,
                "ca-app-pub-3940256099942544~3347511713");




    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    @SuppressLint("HardwareIds")
    public void testPaid(IInAppBillingService service) {

        try {

            Bundle ownedItems = mService.getPurchases(3, getPackageName(), "subs", null);
            Log.v("Bundle test", ownedItems.toString());

            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> ownedSkus =
                        ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String> purchaseDataList =
                        ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String> signatureList =
                        ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                String continuationToken =
                        ownedItems.getString("INAPP_CONTINUATION_TOKEN");


                if (ownedSkus.contains("psaximosubscription") || ownedSkus.contains("psaximoyear")) {
                    Log.v("subscription", "owned!");
                    //app proceeds with function
                } else {

                    if (Settings.Secure.getString(getApplicationContext().getContentResolver(),
                            Settings.Secure.ANDROID_ID).equals("f5c9bbebd5305df0") ||
                            Settings.Secure.getString(getApplicationContext().getContentResolver(),
                                    Settings.Secure.ANDROID_ID).equals("5fffed1b89366878")) {
                        Log.v("device number", "erics device");
                    } else {
                        Log.v("subscription", "not owned");
                        //there is no subscription, so the app starts the purchase page
//                        Intent buyIntent = new Intent(this, PurchaseActivity.class);
//                        startActivity(buyIntent);

                    }

                }

                // if continuationToken != null, call getPurchases again
                // and pass in the token to retrieve more items
            }


        } catch (RemoteException e) {
            e.printStackTrace();


        }

    }

    public void makePurchase() {


    }


    public void stocks(View view) {
        String percentString = percentSpinner.getSelectedItem().toString();
        String volumeString = volumeSpinner.getSelectedItem().toString();
        int sector = sectorSpinner.getSelectedItemPosition();
        String marketcapString = marketcapSpinner.getSelectedItem().toString();

        String sectorString = "";
        if (percentString.equals("Any")) {
            percentString = "percent_change~gte~-100";
        }
        if (percentString.equals("+5%")) {
            percentString = "percent_change~gte~5e-2";
        }
        if (percentString.equals("+10%")) {
            percentString = "percent_change~gte~10e-2";
        }
        if (percentString.equals("+15%")) {
            percentString = "percent_change~gte~15e-2";
        }

        if (volumeString.equals("Any")) {
            volumeString = ",adj_volume~gte~0";
        }
        if (volumeString.equals("low: 0-100k")) {
            volumeString = ",adj_volume~lte~100000";
        }
        if (volumeString.equals("med 100k-1m")) {
            volumeString = ",adj_volume~gte~100000";
        }
        if (volumeString.equals("high 1m up")) {
            volumeString = ",adj_volume~gte~1000000";
        }

        if (marketcapString.equals("Any")) {
            marketcapString = "";
        }
        if (marketcapString.equals("0 - 100m")) {
            marketcapString = ",marketcap~lte~100e6";
        }
        if (marketcapString.equals("100m - 1b")) {
            marketcapString = ",marketcap~gte~1e8,marketcap~lt~1e9";
        }
        if (marketcapString.equals("1b - 10b")) {
            marketcapString = ",marketcap~gte~1e9,marketcap~lt~10e9";
        }
        if (marketcapString.equals("10b - 100b")) {
            marketcapString = ",marketcap~gte~10e9,marketcap~lt~100e9";
        }
        if (marketcapString.equals("100b and up")) {
            marketcapString = ",marketcap~gte~100e9";
        }

        if (sector == 0) {
            sectorString = ",sic~gte~0000";
        }
        if (sector == 1) {
            sectorString = ",sic~gte~0100,sic~lte~0999";
        }
        if (sector == 2) {
            sectorString = ",sic~gte~1000,sic~lte~1499";
        }
        if (sector == 3) {
            sectorString = ",sic~gte~1500,sic~lte~1799";
        }
        if (sector == 4) {
            sectorString = ",sic~gte~2000,sic~lte~3999";
        }

        if (sector == 5) {
            sectorString = ",sic~gte~4000,sic~lte~4999";
        }

        if (sector == 6) {
            sectorString = ",sic~gte~5000,sic~lte~5199";
        }
        if (sector == 7) {
            sectorString = ",sic~gte~5200,sic~lte~5999";
        }

        if (sector == 8) {
            sectorString = ",sic~gte~6000,sic~lte~6799";
        }
        if (sector == 9) {
            sectorString = ",sic~gte~7000,sic~lte~8999";
        }
        if (sector == 10) {
            sectorString = ",sic~eq~3674";
        }
        if (sector == 11) {
            sectorString = ",sic~gte~9900,sic~lte~9999";
        }

        String price = priceSpinner.getSelectedItem().toString();

        if (price.equals("Any")) {
            price = ",close_price~gte~0.00";
        }
        if (price.equals("$0 to $1")) {
            price = ",close_price~lte~1";
        }
        if (price.equals("$1 to $5")) {
            price = ",close_price~gte~1,close_price~lte~5";
        }

        if (price.equals("$5 to $20")) {
            price = ",close_price~gte~5,close_price~lte~20";
        }
        if (price.equals("$20 to $100")) {
            price = ",close_price~gte~20.00,close_price~lte~100.00";
        }
        if (price.equals("$100 to $200")) {
            price = ",close_price~gte~100.00,close_price~lte~200.00";
        }
        if (price.equals("200$ and up")) {
            price = ",close_price~gte~200.00";
        }

        String eps = epsSpinner.getSelectedItem().toString();
        if (eps.equals("Any")) {
            eps = "";
        }
        if (eps.equals("0 - 1$")) {
            eps = ",cashdividendspershare~gte~1.00";
        }
        if (eps.equals("1 - 5$")) {
            eps = ",cashdividendspershare~lte~5.00,cashdividendspershare~gt~1.00";

        }
        if (eps.equals("5$ and up")) {
            eps = ",cashdividendspershare~gte~5.00";
        }

        String peratio = peratioSpinner.getSelectedItem().toString();

        if (peratio.equals("Any")) {
            peratio = ",pricetoearnings~lte~1000";
        }
        if (peratio.equals("0 - 15")) {
            peratio = ",pricetoearnings~gte~0,pricetoearnings~lt~15";
        }
        if (peratio.equals("15 - 30")) {
            peratio = ",pricetoearnings~gte~15,pricetoearnings~lte~30";
        }
        if (peratio.equals("30 and up")) {
            peratio = ",pricetoearnings~gte~30";
        }

        String debtEquity = debtEquitySpinner.getSelectedItem().toString();
        if (debtEquity.equals("Any")) {
            debtEquity = "";
        }
        if (debtEquity.equals("High (>.5)")) {
            debtEquity = ",debttoequity~gte~0.5";
        }
        if (debtEquity.equals("Low (<0.1)")) {
            debtEquity = ",debttoequity~lte~0.1";
        }

        String exchange = stockExchangeSpinner.getSelectedItem().toString();

        if (exchange.equals("Any")) {
            exchange = "";
        }
        if (exchange.equals("NYSE")) {
            exchange = ",stock_exchange~contains~NYSE";
        }
        if (exchange.equals("NASDAQ")) {
            exchange = ",stock_exchange~contains~NASDAQ";
        }

        String profitMargin = profitSpinner.getSelectedItem().toString();
        if (profitMargin.equals("Any")) {
            profitMargin = "";
        }
        if (profitMargin.equals("+5%")) {
            profitMargin = ",profitmargin~gte~5e-2";
        }
        if (profitMargin.equals("+10%")) {
            profitMargin = ",profitmargin~gte~10e-2";
        }
        if (profitMargin.equals("+20%")) {
            profitMargin = ",profitmargin~gte~20e-2";
        }
        if (profitMargin.equals("+30%")) {
            profitMargin = ",profitmargin~gte~30e-2";
        }
        String priceToBook = priceToBookSpinner.getSelectedItem().toString();
        if (priceToBook.equals("Any")) {
            priceToBook = "";
        }
        if (priceToBook.equals("0 - 1")) {
            priceToBook = ",pricetobook~lte~1";
        }
        if (priceToBook.equals("1 - 2")) {
            priceToBook = ",pricetobook~lte~2,pricetobook~gte~1";
        }
        if (priceToBook.equals("2 - 3")) {
            priceToBook = ",pricetobook~lte3,pricetobook~gte~2";
        }
        if (priceToBook.equals("3 and up")) {
            priceToBook = ",pricetobook~gte~3";
        }


        String finalString =
                "https://api.intrinio.com/securities/search?conditions="
                        + percentString + marketcapString + volumeString + sectorString
                        + eps + price + peratio + debtEquity + exchange + profitMargin + priceToBook + ",name~gte~0";


        Log.v("String Test", finalString);



        if (mInterstitialAd.isLoaded())
        {
            mInterstitialAd.show();
        }
        else
        {
            Log.v("Ads", "Interstitial not loaded");
        }


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

    public void announcment(View view)
    {

        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(MainActivity.this);
        dialogbuilder.setMessage("Unfortunately, due to the cost of data required to support Psaximo," +
                "the service will be ending on February 4th 2018. At that date " +
                "all ongoing subscriptions will be canceled. If you'd like a refund feel free to " +
                "email me at eric.zaba@gmail.com");

        dialogbuilder.setCancelable(true);
        dialogbuilder.show();

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
