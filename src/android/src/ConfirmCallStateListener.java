package ru.simdev.confirm.call;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

public class ConfirmCallStateListener extends PhoneStateListener {

    private String compareNumber;

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch(state) {
            case TelephonyManager.CALL_STATE_RINGING:
                Log.d(ConfirmCall.TAG, "RINGING");
                Log.v(ConfirmCall.TAG, incomingNumber);

                if (compareNumber != null && incomingNumber.equals(compareNumber)) {
                    endCall();

                    if (ConfirmCall.getInstance() != null) {
                        ConfirmCall.getInstance().confirm(true);
                    }
                }

                break;
        }
    }

    public void setCompareNumber(String phoneNumber)
    {
        compareNumber = phoneNumber;
    }

    private void endCall() {
        Log.d(ConfirmCall.TAG, "Try to endCall ");

        if (Build.VERSION.SDK_INT >= 28) {
            TelecomManager tm = (TelecomManager) ConfirmCall.mContext.getSystemService(Context.TELECOM_SERVICE);
            if (tm != null && ConfirmCall.mContext.checkCallingOrSelfPermission(Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_GRANTED) {
                tm.endCall();
            }
        } else {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) ConfirmCall.mContext.getSystemService(Context.TELEPHONY_SERVICE);
                Class clazz = Class.forName(telephonyManager.getClass().getName());
                Method method = clazz.getDeclaredMethod("getITelephony");
                method.setAccessible(true);
                ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);
                telephonyService.endCall();
            } catch (Exception e) {
                Log.e(ConfirmCall.TAG, e.getMessage());
            }
        }
    }
}
