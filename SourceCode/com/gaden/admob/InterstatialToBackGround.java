package com.gaden.admob;

import java.util.Date;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.gaden.adApp.AdMobInit;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


import com.gaden.testad.AppParameters;
import com.gaden.testad.OneShotAlarm;
import com.gaden.testad.R;



public class InterstatialToBackGround extends Activity  {

    private InterstitialAd interstitial;
    boolean clickAlreadyTaken = false;
    boolean keepScreenOn = false;

    boolean adRecived,adPresent;
    BroadcastReceiver broadcast = new BroadcastReceiver() {

        //When Event is published, onReceive method is called
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(Intent.ACTION_SCREEN_ON))
            {
                if(keepScreenOn)
                {
                    if(interstitial!=null)
                    {
                        if(adRecived&&adPresent)
                        {
                            interstitial.show();
                        }
                        else
                        {
                            interstitial = null;
                            finish();
                        }
                    }
                    else
                    {
                        finish();
                    }

                }

            }
            else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
            {
                InterstatialHelper.showAdTobackgroundIfNoOneActiveIfCount(0,context,false,true);

            }

        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        if(hasFocus && fromStopState)
        {
            if(interstitial!=null)
            {
                if(adRecived&&adPresent)
                {
                    interstitial.show();
                }
                else
                {
                    interstitial = null;
                    finish();
                }
            }
            else
            {
                finish();
            }
        }
        super.onWindowFocusChanged(hasFocus);
    }

    boolean isClosingIntent = false;

    @Override
    protected void onNewIntent(Intent intent) {

        isClosingIntent = intent.getBooleanExtra("EXIT", false);
        if (isClosingIntent) {
            Log.d("InterStatialPack", "Instertaial---Force-->Close");
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_interstatial);// Create the interstitial

            keepScreenOn = getIntent().getBooleanExtra("keepScreenOn", false);

            isClosingIntent = getIntent().getBooleanExtra("EXIT", false) ;
            if (isClosingIntent) {
                finish();
                return;
            }

            interstitial = new InterstitialAd(this);
            AppParameters p = new AppParameters(this);
            int count = p.getInt("adsAdmobCount", MyConstant.InterstatialKey.length);
            if(count > 1)
            {
                interstitial.setAdUnitId(AdMobInit.getInstance().getKey(MyConstant.InterstatialKey[count - 1]));
            }
            else
            {
                interstitial.setAdUnitId(AdMobInit.getInstance().getKeyAndReset(MyConstant.InterstatialKey[0]));
            }

            interstitial.setAdListener(new ToastAdListener(this));



            // Begin loading your interstitial
            interstitial.loadAd(new AdRequest.Builder().build());

            // Set Ad Listener to use the callbacks below

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run()
                {
                    if(!keepScreenOn)
                        moveTaskToBack(false);
                }
            }, 1000);

            if(keepScreenOn)
            {
                registerReceiver(broadcast, new IntentFilter(Intent.ACTION_SCREEN_ON));
                registerReceiver(broadcast, new IntentFilter(Intent.ACTION_SCREEN_OFF));

                handler.postDelayed(timeOutFinishTask, 90000); // 1.5 minutes
            }
            else
                handler.postDelayed(timeOutFinishTask, 90000);    //1.5 minute

            handler.postDelayed(timeOutFinishTaskBIG, 3* 60*1000);
        }
        catch (Exception ex)
        {

        }

    }

    Runnable timeOutFinishTask = new Runnable() {

        @Override
        public void run() {
            Log.d("InterstatialTOback", "Interstatioal Time out reached");
            if(interstitial!=null)
            {
                if(adPresent&&adRecived)
                {
                    Log.d("InterstatialTOback", ":D Ad visible");
                }
            }
            else
            {
                finish();
            }
        }
    };
    Runnable timeOutFinishTaskBIG = new Runnable() {

        @Override
        public void run() {
            Log.d("InterstatialTOback", "Interstatioal Big Time out reached = 5 minutes");
            finish();
        }
    };
    private boolean fromStopState;
    private String TAG="Interstatial";
    public int retryCount;
    @Override
    protected void onResume()
    {
        Log.d("Interstatial", "onResume");
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        if(interstitial!=null)
        {
            interstitial=null;
        }

        try
        {
            if(keepScreenOn)
                unregisterReceiver(broadcast);
        }
        catch(Exception e)
        {

        }

        super.onDestroy();
    }
    @Override
    protected void onRestart() {
        Log.d("Interstatial", "onRestart");
        super.onRestart();
    }

    @Override
    protected void onStop() {
        Log.d("Interstatial", "onStop");
        fromStopState = true;
        super.onStop();
    }
    @Override
    protected void onStart() {
        Log.d("Interstatial", "onStart");
        super.onStart();
    }

    @Override
    public void finish() {
        Log.d("Interstatial", "finish");

        super.finish();
    }



    @TargetApi(11)
    protected void moveToFront() {
        try {
            if (Build.VERSION.SDK_INT >= 11)
            { // honeycomb
                Log.d(TAG, "----to FRONT HONEY" );
                final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                final List<RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

                for (int i = 0; i < recentTasks.size(); i++)
                {
                    // bring to front
                    if (recentTasks.get(i).baseActivity.toShortString().indexOf(InterstatialToBackGround.class.getName()) > -1) {
                        activityManager.moveTaskToFront(recentTasks.get(i).id, ActivityManager.MOVE_TASK_WITH_HOME);
                        return;
                    }
                }
            }
            else
            {
                Log.d(TAG, "----to FRONT GINGER" );
                Intent i = new Intent(InterstatialToBackGround.this, OneShotAlarm.class).putExtra(OneShotAlarm.KEYMAIN, -999);
                InterstatialToBackGround.this.sendBroadcast(i);
            }
        }
        catch (Exception ex)
        {

        }

    }

    public class ToastAdListener extends AdListener {
        private Context mContext;
        private String TAG = "ToastAdListener";

        public ToastAdListener(Context context) {
            this.mContext = context;
        }

        @Override
        public void onAdLoaded() {
try {
    if(interstitial!=null)
    {
        Log.d(TAG , "onAdLoaded()");
        adRecived = true;
        clickAlreadyTaken = false;
        interstitial.show();
        if(!keepScreenOn)
            moveToFront();
    }
 }
catch (Exception ex)
{

}
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            String errorReason = "";
            switch(errorCode) {
                case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                    errorReason = "Internal error";
                    break;
                case AdRequest.ERROR_CODE_INVALID_REQUEST:
                    errorReason = "Invalid request";
                    break;
                case AdRequest.ERROR_CODE_NETWORK_ERROR:
                    errorReason = "Network Error";
                    break;
                case AdRequest.ERROR_CODE_NO_FILL:
                    errorReason = "No fill";
                    break;
            }
            Log.d(TAG , String.format("onAdFailedToLoad(%s)", errorReason));
            if(interstitial!=null)
            {
                try {
                    // Create ad request
                    adRecived = false;
                    adPresent = false;


                    if(retryCount<10)
                    {
                        // Begin loading your interstitial
                        interstitial.loadAd(new AdRequest.Builder().build());
                        retryCount++;
                        Log.d(TAG, "Retry "+retryCount+" <--");
                    }
                    else
                    {
                        finish();
                    }
                }
                catch (Exception ex)
                {

                }

            }
        }

        @Override
        public void onAdOpened() {
            Log.d(TAG , "onAdOpened()");
            adPresent = true;
            InterstatialHelper.HandleAddPesentTime(InterstatialToBackGround.this, new Date().getMinutes()); // set time add SEEN
        }

        @Override
        public void onAdClosed() {
            Log.d(TAG , "onAdClosed()");
            if(!clickAlreadyTaken)
            {
                InterstatialHelper.AdClickedHandler(InterstatialToBackGround.this);
                clickAlreadyTaken = true;
            }
            finish();
        }

        @Override
        public void onAdLeftApplication() {
            Log.d(TAG , "onAdLeftApplication()");

            if(!clickAlreadyTaken)
            {
                InterstatialHelper.AdClickedHandler(InterstatialToBackGround.this);
                clickAlreadyTaken = true;
            }
        }
    }



}
