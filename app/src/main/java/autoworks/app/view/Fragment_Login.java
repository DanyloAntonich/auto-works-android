package autoworks.app.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.plus.model.people.Person;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import autoworks.app.R;
import br.com.dina.oauth.instagram.InstagramApp;
import helpers.Global;
import helpers.KeyHashAndPassword;
import helpers.MyCustomFragment;
import helpers.UserFunctions;

/**
 * Created by Tan on 8/29/2014.
 */
public class Fragment_Login extends MyCustomFragment implements View.OnClickListener {

    private View myview;
    private String mEmail, mPassword;
    private EditText mcustomer_email, mcustomer_password;
    private RelativeLayout button_Login;
    private RelativeLayout button_Register;
    private TextView button_ForgetPassword;
    private TextView login_error;
    private CheckBox checkBox_RememberLogin;
    private boolean loginUsingSocial = false;
    private boolean autoMoveToRegisterFragment = false;
    public static String TAG = "Fragment_Login";
    // JSON Response node names

    private FragmentManager fragmentManager;

    public Fragment_Login(){
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        loginUsingSocial = false;
        ((MainActivity) activity).onSectionAttached(3);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreateView()
        Log.d("AutoWorks", "oCreateView fragment_login");
        myview = inflater.inflate(R.layout.fragment_login, container,false);
        setView(myview);
        return myview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fragmentManager = getActivity().getSupportFragmentManager();

        //hide logo
        android.support.v7.app.ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.onSectionAttached_fragment_login));

        mcustomer_password = (EditText) myview.findViewById(R.id.customer_password);
        mcustomer_email = (EditText) myview.findViewById(R.id.customer_email);
        //checkBox_RememberLogin = (CheckBox) myview.findViewById(R.id.checkBox_rememberlogin);

        login_error = (TextView) myview.findViewById(R.id.login_error);
        if(!MainActivity.forgotten_password_text.equals("")) {
            login_error.setVisibility(View.VISIBLE);
            login_error.setText(MainActivity.forgotten_password_text);
        }
        else {
            login_error.setVisibility(View.INVISIBLE);
            login_error.setText("");
        }

        button_Login = (RelativeLayout) myview.findViewById(R.id.login_button);
        button_Login.setOnClickListener(this);
        button_ForgetPassword = (TextView) myview.findViewById(R.id.login_forgot_button);
        button_ForgetPassword.setOnClickListener(this);
        button_Register = (RelativeLayout) myview.findViewById(R.id.registration_login_block);
        button_Register.setOnClickListener(this);
        //login_progressBar =(ProgressBar)myview.findViewById(R.id.login_progressBar);
       // login_progressBar.setVisibility(View.GONE);


       setSocialLogin();

    }
    private void setSocialLogin() {
        //social:

        facebook_loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                Fragment_Login.this.facebook_user = user;

                if (user!=null) {
                    //try to login social account
                    loginUsingSocial = true;
                    String userName= KeyHashAndPassword.encodeUsername(getActivity(), user.getId() + "facebook");
                    // String pass = KeyHashAndPassword.encodeUsername(Fragment_Login.this.getActivity(),user.getId() +"facebook");
                    UserFunctions.loginUser(Fragment_Login.this, getActivity().getSupportFragmentManager(),
                            "",userName,"", true, null);
                    // logoutAllSocialNetworks();
                    // Fragment_Login.login(Fragment_Register.this, mcustomer_email.getText().toString(), mcustomer_password.getText().toString());

                }
            }
        });

        twitter_loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with listOfCountries, which provides a TwitterSession for making API calls
                isTwitterLoggedIn = true;
                loginUsingSocial= true;
                long userid = result.data.getUserId();

                String mUsername = KeyHashAndPassword.encodeUsername(getActivity(), userid + "twitter");

                // String pass = KeyHashAndPassword.encodeUsername(Fragment_Login.this.getActivity(),userid+"twitter");
                // mcustomer_password.setVisibility(View.GONE);



                //  logoutAllSocialNetworks();
                //try to login social account
                UserFunctions.loginUser(Fragment_Login.this, getActivity().getSupportFragmentManager(),"",
                        mUsername, "", true, null);
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });

        onGoogleConnectedHandler = new IOnGoogleConnectedHandler() {
            @Override
            public void onConnected(Person person, String email) {

                loginUsingSocial= true;
                // mcustomer_email.setText(email);
                String mUsername = KeyHashAndPassword.encodeUsername(getActivity(), person.getId() + "google");//set Username = email
                //mcustomer_email.setEnabled(false);
                //  String pass = KeyHashAndPassword.encodeUsername(Fragment_Login.this.getActivity(),person.getId() +"googleplus");
                // mcustomer_password.setVisibility(View.GONE);
                //try to login social account
                // logoutAllSocialNetworks();
                UserFunctions.loginUser(Fragment_Login.this, getActivity().getSupportFragmentManager(),"",
                        mUsername, "", true, null);
            }
        };

        instagramApp.setListener(new InstagramApp.OAuthAuthenticationListener() {
            @Override
            public void onSuccess() {
                loginUsingSocial= true;
                String userid = instagramApp.getId();
                String mUsername = KeyHashAndPassword.encodeUsername(getActivity(), instagramApp.getId() + "instagram");
                // String pass = KeyHashAndPassword.encodeUsername(Fragment_Login.this.getActivity(),userid+"twitter");

                //logoutAllSocialNetworks();
                //try to login social account
                UserFunctions.loginUser(Fragment_Login.this, getActivity().getSupportFragmentManager(),"",
                        mUsername, "", true, null);
            }

            @Override
            public void onFail(String error) {

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public boolean ValidateInput(){
        boolean allchecked = true;
        if(!mEmail.equals("")) {
            mcustomer_email.setBackgroundColor(Color.WHITE);
            mcustomer_email.setHint(mcustomer_email.getHint().toString().replace(": " + getString(R.string.error_input_emmpty), ""));
        }
        if(!mPassword.equals("")) {
            mcustomer_password.setBackgroundColor(Color.WHITE);
            mcustomer_password.setHint(mcustomer_password.getHint().toString().replace(": " + getString(R.string.error_input_emmpty), ""));

        }
        //validate
        if(mEmail.equals("") || !Global.isValidEmail(mEmail)){
            mcustomer_email.setHint(mcustomer_email.getHint().toString().replace(getString(R.string.error_input_emmpty),"").replace(": ","") + ": " + getString(R.string.error_input_emmpty));
            mcustomer_email.setBackgroundResource(R.color.error_background_color);
            allchecked = false;
        }
        if(mPassword.equals("")){
            allchecked = false;
            mcustomer_password.setHint(mcustomer_password.getHint().toString().replace(getString(R.string.error_input_emmpty),"").replace(": ","") + ": " + getString(R.string.error_input_emmpty));
            mcustomer_password.setBackgroundResource(R.color.error_background_color);
        }

        return allchecked;
    }

    @Override
    public final void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:{
                mEmail = mcustomer_email.getText().toString();
                mPassword = mcustomer_password.getText().toString();

                if (ValidateInput()) {
                    //start login
                    UserFunctions.loginUser(this, getActivity().getSupportFragmentManager(), mEmail,"", mPassword, true, null);

                }
                else{
                    LoginFail();
                }

                break;
            }
            case R.id.login_forgot_button:{
                Log.d("AutoWorks","onSelected fragment_register");
                Fragment_Forgot_Password fragment_forgot_password = new Fragment_Forgot_Password();
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.container, fragment_forgot_password)
                        .addToBackStack("addToBackStack fragment_register")
                        .commit();
                break;
            }
            case R.id.registration_login_block:{
                Log.d("AutoWorks","onSelected fragment_register");
                Fragment_Register fragment_register = new Fragment_Register();
                if (!clearCacheOnStop)
                {
                    fragment_register.comeFromLogin();//come from login (with social too)
                }

                FragmentManager fm = getFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.container, fragment_register, Fragment_Register.TAG)
                        .addToBackStack("addToBackStack fragment_register")
                        .commit();
                break;
            }
        }
    }

    public void LoginFail(){
        login_error.setVisibility(View.VISIBLE);
        login_error.setText(R.string.login_error);

        if (loginUsingSocial)
        {
            //move to register fragment automatically
            clearCacheOnStop = false;//to prevent social buttons from clearing cache(logout)
            button_Register.callOnClick();
        }

    }
}
