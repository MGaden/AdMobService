package com.gaden.admob;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class AppMonitor extends Service
{

    private String TAG = "AppMonitor";

    TimerTask task;
    Timer timer;


    BroadcastReceiver broadcast = new BroadcastReceiver() {

        //When Event is published, onReceive method is called
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            }
            else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
            {
                InterstatialHelper.showAdTobackgroundIfNoOneActiveIfCount(0,context,false,true);

            }

        }
    };


    @Override
    public void onCreate()
    {
        registerReceiver(broadcast, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(broadcast, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        task = new TimerTask() {
            @Override
            public void run()
            {
                Log.d(TAG, "RUN SERVICE NOTHING every 5 seconds");
            }
        };
        super.onCreate();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        timer = new Timer();
        try {
            timer.schedule(task, 1000, 5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onDestroy()
    {
        timer.cancel();
        timer = null;
        unregisterReceiver(broadcast);
        super.onDestroy();
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
