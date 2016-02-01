package autoworks.app.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import autoworks.app.R;
import autoworks.app.Utilities.LocationUtility;
import autoworks.app.model.Country;
import autoworks.app.model.Zone;
import helpers.HttpPOSTOnTaskCompleted;
import helpers.HttpPOSTProcess;
import autoworks.app.dialogs.TransparentProgressDialog;
import helpers.UserFunctions;


public class Fragment_Update_Profile extends Fragment_Register implements View.OnClickListener {

    private RelativeLayout button_Logout;
    private RelativeLayout button_update_profile;
    private RelativeLayout update_profile_change_password;

    private Spinner spinnerLanguage;

    public Fragment_Update_Profile() {
        useMyCustomFragment = false;

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
    boolean first = true;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //show logo
        android.support.v7.app.ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        ImageView logo = (ImageView) actionBar.getCustomView().findViewById(R.id.actionBarLogo);
        logo.setVisibility(View.VISIBLE);
        actionBar.setTitle(getString(R.string.onSectionAttached_fragment_profile));


        //set fields
        mcustomer_firstname.setText(MainActivity.currentUser.getFirstname());
        mcustomer_lastname.setText(MainActivity.currentUser.getLastname());
        mcustomer_email.setText(MainActivity.currentUser.getEmail());
        mcustomer_mobile.setText(MainActivity.currentUser.getPhone());
        mcustomer_password.setText("12345678");//just for passing the validation process
        mcustomer_password.setVisibility(View.GONE);

        button_update_profile = (RelativeLayout) myview.findViewById(R.id.update_profile_update_profile);
        update_profile_change_password = (RelativeLayout) myview.findViewById(R.id.update_profile_change_password);
        update_profile_change_password.setOnClickListener(this);
        if (button_update_profile != null)
            button_update_profile.setOnClickListener(this);


        spinnerLanguage = (Spinner)myview.findViewById(R.id.spinner_language);
        String[] languages = new String[]{"English", "Arabic"};
        ArrayAdapter<String> adapterZone = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.spinner_item, languages);
        spinnerLanguage.setAdapter(adapterZone);
        first = true;
        SharedPreferences prefs = getActivity().getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);

        String lang = prefs.getString("language", "");
        if (lang.equals("en"))
        {
            spinnerLanguage.setSelection(0);
        }else if (lang.equals("ar"))
        {
            spinnerLanguage.setSelection(1);
        }

      //  spinnerLanguage.setSelection(0);
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Locale locale = null;
                if (position == 0)
                {
                    locale = new Locale("en");
                    //MainActivity.languageID = 1;
                    MainActivity.currentUser.setLanguageId(1);
                }else if (position == 1)
                {
                    locale = new Locale("ar");
                    //MainActivity.languageID = 2;
                    MainActivity.currentUser.setLanguageId(2);
                }
                if (!first) {
                    System.out.println(locale.getDisplayLanguage());
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    //  config.locale = locale;
                    config.setLocale(locale);
                    getActivity().getResources().updateConfiguration(config,
                            getActivity().getResources().getDisplayMetrics());
                    final SharedPreferences prefs = getActivity().getSharedPreferences(
                            MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("language", locale.getLanguage());
                    editor.commit();

                    getActivity().finish();
                    Intent refresh = new Intent(getActivity(), MainActivity.class);
                    startActivity(refresh);
                }

                first = false;

                //getActivity().recreate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    //private View myview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AutoWorks", "oCreateView fragment_register");
        myview = inflater.inflate(R.layout.fragment__update__profile, container, false);

        button_Logout = (RelativeLayout) myview.findViewById(R.id.update_profile_logout);
        button_Logout.setOnClickListener(this);

        //check login
        UserFunctions.isUserLoggedInThenSwitch(getActivity().getSupportFragmentManager());

        return myview;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public final void onClick(View v) {
        switch (v.getId()) {
            case R.id.update_profile_logout: {
                UserFunctions.logoutUser(this, getFragmentManager());
                break;
            }
            case R.id.update_profile_change_password:
                Fragment_Change_Password frag_home_product = new Fragment_Change_Password();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, frag_home_product)
                        .addToBackStack("addToBackStack fragment_change_password")
                        .commit();
                break;

            case R.id.update_profile_update_profile: {

                mEmail = mcustomer_email.getText().toString().trim();
                // if (!isTwitterLoggedIn && !isInstagramLoggedIn)
                //    mUsername = mEmail;//login by twitter or instagram, mUsername is not equal mEmail
                mPassword = mcustomer_password.getText().toString().trim();
                mFirstname = mcustomer_firstname.getText().toString().trim();
                mLastname = mcustomer_lastname.getText().toString().trim();
                mMobile = mcustomer_mobile.getText().toString().trim();
                mMobileCode = mcustomer_country_code.getText().toString().trim();

                mCountry = LocationUtility.countryName;
                mCity = LocationUtility.cityName;
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

                    //show loading progress bar
                    progressDialog = TransparentProgressDialog.createProgressDialog(this.getActivity());
                    progressDialog.show();

                    //comm.checkLogin(LoginChecking(mEmail, mPassword));
                    List<NameValuePair> params = new ArrayList<NameValuePair>();

                    params.add(new BasicNameValuePair(MainActivity.KEY_CUSTOMER_ID, MainActivity.currentUser.getCustomerID() + ""));
                    params.add(new BasicNameValuePair("firstname", mFirstname));
                    params.add(new BasicNameValuePair("lastname", mLastname));
                    params.add(new BasicNameValuePair("email", mEmail));
                    //params.add(new BasicNameValuePair("username", mUsername));
                    //   params.add(new BasicNameValuePair("password", mPassword));
                    params.add(new BasicNameValuePair("zone_id", "1"));
//                    params.add(new BasicNameValuePair("city", mCity));
                    params.add(new BasicNameValuePair("country", mCountry));
                    params.add(new BasicNameValuePair("country_code", mMobileCode));
                    params.add(new BasicNameValuePair("mobile", mMobile));


                    HttpPOSTProcess updatePorfileAsync = new HttpPOSTProcess(MainActivity.getDataURL2, MainActivity.updateCustomer_tag, params, new HttpPOSTOnTaskCompleted() {
                        @Override
                        public void onHttpPOSTOnTaskCompleted(JSONObject json) {
                            try {
                                if (json != null) {
                                    if (!json.isNull(MainActivity.KEY_USER_INFO)) {

                                        JSONObject res = json.getJSONObject(MainActivity.KEY_USER_INFO);
                                        MainActivity.currentUser.setCustomerID(res.getInt(MainActivity.KEY_CUSTOMER_ID));
                                        MainActivity.currentUser.setEmail(res.getString(MainActivity.KEY_EMAIL));
                                        MainActivity.currentUser.setPhone(res.getString(MainActivity.KEY_MOBILE));
                                        MainActivity.currentUser.setFirstname(res.getString(MainActivity.KEY_FIRST_NAME));
                                        MainActivity.currentUser.setLastname(res.getString(MainActivity.KEY_LAST_NAME));
                                        MainActivity.currentUser.setCustomerGroupID(res.getInt(MainActivity.KEY_CUSTOMER_GROUP_ID));
                                        MainActivity.currentUser.setCountryCode(res.getString(MainActivity.KEY_COUNTRY_CODE));
                                        MainActivity.currentUser.setCity(res.getString(MainActivity.KEY_CITY));
                                        //MainActivity.currentUser.setEmail(res.getString("email"));
                                        progressDialog.dismiss();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(Fragment_Update_Profile.this.getActivity());
                                        builder.setMessage("Profile updated successfully!")
                                                .setCancelable(true)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        //do things
                                                        dialog.cancel();
                                                    }
                                                });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                       /* Fragment_Home_Product frag_home_product = new Fragment_Home_Product();
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.container, frag_home_product)
                                                .addToBackStack("addToBackStack fragment_home_product")
                                                .commit();*/
                                    } else {
                                        progressDialog.dismiss();
                                    }
                                    /*else if (json.getString("listOfCountries").equals("DUPLICATE_EMAIL")) {

                                        RegisterFail(true);

                                        Log.d("AutoWorks", "duplicate user");
                                    }*/
                                } else {
                                    progressDialog.dismiss();
                                }
                            } catch (JSONException e) {
                                RegisterFail(false);
                                e.printStackTrace();
                            }
                        }
                    });
                }

                break;
            }
        }
    }

}
