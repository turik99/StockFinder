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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;

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

            final TextView sparkViewPrice = findViewById(R.id.sparkViewPrice);
            final TextView sparkViewDetails = findViewById(R.id.sparkViewDetails);

            GetDescription getDescription = new GetDescription((String)stock.get("ticker"));
            getDescription.execute();

            ScrollView scroll = (ScrollView)findViewById(R.id.stockViewScrollView);

            sparkView = findViewById(R.id.sparkView);

            GetStockChart getStockChart = new GetStockChart(sparkView, stock.getString("ticker"), "1");

            getStockChart.execute();

            List<Float> yPoints = sparkView.getYPoints();

            Log.v("y points test", yPoints.toString());
            sparkView.setScrubListener(new SparkView.OnScrubListener() {
                @Override
                public void onScrubbed(Object value) {
                    if(value == null)
                    {
                        sparkViewPrice.setText("0");
                    }
                    else
                    {
                        sparkViewDetails.setText(String.valueOf(value));
                        sparkViewPrice.setText(String.valueOf(sparkView.getY()));
                    }
                }
            });


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



    }


    public class GetDescription extends AsyncTask<String, String, String>
    {
        private String ticker;
        private String dataString;
        private TextView description;
        public GetDescription(String ticker)
        {
            this.ticker = ticker;
        }


        protected void onPreExecute()
        {
            description = (TextView) findViewById(R.id.aboutText);
            this.ticker = "https://api.intrinio.com/data_point?identifier=" + this.ticker + "&item=short_description";


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
                description.setText(jsonObject.getString("value"));

            }
            catch (Exception e)
            {
                e.printStackTrace();
                description.setText("No description available");

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
                String url = "https://api.iextrading.com/1.0/stock/"+ this.ticker +"/chart/1y";
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
                        return jsonArray.length();
                    }

                    @Override
                    public Object getItem(int index) {
                        try {
                            return jsonArray.getJSONObject(index).getString("label");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return "yeet";
                    }

                    @Override
                    public float getY(int index) {
                        try {
                            return (float) jsonArray.getJSONObject(index).getDouble("open");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return 0;
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




    public static class RandomizedAdapter extends SparkAdapter {
        private final float[] yData;
        private final Random random;

        private RandomizedAdapter() {
            random = new Random();
            yData = new float[50];
            randomize();
        }

        private void randomize() {
            for (int i = 0, count = yData.length; i < count; i++) {
                yData[i] = random.nextFloat();
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return yData.length;
        }

        @NonNull
        @Override
        public Object getItem(int index) {
            return yData[index];
        }

        @Override
        public float getY(int index) {
            return yData[index];
        }
    }
}


