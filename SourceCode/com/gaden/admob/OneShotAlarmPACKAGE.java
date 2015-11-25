package com.gaden.admob;


import com.gaden.testad.AppParameters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class OneShotAlarmPACKAGE extends BroadcastReceiver
{
    Context context;
    AppParameters p;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        this.context=context;

        p = new AppParameters(context);

        InterstatialHelper.showAdTobackgroundIfNoOneActiveIfCount(0,context,false,false);

        InterstatialHelper.disableNewDayAlarm(context);
        InterstatialHelper.setNewDayAllarmTasker(context,23, 0);
        context.startService(new Intent(context, AppMonitor.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }








}
