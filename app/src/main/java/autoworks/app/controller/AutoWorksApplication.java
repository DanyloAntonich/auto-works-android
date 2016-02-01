package autoworks.app.controller;

import android.app.Application;
import android.content.Context;

import com.inscripts.cometchat.sdk.CometChat;

/**
 * Created by Administrator on 1/13/2016.
 */
public class AutoWorksApplication extends Application {

    public static AutoWorksApplication autoWorksApplication;
    private String COMETCHAT_API_KEY = "";
    public static CometChat cometChat;

    @Override
    public void onCreate() {
        super.onCreate();

        autoWorksApplication = this;
        cometChat = CometChat.getInstance(this, COMETCHAT_API_KEY);
    }

}
