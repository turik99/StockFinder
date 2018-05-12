package com.ericz.stockfinder;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class StockActivity extends AppCompatActivity {

    private JSONObject stock;
    private String html;
    private  float[] yData;
    private SparkView sparkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        try
        {

            final Button day = findViewById(R.id.dayButton);
            final Button month = findViewById(R.id.monthButton);
            final Button threeMonth = findViewById(R.id.threeMonthButton);
            final Button sixMonth = findViewById(R.id.sixMonthButton);
            final Button year = findViewById(R.id.yearButton);
            final Button fiveYear = findViewById(R.id.fiveYearButton);

            stock = new JSONObject(getIntent().getStringExtra("object"));
            Log.v("objet test", getIntent().getStringExtra("object"));
            TextView volume = (TextView) findViewById(R.id.volumeTextStock);
            volume.setText(stock.get("adj_volume").toString());

            TextView peratio = (TextView) findViewById(R.id.peRatioTextStock);
            peratio.setText(stock.get("pricetoearnings").toString());

            TextView name = (TextView) findViewById(R.id.stockNameActivity);
            name.setText(stock.getString("name"));
            TextView ticker = findViewById(R.id.tickerTextStock);
            ticker.setText(stock.getString("ticker"));
            TextView sector = (TextView) findViewById(R.id.sectorTextStock);
            sector.setText(getIntent().getStringExtra("sector"));
            TextView descriptionText = (TextView) findViewById(R.id.aboutText);

            TextView change = findViewById(R.id.stock1ActivityChange);
            change.setText(stock.getString("percent_change") + "%");
            TextView price = findViewById(R.id.stock1Activityprice);
            price.setText(stock.getString("close_price"));

            final TextView sparkViewPrice = findViewById(R.id.sparkViewPrice);
            final TextView sparkViewDetails = findViewById(R.id.sparkViewDetails);

            sparkViewPrice.setText("$" + stock.getString("close_price"));

            GetDescription getDescription = new GetDescription((String)stock.get("ticker"));
            getDescription.execute();


            sparkView = findViewById(R.id.sparkView);


            GetStockChart getStockChart = new GetStockChart(sparkView, stock.getString("ticker"), "1d");
            getStockChart.execute();

            MorphSparkAnimator morphSparkAnimator1 = new MorphSparkAnimator();
            sparkView.setSparkAnimator(morphSparkAnimator1);


            day.setBackgroundColor(getResources().getColor(R.color.colorAccent));


            day.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        GetStockChart getStockChart = new GetStockChart(sparkView, stock.getString("ticker"), "1d");
                        getStockChart.execute();

                        day.setBackgroundColor(getResources().getColor(R.color.colorAccent));

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
                        GetStockChart getStockChart = new GetStockChart(sparkView, stock.getString("ticker"), "1m");
                        getStockChart.execute();

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
                        GetStockChart getStockChart = new GetStockChart(sparkView, stock.getString("ticker"), "3m");
                        getStockChart.execute();
                        threeMonth.setBackgroundColor(getResources().getColor(R.color.colorAccent));

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
                        GetStockChart getStockChart = new GetStockChart(sparkView, stock.getString("ticker"), "6m");
                        getStockChart.execute();

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
                        GetStockChart getStockChart = new GetStockChart(sparkView, stock.getString("ticker"), "1y");
                        getStockChart.execute();
                        year.setBackgroundColor(getResources().getColor(R.color.colorAccent));

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
                        GetStockChart getStockChart = new GetStockChart(sparkView, stock.getString("ticker"), "5y");
                        getStockChart.execute();


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


            sparkView.setScrubListener(new SparkView.OnScrubListener() {
                @Override
                public void onScrubbed(Object value) {
                    if(value == null)
                    {

                        try {
                            sparkViewPrice.setText("$" + stock.getString("close_price"));
                            sparkViewDetails.setText(" ");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        try {
                            sparkViewDetails.setText(String.valueOf(new JSONObject(String.valueOf(value)).getString("label")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            sparkViewPrice.setText("$" +String.valueOf(new JSONObject(String.valueOf(value)).getDouble("open")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        public GetStockChart(SparkView sparkView, String ticker, String period)
        {
            this.sparkView = findViewById(R.id.sparkView);
            this.ticker = ticker;
            this.period = period;
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
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result)
        {

            SparkAdapter sparkAdapter = null;

            try
            {
                sparkAdapter = new SparkAdapter() {
                    @Override
                    public int getCount() {

                        try
                        {
                            return jsonArray.length();

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
                            return jsonArray.getJSONObject(index);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return "yeet";
                    }

                    @Override
                    public float getY(int index) {

                        if(period == "1d")
                        {
                            try {
                                return (float) jsonArray.getJSONObject(index).getDouble("average");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                return 0;
                            }


                        }
                        else
                        {
                            try {
                                return (float) jsonArray.getJSONObject(index).getDouble("open");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                return 0;
                            }

                        }
                    }
                };
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
            this.ticker = "https://api.intrinio.com/data_point?identifier=" + this.ticker + "&item=short_description,market_cap";


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
                mktCap.setText(jsonObject.getJSONArray("data").getJSONObject(1).getString("value"));
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

}


