package helpers;

/**
 * Created by Vo Ly Minh Nhan on 9/18/14.
 */

import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.inscripts.callbacks.Callbacks;

import autoworks.app.Utilities.Utils;
import autoworks.app.controller.AutoWorksApplication;
import autoworks.app.view.Fragment_Home_Product;
import autoworks.app.view.Fragment_Login;
import autoworks.app.view.MainActivity;
import autoworks.app.R;
import autoworks.app.dialogs.TransparentProgressDialog;
import autoworks.app.model.Customer;
import autoworks.app.model.NotificationDataSource;
import gcm.PendingOperation;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserFunctions {

    private JSONParser jsonParser;

    // Testing in localhost using wamp or xampp
    // use http://10.0.2.2/ to connect to your localhost ie http://localhost/
    private static String loginURL = MainActivity.getDataURL2;
    private static String registerURL = MainActivity.getDataURL2;

    private static String login_tag = "login";
    private static String register_tag = "registerCustomer";

    private FragmentManager fragmentManager;

    // constructor
    public UserFunctions(){
        jsonParser = new JSONParser();
    }

    /**
     * function make Login Request
     * @param email
     * @param password
     * */
     private static boolean login_result = false;

     public static boolean loginUser(final Fragment currentFragment, final FragmentManager fragmentManager, String email, final String username,
                                     final String password, final boolean is_from_login, final LoginHandler handler) {

         final Dialog login_progressDialog = TransparentProgressDialog.createProgressDialog(currentFragment.getActivity());
         login_progressDialog.show();

         //comm.checkLogin(LoginChecking(mEmail, mPassword));
         List<NameValuePair> params = new ArrayList<NameValuePair>();
         if (TextUtils.isEmpty(email) && !TextUtils.isEmpty(username)) {
             //social login
             params.add(new BasicNameValuePair("username", username));
         } else
         {
             params.add(new BasicNameValuePair("email", email));
             params.add(new BasicNameValuePair("password", password));
         }


        HttpPOSTProcess loginAsync = new HttpPOSTProcess(MainActivity.getDataURL2, MainActivity.login_tag, params, new HttpPOSTOnTaskCompleted() {
            @Override
            public void onHttpPOSTOnTaskCompleted(JSONObject json) {
                try {
                    if(login_progressDialog != null) {
                        login_progressDialog.dismiss();
                    }
                  //  if (json!=null && !json.isNull(MainActivity.KEY_USER_INFO)) {
                        //loginErrorMsg.setText("");
                        JSONObject res = json.getJSONObject(MainActivity.KEY_USER_INFO);

                        MainActivity.currentUser.setCustomerID(res.getInt(MainActivity.KEY_CUSTOMER_ID));
                        MainActivity.currentUser.setEmail(res.getString(MainActivity.KEY_EMAIL));
                        MainActivity.currentUser.setPhone(res.getString(MainActivity.KEY_MOBILE));
                        MainActivity.currentUser.setFirstname(res.getString(MainActivity.KEY_FIRST_NAME));
                        MainActivity.currentUser.setLastname(res.getString(MainActivity.KEY_LAST_NAME));
                        MainActivity.currentUser.setCustomerGroupID(res.getInt(MainActivity.KEY_CUSTOMER_GROUP_ID));
                        MainActivity.currentUser.setCountryCode(res.getString(MainActivity.KEY_COUNTRY_CODE));
                        MainActivity.currentUser.setCity(res.getString(MainActivity.KEY_CITY));
                       // MainActivity.isCustomerLoggedIn = true;
                        MainActivity.customerToXml(currentFragment.getActivity());//save customer
                        //MainActivity.currentUser.setEmail(res.getString("email"));

                        //hide items after login
                        MainActivity.mNavigationDrawerFragment.hideItemsAfterLogin();


                    //////////////login to comet chat
//                    loginToCometChat(username, password);

                        Fragment_Home_Product frag_home_product = new Fragment_Home_Product();
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, frag_home_product)
                                .addToBackStack("addToBackStack fragment_home_product")
                                .commit();

                        Toast.makeText(currentFragment.getActivity(), "Login successfully, welcome " + MainActivity.currentUser.getFirstname(), Toast.LENGTH_SHORT).show();
                        if (handler!=null)
                        {
                            handler.onCompleted(true);
                        }
                        //start register GCM
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                PendingOperation operation = new PendingOperation.PendingRegisterGCM();
                                boolean success =operation.doWork(currentFragment.getActivity());
                                if (!success)
                                {
                                    PendingOperation.addOperation(currentFragment.getActivity(), operation);
                                }

                            }
                        }).start();

                   // }
                }
                catch (JSONException e) {

                    if (handler!=null)
                    {
                        handler.onCompleted(false);
                    }
                    if(is_from_login) {
                        Fragment_Login fragment_login = (Fragment_Login)currentFragment;
                        fragment_login.LoginFail();

                        e.printStackTrace();
                    }
                }
            }
        });

         return login_result;
    }
    public static void loginToCometChat(String fullname, String passoword) {
        AutoWorksApplication.cometChat.login(fullname, passoword, new Callbacks() {
            @Override
            public void successCallback(JSONObject jsonObject) {
                try {
                    String message = jsonObject.getString("message");
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failCallback(JSONObject jsonObject) {

            }
        });
    }
    /**
     * function make Login Request
     * @param name
     * @param email
     * @param password
     * */
    public JSONObject registerUser(String name, String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));

        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        // return json
        return json;
    }

    /**
     * Function get Login status then switch to login
     * */
    public static boolean isUserLoggedInThenSwitch(final FragmentManager manager){
        if(MainActivity.currentUser.getCustomerID() <= 0) {
            Fragment_Login fragment_login = new Fragment_Login();
            manager.beginTransaction()
                    .replace(R.id.container, fragment_login)
                    .addToBackStack("addToBackStack fragment_login")
                    .commit();
            return false;
        }
        return true;
    }




    /**
     * Function to logout user
     * Reset Database
     * */
    public static void logoutUser(final Fragment fragment, final FragmentManager fragmentManager){
        //start unregister GCM
        new Thread(new Runnable() {
            @Override
            public void run() {
                PendingOperation operation = new PendingOperation.PendingUnRegisterGCM();
                boolean success =operation.doWork(fragment.getActivity());
                if (!success)
                {
                    PendingOperation.addOperation(fragment.getActivity(), operation);
                }
               // MainActivity.isCustomerLoggedIn = false;
                MainActivity.currentUser = new Customer();
                NotificationDataSource.getInstance(fragment.getActivity()).clear();
                NotificationDataSource.getInstance(fragment.getActivity()).dataToXml();//clear & save notification list
                MainActivity.customerToXml(fragment.getActivity());//save empty customer
            }
        }).start();


        MainActivity.mNavigationDrawerFragment.addItemsAfterLogout();
        MyCustomFragment.logoutAllSocialNetworks();

        final Fragment_Home_Product frag_home_product = new Fragment_Home_Product();
        fragmentManager.beginTransaction()
                .replace(R.id.container, frag_home_product)
                .addToBackStack("addToBackStack fragment_home_product")
                .commit();


    }

    public interface LoginHandler
    {
        void onCompleted(boolean success);
    }

}