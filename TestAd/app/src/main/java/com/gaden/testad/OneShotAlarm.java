package com.gaden.testad;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gaden.adApp.AdMobInit;
import com.gaden.admob.InterstatialHelper;
import com.gaden.admob.AppMonitor;
import com.gaden.admob.MyConstant;

public class OneShotAlarm extends BroadcastReceiver
{
    public static final String KEYMAIN = "keyTimer";
    Context context;
    AppParameters p;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        this.context=context;

        p = new AppParameters(context);

        int i=0;

        if(intent.getAction() == null)
        {
            if(intent!=null)
                i  = intent.getExtras().getInt(KEYMAIN);

        }

        if(i==-888)
        {
            InterstatialHelper.AdResetHandler(context, MyConstant.InterstatialKey.length);
            AdMobInit.getInstance().initializeAdMob();
        }
        else
        {
            AdMobInit.getInstance().refreshAdMob();
            InterstatialHelper.showAdTobackgroundIfNoOneActiveIfCount(i,context,false,false);

            InterstatialHelper.disableNewDayAlarm(context);
            InterstatialHelper.setNewDayAllarmTasker(context,23, 0);
            context.startService(new Intent(context, AppMonitor.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }


    }
}
