package com.gaden.testad;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppParameters {

    SharedPreferences AppProSettings;
    Editor editor;
    public static final String APP_PREFERENCES = "AdAppPrefs";
    Context context;

    public AppParameters(Context context)
    {
        this.context = context;
    }

    private void openConnection()
    {
        AppProSettings = context.getSharedPreferences(APP_PREFERENCES,
                Context.MODE_PRIVATE);
        editor = AppProSettings.edit();
    }

    private void closeConnection()
    {
        editor = null;
        AppProSettings = null;
    }

    public void setInt(int value,String key)
    {
        openConnection();
        editor.putInt(key, value);
        editor.commit();
        closeConnection();
    }

    public int getInt(String key)
    {
        int result = 0;
        openConnection();

        if (AppProSettings.contains(key)) {
            result = AppProSettings.getInt(key,0);
        }

        closeConnection();
        return result;
    }

    public int getInt(String key,int defValue)
    {
        int result = defValue;
        openConnection();

        if (AppProSettings.contains(key)) {
            result = AppProSettings.getInt(key,defValue);
        }

        closeConnection();
        return result;
    }


}
