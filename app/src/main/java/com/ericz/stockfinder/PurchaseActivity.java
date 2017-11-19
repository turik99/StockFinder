package com.ericz.stockfinder;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.ericz.stockfinder.util.IabHelper;
import com.ericz.stockfinder.util.IabResult;
import com.ericz.stockfinder.util.Purchase;

public class PurchaseActivity extends AppCompatActivity {

    private boolean paid;
    private static final String TAG = "inappbilling";
    IabHelper mHelper;
    static final String ITEM_SKU = "psaximosubscription";

    private IInAppBillingService mService;
    private void finishThisActivity()
    {
        this.finish();
    }

    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        sharedPreferences = getSharedPreferences("preferences", MODE_APPEND);

        this.setFinishOnTouchOutside(false);


        Button button = (Button) findViewById(R.id.subbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try
                {
                    mHelper.launchSubscriptionPurchaseFlow(PurchaseActivity.this, ITEM_SKU, 10001,
                            mPurchaseFinishedListener, "mypurchasetoken");
                }

                catch (IabHelper.IabAsyncInProgressException e)
                {
                    e.printStackTrace();
                }

            }
        });







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

                    try
                    {
                        mHelper.launchSubscriptionPurchaseFlow(PurchaseActivity.this, ITEM_SKU, 10001,
                                mPurchaseFinishedListener, "mypurchasetoken");
                    }

                    catch (IabHelper.IabAsyncInProgressException e)
                    {
                        e.printStackTrace();
                    }
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

        if (resultCode == 7)
        {
            Toast.makeText(this, "you are already subscribed!", Toast.LENGTH_SHORT).show();
            finishThisActivity();
        }
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
                //do nothing lol
            }
            else if (purchase.getSku().equals(ITEM_SKU)) {
                Log.v("SUCCESS", "SUCCESS!!!!!!!!!!!");
                finishThisActivity();

            }
        }
    };

    @Override
    public void onBackPressed()
    {

    }

}
