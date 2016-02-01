package autoworks.app.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import autoworks.app.R;
import autoworks.app.Utilities.LocationUtility;
import autoworks.app.Utilities.Utils;
import autoworks.app.controller.AutoWorksApplication;
import autoworks.app.controller.Constant;
import autoworks.app.dialogs.TransparentProgressDialog;
import autoworks.app.model.CountryCode;
import autoworks.app.model.Country;
import autoworks.app.model.Customer;
import autoworks.app.model.Zone;

import com.facebook.android.Util;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.plus.model.people.Person;
import com.inscripts.callbacks.Callbacks;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import br.com.dina.oauth.instagram.InstagramApp;
import helpers.*;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tan on 9/19/2014.
 */
public class Fragment_Register extends MyCustomFragment implements View.OnClickListener {

    TelephonyManager telephonyManager;
    protected View myview;
    protected EditText mcustomer_email, mcustomer_password, mcustomer_firstname, mcustomer_lastname, mcustomer_mobile, mcustomer_country_code;
    protected EditText mcustomer_country, mcustomer_city;
    private RelativeLayout button_Register, button_Login;
    protected String mEmail, mPassword, mFirstname, mLastname, mCountry, mCity, mZoneId, mMobile, mMobileCode, mUsername;
    private Customer user_customer;
    private TextView register_error;
    protected Dialog progressDialog;


    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_UID = "uid";

    protected FragmentManager fragmentManager;

    private boolean comeFromLogin = false;

    public static String TAG = "Fragment_Register";

    public Fragment_Register() {

    }

    public void comeFromLogin() {
        //don't need to check for logging in again
        comeFromLogin = true;

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
//        CountryCode.test(this.getActivity().getBaseContext());

        KeyHashAndPassword.GetKeyHash(this.getActivity());
        fragmentManager = getActivity().getSupportFragmentManager();

        user_customer = new Customer();


        mcustomer_email = (EditText) myview.findViewById(R.id.customer_email);
        mcustomer_password = (EditText) myview.findViewById(R.id.customer_password);
        mcustomer_firstname = (EditText) myview.findViewById(R.id.customer_firstname);
        mcustomer_lastname = (EditText) myview.findViewById(R.id.customer_lastname);
        mcustomer_city = (EditText) myview.findViewById(R.id.customer_city);
        mcustomer_country = (EditText) myview.findViewById(R.id.customer_country);
        mcustomer_country_code = (EditText) myview.findViewById(R.id.customer_country_code);
        mcustomer_mobile = (EditText) myview.findViewById(R.id.customer_mobile);

        register_error = (TextView) myview.findViewById(R.id.register_error);


        button_Login = (RelativeLayout) myview.findViewById(R.id.registration_login_block);
        if (button_Login != null)
            button_Login.setOnClickListener(this);

        button_Register = (RelativeLayout) myview.findViewById(R.id.registration_register);
        if (button_Register != null)
            button_Register.setOnClickListener(this);


//        mcustomer_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                mcustomer_city.setBackgroundColor(Color.WHITE);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        // register_progressBar = (ProgressBar)myview.findViewById(R.id.register_progressBar);
        // register_progressBar.setVisibility(View.GONE);


        //hide logo
        android.support.v7.app.ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.onSectionAttached_fragment_register));

        //set phone number and coutry code
        //isDualSimOrNot();

        if (LocationUtility.getAddressFromCoordination(getActivity())) {
            mCountry = LocationUtility.countryName;
            mCity = LocationUtility.cityName;
            mMobileCode = LocationUtility.phoneCode;

            mcustomer_country.setText(mCountry);
            mcustomer_city.setText(mCity);
            mcustomer_country_code.setText(mMobileCode);
        }
//        String phoneNumber = Utils.getPhoneNumber();

        isDualSimOrNot();
        mcustomer_country.setBackgroundColor(Color.WHITE);
