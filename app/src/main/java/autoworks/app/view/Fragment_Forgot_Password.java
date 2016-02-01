package autoworks.app.view;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import autoworks.app.R;
import helpers.Global;
import helpers.HttpPOSTOnTaskCompleted;
import helpers.HttpPOSTProcess;
import autoworks.app.dialogs.TransparentProgressDialog;

public class Fragment_Forgot_Password extends Fragment implements View.OnClickListener{

    private EditText mcustomer_email;
    private String mEmail;
    private FragmentManager fragmentManager;
    private RelativeLayout button_Login;
    private static Dialog forgotpassword_progressDialog;

    public Fragment_Forgot_Password() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(3);
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

        fragmentManager = getActivity().getSupportFragmentManager();

        //show logo
        android.support.v7.app.ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        ImageView logo = (ImageView)actionBar.getCustomView().findViewById(R.id.actionBarLogo);
        logo.setVisibility(View.VISIBLE);


    }
    private View myview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AutoWorks", "oCreateView fragment_forgottenn_password");
        myview = inflater.inflate(R.layout.fragment_forgot_password, container,false);

        mcustomer_email = (EditText)myview.findViewById(R.id.customer_email);
        mEmail = mcustomer_email.getText().toString().trim();

        button_Login = (RelativeLayout) myview.findViewById(R.id.forgot_send);
        button_Login.setOnClickListener(this);

        return myview;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private Integer savedBackground =  Color.TRANSPARENT;
    private Integer savedParentID = 0;
    private Integer savedChildID = 0;
    private Integer savedRemovedIndex = 0;
    private String savedText = "";

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.forgot_send:{
                mEmail = mcustomer_email.getText().toString().trim();

                if(ValidateInput()) {
                    //comm.checkLogin(LoginChecking(mEmail, mPassword));
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("email", mEmail));

                    forgotpassword_progressDialog = TransparentProgressDialog.createProgressDialog(this.getActivity());
                    forgotpassword_progressDialog.show();


                    HttpPOSTProcess forgottenAsync = new HttpPOSTProcess(MainActivity.getDataURL2, MainActivity.forgotten_password_tag, params, new HttpPOSTOnTaskCompleted() {
                        @Override
                        public void onHttpPOSTOnTaskCompleted(JSONObject json) {
                            try {
                                if (json!=null && !json.isNull(MainActivity.KEY_RESULT)) {
                                    if(json.getString(MainActivity.KEY_RESULT).equals("1")) {
                                        MainActivity.forgotten_password_text = getString(R.string.forgotten_password_text);

                                        if(forgotpassword_progressDialog != null) {
                                            forgotpassword_progressDialog.dismiss();
                                        }

                                        Fragment_Login fragment_login = new Fragment_Login();
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.container, fragment_login)
                                                .addToBackStack("addToBackStack fragment_home_product")
                                                .commit();
                                    }
                                }
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                break;
            }
        }
    }

    public boolean ValidateInput(){
        boolean allchecked = true;
        if(!mEmail.equals("")) {
            mcustomer_email.setBackgroundColor(Color.WHITE);
            mcustomer_email.setHint(mcustomer_email.getHint().toString().replace(": " + getString(R.string.error_input_emmpty), ""));
        }
        //validate
        if(mEmail.equals("") || !Global.isValidEmail(mEmail)){
            mcustomer_email.setHint(mcustomer_email.getHint().toString().replace(getString(R.string.error_input_emmpty),"").replace(": ","") + ": " + getString(R.string.error_input_emmpty));
            mcustomer_email.setBackgroundResource(R.color.error_background_color);
            allchecked = false;
        }

        return allchecked;
    }

}
