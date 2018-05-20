package com.ericz.stockfinder;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;
import com.robinhood.spark.animation.LineSparkAnimator;
import com.robinhood.spark.animation.MorphSparkAnimator;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class StockActivity extends AppCompatActivity {

    private JSONObject stock;
    private String html;
    private  float[] yData;
    private SparkView sparkView;
    private ShortNumberFormatter snFormatter = new ShortNumberFormatter();
    private boolean dayOn;
    private int listSize;
    private JSONArray mainArray;
    private DecimalFormat decimalFormat = new DecimalFormat("%#0.000");

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        Intent intent = getIntent();

        try
        {

            final DecimalFormat df = new DecimalFormat("###.##");


            final Button day = findViewById(R.id.dayButton);
            final Button month = findViewById(R.id.monthButton);
            final Button threeMonth = findViewById(R.id.threeMonthButton);
            final Button sixMonth = findViewById(R.id.sixMonthButton);
            final Button year = findViewById(R.id.yearButton);
            final Button fiveYear = findViewById(R.id.fiveYearButton);

            stock = new JSONObject(getIntent().getStringExtra("object"));
            Log.v("objet test", getIntent().getStringExtra("object"));
            TextView volume = (TextView) findViewById(R.id.volumeTextStock);
            volume.setText(snFormatter.format(stock.get("adj_volume").toString()));

            TextView peratio = (TextView) findViewById(R.id.peRatioTextStock);
            peratio.setText(stock.get("pricetoearnings").toString());


            TextView name = (TextView) findViewById(R.id.stockNameActivity);
            String nameString = stock.getString("name");
            if (nameString.length()>=15)
            {
                name.setText(stock.getString("name").substring(0, 15) + "...");

            }
            else
            {
                name.setText(nameString);
            }
            TextView ticker = findViewById(R.id.tickerTextStock);
            ticker.setText(stock.getString("ticker"));
            TextView sector = (TextView) findViewById(R.id.sectorTextStock);
            sector.setText(getIntent().getStringExtra("sector"));
            TextView descriptionText = (TextView) findViewById(R.id.aboutText);

            TextView change = findViewById(R.id.stock1ActivityChange);
            final String percentChange = getIntent().getStringExtra("percent_change");

            Log.v("percent change test", percentChange);
            if (percentChange.contains("-"))
            {
                change.setText(percentChange);
                change.setTextColor(Color.RED);
            }
            else if (percentChange.contains("+"))
            {
                change.setText(percentChange);
                change.setTextColor(Color.GREEN);
            }
            else
            {
                change.setText("0.0");
            }

            TextView price = findViewById(R.id.stock1Activityprice);
            price.setText(df.format(Float.valueOf(stock.getString("close_price"))));

            final TickerView sparkViewPrice = findViewById(R.id.sparkViewPrice);
            final TextView sparkViewDetails = findViewById(R.id.sparkViewDetails);
            sparkViewPrice.setCharacterLists(TickerUtils.provideNumberList());

            sparkViewPrice.setText("$" + df.format(Float.valueOf(stock.getString("close_price"))));

            GetDescription getDescription = new GetDescription((String)stock.get("ticker"));
            getDescription.execute();



            sparkView = findViewById(R.id.sparkView);


            mainArray = new JSONArray();
            GetStockChart getStockChart = new GetStockChart(sparkView, sparkViewDetails, stock.getString("ticker"), "1d", percentChange);
            getStockChart.execute();
            this.dayOn = true;


            final DecimalFormat percentFormat = new DecimalFormat("0.00%");

            day.setBackgroundColor(getResources().getColor(R.color.colorAccent));




            sparkView.setScrubListener(new SparkView.OnScrubListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onScrubbed(Object value) {
                    if(value == null)
                    {
                        if (dayOn)
                        {
                            try {
                                sparkViewPrice.setText("$" + df.format(Float.valueOf(stock.getString("close_price"))));
                                sparkViewDetails.setText(getIntent().getStringExtra("percent_change"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            try {


                                JSONObject first = mainArray.getJSONObject(0);
                                JSONObject last = mainArray.getJSONObject(mainArray.length()-1);
                                sparkViewPrice.setText("$" + df.format(Float.valueOf(stock.getString("close_price"))));

//                                sparkViewDetails.setText(last.getDouble("changeOverTime")
//                                        + " since" + first.getString("label"));

                                sparkViewDetails.setText(percentFormat.format(last.getDouble("changeOverTime"))+" since "+first.getString("label"));


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                    //SCRUBBED VALUE IS NOT NULL HERE
                    else
                    {
                        if(dayOn)
                        {

                            try
                            {
                                JSONObject first = mainArray.getJSONObject(0);
                                JSONObject last = mainArray.getJSONObject(mainArray.length()-1);

                                JSONObject jsonObject = new JSONObject(String.valueOf(value));
                                sparkViewDetails.setText(percentFormat.format(jsonObject.getDouble("marketChangeOverTime"))
                                        + " since "+ String.valueOf(first.getString("label")));
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                            try
                            {
                                sparkViewPrice.setText("$" +df.format(Float.valueOf(String.valueOf(new JSONObject(String.valueOf(value)).getDouble("marketAverage")))));
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            try
                            {
                                JSONObject jsonObject = new JSONObject(String.valueOf(value));
                                sparkViewDetails.setText(jsonObject.getString("label"));
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                            try {
                                sparkViewPrice.setText("$" +df.format(Float.valueOf(String.valueOf(new JSONObject(
                                        String.valueOf(value))
                                        .getDouble("open")))));
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            });

            day.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        GetStockChart getStockChart = new GetStockChart(sparkView, sparkViewDetails, stock.getString("ticker"), "1d", percentChange);
                        getStockChart.execute();

                        day.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                        dayOn = true;
                        month.setBackgroundColor(0);
                        threeMonth.setBackgroundColor(0);
                        sixMonth.setBackgroundColor(0);
                        year.setBackgroundColor(0);
                        fiveYear.setBackgroundColor(0);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            month.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        GetStockChart getStockChart = new GetStockChart(sparkView, sparkViewDetails, stock.getString("ticker"), "1m", percentChange);
                        getStockChart.execute();
                        dayOn = false;
                        month.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                        day.setBackgroundColor(0);
                        threeMonth.setBackgroundColor(0);
                        sixMonth.setBackgroundColor(0);
                        year.setBackgroundColor(0);
                        fiveYear.setBackgroundColor(0);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            threeMonth.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        GetStockChart getStockChart = new GetStockChart(sparkView, sparkViewDetails, stock.getString("ticker"), "3m", percentChange);
                        getStockChart.execute();
                        threeMonth.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        dayOn = false;

                        day.setBackgroundColor(0);
                        month.setBackgroundColor(0);
                        day.setBackgroundColor(0);
                        sixMonth.setBackgroundColor(0);
                        year.setBackgroundColor(0);
                        fiveYear.setBackgroundColor(0);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            sixMonth.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        GetStockChart getStockChart = new GetStockChart(sparkView, sparkViewDetails, stock.getString("ticker"), "6m", percentChange);
                        getStockChart.execute();
                        dayOn = false;

                        sixMonth.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                        month.setBackgroundColor(0);
                        threeMonth.setBackgroundColor(0);
                        day.setBackgroundColor(0);
                        year.setBackgroundColor(0);
                        fiveYear.setBackgroundColor(0);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            year.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Log.v("year graph test", "occured");
                        GetStockChart getStockChart = new GetStockChart(sparkView, sparkViewDetails,
                                stock.getString("ticker"), "1y", percentChange);
                        getStockChart.execute();
                        year.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        dayOn = false;

                        day.setBackgroundColor(0);
                        month.setBackgroundColor(0);
                        threeMonth.setBackgroundColor(0);
                        sixMonth.setBackgroundColor(0);
                        fiveYear.setBackgroundColor(0);



                    } catch (JSONException e) {
                        Log.v("year graph test", "failure");

                        e.printStackTrace();
                    }

                }
            });

            fiveYear.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        GetStockChart getStockChart = new GetStockChart(
                                sparkView, sparkViewDetails, stock.getString("ticker"), "5y", percentChange);
                        getStockChart.execute();

                        dayOn = false;

                        month.setBackgroundColor(0);
                        threeMonth.setBackgroundColor(0);
                        day.setBackgroundColor(0);
                        sixMonth.setBackgroundColor(0);
                        year.setBackgroundColor(0);

                        fiveYear.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    public class GetStockChart extends AsyncTask<String, String, String>
    {
        private String ticker;
        private String period;
        private SparkView sparkView;
        private String data;
        private JSONArray jsonArray;
        private JSONArray cleanArray;
        private TextView sparkViewDetails;
        private JSONObject first;
        private JSONObject last;
        private DecimalFormat decimalFormat = new DecimalFormat("#0.00%");
        private String percentChange;

        public GetStockChart(SparkView sparkView, TextView sparkViewDetails, String ticker, String period, String percentChange)
        {
            this.sparkView = findViewById(R.id.sparkView);
            this.ticker = ticker;
            this.period = period;
            this.sparkViewDetails = sparkViewDetails;
            this.percentChange = percentChange;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String url = "https://api.iextrading.com/1.0/stock/"+ this.ticker +"/chart/" + period;
                this.data = Jsoup.connect(url)
                        .ignoreContentType(true).execute().body();

                Log.v("IEX API test", this.data);
                Log.v("IEX API URL", url);
                jsonArray = new JSONArray(this.data);
                cleanArray = new JSONArray();


                if(period.equals("1d"))
                {
                    for (int i = 0; i<jsonArray.length(); i++)
                    {
                        if (jsonArray.getJSONObject(i).getDouble("marketAverage") != -1)
                        {
                            Log.v("data added", "added" + jsonArray.getJSONObject(i).getString("label"));
                            cleanArray.put(jsonArray.getJSONObject(i));

                        }
                    }

                    for (int i = 0; i<cleanArray.length(); i++)
                    {
                        Log.v("clean array test", cleanArray.getJSONObject(i).getString("marketAverage"));
                    }
                    StockActivity.this.listSize = cleanArray.length();

                    StockActivity.this.mainArray = cleanArray;

                }
                else
                {
                    for (int i = 0; i<jsonArray.length(); i++)
                    {
                        if (jsonArray.getJSONObject(i).getDouble("open") != -1)
                        {
                            Log.v("data added", "added" + jsonArray.getJSONObject(i).getString("label"));
                            cleanArray.put(jsonArray.getJSONObject(i));

                        }
                    }

                    for (int i = 0; i<cleanArray.length(); i++)
                    {
                        Log.v("clean array test", cleanArray.getJSONObject(i).getString("open"));
                    }
                    StockActivity.this.listSize = cleanArray.length();

                    StockActivity.this.mainArray = cleanArray;

                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @SuppressLint("SetTextI18n")
        protected void onPostExecute(String result)
        {

            SparkAdapter sparkAdapter = null;

            try
            {
                sparkAdapter = new SparkAdapter() {
                    String period = GetStockChart.this.period;


                    @Override
                    public int getCount() {

                        try
                        {
                            return cleanArray.length();

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            return 0;
                        }
                    }

                    @Override
                    public Object getItem(int index) {
                        try {
                            return cleanArray.getJSONObject(index);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return "yeet";
                    }

                    @Override
                    public float getY(int index) {
                        float lastY = 0;
                        if(period.equals("1d"))
                        {

                            try {
                                Log.v("first last y test", String.valueOf((float) cleanArray.getJSONObject(0).getDouble("marketOpen")));
                                lastY = (float) cleanArray.getJSONObject(0).getDouble("marketOpen");
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                float mktAvg = (float) cleanArray.getJSONObject(index).getDouble("marketAverage");
                                Log.v("market average test", String.valueOf(mktAvg));
                                if (mktAvg == -1.0)
                                {
                                    Log.v(" CHANGE last y test", String.valueOf(lastY));
                                    return lastY;

                                }
                                else
                                {
                                    lastY = mktAvg;
                                    return mktAvg;

                                }
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                                return lastY;
                            }


                        }
                        else
                        {
                            try {
                                return (float) cleanArray.getJSONObject(index).getDouble("open");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                return lastY;
                            }

                        }
                    }

                    @Override
                    public boolean hasBaseLine() {
                        return period.equals("1d");
                    }

                    public float getBaseLine()
                    {
                        try {
                            Log.v("mkt open baseline test", String.valueOf((float) cleanArray.getJSONObject(0).getDouble("marketOpen")));
                            return (float) cleanArray.getJSONObject(0).getDouble("marketOpen");

                        } catch (JSONException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }



                };
                first = cleanArray.getJSONObject(0);
                last = cleanArray.getJSONObject(mainArray.length()-1);

                if (dayOn)
                {
                    sparkViewDetails.setText(percentChange);
                }
                else
                {
                    sparkViewDetails.setText(decimalFormat.format(last.getDouble("changeOverTime") ) + " since " + first.getString("label"));
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }



            sparkView.setAdapter(sparkAdapter);
        }
    }



    public class GetDescription extends AsyncTask<String, String, String>
    {
        private String ticker;
        private String dataString;
        private TextView description;
        private TextView mktCap;
        public GetDescription(String ticker)
        {
            this.ticker = ticker;
        }


        protected void onPreExecute()
        {
            mktCap = findViewById(R.id.marketCap);
            description = (TextView) findViewById(R.id.aboutText);
            this.ticker = "https://api.intrinio.com/data_point?identifier=" + this.ticker + "&item=short_description,marketcap";


        }
        @Override
        protected String doInBackground(String... strings) {
            String username = "8ce8fe36e93c5bedf609fef9d63050f9";
            String password = "210addd4e3ac39f48c4ca84c50ff81c9";
            String login = username + ":" + password;

            try
            {
                dataString = Jsoup.connect(this.ticker)
                        .header("Authorization", "Basic " + Base64.encodeToString(login.getBytes(), Base64.NO_WRAP))
                        .ignoreContentType(true)
                        .execute().body();


            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
            Log.v("data string", dataString);


            return dataString;


        }
        protected void onPostExecute(String result)
        {
            try
            {
                JSONObject jsonObject = new JSONObject(result);
                description.setText(jsonObject.getJSONArray("data").getJSONObject(0).getString("value"));
                mktCap.setText(snFormatter.format(jsonObject.getJSONArray("data").getJSONObject(1).getString("value")));
            }
            catch (Exception e)
            {
                e.printStackTrace();
                description.setText("No description available");
                mktCap.setText("No Market cap listed");

            }
        }

    }

    protected void stockVote(View view)
    {
        final String appPackageName = "com.ericz.stockvote"; // getPackageName() from Context or Activity object


        try
        {
            openApp(getApplicationContext(), appPackageName);
        }
        catch (Exception e)
        {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }

        }

    }



    /** Open another app.
     * @param context current Context, like Activity, App, or Service
     * @param packageName the full package name of the app to open
     * @return true if likely successful, false if unsuccessful
     */
    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {
                return false;
                //throw new ActivityNotFoundException();
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    public class ShortNumberFormatter{

        public static final long BILLION=1000000000;
        public static final long MILLION=1000000;
        public static final long THOUSAND=1000;


        public ShortNumberFormatter(){}

        private String format(String stringValue){
            if(stringValue != null){
                try {
                    long l = (long)Double.parseDouble(stringValue.trim());
                    long millions = l/MILLION;
                    long billions = l/BILLION;

                    if (billions >= 1)
                    {
                        return billions + "B";
                    }

                    if(millions>=1)
                    {
                        return millions + "M";
                    }

                    else
                    {
                        long thousands = l/THOUSAND;
                        if(thousands>=1)
                        {
                            return thousands + "K";
                        }
                        else
                        {
                            return String.valueOf(l);
                        }
                    }
                } catch(NumberFormatException nfe) {
                    nfe.printStackTrace();
                    return "0";
                }
            }
            return "0";
        }

//        public static void main(String []args){
//            ShortNumberFormatter snFormatter = new ShortNumberFormatter();
//            System.out.println(snFormatter.format("100038"));
//            System.out.println(snFormatter.format("10.43e7"));
//            System.out.println(snFormatter.format("123"));
//        }

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();

    }


}


