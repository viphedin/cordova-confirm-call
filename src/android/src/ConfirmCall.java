package ru.simdev.confirm.call;

import android.content.Context;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

public class ConfirmCall extends CordovaPlugin {

    private static final String TAG = "ConfirmCall";

    public static Context mContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        this.cordova = cordova;
        mContext = cordova.getActivity().getApplicationContext();

        livetexContext = new LivetexContext(mContext);
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case "init":
                init(callbackContext);
                return true;
        }

        return false;
    }

    public void init(final CallbackContext callbackContext) {
        try {
            
        } catch (Exception e) {

        }
    }
}