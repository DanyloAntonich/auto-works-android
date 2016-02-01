package autoworks.app.model;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import autoworks.app.view.MainActivity;
import helpers.JSONParser;

/**
 * Created by INNOVATION on 2/19/2015.
 */
public class Notification {
    private String id;
    private String title;
    private String message;
    private boolean isRead;

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;


        if (isRead)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("customer_notification_id", getId()));
                    params.add(new BasicNameValuePair("tag", "setNotificationRead"));


                    JSONParser jsonParser = new JSONParser();
                    JSONObject json = jsonParser.getJSONFromUrl(MainActivity.getDataURL2, params);
                }
            }).start();

        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



}
