package gcm;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;

import autoworks.app.view.MainActivity;
import helpers.JSONParser;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegisterGCM {

    private static final String ADD_REG_ID_TAG = "addRegID";
    private static final String REMOVE_REG_ID_TAG = "removeRegID";
    // Button btnGCMRegister;
    //Button btnAppShare;
    //GoogleCloudMessaging gcm;
    // Context activity;
    String regId;

    public static final String REG_ID = "regId";
    public static final String CUSTOMER_ID = "customerId";
    private static final String APP_VERSION = "appVersion";

    static final String TAG = "Register Context";

   /* public static void RegisterGCM(final Context activity) {
        // this.activity = activity;

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                registerGCMAndShareWithWebServer(activity);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);
    }

    public static void unRegisterGCM(final Context activity) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                unRegisterGCMAndShareWithWebServer(activity);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);
    }*/

    public static boolean unRegisterGCMAndShareWithWebServer(final Context activity) {
        boolean isSuccess = true;
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(activity);
        try {
            //post to server to delete reg_id from current logged in customer
            String oldRedId = getRegistrationId(activity);
            int oldCustomerId = getCustomerId(activity);
            if (!TextUtils.isEmpty(oldRedId)) {
                gcm.unregister();
                //post to delete
                isSuccess = postRegID(false, oldCustomerId +"", oldRedId, activity);
                if (isSuccess)
                    storeRegistrationId(activity, null);
            }

        } catch (IOException e) {
            isSuccess = false;
            e.printStackTrace();
        }
        return isSuccess;

    }

    public static boolean registerGCMAndShareWithWebServer(final Context activity) {
        boolean isSuccess = false;
        //register with GCM server:

        if (MainActivity.currentUser.getCustomerID() > 0) {

            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(activity);
            String regId = "";
            try {
                regId = getRegistrationId(activity);
                if (TextUtils.isEmpty(regId))
                    regId = gcm.register(Config.GOOGLE_PROJECT_ID);
                storeRegistrationId(activity, regId);
                storeCustomerId(activity, MainActivity.currentUser.getCustomerID());
                Log.d("RegisterActivity",
                        "registerGCM - successfully registered with GCM server - regId: "
                                + regId);

                //post to insert
                isSuccess = postRegID(true, MainActivity.currentUser.getCustomerID() + "", regId, activity);


            } catch (IOException ex) {
                Log.d("RegisterActivity", "Error");
                isSuccess = false;
            }


        }
        return isSuccess;

    }

    /**
     * @param adding      -> true: add to database, else remove from database
     * @param customer_id
     * @param regId
     * @param activity
     */
    private static boolean postRegID(final boolean adding, String customer_id, String regId, final Context activity) {
        boolean success = true;
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("customer_id", customer_id));
        params.add(new BasicNameValuePair("reg_id", regId));
        final String tag = adding ? ADD_REG_ID_TAG : REMOVE_REG_ID_TAG;
        params.add(new BasicNameValuePair("tag", tag));


        JSONParser jsonParser = new JSONParser();
        JSONObject json = jsonParser.getJSONFromUrl(MainActivity.getDataURL2, params);

        try {
            if (json != null && !json.isNull(MainActivity.KEY_RESULT)) {

                String result = json.getString(MainActivity.KEY_RESULT);
                if (result.equals("SUCCESSFULLY")) {
                    success = true;
                    Log.d("AutoWorks", "postRegID SUCCESSFULLY, isAdding = " + adding +", cus id = " + customer_id +", regId =" + regId);
                } else if (json.getString("result").equals("ERROR")) {

                    success = false;
                    Log.d("AutoWorks", "postRegID ERROR!, isAdding = " + adding+", cus id = " + customer_id  + ", regId =" + regId);

                }
            }
        }catch (Exception ex)
        {
            success = false;
        }
        return success;

    }

    private static void postFail(boolean adding, List<NameValuePair> params) {
        //adding to pending operations
    }







    private static String getRegistrationId(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
       /* int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }*/
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            Log.d("RegisterActivity",
                    "I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }

    private static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = context.getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);

        // editor.putInt(APP_VERSION, appVersion);
        editor.commit();
    }


    private static void storeCustomerId(Context context, int customerId) {
        final SharedPreferences prefs = context.getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(CUSTOMER_ID, customerId);

        // editor.putInt(APP_VERSION, appVersion);
        editor.commit();
    }

    private static int getCustomerId(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        int customerId = prefs.getInt(CUSTOMER_ID, 0);
        if (customerId == 0) {
            Log.i(TAG, "Customer not found.");
        }

        return customerId;
    }
}
