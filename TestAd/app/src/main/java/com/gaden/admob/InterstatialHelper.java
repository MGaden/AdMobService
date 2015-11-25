package com.gaden.admob;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.gaden.testad.AppParameters;
import com.gaden.testad.OneShotAlarm;


public class InterstatialHelper  {

    private static final String TAG = "InterstatialHelper";

    public static void AdClickedHandler(Context cont) {
        AppParameters p = new AppParameters(cont);
        int count = p.getInt("adsAdmobCount", MyConstant.InterstatialKey.length);
        if(count>=1)
        {
            count --;
            p.setInt(count, "adsAdmobCount");
        }

    }
    public static void AdResetHandler(Context cont,int maxAdsCount) {
        AppParameters p = new AppParameters(cont);
        p.setInt(maxAdsCount, "adsAdmobCount");
        Log.d(TAG, "AdResetHandler : set count to "+maxAdsCount);

    }


    public static boolean isOnline(Context context)
    {
        boolean connected = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo)
        {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    connected = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    connected = true;
        }
        return connected;
    }

    public static void showAdTobackgroundIfNoOneActiveIfCount(int keymain, Context cont,boolean isfinishing,boolean KeepOnScreen)
    {
        if(isOnline(cont))
        {
            AppParameters p = new AppParameters(cont);
            int count = p.getInt("adsAdmobCount",0);

            Log.d(TAG, "showAdCalled : Count =  "+count);

            if (isfinishing) {
                if(isAlreadyThereInterstatialInBack(cont))
                {
                }
                else
                {
                    Intent intentt = new Intent(cont, InterstatialToBackGround.class);
                    intentt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    cont.startActivity(intentt);
                }
            }
            else if(count>0)
            {
                if(keymain==-999)
                {
                        // show it Ginger Bread ONly
                        Intent intentt = new Intent(cont, InterstatialToBackGround.class);
                        intentt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        cont.startActivity(intentt);


                }
                else if(isAlreadyThereInterstatialInBack(cont))
                {
                }

                else
                {
                        if(isTimeToRequestAdd(cont))
                        {
                            Intent intentt = new Intent(cont, InterstatialToBackGround.class);
                            intentt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("keepScreenOn", KeepOnScreen);
                            intentt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            cont.startActivity(intentt);
                        }
                }

            }
            else
            {
                Log.d(TAG, "Max Ad Count reached");
            }
        }
    }


    private static boolean isAlreadyThereInterstatialInBack(Context cont)
    {

        final ActivityManager activityManager = (ActivityManager) cont.getSystemService(Context.ACTIVITY_SERVICE);
        final List<RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (int i = 0; i < recentTasks.size(); i++)
        {

            // bring to front
            if (recentTasks.get(i).baseActivity.toShortString().indexOf(InterstatialToBackGround.class.getName()) > -1) {

                return true;
            }
        }

        return false;
    }

    public static void HandleAddPesentTime(Context cont,int lastTimeAddSeenInMinutes)
    {
        AppParameters p = new AppParameters(cont);
        p.setInt(lastTimeAddSeenInMinutes, "adsAdmoblastMinute");

        Log.d(TAG, "HandleAddPesentTime : lastTime =" +lastTimeAddSeenInMinutes);
    }


    private static boolean isTimeToRequestAdd(Context cont)
    {
        AppParameters p = new AppParameters(cont);
        int timeDiff = Math.abs((p.getInt("adsAdmoblastMinute")-new Date().getMinutes()));

        if(timeDiff>=10)
        {
            Log.d(TAG, "isTimeToRequestAdd : Diff = true , diff = "+timeDiff);
            return true;
        }
        else
        {
            Log.d(TAG, "isTimeToRequestAdd : Diff = false, diff = "+timeDiff);
            return false;
        }


    }


    private static int [] remainingTime(int hr,int minutes)
    {
        int [] rT = new int[2] ;

        Date todayDate = new Date();
        int currentHr = todayDate.getHours();
        int currentMin = todayDate.getMinutes();

        if(hr - currentHr > 0)                      // in the same day -- still coming
            rT[0] =  hr - currentHr;
        else if(hr - currentHr == 0)                // the same hour -- may be still coming -- or may not ..... depends on minutes
        {
            if(minutes - currentMin > 0)            // in the same day -- still coming
                rT[0] =  hr - currentHr ;           // = 0
            else if(minutes - currentMin <= 0)      // not in day -- still coming but tomorrow
                rT[0] = ( 24 - currentHr) + hr;     // = 24
        }

        else                                        // not in day -- still coming but tomorrow
            rT[0] = ( 24 - currentHr) + hr;

        rT[1] = minutes - currentMin;

        if(rT[1] < 0)
        {
            rT[0]--;
            rT[1] = 60 + rT[1] ;        //rT[1] is negative
        }
        Log.d(TAG, "remaing h = "+rT[0]+"m =" +rT[1]);
        return rT;
    }


    public static void disableNewDayAlarm(Context context)
    {
        AlarmManager am = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        Intent intent4 = new Intent(context, OneShotAlarm.class).putExtra(OneShotAlarm.KEYMAIN, -888).addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);;
        PendingIntent pIntent4 = PendingIntent.getBroadcast(context,4, intent4, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pIntent4);
        Log.d(TAG, "NewDayAlarm Alarm off");
    }

    public static void setNewDayAllarmTasker(Context context,int hour, int minute)
    {

        Log.d(TAG, "NewDay AllarmTasker, Alarm Set @ " + hour + ":" + minute);

        AlarmManager am = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        Intent intent4 = new Intent(context, OneShotAlarm.class).putExtra(OneShotAlarm.KEYMAIN, -888).addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        PendingIntent pIntent4 = PendingIntent.getBroadcast(context,4, intent4, PendingIntent.FLAG_UPDATE_CURRENT);



        int[] remaining = remainingTime(hour,minute);
        int minutes = remaining[1];
        int hours = remaining[0];
        int remaingMinutes = minutes + hours*60;

        // We want the alarm to go off "remaingMinutes" seconds from now.
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(System.currentTimeMillis());
        calendar1.add(Calendar.MINUTE, remaingMinutes);

        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(),24 *60 * 60 *1000, pIntent4);
    }







}
