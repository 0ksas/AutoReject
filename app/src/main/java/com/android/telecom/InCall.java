
package com.android.telecom;

import android.os.Build;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.telephony.PhoneNumberUtils;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import com.example.rework.MainActivity;

import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.N)
public class InCall extends CallScreeningService {

    @Override
    public void onScreenCall(Call.Details details) {
        String phoneNumber = details.getHandle().getSchemeSpecificPart();
        String formatedPhoneNumber = PhoneNumberUtils.formatNumber(phoneNumber, Locale.getDefault().getCountry());

        //Code for the green-list.
        if(MainActivity.isGreenListActive(getApplicationContext()))
        {if(((formatedPhoneNumber != null && MainActivity.containsGreenList(formatedPhoneNumber, getApplicationContext()))
                || MainActivity.containsGreenList(phoneNumber, getApplicationContext()))) return;
        else{
            respondToCall(details, new CallResponse.Builder().setDisallowCall(true).setRejectCall(true).build());
            Toast.makeText(getApplicationContext(), "Phone call from " + formatedPhoneNumber + " rejected", Toast.LENGTH_SHORT).show();
        }}

        //Code for the auto-reject list.
        if(MainActivity.isBlackListActive(getApplicationContext()))
        {if((formatedPhoneNumber != null && MainActivity.containsList(formatedPhoneNumber, getApplicationContext()))
                || MainActivity.containsList(phoneNumber, getApplicationContext())
        ) {
            respondToCall(details, new CallResponse.Builder().setDisallowCall(true).setRejectCall(true).build());
            Toast.makeText(getApplicationContext(), "Phone call from " + formatedPhoneNumber + " rejected", Toast.LENGTH_SHORT).show();
        }}
    }
}
