package autoworks.app.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inscripts.callbacks.Callbacks;
import com.inscripts.cometchat.sdk.CometChat;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import autoworks.app.R;
import autoworks.app.controller.AutoWorksApplication;
import autoworks.app.view.cometchat.ChatActivity;
import helpers.HttpPOSTOnTaskCompleted;
import helpers.HttpPOSTProcess;
import autoworks.app.dialogs.TransparentProgressDialog;
import helpers.UserFunctions;

public class Fragment_Contact_Us extends Fragment implements View.OnClickListener{

    private EditText mEditContactTitle, mEditContactBody;
    private String mContactTitle, mContactBody;
    private Button mContactSend, mChat;
    private static Dialog contactus_progressDialog;

    public Fragment_Contact_Us() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(4);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //show logo
        android.support.v7.app.ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        ImageView logo = (ImageView)actionBar.getCustomView().findViewById(R.id.actionBarLogo);
        logo.setVisibility(View.VISIBLE);

    }
    private View myview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AutoWorks", "oCreateView fragment_register");
        myview = inflater.inflate(R.layout.fragment__contact__us, container,false);

        mEditContactTitle = (EditText)myview.findViewById(R.id.contact_tittle);
        mEditContactBody = (EditText)myview.findViewById(R.id.contact_body);
        mContactSend = (Button)myview.findViewById(R.id.contact_send);
        mContactSend.setOnClickListener(this);
        mChat = (Button)myview.findViewById(R.id.rl_contact_chat);
        mChat.setOnClickListener(this);

        return myview;
    }

    public boolean ValidateInput(){
        boolean allchecked = true;
        if(!mContactTitle.equals("")) {
            mEditContactTitle.setBackgroundResource(R.drawable.contact_edittext_background_style);
            mEditContactTitle.setHint(mEditContactTitle.getHint().toString().replace(": " + getString(R.string.error_input_emmpty), ""));
        }
        if(!mContactBody.equals("")) {
            mEditContactTitle.setBackgroundResource(R.drawable.contact_edittext_background_style);
            mEditContactBody.setHint(mEditContactBody.getHint().toString().replace(": " + getString(R.string.error_input_emmpty), ""));

        }
        //validate
        if(mContactTitle.equals("")){
            mEditContactTitle.setHint(mEditContactTitle.getHint().toString().replace(getString(R.string.error_input_emmpty),"").replace(": ","") + ": " + getString(R.string.error_input_emmpty));
            mEditContactTitle.setBackgroundResource(R.color.error_background_color);
            allchecked = false;
        }
        if(mContactBody.equals("")){
            allchecked = false;
            mEditContactBody.setHint(mEditContactBody.getHint().toString().replace(getString(R.string.error_input_emmpty),"").replace(": ","") + ": " + getString(R.string.error_input_emmpty));
            mEditContactBody.setBackgroundResource(R.color.error_background_color);
        }

        return allchecked;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.contact_send:{

                mContactTitle = mEditContactTitle.getText().toString();
                mContactBody = mEditContactBody.getText().toString();

                if(ValidateInput()) {

                    contactus_progressDialog = TransparentProgressDialog.createProgressDialog(this.getActivity());
                    contactus_progressDialog.show();

                    //comm.checkLogin(LoginChecking(mEmail, mPassword));
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("title", mContactTitle));
                    params.add(new BasicNameValuePair("content", mContactBody));

                    HttpPOSTProcess contactusAsync = new HttpPOSTProcess(MainActivity.getDataURL2, MainActivity.contact_us_tag, params, new HttpPOSTOnTaskCompleted() {
                        @Override
                        public void onHttpPOSTOnTaskCompleted(JSONObject json) {
                            try {
                                if (json!=null && !json.isNull(MainActivity.KEY_RESULT)) {
                                    if(json.getString(MainActivity.KEY_RESULT).equals("1")) {

                                        if(contactus_progressDialog != null) {
                                            contactus_progressDialog.dismiss();
                                        }

                                        //clear text
                                        mEditContactTitle.setText("");
                                        mEditContactBody.setText("");

                                        //make toast
                                        Toast.makeText(getActivity().getBaseContext(), R.string.success_contactus,
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }
                            catch (JSONException e) {
//                                loginCometChat();
                                e.printStackTrace();
                            }
                        }
                    });
                }

                break;
            }
            case R.id.rl_contact_chat:
                if(checkLogin()) {
//                loginCometChat();
                }

                break;
        }
    }
    private boolean checkLogin() {
        //check login
        return UserFunctions.isUserLoggedInThenSwitch(getActivity().getSupportFragmentManager());
    }
    private void loginCometChat() {

        AutoWorksApplication.cometChat.login(" http://test.auto-works.cc/cometchat/", "paypal@auto-works.cc", "83ma63m1ca", new Callbacks() {
            @Override
            public void successCallback(JSONObject success) {
                try {
                    if (success.getString("message").equals("Login successful")) {

                        // Create custom dialog object
                        final Dialog dialog = new Dialog(getActivity());
                        // Include dialog.xml file
                        dialog.setContentView(R.layout.item_select_dialoge);
                        // Set dialog title
                        dialog.setTitle(getContext().getResources().getString(R.string.choose_label));

                        // set values for custom dialog components -
                        final RadioButton rdTech = (RadioButton)dialog.findViewById(R.id.rd_technical);
                        final RadioButton rdBilling = (RadioButton)dialog.findViewById(R.id.rd_billing);



                        Button choose = (Button) dialog.findViewById(R.id.btn_choose);
                        // if decline button is clicked, close the custom dialog
                        choose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent toChat = new Intent(getContext(), ChatActivity.class);
                                int type = -1;
                                if (rdBilling.isChecked()) {
                                    type = 1;
                                } else if (rdTech.isChecked()) {
                                    type = 0;
                                }
                                if (type != -1) {
                                    toChat.putExtra("type", type);
                                    getContext().startActivity(toChat);
                                }
                                // Close dialog
                                dialog.dismiss();
                            }
                        });

                        Button cancle = (Button) dialog.findViewById(R.id.btn_cancel);
                        // if decline button is clicked, close the custom dialog
                        cancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Close dialog
                                dialog.dismiss();
                            }
                        });
                        dialog.show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failCallback(JSONObject fail) {
            }
        });
    }
}
