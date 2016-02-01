package autoworks.app.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import autoworks.app.R;
import autoworks.app.model.Customer;
import autoworks.app.model.Order;
import autoworks.app.model.Product;
import gcm.GCMNotificationIntentService;
import helpers.CartCustomProductSaver;
import helpers.JSONParser;
import helpers.MyCustomFragment;
import helpers.UserFunctions;

import org.apache.http.NameValuePair;
import org.simpleframework.xml.core.Persister;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import autoworks.app.model.*;
import autoworks.app.model.CustomProduct;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    public static NavigationDrawerFragment mNavigationDrawerFragment;
    //private FragmentManager fragmentManager = getSupportFragmentManager();

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */

    public static boolean isRunning = false;
    private CharSequence mTitle;
    private int navigationPosition = 2;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    UserFunctions userFunctions;

    public static JSONParser jsonParser;

    static public int currentUserID;
    static public Customer currentUser;

    //static public ArrayList<CustomProduct> cartList;
    static public ArrayList<Product> productList = null;
    static public ArrayList<OrderItem> orderList = null;
    static public ArrayList<CustomProduct> productSearchList = null;
    static public ArrayList<CustomProduct> specialOffersList = null;
    static public List<NameValuePair> mpParamsSearchList = new LinkedList<NameValuePair>();
    static public ArrayList<Country> listOfCountries;
    static public ArrayList<Zone> listOfZones;
    static public boolean isGettingProduct = false;
    //static public boolean isCustomerLoggedIn = false;
    static public ArrayList<Category> categoryList = null;
    static public int currentCateogoryLevel = 0;
    static public Integer limitItemPerLoading = 6;
    static public Integer languageID = 1;
    static public ArrayList<Category> rootCategoriesList = null;
    static public ArrayList<Category> allCategories = new ArrayList<Category>();

    static public String keyword = "";

    static public ArrayList<ProductCollection> collectionList = null;
    static public ArrayList<Customer> userList = null;

    static public ArrayList<Order> myCart = null;
    static public ArrayList<Order> myOrder = null;
    static InputStream is = null;

    public static String KEY_PRODUCT = "product";
    public static String KEY_PRODUCTS = "products";
    public static String KEY_PRODUCT_ID = "product_id";
    public static String KEY_PRODUCT_NAME = "name";
    public static String KEY_PRODUCT_PICTURE = "image";
    public static String KEY_PRODUCT_PICTURES = "images";
    public static String KEY_PRODUCT_DATE = "product_date";
    public static String KEY_PRODUCT_SHORT_DESCRIPTION = "meta_description";
    public static String KEY_PRODUCT_DESCRIPTION = "description";
    public static String KEY_PRODUCT_VIEW = "viewed";
    public static String KEY_PRODUCT_PRICE = "price";
    public static String KEY_PRODUCT_SPECIAL = "special";
    public static String KEY_PRODUCT_DISCOUNT = "discount";
    public static String KEY_PRODUCT_QUANTITY = "quantity";
    public static String KEY_PRODUCT_TOTAL_PRICE = "total";
    public static String KEY_RELATED_PRODUCTS = "related_products";

    public static String KEY_COLLECTION = "collection";
    public static String KEY_COLLECTION_ID = "collection_id";
    public static String KEY_COLLECTION_NAME = "collection_name";

    public static String KEY_USERS = "users";
    public static String KEY_USER_ID = "customer_id";
    public static String KEY_USER_EMAIL = "email";
    public static String KEY_USER_FNAME = "firstname";
    public static String KEY_USER_LNAME = "lastname";
    public static String KEY_USER_ADDRESS = "address_1";
    public static String KEY_USER_PHONE = "telephone";
    public static String KEY_USER_AVATAR = "customer_avatar";

    //public static String KEY_CITY_NAME = "zone_name";

    public static String KEY_GENDER_ID = "gender_id";
    public static String KEY_FAVORITE = "favorite";

    public static String KEY_IMAGE = "image";
    public static String KEY_IMAGES = "images";
    public static String KEY_CART = "cart";
    public static String KEY_BUYER_ID = "buyer_id";
    public static String KEY_PRODUCT_COUNT = "product_quantity";
    public static String KEY_STATUS_ID = "status_id";


    static public String HOST_NAME = "http://bigmarket.esy.es/";
    static public String HOST_NAME2 = "http://test.auto-works.cc/";
    static public String HOST_NAME3 = "http://192.168.1.103:8081/auto-works_cc/";//localhost test
    //static public String HOST_NAME = "http://192.168.1.147/finalyear_en/";
    static public String getDataURL = HOST_NAME + "index.php?route=product/product/getBigMarketData";
    static public String getDataURL2 = HOST_NAME2 + "index.php?route=mobileapi/listener/getData";
    static public String ROOT_API_URL = HOST_NAME2 + "index.php?route=mobileapi/listener";
    //static public String getDataURL = HOST_NAME + "/GetData.php";

    public static String get_most_sold_product = "getMostSoldProducts";
    public static String get_related_products_customer_bought = "getRelatedProductsWhichCustomerBought";
    public static String get_product_tag = "getProduct";
    public static String get_product_detail_tag = "getProductDetail";
    public static String slideshow_image = "slideShowImages";
    public static String getCountries = "getCountries";
    public static String get_zone_by_country_id = "getZonesByCountryID";
    public static String getCategories = "getCategories";
    public static String getCategoriesByParent = "getCategoriesByParent";
    public static String product_tag = "product";
    public static String login_tag = "login";
    public static String register_tag = "registerCustomer";
    public static String change_pass_tag = "updatePassword";
    // public static String updateCustomer_tag = "updateCustomer";

    public static String forgotten_password_tag = "forgottenPassword";
    public static String contact_us_tag = "contactUs";
    public static String updateCustomer_tag = "updateCustomer";

    public static String forgotten_password_text = "";

    public static String collection_tag = "collection";
    public static String user_tag = "user";
    public static String cart_tag = "cart";
    public static String order_tag = "getOrderHistory";

    public static String KEY_CATEGORIES = "categories";
    public static String KEY_CATEGORY_ID = "category_id";
    public static String KEY_CATEGORY_NAME = "name";
    public static String KEY_CATEGORY_IMAGE = "image";
    public static String KEY_CATEGORY_DESCRIPTION = "description";
    public static String KEY_CATEGORY_PARENT_ID = "parent_id";


    public static String KEY_RESULT = "result";
    public static String KEY_USER_INFO = "userInfo";
    public static String KEY_FIRST_NAME = "firstname";
    public static String KEY_LAST_NAME = "lastname";
    public static String KEY_CUSTOMER_GROUP_ID = "customer_group_id";
    public static String KEY_CUSTOMER_ID = "customer_id";
    public static String KEY_EMAIL = "email";
    public static String KEY_PASSWORD = "password";
    public static String KEY_CITY = "zone_id";
    public static String KEY_COUNTRY = "country";
    public static String KEY_COUNTRY_CODE = "country_code";

    public static String KEY_MOBILE = "telephone";



    public static String add_order_tag = "addOrder";
    public static String get_payment_shipping = "getPaymentAndShipping";
    public static String new_payment_shipping = "newPaymentAndShipping";
    public static String get_special_offer = "getSpecialOffers";
    public static String KEY_METHODS = "methods";
    public static String KEY_SHIPPING = "shipping";
    public static String KEY_PAYMENT = "payment";
    public static String KEY_TITLE = "title";
    public static String KEY_CODE = "code";
    public static String KEY_ORDER = "orders";
    public static String KEY_ORDER_ID = "order_id";
    public static String KEY_ORDER_STATUS = "status";
    public static String KEY_ORDER_DATE = "date_added";
    public static String KEY_ORDER_TOTAL = "total";

    public static String KEY_NOTIFICATION = "notifications";


    public static String KEY_COUNTRIES = "countries";
    public static String KEY_COUNTRY_CODE_ISO = "iso_code_2";
    public static String KEY_COUNTRY_NAME = "name";

    public static String KEY_ZONES = "zones";
    public static String KEY_ZONE_ID = "zone_id";
    public static String KEY_ZONE_NAME = "name";

    public static MenuItem notification = null;


    private void restoreActionBar() {
         /* start actionbar and top status*/
        //actionbar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.action_bar_custom);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        actionBar.setTitle(mTitle);

        if(notification != null) {
            int currentCartQuantity = CartCustomProductSaver.getInstance(this).getListOfCartProductIds().size();
            if(currentCartQuantity > 0) {
                //set cart number indicator
                MainActivity.notification.setTitle(String.valueOf(currentCartQuantity));
                MainActivity.notification.setVisible(true);
            }
            else {
                MainActivity.notification.setTitle("");
//                MainActivity.notification.setVisible(false);
            }
        }
        /*end actionbar and top status*/



    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);

        Configuration config = getBaseContext().getResources().getConfiguration();

        String lang = prefs.getString("language", "");
        if (! "".equals(lang) && ! config.locale.getLanguage().equals(lang))
        {
            Locale locale1 = new Locale(lang);
            Locale.setDefault(locale1);
            config.setLocale(locale1);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
        setContentView(R.layout.activity_main);
        restoreActionBar();

        initVariables();

        if (savedInstanceState == null) {
            navigationPosition = 2;
        } else {
            Log.d("AutoWorks", "onGet savedInstanceState MainActivity");
            navigationPosition = savedInstanceState.getInt("navigationPosition");
        }

        Log.d("AutoWorks", "onCreate MainActivity");


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();


        String locale = getResources().getConfiguration().locale.getDisplayName();
        System.out.println("locale = " + locale);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Set up the drawer.



       // currentUser = new Customer();
        //load current user
        xmlToCustomer(this);



       /* Locale locale1 = null;
        locale1 = new Locale("ar_EG");


        Locale.setDefault(locale1);
        Configuration config = new Configuration();
       // config.locale = locale1;
        config.setLocale(locale1);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());*/



    }
    private void initVariables() {
        productList = new ArrayList<Product>();
        categoryList = new ArrayList<Category>();
        rootCategoriesList = new ArrayList<Category>();
        productSearchList  = new ArrayList<CustomProduct>();
        specialOffersList = new ArrayList<CustomProduct>();
        orderList = new ArrayList<OrderItem>();
        mpParamsSearchList = new LinkedList<>();
        listOfCountries = new ArrayList<>();


        jsonParser = new JSONParser();
        myCart = new ArrayList<Order>();
        userFunctions = new UserFunctions();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Configuration config = getResources().getConfiguration();
        // refresh your views here
        SharedPreferences prefs = getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String language  = prefs.getString("language", "");
        if (!TextUtils.isEmpty(language)) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
           // config.locale = locale;
            config.setLocale(locale);
            System.out.println("change locale = " + locale.getDisplayLanguage());
        }
        super.onConfigurationChanged(newConfig);

    }

    private void gotoShoppingCart() {
        Fragment_Shopping_Cart fragment_shopping_cart = new Fragment_Shopping_Cart();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment_shopping_cart)
                .addToBackStack("addToBackStack fragment_shopping_cart")
                .commit();
    }


    static final String CUSTOMER_FILE = "customer.xml";
    static Persister persister  = new Persister();
    public static void customerToXml(Context context)
    {
        try {
            OutputStream outputStream = context.openFileOutput(CUSTOMER_FILE, Context.MODE_PRIVATE);
            persister.write(MainActivity.currentUser, outputStream);
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void xmlToCustomer(Context context)
    {
        try {
            InputStream inputStream = context.openFileInput(CUSTOMER_FILE);
            MainActivity.currentUser = persister.read(Customer.class, inputStream);

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.currentUser = new Customer();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        isRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
        //String value = intent.getStringExtra("GCM_Extra");
      //  showListNotificationFragment(value);
        //Log.d("AutoWorks", "onNewIntent" + value);
    }

    @Override
    protected void onResume()
    {

        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.currentUser.getCustomerID() > 0)
                {
                    MainActivity.mNavigationDrawerFragment.hideItemsAfterLogin();
                }
            }
        }, 5000);


        String id = getIntent().getStringExtra(GCMNotificationIntentService.KEY_ID);
        String title = getIntent().getStringExtra(GCMNotificationIntentService.KEY_TITLE);
        String message = getIntent().getStringExtra(GCMNotificationIntentService.KEY_MESSAGE);
        boolean isRead = getIntent().getBooleanExtra(GCMNotificationIntentService.KEY_IS_READ, false);
       // boolean running = getIntent().getBooleanExtra(GCMNotificationIntentService.KEY_RUNNING, false);
       // showListNotificationFragment(id, title, message);
        showNotificationDetail(id, title, message, isRead);
        Log.d("AutoWorks", "onResume" + title +", " + message);


        restoreActionBar();
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        getIntent().putExtra(GCMNotificationIntentService.KEY_ID, "");
        getIntent().putExtra(GCMNotificationIntentService.KEY_TITLE, "");
        getIntent().putExtra(GCMNotificationIntentService.KEY_MESSAGE, "");
        getIntent().putExtra(GCMNotificationIntentService.KEY_IS_READ, false);
    }

    private void showListNotificationFragment(String id, String title, String message)
    {
        if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(title) && !TextUtils.isEmpty(message))
        {
            //start List notification fragment
            Fragment_Notification_List fragment_notification_list = new Fragment_Notification_List();
            Notification notification = new Notification();
            notification.setId(id);
            notification.setTitle(title);
            notification.setMessage(message);
           // fragment_notification_list.addNotification(notification); //WORKING

            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment_notification_list)
                    .commit();


        }
    }

    private void showNotificationDetail(String id, String title, String message, boolean isRead)
    {
        if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(title) && !TextUtils.isEmpty(message)) {
            Notification notification = new Notification();
            notification.setId(id);
            notification.setRead(isRead);
            notification.setTitle(title);
            notification.setMessage(message);
            Fragment_Notification_Detail.notification = notification;
            Fragment_Notification_List fragment_notification_list = new Fragment_Notification_List();
           // Fragment_Notification_Detail fragment_notification_detail = new Fragment_Notification_Detail();
            fragmentManager.beginTransaction()
                    .replace(R.id.container,fragment_notification_list)
                    .addToBackStack("fragment_notification_list")
                    .commit();

          /*  fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment_notification_detail)
                    .addToBackStack("fragment_notification_detail")

                    .commit();*/
        }
    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        android.support.v7.app.ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);

        // update the main content by replacing fragments
        if (position == 0) {
            navigationPosition = position;
            Fragment_Home_Product frag_home_product = new Fragment_Home_Product();
            //Fragment_MyProduct_Add frag_home_product = new Fragment_MyProduct_Add();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, frag_home_product)
                    .addToBackStack("addToBackStack fragment_home_product")
                    .commit();
        } else if (position == 1) {
            navigationPosition = position;
            Fragment_Category_List fragment_category_list = new Fragment_Category_List();
            //fragment_special_offers.GetUserProfile(currentUser);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment_category_list)
//                    .addToBackStack("addToBackStack fragment_special_offers")
                    .addToBackStack("addToBackStack fragment_categories")
                    .commit();
        }
        else if (position == 2) {
            navigationPosition = position;
            Fragment_Notification_List fragment_notification_list = new Fragment_Notification_List();
            //fragment_special_offers.GetUserProfile(currentUser);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment_notification_list)
                    .addToBackStack("addToBackStack fragment_notification_list")
                    .commit();
        }
        else if (position == 3) {
            navigationPosition = position;
            Fragment_Special_Offers fragment_special_offers = new Fragment_Special_Offers();
            //fragment_special_offers.GetUserProfile(currentUser);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment_special_offers)
                    .addToBackStack("addToBackStack fragment_special_offers")
                    .commit();
        } else if (position == 4) {
            navigationPosition = position;
            Fragment_Shopping_Cart fragment_shopping_cart = new Fragment_Shopping_Cart();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment_shopping_cart)
                    .addToBackStack("addToBackStack fragment_shopping_cart")
                    .commit();
        } else if (position == 5) {
            navigationPosition = position;
            Fragment_Wish_List fragment_wish_list = new Fragment_Wish_List();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment_wish_list)
                    .addToBackStack("addToBackStack fragment_wish_list")
                    .commit();
        } else if (position == 6) {
            navigationPosition = position;
            Fragment_Update_Profile fragment_update_profile = new Fragment_Update_Profile();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment_update_profile)
                    .addToBackStack("addToBackStack fragment_update_profile")
                    .commit();
        } else if (position == 7) {
            navigationPosition = position;
            Fragment_Order_History fragment_order_history = new Fragment_Order_History();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment_order_history)
                    .addToBackStack("addToBackStack fragment_order_history")
                    .commit();
        } else if (position == 8) {
            navigationPosition = position;
            Fragment_Map fragment_map = new Fragment_Map();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment_map)
                    .addToBackStack("addToBackStack fragment_map")
                    .commit();
        } else if (position == 9) {
            navigationPosition = position;
            Fragment_Contact_Us fragment_contact_us = new Fragment_Contact_Us();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment_contact_us)
                    .addToBackStack("addToBackStack fragment_contact_us")
                    .commit();
        }
        else if (position == 10) {
            page = "0";
            navigationPosition = position;
            Fragment_Login fragment_login = new Fragment_Login();

            Fragment old = fragmentManager.findFragmentByTag(Fragment_Register.TAG);
            Log.d("MyCustomFragment", old +"");
            if (old != null) {
                if (old instanceof MyCustomFragment) {
                    ((MyCustomFragment)old).logoutAllSocialNetworks();
                    // old.onDestroy();
                }
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment_login, Fragment_Login.TAG)
                    .commit();

        } else if (position == 11) {
            navigationPosition = position;

            Fragment_Register fragment_register = new Fragment_Register();

            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment_register, Fragment_Register.TAG)
                    .commit();

        }

        /*
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
                */
    }


    public void onSectionAttached(int number) {
        switch (number) {
            case 9:
                mTitle = getString(R.string.onSectionAttached_fragment_login);
                break;
            case 10:
                mTitle = getString(R.string.onSectionAttached_fragment_register);
                break;
            default:
                mTitle = "";
                break;
        }
        restoreActionBar();
    }

//    public void restoreActionBar() {
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null && mTitle != "") {
//            // actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//            actionBar.setDisplayShowTitleEnabled(true);
//            actionBar.setTitle(mTitle);
//        }
//        else if(actionBar != null){
//            //hide logo
////            ImageView logo = (ImageView)actionBar.getCustomView().findViewById(R.id.actionBarLogo);
////            logo.setVisibility(View.VISIBLE);
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity listOfCountries to the fragment, which will
        // then pass the listOfCountries to the login button.
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(Fragment_Register.TAG);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        fragment = getSupportFragmentManager()
                .findFragmentByTag(Fragment_Login.TAG);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (!mNavigationDrawerFragment.isDrawerOpen()) {
//
//        }
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem gotocart = menu.findItem(R.id.goto_shopping_cart);
        MainActivity.notification = menu.findItem(R.id.shopping_cart_count);

        gotocart.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                gotoShoppingCart();
                return false;
            }
        });

        restoreActionBar();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("navigationPosition", navigationPosition);
    }

    public static String page = "0";

}