//        mcustomer_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                // your code here
//
//                Country country = (Country)parentView.getAdapter().getItem(position);
//                mcustomer_country.setBackgroundColor(Color.WHITE);
//
//                mcustomer_country_code.setText(getCountryZipCode(country.getCcode()));
//
//                if (progressDialog==null)
//                    progressDialog = TransparentProgressDialog.createProgressDialog(getActivity());
//                progressDialog.show();
//
//
//                //get Zones by country id
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("country_code", country.getCcode()));
//                HttpPOSTProcess getZoneAsync = new HttpPOSTProcess(MainActivity.getDataURL2, MainActivity.get_zone_by_country_id, params, new HttpPOSTOnTaskCompleted() {
//                    @Override
//                    public void onHttpPOSTOnTaskCompleted(JSONObject json) {
//                        try {
//                            int currentCityIndex = 0;
//                            ArrayList<Zone> zones = new ArrayList<Zone>();
//                            Zone select_zone = new Zone("", getString(R.string.spinner_select_zone));
//                            zones.add(select_zone);
//
//                            if (json != null && json.getJSONArray(MainActivity.KEY_ZONES) != null) {
//                                JSONArray arr = json.getJSONArray(MainActivity.KEY_ZONES);
//                                int lenth = arr.length();
//                                for (int i = 0; i < arr.length(); i++) {
//                                    Zone zone = new Zone();
//                                    JSONObject jsObj = arr.getJSONObject(i);
//                                    zone.setZid(jsObj.getString(MainActivity.KEY_ZONE_ID));
//                                    zone.setZname(jsObj.getString(MainActivity.KEY_ZONE_NAME));
//                                    int operandIndex = zone.getZname().indexOf('(');
//                                    if (operandIndex > 0)
//                                        zone.setZname(zone.getZname().substring(0, operandIndex));
//
//                                    zones.add(zone);
//
//                                    if (MainActivity.currentUser.getCountryCode() != null &&
//                                            MainActivity.currentUser.getCity().equals(zone.getZid())) {
//                                        currentCityIndex = zones.size() - 1;
//                                    }
//                                }
//                            }
//                            final ArrayAdapter<Zone> adapterZone = new ArrayAdapter<Zone>(getActivity().getApplicationContext(), R.layout.spinner_item, zones);
//                            mcustomer_city.setAdapter(adapterZone);
//                            mcustomer_city.setSelection(currentCityIndex);
//
//                            if(progressDialog != null) {
//                                progressDialog.dismiss();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // your code here
//            }
//
//        });
//
//        if(MainActivity.listOfCountries.size() <= 0) {
//            if (progressDialog == null)
//                progressDialog = TransparentProgressDialog.createProgressDialog(this.getActivity());
//            progressDialog.show();
//            //get country list
//            HttpPOSTProcess getCountry = new HttpPOSTProcess(MainActivity.getDataURL2, MainActivity.getCountries, new LinkedList<NameValuePair>(), new HttpPOSTOnTaskCompleted() {
//                @Override
//                public void onHttpPOSTOnTaskCompleted(JSONObject json) {
//
//                    int currentCountryIndex = 0;
//                    try {
//                        if (json != null && json.getJSONArray(MainActivity.KEY_COUNTRIES) != null) {
//                            JSONArray arr = json.getJSONArray(MainActivity.KEY_COUNTRIES);
//                            int lenth = arr.length();
//                            for (int i = 0; i < arr.length(); i++) {
//                                Country country = new Country();
//                                JSONObject jsObj = arr.getJSONObject(i);
//                                country.setCcode(jsObj.getString(MainActivity.KEY_COUNTRY_CODE_ISO));
//                                country.setCname(jsObj.getString(MainActivity.KEY_COUNTRY_NAME));
//                                //if (listOfCountries.contains(country))
//                               MainActivity.listOfCountries.add(country);
//
//                                if (MainActivity.currentUser.getCountryCode() != null &&
//                                        MainActivity.currentUser.getCountryCode().equals(country.getCcode())) {
//                                    currentCountryIndex = MainActivity.listOfCountries.size() - 1;
//                                }
//                            }
//                        }
//
//                        Country select_country = new Country("", getString(R.string.spinner_select_country));
//                        MainActivity.listOfCountries.add(select_country);
//                        final ArrayAdapter<Country> adapter = new ArrayAdapter<Country>(getActivity().getApplicationContext(), R.layout.spinner_item, MainActivity.listOfCountries);
//                        mcustomer_country.setAdapter(adapter);
//                        mcustomer_country.setSelection(currentCountryIndex);
//
//                        if(progressDialog != null) {
//                            progressDialog.dismiss();
//                        }
//
//                    } catch (Exception e) {
//                        Log.d("Exception", e.toString());
//                    }
//                }
//            });
//        }
//        else {
//            Country select_country = new Country("", getString(R.string.spinner_select_country));
//            if (!MainActivity.listOfCountries.get(MainActivity.listOfCountries.size() - 1).getCname().equals(select_country.getCname()))
//            {
//                MainActivity.listOfCountries.add(select_country);
//            }
//
//            final ArrayAdapter<Country> adapter = new ArrayAdapter<Country>(getActivity().getApplicationContext(), R.layout.spinner_item, MainActivity.listOfCountries);
//            int currentCountryIndex = 0;
//            for (int i=0;i<MainActivity.listOfCountries.size();i++)
//            {
//                if (MainActivity.currentUser.getCountryCode() != null &&
//                        MainActivity.currentUser.getCountryCode().equals(MainActivity.listOfCountries.get(i).getCcode())) {
//                    currentCountryIndex = i;
//                    break;
//                }
//            }
//            mcustomer_country.setAdapter(adapter);
//            mcustomer_country.setSelection(currentCountryIndex);
//        }

        setSocialRegister();

    }
    private void setSocialRegister() {
        if (useMyCustomFragment) {
            facebook_loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
                @Override
                public void onUserInfoFetched(GraphUser user) {
                    Fragment_Register.this.facebook_user = user;
                    if (user != null) {
                        // isFacebookLoggedIn = true;
                        Log.d("AutoWorks", "user id = " + user.getId());
                        Log.d("AutoWorks", "user name = " + user.getName());
                        isFacebookLoggedIn = true;
                        mcustomer_firstname.setText(user.getFirstName());
                        // mcustomer_firstname.setEnabled(false);
                        mcustomer_lastname.setText(user.getLastName());
                        //mcustomer_lastname.setEnabled(false);
                        mcustomer_email.setText(user.getProperty("email").toString());
                        // mUsername = mcustomer_email.getText().toString();//set Username = email
                        mUsername = KeyHashAndPassword.encodeUsername(getActivity(), user.getId() + "facebook");
                        mcustomer_email.setEnabled(false);
                        mcustomer_password.setText("123456789");
                        mcustomer_password.setVisibility(View.GONE);
                        mFirstname = mcustomer_firstname.getText().toString();
                        mLastname = mcustomer_lastname.getText().toString();
                        mEmail = mcustomer_email.getText().toString();
                        if (!comeFromLogin) {
                            //try to login social account
                            UserFunctions.loginUser(Fragment_Register.this, getActivity().getSupportFragmentManager(),"",
                                    mUsername, mcustomer_password.getText().toString(), false, new UserFunctions.LoginHandler() {
                                        @Override
                                        public void onCompleted(boolean success) {
                                            if (!success){
                                                register("");
                                            }

                                        }
                                    });
                            // Fragment_Login.login(Fragment_Register.this, mcustomer_email.getText().toString(), mcustomer_password.getText().toString());
                        }
                        else{
                            register("");
                        }



                    }
                }
            });

            twitter_loginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    // Do something with listOfCountries, which provides a TwitterSession for making API calls
                    isTwitterLoggedIn = true;
                    TwitterAuthToken authToken = result.data.getAuthToken();
                    String token = authToken.token;
                    String secret = authToken.secret;
                    String username = KeyHashAndPassword.encodeUsername(getActivity(), result.data.getUserId() + "twitter");
                    long userid = result.data.getUserId();
                    Log.d(TAG, "username = " + username + ", id = " + userid);

                    mcustomer_firstname.setText(result.data.getUserName());
                    mcustomer_lastname.setText(result.data.getUserName());
                    mcustomer_email.setText("");
                    mcustomer_email.setEnabled(true);
                    mUsername = username;

                    mcustomer_password.setText("123456789");
                    mcustomer_password.setVisibility(View.GONE);


                    // logoutAllSocialNetworks();
                    if (!comeFromLogin) {
                        //try to login social account
                        UserFunctions.loginUser(Fragment_Register.this, getActivity().getSupportFragmentManager(), "",
                                mUsername, mcustomer_password.getText().toString(), false, null);
                    }
                }

                @Override
                public void failure(TwitterException exception) {
                    // Do something on failure
                }
            });

            onGoogleConnectedHandler = new IOnGoogleConnectedHandler() {
                @Override
                public void onConnected(Person person, String email) {
                    isGooglePlusLoggedIn = true;
                    mcustomer_firstname.setText(person.getName().getGivenName());
                    // mcustomer_firstname.setEnabled(false);
                    mcustomer_lastname.setText(person.getName().getFamilyName());
                    //mcustomer_lastname.setEnabled(false);
                    mcustomer_email.setText(email);
                    mUsername = KeyHashAndPassword.encodeUsername(getActivity(), person.getId() + "google");
                    //mcustomer_email.setEnabled(false);
                    mcustomer_password.setText("123456789");
                    mcustomer_password.setVisibility(View.GONE);
                    mFirstname = mcustomer_firstname.getText().toString();
                    mLastname = mcustomer_lastname.getText().toString();
                    mEmail = mcustomer_email.getText().toString();
                    //logoutAllSocialNetworks();
                    if (!comeFromLogin) {
                        //try to login social account
                        UserFunctions.loginUser(Fragment_Register.this, getActivity().getSupportFragmentManager(),"",
                                mUsername, mcustomer_password.getText().toString(), false, new UserFunctions.LoginHandler() {
                                    @Override
                                    public void onCompleted(boolean success) {
                                        if (!success){
                                            register("");
                                        }
//
                                    }
                                });
                    } else{
                        register("");
                    }
                }
            };

            instagramApp.setListener(new InstagramApp.OAuthAuthenticationListener() {
                @Override
                public void onSuccess() {
                    isInstagramLoggedIn = true;
                    mcustomer_firstname.setText(instagramApp.getUserName());
                    mcustomer_lastname.setText(instagramApp.getUserName());
                    mcustomer_email.setText("");
                    mUsername = KeyHashAndPassword.encodeUsername(getActivity(), instagramApp.getId() + "instagram");

                    mcustomer_password.setText("123456789");
                    mcustomer_password.setVisibility(View.GONE);
                    //  logoutAllSocialNetworks();

                    if (!comeFromLogin) {
                        //try to login social account
                        UserFunctions.loginUser(Fragment_Register.this, getActivity().getSupportFragmentManager(), "",
                                mUsername, mcustomer_password.getText().toString(), false, null);
                    }
                }

                @Override
                public void onFail(String error) {

                }
            });
        }
    }

    public final String getCountryZipCode(String countryID) {
        String countryZipCode = "";

        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(countryID.trim())) {
                countryZipCode = g[0];
                break;
            }
        }
        return countryZipCode;
    }

    private void isDualSimOrNot() {
        SimInfo telephonyInfo = SimInfo.getInstance(getActivity().getApplicationContext());

        String imeiSIM1 = telephonyInfo.getImeiSIM1();
        String imeiSIM2 = telephonyInfo.getImeiSIM2();

        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();

        boolean isDualSIM = telephonyInfo.isDualSIM();
        if (isDualSIM) {
            //process for dual sim
        } else {
            //get country code and disabled the text
            mcustomer_country_code.setText(getCountryZipCode(telephonyManager.getSimCountryIso().toUpperCase()));
            mcustomer_country_code.setEnabled(false);
            mcustomer_country_code.setBackgroundResource(R.color.disabled_color);

            String numberSIM1 = telephonyManager.getLine1Number();
            if (numberSIM1 != null && !numberSIM1.isEmpty()) {
                mcustomer_mobile.setText(numberSIM1);
                mcustomer_mobile.setEnabled(false);
                mcustomer_mobile.setBackgroundResource(R.color.disabled_color);
            } else {
                mcustomer_mobile.setBackgroundColor(Color.WHITE);
                mcustomer_mobile.setEnabled(true);
            }
        }
        Log.i("Dual = "," IME1 : " + imeiSIM1 + "\n" +
                " IME2 : " + imeiSIM2 + "\n" +
                " IS DUAL SIM : " + isDualSIM + "\n" +
                " IS SIM1 READY : " + isSIM1Ready + "\n" +
                " IS SIM2 READY : " + isSIM2Ready + "\n");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AutoWorks", "oCreateView fragment_register");
        myview = inflater.inflate(R.layout.fragment_register, container, false);
        setView(myview);
        //setup telephonyManager
        telephonyManager = ((TelephonyManager) getActivity().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE));


        return myview;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    private void register(String city_name)
    {
        Toast.makeText(this.getActivity(), "Registering new account", Toast.LENGTH_SHORT).show();
        //show loading progress bar
        if (progressDialog ==null)
            progressDialog = TransparentProgressDialog.createProgressDialog(this.getActivity());
        progressDialog.show();

        //comm.checkLogin(LoginChecking(mEmail, mPassword));
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("firstname", mFirstname));
        params.add(new BasicNameValuePair("lastname", mLastname));
        params.add(new BasicNameValuePair("email", mEmail));
        params.add(new BasicNameValuePair("username", " "));
        if (!isInstagramLoggedIn && !isTwitterLoggedIn && !isFacebookLoggedIn && !isGooglePlusLoggedIn)
        {
            params.add(new BasicNameValuePair("password", mPassword));
        }
        params.add(new BasicNameValuePair("zone_id", "1"));
