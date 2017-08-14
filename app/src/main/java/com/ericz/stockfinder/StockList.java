package com.ericz.stockfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by ericz on 8/3/2017.
 */

public class StockList extends ArrayAdapter<String> {

    private JSONArray stocks;
    private Activity context;
    private String sicCode;
    private InterstitialAd mInterstitialAd;


    public StockList(Context context, JSONArray stocks) {
        super(context, R.layout.stock_list_single);
        this.context = (Activity) context;
        this.stocks = stocks;



        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-4430034146252858/3454610969");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

    }

    @Override
    public int getCount()
    {
        return stocks.length();
    }

    @NonNull
    public View getView(final int position, View rowView, ViewGroup parent)
    {

        LayoutInflater inflater = context.getLayoutInflater();
        ViewHolder holder;


        if (rowView == null)
        {
            holder = new ViewHolder();

            rowView = inflater.inflate(R.layout.stock_list_single, null, true);
            holder.ticker = (TextView) rowView.findViewById(R.id.tickerTextSingle);
            holder.percent = (TextView) rowView.findViewById(R.id.percentChangeList);
            holder.sector = (TextView) rowView.findViewById(R.id.sectorTextList);
            holder.price = (TextView) rowView.findViewById(R.id.stockPriceList);
            holder.name = (TextView) rowView.findViewById(R.id.companyNameList);
            rowView.setTag(holder);
        }
        else
        {
            holder  =  (ViewHolder) rowView.getTag();
        }

        try
        {
            JSONObject stock = stocks.getJSONObject(position);

            String percent = stock.get("percent_change").toString();

            final String stockString = stock.toString();
            String name = stock.getString("name");
            if (percent.contains("-"))
            {
                holder.percent.setTextColor(Color.RED);
                percent = percent.substring(1);
                holder.price.setText(stock.getString("close_price"));
                percent = String.valueOf(round(Float.valueOf(percent) * 100, 2));
                percent = "-" + percent + "%";
                holder.name.setText(name);
            }
            else
            {
                holder.price.setText(stock.getString("close_price"));

                holder.percent.setTextColor(Color.GREEN);
                percent = String.valueOf(round(Float.valueOf(percent) * 100, 2));
                percent = "+" + percent + "%";
                holder.name.setText(stock.getString("name"));


            }
            String ticker = stock.getString("ticker");
            holder.ticker.setText(ticker);
            holder.percent.setText(percent);
            final String sector = sicToIndustry(stock.getInt("sic"));
            if (name.length() > 15)
            {
                holder.name.setText(name.substring(0, 15) + "...");
            }
            if (sector.length() < 15)
            {
                holder.sector.setText(sector);
            }
            else
            {
                holder.sector.setText(sector.substring(0, 13) + "...");
            }


            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(getContext(), StockActivity.class);
                    intent.putExtra("object", stockString);
                    intent.putExtra("sector", sector);

                    getContext().startActivity(intent);

                    if (mInterstitialAd.isLoaded())
                    {
                        mInterstitialAd.show();
                    }
                }
            });


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return rowView;

    }


    static class ViewHolder
    {
        CardView cardView;
        TextView sector;
        TextView percent;
        TextView ticker;
        TextView price;
        TextView name;
    }

    public String sicToIndustry(float sicCode)
    {
        if(sicCode == 3674)
        {
            return "Semiconductors";
        }

        if (sicCode >= 100 && sicCode<= 999)
        {
            return "Agriculture, Forestry";
        }
        if (sicCode >= 1000 && sicCode <= 1499)
        {
            return "Mining";
        }
        if(sicCode >= 1500 && sicCode <= 1799){
            return "Construction";
        }
        if(sicCode >= 2000 && sicCode <= 3999)
        {
            return "Manufacturing";
        }
        if(sicCode >= 4000 && sicCode<= 4999)
        {
            return "Transportation";
        }
        if (sicCode >= 5000 && sicCode <= 5199)
        {
            return "Wholesale Trade";
        }
        if(sicCode >= 5200 && sicCode <= 5999)
        {
            return "Retail Trade";
        }
        if (sicCode >= 6000 && sicCode <= 6799)
        {
            return "Finance, Insurance, Real Estate";
        }
        if(sicCode >= 7000 && sicCode <= 8999)
        {
            return "Services";
        }
        if(sicCode >= 9100 && sicCode <=9729)
        {
            return "Public Administration";
        }
        if(sicCode >= 9730 && sicCode <= 9999)
        {
            return "Miscellaneous";
        }
        else
        {
            return "Miscellaneous";
        }

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();

    }


}
