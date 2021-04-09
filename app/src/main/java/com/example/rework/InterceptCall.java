package com.example.rework;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import androidx.annotation.RequiresApi;
import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

public class InterceptCall extends BroadcastReceiver {

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            //Toast.makeText(context, "Ringing!", Toast.LENGTH_SHORT).show();
        } catch(Exception e) {
            e.printStackTrace();
        }
        ITelephony telephonyService;
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    @SuppressLint("SoonBlockedPrivateApi") Method m = tm.getClass().getDeclaredMethod("getITelephony");
                    //Toast.makeText(context, "Getting call from " + number, Toast.LENGTH_LONG).show();
                    m.setAccessible(true);
                    telephonyService = (ITelephony) m.invoke(tm);

                    if((number != null) && MainActivity.containsList(number, context)) {
                        telephonyService.endCall();
                        //Toast.makeText(context, "Ending the call from: " + number, Toast.LENGTH_SHORT).show();
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }


    }

}