//        params.add(new BasicNameValuePair("city", city_name));//???
        params.add(new BasicNameValuePair("country", mCountry));
        params.add(new BasicNameValuePair("country_code", mMobileCode));
        params.add(new BasicNameValuePair("mobile", mMobile));


        HttpPOSTProcess registerAsync = new HttpPOSTProcess(MainActivity.getDataURL2, MainActivity.register_tag, params, new HttpPOSTOnTaskCompleted() {
            @Override
            public void onHttpPOSTOnTaskCompleted(JSONObject json) {
                try {
                    if (json != null) {
                        if (!json.isNull(MainActivity.KEY_USER_INFO)) {

                            JSONObject res = json.getJSONObject(MainActivity.KEY_USER_INFO);

                            //for test////
                            int customerId = res.getInt(MainActivity.KEY_CUSTOMER_ID);
                            String email = res.getString(MainActivity.KEY_EMAIL);
                            String phone = res.getString(MainActivity.KEY_MOBILE);
                            String fistName = res.getString(MainActivity.KEY_FIRST_NAME);
                            String lastName = res.getString(MainActivity.KEY_LAST_NAME);
                            String customerGroupId = res.getString(MainActivity.KEY_CUSTOMER_GROUP_ID);
                            String countryCode = res.getString(MainActivity.KEY_COUNTRY_CODE);
                            String city = res.getString(MainActivity.KEY_CITY);
                            /////

                            MainActivity.currentUser.setCustomerID(res.getInt(MainActivity.KEY_CUSTOMER_ID));
                            MainActivity.currentUser.setEmail(res.getString(MainActivity.KEY_EMAIL));
                            MainActivity.currentUser.setPhone(res.getString(MainActivity.KEY_MOBILE));
                            MainActivity.currentUser.setFirstname(res.getString(MainActivity.KEY_FIRST_NAME));
                            MainActivity.currentUser.setLastname(res.getString(MainActivity.KEY_LAST_NAME));
                            MainActivity.currentUser.setCustomerGroupID(res.getInt(MainActivity.KEY_CUSTOMER_GROUP_ID));
                            MainActivity.currentUser.setCountryCode(res.getString(MainActivity.KEY_COUNTRY_CODE));
                            MainActivity.currentUser.setCity(res.getString(MainActivity.KEY_CITY));

                            MainActivity.mNavigationDrawerFragment.hideItemsAfterLogin();
                            MainActivity.customerToXml(getActivity());//save customer
                            //MainActivity.currentUser.setEmail(res.getString("email"));
                            ///register to comet chat
                            registerToCometChat(mEmail, mPassword);

                            progressDialog.dismiss();

                            Fragment_Home_Product frag_home_product = new Fragment_Home_Product();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, frag_home_product)
                                    .addToBackStack("addToBackStack fragment_home_product")
                                    .commit();

                        } else if (json.getString("error").equals("DUPLICATE_EMAIL")) {

                            RegisterFail(true);

                            Log.d("AutoWorks", "duplicate user");
                        }
                    }
                } catch (JSONException e) {
                    RegisterFail(false);
                    e.printStackTrace();
                }
            }
        });
    }
    private void registerToCometChat(String fullname, String passoword) {
        AutoWorksApplication.cometChat.createUser(fullname, passoword, fullname, "", null,  new Callbacks() {
            @Override
            public void successCallback(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("1000")){
                        String message = response.getString("message");
                        JSONObject jsonObjectData = response.getJSONObject("data");
                        String id = jsonObjectData.getString("userid");
//                        Utils.setOnPreference(getContext(), Constant.COMETCHAT_USERID, id);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failCallback(JSONObject response) {
                try {
                    String code = response.getString("code");
                    if (code.equals("1001")){
                        String message = response.getString("message");
                        Utils.showToast(getContext(), message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registration_register: {

                mEmail = mcustomer_email.getText().toString().trim();
               // if (!isTwitterLoggedIn && !isInstagramLoggedIn)
                 //   mUsername = mEmail;//login by twitter or instagram, mUsername is not equal mEmail
                mPassword = mcustomer_password.getText().toString().trim();
                mFirstname = mcustomer_firstname.getText().toString().trim();
                mLastname = mcustomer_lastname.getText().toString().trim();
                mMobile = mcustomer_mobile.getText().toString().trim();
                mMobileCode = mcustomer_country_code.getText().toString().trim();

//                Country sel_country = (Country) mcustomer_country.getSelectedItem();
//                mCountry = sel_country.getCcode().trim();
//
//                Zone sel_zone = (Zone) mcustomer_city.getSelectedItem();
//                String city_name = "";
//                if (sel_zone != null) {
//                    mCity = sel_zone.getZid().trim();
//                    city_name = sel_zone.getZname();
//                }

                if (ValidateInput()) {

                  register(mCity);
                }

                break;
            }
            case R.id.registration_login_block: {
                Log.d("AutoWorks", "onSelected fragment_login");
                Fragment_Login fragment_login = new Fragment_Login();
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.container, fragment_login)
                        .addToBackStack("addToBackStack fragment_login")
                        .commit();
                break;
            }
        }
    }

    public boolean ValidateInput() {
        boolean allchecked = true;
        if (!mEmail.equals("")) {
            mcustomer_email.setBackgroundColor(Color.WHITE);
            mcustomer_email.setHint(mcustomer_email.getHint().toString().replace(": " + getString(R.string.error_input_emmpty), ""));
        }
        if (!mPassword.equals("")) {
            mcustomer_password.setBackgroundColor(Color.WHITE);
            mcustomer_password.setHint(mcustomer_password.getHint().toString().replace(": " + getString(R.string.error_input_emmpty), ""));
        }
        if (!mFirstname.equals("")) {
            mcustomer_firstname.setBackgroundColor(Color.WHITE);
            mcustomer_firstname.setHint(mcustomer_firstname.getHint().toString().replace(": " + getString(R.string.error_input_emmpty), ""));
        }
        if (!mLastname.equals("")) {
            mcustomer_lastname.setBackgroundColor(Color.WHITE);
            mcustomer_lastname.setHint(mcustomer_lastname.getHint().toString().replace(": " + getString(R.string.error_input_emmpty), ""));
        }
//        Zone sel_zone = (Zone) mcustomer_city.getSelectedItem();
//        if (mCity != null && !mCity.equals(!sel_zone.getZname().equals(getString(R.string.spinner_select_zone)))) {
//            TextView sel_zone_txt = (TextView) mcustomer_city.getSelectedView();
//            sel_zone_txt.setBackgroundColor(Color.WHITE);
//            mcustomer_city.setBackgroundColor(Color.WHITE);
//        }
        if (!mMobileCode.equals("")) {
            mcustomer_country_code.setBackgroundColor(Color.WHITE);
            mcustomer_country_code.setHint(mcustomer_country_code.getHint().toString().replace(": " + getString(R.string.error_input_emmpty), ""));
        }
        if (!mMobile.equals("")) {
            mcustomer_mobile.setBackgroundColor(Color.WHITE);
            mcustomer_mobile.setHint(mcustomer_mobile.getHint().toString().replace(": " + getString(R.string.error_input_emmpty), ""));
        }
//        Country sel_country = (Country) mcustomer_country.getSelectedItem();
//        if (!sel_country.getCname().equals(getString(R.string.spinner_select_country))) {
//            TextView sel_country_txt = (TextView) mcustomer_country.getSelectedView();
//            sel_country_txt.setBackgroundColor(Color.WHITE);
//            mcustomer_mobile.setBackgroundColor(Color.WHITE);
//        }

        //validate
        if (mEmail.equals("") || !Global.isValidEmail(mEmail)) {
            mcustomer_email.setHint(mcustomer_email.getHint().toString().replace(getString(R.string.error_input_emmpty), "").replace(": ", "") + ": " + getString(R.string.error_input_emmpty));
            mcustomer_email.setBackgroundResource(R.color.error_background_color);
            allchecked = false;
        }
        if (mPassword.equals("")) {
            allchecked = false;
            mcustomer_password.setHint(mcustomer_password.getHint().toString().replace(getString(R.string.error_input_emmpty), "").replace(": ", "") + ": " + getString(R.string.error_input_emmpty));
            mcustomer_password.setBackgroundResource(R.color.error_background_color);
        }
        if (mPassword.length() < 6) {
            allchecked = false;
            mcustomer_password.setHint(mcustomer_password.getHint().toString().replace(getString(R.string.error_input_emmpty), "").replace(": ", "") + ": " + getString(R.string.error_password_length));
            mcustomer_password.setBackgroundResource(R.color.error_background_color);
        }

        if (mFirstname.equals("")) {
            allchecked = false;
            mcustomer_firstname.setHint(mcustomer_firstname.getHint().toString().replace(getString(R.string.error_input_emmpty), "").replace(": ", "") + ": " + getString(R.string.error_input_emmpty));
            mcustomer_firstname.setBackgroundResource(R.color.error_background_color);
        }
        if (mLastname.equals("")) {
            allchecked = false;
            mcustomer_lastname.setHint(mcustomer_lastname.getHint().toString().replace(getString(R.string.error_input_emmpty), "").replace(": ", "") + ": " + getString(R.string.error_input_emmpty));
            mcustomer_lastname.setBackgroundResource(R.color.error_background_color);
        }

//        if (sel_zone == null || (sel_zone != null && sel_zone.getZname().equals(getString(R.string.spinner_select_zone)))) {
//            allchecked = false;
//            mcustomer_city.setBackgroundResource(R.color.error_background_color);
//
//          /*  if (sel_zone!=null) {
//                TextView sel_zone_text = (TextView) mcustomer_city.getSelectedView();
//                sel_zone_text.setBackgroundResource(R.color.error_background_color);
//            }*/
//        }


        if (mMobileCode.equals("") || !CountryCode.isNumeric(mMobileCode)) {
            allchecked = false;
            //  mcustomer_country_code.setHint(mcustomer_country_code.getHint().toString().replace(getString(R.string.error_input_emmpty), "").replace(": ","") + ": " + getString(R.string.error_input_emmpty));
            mcustomer_country_code.setBackgroundResource(R.color.error_background_color);
        }
        if (mMobile.equals("") || !CountryCode.isNumeric(mMobile)) {
            allchecked = false;
            mcustomer_mobile.setHint(mcustomer_mobile.getHint().toString().replace(getString(R.string.error_input_emmpty), "").replace(": ", "") + ": " + getString(R.string.error_input_emmpty));
            mcustomer_mobile.setBackgroundResource(R.color.error_background_color);
        }
//        if (sel_country.getCname().equals(getString(R.string.spinner_select_country))) {
//            allchecked = false;
//            mcustomer_country.setBackgroundResource(R.color.error_background_color);
//            TextView sel_country_txt = (TextView) mcustomer_country.getSelectedView();
//            sel_country_txt.setBackgroundResource(R.color.error_background_color);
//        }

        return allchecked;
    }

    public void RegisterFail(boolean isDuplicate) {
        if (isDuplicate) {
            mcustomer_email.setBackgroundResource(R.color.error_background_color);
            register_error.setVisibility(View.VISIBLE);
            register_error.setText(R.string.login_duplicate_error);
        } else {

            register_error.setVisibility(View.VISIBLE);
            register_error.setText(R.string.login_error);
        }
        // Global.recoverAfterAddLoadingIcon(myview, getActivity(), savedParentID, savedChildID, savedBackground, savedText, savedRemovedIndex);
        // register_progressBar.setVisibility(View.INVISIBLE);
        if (button_Register != null)
            button_Register.setVisibility(View.VISIBLE);
        if (progressDialog != null)
            progressDialog.dismiss();
    }
}
