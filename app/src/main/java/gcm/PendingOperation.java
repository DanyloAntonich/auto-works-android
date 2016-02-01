package gcm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import autoworks.app.view.MainActivity;

/**
 * Created by INNOVATION on 3/14/2015.
 */
public abstract class PendingOperation {
    //all pending operation will be triggered when calling run
    private static final String POOL = "PendingKeysPool";
    public abstract boolean doWork(Context context);
    public abstract String getKey();//key should have only one character
    private static boolean isRunningPendingOperation = false;
    private static Toast toast;
    public static void runPendedOperations(final Context context) {

        if (isRunningPendingOperation)
            return;
        isRunningPendingOperation = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(POOL, "start runPendedOperations");

                final SharedPreferences prefs = context.getSharedPreferences(
                        MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);

                String pendingString = prefs.getString(POOL, "");

                Log.d(POOL, "start pendingString " + pendingString);
                showToast(context, "runPendedOperations  pendingString = " + pendingString);

                for (int i = 0; i < pendingString.length(); i++) {
                    String key = pendingString.charAt(i) + "";
                    String operationClassName = prefs.getString(key, "");

                    Class<?> clazz = null;
                    try {
                        String className = PendingOperation.class.getPackage().getName() +"."+PendingOperation.class.getSimpleName()+"$"+operationClassName;
                        clazz = Class.forName(className);
                        Object pendingOperation = clazz.newInstance();
                        if (pendingOperation instanceof PendingOperation) {
                            boolean success = ((PendingOperation) pendingOperation).doWork(context);
                            if (success) {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.remove(key);//remove key
                                pendingString = pendingString.replace(key, "");//update pendingString
                                editor.putString(POOL, pendingString);
                                editor.commit();
                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                isRunningPendingOperation = false;
            }
        }).start();

    }
    private static void showToast(final Context context,final String message)
    {
        if (context instanceof Activity) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (toast != null)
                        toast.cancel();
                    toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

        }
    }

    public static void addOperation(final Context context,final PendingOperation operation) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(POOL, "addOperation " + operation.getKey());

                final SharedPreferences prefs = context.getSharedPreferences(
                        MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);

                String pendingPool = prefs.getString(POOL, "");
                if (pendingPool.contains(operation.getKey())) {
                    //already has this key
                    //that mean current operation has not finished yet
                    //exit
                    return;
                } else {
                    pendingPool += operation.getKey();
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(POOL, pendingPool);
                editor.putString(operation.getKey(), operation.getClass().getSimpleName());
                // editor.putInt(APP_VERSION, appVersion);
                editor.commit();
            }
        }).start();

    }

    public static class PendingBroadcastReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                Log.d(POOL, "has internet");
                //has internet connection
                PendingOperation.runPendedOperations(context);
            }else
            {
                Log.d(POOL, "lose internet");
            }
        }
    }

        public static class PendingRegisterGCM extends PendingOperation {
        public boolean doWork(Context context) {
            Log.d(POOL, "doWork() registerGCMAndShareWithWebServer");

            boolean success = RegisterGCM.registerGCMAndShareWithWebServer(context);

            showToast(context, "doWork() registerGCMAndShareWithWebServer  success = " + success);

            return success;
        }

        @Override
        public String getKey() {
            return "R";
        }

    }

    public static class PendingUnRegisterGCM extends PendingOperation {
        public boolean doWork(Context context) {
            Log.d(POOL, "doWork() unRegisterGCMAndShareWithWebServer");
            boolean success = RegisterGCM.unRegisterGCMAndShareWithWebServer(context);
            showToast(context, "doWork() unRegisterGCMAndShareWithWebServer success = " + success);

            return  success;
        }
        @Override
        public String getKey() {
            return "U";
        }
    }
    //get all stored pending posts and send
   /* public static void doPendingPosts(Activity activity, String key)
    {
        final SharedPreferences prefs = activity.getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        prefs.
        String registrationId = prefs.getString(REG_ID, "");

        return registrationId;
    }

    public static void storePendingPost(Activity activity, String key, List<NameValuePair> params, String tag) {
        final SharedPreferences prefs = activity.getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        for (NameValuePair nameValuePair : params) {

            editor.putString(key +"-" + nameValuePair.getName(), nameValuePair.getValue());
        }
        editor.putString(key +"-TAG", tag);
        // editor.putInt(APP_VERSION, appVersion);
        editor.commit();
    }*/
}
