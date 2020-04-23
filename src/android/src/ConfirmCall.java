package ru.simdev.confirm.call;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ConfirmCall extends CordovaPlugin {

    public static final String TAG = "ConfirmCall";

    private static ConfirmCall instance;

    public static Context mContext;

    private static ConfirmCallStateListener phoneListener;

    private static String targetURL;
    private static String token;
    private static String phoneNumber;

    CallbackContext confirmCallback;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        this.cordova = cordova;
        mContext = cordova.getActivity().getApplicationContext();

        phoneListener = new ConfirmCallStateListener();

        instance = this;
    }

    public static ConfirmCall getInstance() {
        return instance;
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case "init":
                init(args.getString(0), args.getString(1), args.getString(2), callbackContext);
                return true;
            case "stop":
                stop(callbackContext);
                return true;
        }

        return false;
    }

    public void init(String phoneNumber, String targetURL, String token, final CallbackContext callbackContext) {
        confirmCallback = callbackContext;

        this.phoneNumber = phoneNumber;
        this.targetURL = targetURL;
        this.token = token;

        try {
            phoneListener.setCompareNumber(phoneNumber);
            TelephonyManager telephony = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
            telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
            Log.d(TAG, "set listen " + phoneNumber);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void stop(final CallbackContext callbackContext) {
        try {
            TelephonyManager telephony = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
            telephony.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }
    private void checkPermissions() {
        if (!checkPermission(Manifest.permission.READ_PHONE_STATE)) {
            ActivityCompat.requestPermissions(cordova.getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
        }

        if (!checkPermission(Manifest.permission.READ_CALL_LOG)) {
            ActivityCompat.requestPermissions(cordova.getActivity(), new String[]{Manifest.permission.READ_CALL_LOG}, 0);
        }

        if (!checkPermission(Manifest.permission.ANSWER_PHONE_CALLS)) {
            ActivityCompat.requestPermissions(cordova.getActivity(), new String[]{Manifest.permission.ANSWER_PHONE_CALLS}, 0);
        }
    }

    private boolean checkPermission(String permission) {
        int res = mContext.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void confirm(boolean confirmed) {
        cordova.getThreadPool().execute(() -> {
            boolean result = sendConfirm(confirmed);

            if (confirmCallback != null) {
                confirmCallback.success(result ? "success" : "error");
            }

            stop(confirmCallback);
        });
    }

    private boolean sendConfirm(boolean confirmed) {
        Log.d(TAG, "sendConfirm");
        if (targetURL != null && token != null) {
            Log.d(TAG, targetURL);
            Log.d(TAG, token);
            HttpURLConnection connection = null;

            try {
                URL url = new URL(targetURL);

                String urlParameters = "confirmed=" + (confirmed ? "1" : "0") + "&phone=" + URLEncoder.encode(phoneNumber, StandardCharsets.UTF_8.name());
                Log.d(TAG, urlParameters);

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", token);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));

                connection.setUseCaches(false);
                connection.setDoOutput(true);

                try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                    wr.writeBytes(urlParameters);
                }

                connection.connect();

                Log.d(TAG, "Response code: " + connection.getResponseCode());

                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
                String line;

                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();

                Log.d(TAG, "Response: " + response.toString());

                return connection.getResponseCode() == 200;

/*
                JSONObject json = new JSONObject(response.toString());
                String status = json.getString("status");

                return status.equals("ok");

 */
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        return false;
    }
}