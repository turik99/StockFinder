package com.ericz.stockfinder;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.json.JSONObject;
import org.jsoup.Jsoup;

public class StockActivity extends AppCompatActivity {

    private JSONObject stock;
    private String html;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        try
        {
            stock = new JSONObject(getIntent().getStringExtra("object"));
            Log.v("objet test", getIntent().getStringExtra("object"));
            TextView volume = (TextView) findViewById(R.id.volumeTextStock);
            volume.setText("Volume: " + stock.get("adj_volume").toString());
            final CardView card = (CardView) findViewById(R.id.descriptionCardView);
            TextView peratio = (TextView) findViewById(R.id.peRatioTextStock);
            peratio.setText("P/E Ratio: " + stock.get("pricetoearnings").toString());
            TextView ticker = (TextView) findViewById(R.id.tickerTextStock);
            ticker.setText(stock.getString("name"));
            TextView sector = (TextView) findViewById(R.id.sectorTextStock);
            sector.setText("Sector: " + getIntent().getStringExtra("sector"));
            final TextView descriptionText = (TextView) findViewById(R.id.descriptionText);
            String html = "<!-- TradingView Widget BEGIN -->\n" +
                    "<script type=\"text/javascript\" src=\"https://d33t3vvu2t2yu5.cloudfront.net/tv.js\"></script>\n" +
                    "<script type=\"text/javascript\">\n" +
                    "new TradingView.widget({\n" +
                    "  \"autosize\": true,\n" +
                    "  \"symbol\": \" " + stock.get("ticker")+ "\",\n" +
                    "  \"interval\": \"W\",\n" +
                    "  \"timezone\": \"Etc/UTC\",\n" +
                    "  \"theme\": \"Black\",\n" +
                    "  \"style\": \"2" +
                    "\",\n" +
                    "  \"locale\": \"en\",\n" +
                    "  \"toolbar_bg\": \"#f1f3f6\",\n" +
                    "  \"enable_publishing\": false,\n" +
                    "  \"save_image\": false,\n" +
                    "  \"hideideas\": true\n" +
                    "});\n" +
                    "</script>\n" +
                    "<!-- TradingView Widget END -->\n";

            final WebView webView = (WebView) findViewById(R.id.webView) ;
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl("https://d33t3vvu2t2yu5.cloudfront.net/tv.js");
            webView.loadData(html, "text/html", "utf-8");

            WebSettings settings = webView.getSettings();
            settings.setDomStorageEnabled(true);

            webView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return (event.getAction() == MotionEvent.ACTION_MOVE);
                }
            });


            GetDescription getDescription = new GetDescription((String)stock.get("ticker"));
            getDescription.execute();

            ScrollView scroll = (ScrollView)findViewById(R.id.stockViewScrollView);

            descriptionText.setMaxLines(5);
            final Button moreButton = (Button) findViewById(R.id.moreButton);
            moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (descriptionText.getMaxLines() == 5)
                    {

                        descriptionText.setMaxLines(100);
                        moreButton.setText("Less");

                    }
                    else
                    {
                        moreButton.setText("More");
                        descriptionText.setMaxLines(5);
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
            description = (TextView) findViewById(R.id.descriptionText);
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

}


