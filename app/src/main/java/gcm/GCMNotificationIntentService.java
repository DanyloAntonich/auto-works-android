package gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import autoworks.app.view.MainActivity;
import autoworks.app.R;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMNotificationIntentService extends IntentService {

    public static String KEY_RUNNING = "GCM_EXTRA_RUNNING";
    public static String KEY_ID = "GCM_EXTRA_ID";
    public static String KEY_TITLE = "GCM_EXTRA_TITLE";
    public static String KEY_MESSAGE = "GCM_EXTRA_MESSAGE";
    public static String KEY_IS_READ = "GCM_EXTRA_IS_READ";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;


    public GCMNotificationIntentService() {
        super("GcmIntentService");
        //	Intent i = new Intent()
    }

    public static final String TAG = "GCMNotificationIntentService";

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                //sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                //sendNotification("Deleted messages on server: "
                //+ extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {

				/*for (int i = 0; i < 3; i++) {
                    Log.i(TAG,
							"Working... " + (i + 1) + "/5 @ "
									+ SystemClock.elapsedRealtime());
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}

				}
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());*/
                if (MainActivity.currentUser==null)
                {
                    MainActivity.xmlToCustomer(this);
                }
                if (MainActivity.currentUser.getCustomerID() > 0) {
                    sendNotification(extras.get(Config.ID_KEY) + "", extras.get(Config.TITLE_KEY) + "", extras.get(Config.MESSAGE_KEY) + "", extras.getBoolean(Config.IS_READ_KEY));
                    sendBroadcast(extras.get(Config.ID_KEY) + "", extras.get(Config.TITLE_KEY) + "", extras.get(Config.MESSAGE_KEY) + "", extras.getBoolean(Config.IS_READ_KEY));
                }
                //Log.i(TAG, "Received: " + extras.toString());
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendBroadcast(String id, String title, String message, boolean isRead) {

        //send broadcast to Activity (PostsActivity)
        Intent intent = new Intent("myBroadcastIntent");
        intent.putExtra(GCMNotificationIntentService.KEY_ID, id);
        intent.putExtra(GCMNotificationIntentService.KEY_TITLE, title);
        intent.putExtra(GCMNotificationIntentService.KEY_MESSAGE, message);
        intent.putExtra(GCMNotificationIntentService.KEY_IS_READ, isRead);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendNotification(String id, String title, String msg, boolean isRead) {
        Log.d(TAG, "Preparing to send notification...: " + msg);
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, MainActivity.class);

        notificationIntent.putExtra(GCMNotificationIntentService.KEY_RUNNING, MainActivity.isRunning);
        notificationIntent.putExtra(GCMNotificationIntentService.KEY_ID, id);
        notificationIntent.putExtra(GCMNotificationIntentService.KEY_TITLE, title);
        notificationIntent.putExtra(GCMNotificationIntentService.KEY_MESSAGE, msg);
        notificationIntent.putExtra(GCMNotificationIntentService.KEY_IS_READ, isRead);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // notificationIntent.set

        //  notificationIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        // notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);//this line make the Intent to be launched
        // notificationIntent.setFlags(F)

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent
                , PendingIntent.FLAG_UPDATE_CURRENT);//FLAG_UPDATE_CURRENT -> use this to update Intent

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.gcm_cloud)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentInfo("GCM info")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Log.d(TAG, "Notification sent successfully.");
    }
}
