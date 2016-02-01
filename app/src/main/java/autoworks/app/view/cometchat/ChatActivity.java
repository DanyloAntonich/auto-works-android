package autoworks.app.view.cometchat;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.inscripts.callbacks.Callbacks;
import com.inscripts.callbacks.SubscribeCallbacks;
import com.inscripts.enums.Languages;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import autoworks.app.R;
import autoworks.app.controller.AutoWorksApplication;

public class ChatActivity extends Activity {


    private ListView chatListView;
    private EditText etMsg;
    private Button btnSend;
    Context mContext;

    ArrayList<CometChatModel> arrMsg;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initVariables();
        initView();
    }
    private void initVariables() {
        mContext = this;
        arrMsg = new ArrayList<>();
        type = getIntent().getIntExtra("type", 0);
    }
    private void initView() {
        chatListView = (ListView)findViewById(R.id.chat_list);
        btnSend = (Button)findViewById(R.id.chat_btnSend);
        etMsg = (EditText)findViewById(R.id.chat_compose_msg);
    }

    private void subscribe() {
        /*When boolean flag is set to true you will receive emoji tags (e.g :smiley: format) instead of entire html content for emoji messages.
        Once subscription in successful, you will start receiving your profile information,
        updated buddy list, one-on-one chat messages and announcements in their respective callback methods.
        The details about all the callbacks are as follows:*/
        AutoWorksApplication.cometChat.subscribe(true, new SubscribeCallbacks() {
            @Override
            public void onMessageReceived(JSONObject receivedMessage) {
                CometChatModel cometChatModel = new CometChatModel();
                try {
                    JSONObject jsonObject = receivedMessage.getJSONObject("message");

                    cometChatModel.setId(jsonObject.getString("id"));/////////////////id : This is the id of message
                    cometChatModel.setMessage(jsonObject.getString("message"));///////message : This will be the message body
                    cometChatModel.setSelf(jsonObject.getInt("self"));////////////////self : This will specify that the message is sent by you or not, if self is “1” then the message is sent by you
                    cometChatModel.setOld(jsonObject.getString("old"));////////////////old : This will specify that the message is old
                    cometChatModel.setSent(jsonObject.getLong("sent"));////////////////sent : This will be the timestamp of the message
                    cometChatModel.setFrom(jsonObject.getString("from"));//////////////from : Id of user from which message is received
                    cometChatModel.setMessage_type(jsonObject.getString("message_type"));////////message_type : This will depict the type of message

                    addNewMsgToBelow(cometChatModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void gotProfileInfo(JSONObject profileInfo) {
                try {
                    CometChatUserModel cometChatUserModel = new CometChatUserModel();
                    cometChatUserModel.setId(profileInfo.getString("id"));///id : This represents the id of user who is logged in
                    cometChatUserModel.setL(profileInfo.getString("l"));///l : If the user has set any link for his profile then it will appear here
                    cometChatUserModel.setA(profileInfo.getString("a"));///a : This will be the url to your profile picture
                    cometChatUserModel.setS(profileInfo.getString("s"));///s : This is your status
                    cometChatUserModel.setM(profileInfo.getString("m"));///m : This is status message
                    cometChatUserModel.setN(profileInfo.getString("n"));///n : Name of user who is logged in
                    cometChatUserModel.setPush_channel(profileInfo.getString("push_channel"));///push_channel : If you are implementing push notification in your app then you need to use this push notification channel on which you will subscribe so that you will get push notifications from CometChat
                    cometChatUserModel.setPush_an_channel(profileInfo.getString("push_an_channel"));///push_an_channel : You have to use this push notification channel to get the push notifications for announcement sent from CometChat administration panel

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /*Note: You may get some extra parameters in the json response as per your version of CometChat, you can use them as per your requirements.
            The fields in the response are pretty same as you get for your profile. The new field is d which specifies whether a user is logged in from the device or web.
            With CometChat v5.7 you will get 2 new parameters “ls” and “lstn”. These 2 paramters provide information about last seen.
            “ls” parameter provides you the timestamp which is a 10 digit value. You can use this value to show the last seen of a user in your app.
            You can also use this value to show that a user is online if the time difference between your mobile and the obtained time is less than 1 minute or anything suitable to your need.
            “lstn” is a flag which tells whether the last seen is enabled or disabled for a user. The value “1” suggests that user has disabled last seen, so you cannot see his last seen.
            With CometChat v5.7 and CometService enabled you will find one more parameter as “ch”. This is a channel for the user which you use in isTyping() function.*/
            @Override
            public void gotOnlineList(JSONObject onlineUsers) {
//                ArrayList<CometChatUserModel> arrOnlineUsers = new ArrayList<CometChatUserModel>();
//                try {
//                    JSONArray jsonArray = onlineUsers.getJSONArray("");
//                    int length = jsonArray.length();
//                    for (int i = 0; i < length; i ++) {
//                        JSONObject object = jsonArray.getJSONObject(i);
//                        CometChatUserModel userModel = new CometChatUserModel();
//
//                        userModel.setId(object.getString("id"));
//                        userModel.setL(object.getString("l"));///l : If the user has set any link for his profile then it will appear here
//                        userModel.setA(object.getString("a"));///a : This will be the url to your profile picture
//                        userModel.setS(object.getString("s"));///s : This is your status
//                        userModel.setM(object.getString("m"));///m : This is status message
//                        userModel.setN(object.getString("n"));///n : Name of user who is logged in
//                        userModel.setD(object.getString("d"));///d : which specifies whether a user is logged in from the device or web.
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onError(JSONObject errorResponse) {
                try {
                    String code = errorResponse.getString("code");
                    String errMsg = errorResponse.getString("message");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void gotAnnouncement(JSONObject announcement) {
                try {
                    String id = announcement.getString("id");///id is the announcement id
                    String m = announcement.getString("m");///m is the message of announcement
                    String t = announcement.getString("t");///t is time of when the announcement is sent.
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//            @Override
//            public void onAVChatMessageReceived(JSONObject response) {
//            }
        });
    }
    private void addNewMsgToBelow(CometChatModel cometChatModel) {

    }
    private void sendMessage(final String receiverUserId, String ms) {
        AutoWorksApplication.cometChat.sendMessage(receiverUserId, ms, new Callbacks() {
            @Override
            public void successCallback(JSONObject response) {
                try {
                    String msgId = response.getString("id");///id is the message id
                    String msg = response.getString("m");///m will the message which you sent.
                    ////You can also get "from" field in the json response which will contain the id of the user to whom you sent the message.
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failCallback(JSONObject response) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void sendImage(Bitmap bitmap, String receiverUserId) {
        AutoWorksApplication.cometChat.sendImage(bitmap, receiverUserId, new Callbacks() {
            @Override
            public void successCallback(JSONObject response) {
                try {
                    String msgId = response.getString("id");///id is the message id
                    String original_file = response.getString("original_file");/// filepath of the image is which is sent..

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failCallback(JSONObject response) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void sendVideo(File file, String receiverUserId) {
//        AutoWorksApplication.cometChat.sendVideo("/storage/sdcard0/video1.mp4", "15", new Callbacks() {
        AutoWorksApplication.cometChat.sendVideo(file, receiverUserId, new Callbacks() {
            @Override
            public void successCallback(JSONObject response) {
                try {
                    String msgId = response.getString("id");///id is the message id
                    String original_file = response.getString("original_file");/// filepath of the image is which is sent..

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failCallback(JSONObject response) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void sendAudio(File file, String receiverUserId) {
//        AutoWorksApplication.cometChat.sendVideo("/storage/sdcard0/video1.mp4", "15", new Callbacks() {
        AutoWorksApplication.cometChat.sendAudioFile(file, receiverUserId, new Callbacks() {
            @Override
            public void successCallback(JSONObject response) {
                try {
                    String msgId = response.getString("id");///id is the message id
                    String original_file = response.getString("original_file");/// filepath of the image is which is sent..

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failCallback(JSONObject response) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void sendFile(File file, String receiverUserId) {
//        AutoWorksApplication.cometChat.sendVideo(new File("/storage/sdcard0/document.txt"), "15", new Callbacks() {
        AutoWorksApplication.cometChat.sendFile(file, receiverUserId, new Callbacks() {
            @Override
            public void successCallback(JSONObject response) {
                try {
                    String msgId = response.getString("id");///id is the message id
                    String original_file = response.getString("original_file");/// filepath of the image is which is sent..

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failCallback(JSONObject response) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    ///Get all announcements, which are sent by the admin. The syntax is as follows:
    private void getAllAnnouncements() {
        AutoWorksApplication.cometChat.getAllAnnouncements(new Callbacks() {
            @Override
            public void successCallback(JSONObject response) {

            }

            @Override
            public void failCallback(JSONObject response) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void getOnlineUsers() {
    //       ArrayList<CometChatUserModel> arrOnlineUsers = new ArrayList<CometChatUserModel>();
//                try {
//                    JSONArray jsonArray = onlineUsers.getJSONArray("");
//                    int length = jsonArray.length();
//                    for (int i = 0; i < length; i ++) {
//                        JSONObject object = jsonArray.getJSONObject(i);
//                        CometChatUserModel userModel = new CometChatUserModel();
//
//                        userModel.setId(object.getString("id"));
//                        userModel.setL(object.getString("l"));///l : If the user has set any link for his profile then it will appear here
//                        userModel.setA(object.getString("a"));///a : This will be the url to your profile picture
//                        userModel.setS(object.getString("s"));///s : This is your status
//                        userModel.setM(object.getString("m"));///m : This is status message
//                        userModel.setN(object.getString("n"));///n : Name of user who is logged in
//                        userModel.setD(object.getString("d"));///d : which specifies whether a user is logged in from the device or web.
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
    }
    private void getChatHistory(Long UserId, Long MsgId) {
        AutoWorksApplication.cometChat.getChatHistory(UserId,MsgId,new Callbacks() {
            @Override
            public void successCallback(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("history");
                    int lenth = jsonArray.length();

                    for (int i = 0; i < lenth; i ++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        CometChatModel cometChatModel = new CometChatModel();

                        cometChatModel.setId(jsonObject.getString("id"));/////////////////id : This is the id of message
                        cometChatModel.setMessage(jsonObject.getString("message"));///////message : This will be the message body
                        cometChatModel.setSelf(jsonObject.getInt("self"));////////////////self : This will specify that the message is sent by you or not, if self is “1” then the message is sent by you
                        cometChatModel.setOld(jsonObject.getString("old"));////////////////old : This will specify that the message is old
                        cometChatModel.setSent(jsonObject.getLong("sent"));////////////////sent : This will be the timestamp of the message
                        cometChatModel.setFrom(jsonObject.getString("from"));//////////////from : Id of user from which message is received
                        cometChatModel.setMessage_type(jsonObject.getString("message_type"));////////message_type : This will depict the type of message

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void failCallback(JSONObject response) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void checkCometChatInstalled(String url) {
        AutoWorksApplication.cometChat.isCometChatInstalled(url, new Callbacks() {
            @Override
            public void successCallback(JSONObject response) {
                try {
                    String url_download = response.getString("cometchat_url");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failCallback(JSONObject response) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void translateLanguage(Languages languages) {
//        AutoWorksApplication.cometChat.setTranslateLanguage(Languages.Spanish,new Callbacks() {
        AutoWorksApplication.cometChat.setTranslateLanguage(languages, new Callbacks() {
            @Override
            public void successCallback(JSONObject response) {
                try {
                    String currentLang = response.getString("Selected language");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failCallback(JSONObject response) {
            }
        });
    }
    private void unsubscribe() {
        AutoWorksApplication.cometChat.unsubscribe();
    }
    private boolean checkLogin() {
        return AutoWorksApplication.cometChat.isLoggedIn();
    }
}
