package autoworks.app.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import autoworks.app.R;
import autoworks.app.view.adapters.CheckoutCustomProductAdapter;
import autoworks.app.model.Country;
import autoworks.app.model.CountryCode;
import autoworks.app.model.CustomProduct;
import autoworks.app.model.PaymentMethodItem;
import autoworks.app.model.ShippingMethodItem;
import autoworks.app.model.Zone;
import helpers.CartCustomProductSaver;
import helpers.HttpPOSTOnTaskCompleted;
import helpers.HttpPOSTProcess;
import helpers.UserFunctions;


public class Fragment_Order_Checkout_Shipping extends Fragment implements View.OnClickListener {
    private View myview;

    private CheckoutCustomProductAdapter adapter;
    private ListView listProductCart;
    private View footerSummaryView, footerShippingView, footerPaymentView;
    private TextView total_price, total_quantity, total_ship, total_price_plus_total_ship;
    private EditText  mFirstName,mLastName, mPhone, mAddress;
    private String  mFirstNameText, mLastNameText, mPhoneText, mAddressText, mPaymentTitle, mPaymentCode, mShippingTitle, mShippingCode;
    private Spinner mCity, mCountry;
    private ImageView select_shipping_method_imageview, select_payment_method_imageview;
    private RelativeLayout mButtonSubmit, mButtonSubmit2;
    private ArrayList<CustomProduct> customProductArrayList;

    private RadioGroup mShippingMethods, mPaymentMethods;
    private CheckBox mIsNewAddress;
    private ArrayList<PaymentMethodItem> listPaymentMethods = new ArrayList<>();
    private ArrayList<ShippingMethodItem> listShippingMethods = new ArrayList<>();

    private boolean set_shipping = false;
    private boolean set_payment = false;


    public Fragment_Order_Checkout_Shipping() {
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


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //show logo
        android.support.v7.app.ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        ImageView logo = (ImageView) actionBar.getCustomView().findViewById(R.id.actionBarLogo);
        logo.setVisibility(View.VISIBLE);

    }


    @Override
    public void onStop() {
        super.onStop();
        CartCustomProductSaver.getInstance(this.getActivity()).dataToXml();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AutoWorks", "oCreateView fragment_register");
        myview = inflater.inflate(R.layout.fragment__order__checkout__shipping, container, false);
        //check login
        UserFunctions.isUserLoggedInThenSwitch(getActivity().getSupportFragmentManager());

        listProductCart = (ListView) myview.findViewById(R.id.orderListView);


        Bundle bundle =new Bundle();

        customProductArrayList = CartCustomProductSaver.getInstance(this.getActivity()).getListOfCartProducts();
        adapter = new CheckoutCustomProductAdapter(this,R.layout.vertical_product_checkout_item, customProductArrayList);


        CartCustomProductSaver.getInstance(this.getActivity()).setEventHandler(new CartCustomProductSaver.IEventHandler() {
            @Override
            public void onListChanged() {

                adapter.notifyDataSetChanged();
                updateTotalPrice();
            }

            @Override
            public void onTotalPriceChanged() {
                updateTotalPrice();
            }
        });
        if (customProductArrayList.isEmpty()) {
            final  RelativeLayout progressDialog = (RelativeLayout) myview.findViewById(R.id.productCheckoutLoadingPanel);
            CartCustomProductSaver.getInstance(this.getActivity()).loadListOfCartProducts(progressDialog);
        }


        footerSummaryView= inflater.inflate(R.layout.fragment__order__checkout__summary__footer, null);
        footerShippingView = inflater.inflate(R.layout.fragment__order__checkout__shipping__footer, null);
        footerPaymentView = inflater.inflate(R.layout.fragment__order__checkout__payment_footer, null);

        total_price = (TextView) footerSummaryView.findViewById(R.id.total_price);
        total_quantity = (TextView) footerSummaryView.findViewById(R.id.total_quantity);
        //total_ship = (TextView) footerSummaryView.findViewById(R.id.total_ship);
        total_price_plus_total_ship = (TextView) footerSummaryView.findViewById(R.id.total_price_plus_total_ship);

        select_shipping_method_imageview= (ImageView) footerPaymentView.findViewById(R.id.select_shipping_method_imageview);
        select_shipping_method_imageview.setOnClickListener(this);
        select_payment_method_imageview= (ImageView) footerShippingView.findViewById(R.id.select_payment_method_imageview);
        select_payment_method_imageview.setOnClickListener(this);

        mFirstName = (EditText) footerShippingView.findViewById(R.id.customer_firstname);
        mLastName = (EditText) footerShippingView.findViewById(R.id.customer_lastname);
        mPhone = (EditText) footerShippingView.findViewById(R.id.customer_phone);
        mAddress = (EditText) footerShippingView.findViewById(R.id.customer_address);
        mCountry = (Spinner) footerShippingView.findViewById(R.id.customer_country);
        mCity = (Spinner) footerShippingView.findViewById(R.id.customer_city);

        mIsNewAddress = (CheckBox) footerShippingView.findViewById(R.id.is_new_address);
        mIsNewAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    ValidateInput();
                    disableAllInputs();
                } else {
                    mFirstName.setEnabled(true);
                    mLastName.setEnabled(true);
                    mAddress.setEnabled(true);
                    mCity.setEnabled(true);
                    mPhone.setEnabled(true);
                    mCountry.setEnabled(true);
                }
            }
        });
        mIsNewAddress.setChecked(false);

        mShippingMethods = (RadioGroup) footerPaymentView.findViewById(R.id.shipping_methods);
        mShippingMethods.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Integer ra_id = group.getCheckedRadioButtonId();

                ShippingMethodItem item = listShippingMethods.get(ra_id);
                mShippingTitle = item.getmTitle();
                mShippingCode = item.getmCode();
                //Log.e("checked", ra_id.toString());
            }
        });

        mPaymentMethods = (RadioGroup) footerPaymentView.findViewById(R.id.payments_methods);
        mPaymentMethods.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Integer ra_id = group.getCheckedRadioButtonId();

                PaymentMethodItem item = listPaymentMethods.get(ra_id);
                mPaymentTitle = item.getmTitle();
                mPaymentCode = item.getmCode();

                //Log.e("checked", ra_id.toString());
            }
        });

        mFirstName.setText(MainActivity.currentUser.getFirstname());
        mLastName.setText(MainActivity.currentUser.getLastname());
        mPhone.setText(MainActivity.currentUser.getPhone());
        mAddress.setText(MainActivity.currentUser.getAddress());


//        if(MainActivity.listOfCountries.size() <= 0) {
//            //get country list
//            final RelativeLayout progressDialog = (RelativeLayout) myview.findViewById(R.id.productCheckoutLoadingPanel);
//            progressDialog.setVisibility(View.VISIBLE);
//
//            HttpPOSTProcess getCountry = new HttpPOSTProcess(MainActivity.getDataURL2, MainActivity.getCountries, new LinkedList<NameValuePair>(), new HttpPOSTOnTaskCompleted() {
//                @Override
//                public void onHttpPOSTOnTaskCompleted(JSONObject json) {
//
//                    int currentCountryIndex = 0;
//                    try {
//                        if (json != null && json.getJSONArray(MainActivity.KEY_COUNTRIES) != null) {
//                            JSONArray arr = json.getJSONArray(MainActivity.KEY_COUNTRIES);
//
//                            for (int i = 0; i < arr.length(); i++) {
//                                Country country = new Country();
//                                JSONObject jsObj = arr.getJSONObject(i);
//                                country.setCcode(jsObj.getString(MainActivity.KEY_COUNTRY_CODE_ISO));
//                                country.setCname(jsObj.getString(MainActivity.KEY_COUNTRY_NAME));
//                                //if (listOfCountries.contains(country))
//                                MainActivity.listOfCountries.add(country);
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
//                        mCountry.setAdapter(adapter);
//                        mCountry.setSelection(currentCountryIndex);
//
//                        if(progressDialog != null) {
//                            progressDialog.setVisibility(View.GONE);
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
//            MainActivity.listOfCountries.add(select_country);
//            final ArrayAdapter<Country> adapter = new ArrayAdapter<Country>(getActivity().getApplicationContext(), R.layout.spinner_item, MainActivity.listOfCountries);
//            mCountry.setAdapter(adapter);
//        }

        mCountry.setBackgroundColor(Color.WHITE);
//        mCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                // your code here
//
//                Country country = (Country)parentView.getAdapter().getItem(position);
//                mCountry.setBackgroundColor(Color.WHITE);
//
//                //mcustomer_country_code.setText(getCountryZipCode(country.getCcode()));
//
//                //get Zones by country id
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("country_code", country.getCcode()));
//
//                final RelativeLayout progressDialog = (RelativeLayout) myview.findViewById(R.id.productCheckoutLoadingPanel);
//                progressDialog.setVisibility(View.VISIBLE);
//
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
//
//                                for (int i = 0; i < arr.length(); i++) {
//                                    Zone zone = new Zone();
//                                    JSONObject jsObj = arr.getJSONObject(i);
//                                    zone.setZid(jsObj.getString(MainActivity.KEY_ZONE_ID));
//                                    zone.setZname(jsObj.getString(MainActivity.KEY_ZONE_NAME));
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
//                            mCity.setAdapter(adapterZone);
//                            mCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                                @Override
//                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                    //check address
//                                    ValidateInput();
//                                    //disableAllInputs();
//                                }
//
//                                @Override
//                                public void onNothingSelected(AdapterView<?> parent) {
//
//                                }
//                            });
//                            mCity.setSelection(currentCityIndex);
//
//                            if(progressDialog != null) {
//                                progressDialog.setVisibility(View.GONE);
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


        mButtonSubmit = (RelativeLayout) footerShippingView.findViewById(R.id.order_shipping_submit);
        mButtonSubmit.setOnClickListener(this);

        mButtonSubmit2 = (RelativeLayout) footerPaymentView.findViewById(R.id.order_payment_submit);
        mButtonSubmit2.setOnClickListener(this);

        updateTotalPrice();
        listProductCart.addFooterView(footerSummaryView);
        listProductCart.addFooterView(footerShippingView);
        listProductCart.addFooterView(footerPaymentView);
        footerPaymentView.findViewById(R.id.order_payment_bottom_layout).setVisibility(View.GONE);
        listProductCart.setAdapter(adapter);

        return myview;
    }

    private void disableAllInputs() {
        mFirstName.setEnabled(false);
        mLastName.setEnabled(false);
        mAddress.setEnabled(false);
        mCity.setEnabled(false);
        mPhone.setEnabled(false);
        mCountry.setEnabled(false);

        if (mFirstNameText.equals("")) {
            mFirstName.setEnabled(true);
        }

        if (mLastNameText.equals("")) {
            mLastName.setEnabled(true);
        }

        if (mAddressText.equals("")) {
            mAddress.setEnabled(true);
        }

        Zone sel_zone = (Zone) mCity.getSelectedItem();
        TextView sel_zone_txt = (TextView) mCity.getSelectedView();
        if (sel_zone.getZname().equals(getString(R.string.spinner_select_zone))) {
            sel_zone_txt.setEnabled(true);
        }

        if (mPhoneText.equals("")) {
            mPhone.setEnabled(true);
        }

        Country sel_country = (Country) mCountry.getSelectedItem();
        TextView sel_country_txt = (TextView) mCountry.getSelectedView();
        if (sel_country.getCname().equals(getString(R.string.spinner_select_country))) {
            sel_country_txt.setEnabled(true);
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

    void updateTotalPrice() {
        double totalPrice = 0;
        int totalQuantity = 0;
        for (CustomProduct product : new ArrayList<>(customProductArrayList)) {
          /*  try {
                product.setTotalPrice(Double.parseDouble(product.getProductPrice()) * product.getQuantity());
            } catch (Exception ex) {
            }*/
            totalPrice += product.getTotalPrice();
            totalQuantity += product.getQuantity();
        }
        total_price.setText("$ " + totalPrice);
        total_quantity.setText(totalQuantity + "");
        total_price_plus_total_ship.setText("$" + totalPrice);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.select_payment_method_imageview:
                if(!ValidateInput()) {
                    return;
                }
                //switch to payment view
                footerShippingView.findViewById(R.id.order_ship_bottom_layout).setVisibility(View.GONE);
                footerPaymentView.findViewById(R.id.order_payment_bottom_layout).setVisibility(View.VISIBLE);

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("is_new", ((mIsNewAddress.isChecked()) ? "1" : "0")));
                params.add(new BasicNameValuePair("email", MainActivity.currentUser.getEmail()));
                params.add(new BasicNameValuePair("firstname", mFirstNameText));
                params.add(new BasicNameValuePair("lastname",  mLastNameText));
                params.add(new BasicNameValuePair("address",  mAddressText));
                params.add(new BasicNameValuePair("telephone",  mPhoneText));
                Country sel_country = (Country) mCountry.getSelectedItem();
                params.add(new BasicNameValuePair("country_code", sel_country.getCcode()));
                Zone sel_zone = (Zone) mCity.getSelectedItem();
                params.add(new BasicNameValuePair("zone_id",  sel_zone.getZid()));

                final RelativeLayout progressDialog = (RelativeLayout) myview.findViewById(R.id.productCheckoutLoadingPanel);
                progressDialog.setVisibility(View.VISIBLE);

                //check for new address
                String check_out_flag = MainActivity.get_payment_shipping;
                if(mIsNewAddress.isChecked()) {
                    check_out_flag = MainActivity.new_payment_shipping;
                }

                HttpPOSTProcess getOrderAndShippingAsync = new HttpPOSTProcess(MainActivity.getDataURL2, check_out_flag, params, new HttpPOSTOnTaskCompleted() {
                    @Override
                    public void onHttpPOSTOnTaskCompleted(JSONObject json) {

                        try {
                            if (json!=null && !json.isNull(MainActivity.KEY_RESULT)) {
                                JSONObject result = json.getJSONObject(MainActivity.KEY_METHODS);

                                JSONArray shipping = result.getJSONArray(MainActivity.KEY_SHIPPING);

                                //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 30);

                                mShippingMethods.removeAllViews();
                                mPaymentMethods.removeAllViews();

                                if(shipping.length() > 0) {
                                    for(int i=0; i< shipping.length(); i ++) {
                                        JSONObject shipping_item = shipping.getJSONObject(i);

                                        RadioButton ra = new RadioButton(getActivity().getApplicationContext());
                                        ra.setText(shipping_item.getString(MainActivity.KEY_TITLE));
                                        ra.setTextColor(getResources().getColor(R.color.content_text));
                                        listShippingMethods.add(new ShippingMethodItem(shipping_item.getString(MainActivity.KEY_TITLE), shipping_item.getString(MainActivity.KEY_CODE)));

                                        if(i == 0) {
                                            ra.setChecked(true);
                                            mShippingTitle = shipping_item.getString(MainActivity.KEY_TITLE);
                                            mShippingCode = shipping_item.getString(MainActivity.KEY_CODE);
                                            set_shipping = true;
                                        }
                                        ra.setId(i);
                                        mShippingMethods.addView(ra);
                                    }
                                }

                                JSONArray payment = result.getJSONArray(MainActivity.KEY_PAYMENT);

                                if(payment.length() > 0) {
                                    for(int i=0; i< payment.length(); i ++) {
                                        JSONObject payment_item = payment.getJSONObject(i);

                                        RadioButton ra = new RadioButton(getActivity().getApplicationContext());
                                        ra.setText(payment_item.getString(MainActivity.KEY_TITLE));
                                        ra.setTextColor(getResources().getColor(R.color.content_text));
                                        ra.setId(i);
                                        listPaymentMethods.add(new PaymentMethodItem(payment_item.getString(MainActivity.KEY_TITLE), payment_item.getString(MainActivity.KEY_CODE)));

                                        if(i == 0) {
                                            ra.setChecked(true);
                                            set_payment = true;
                                            mPaymentTitle = payment_item.getString(MainActivity.KEY_TITLE);
                                            mPaymentCode = payment_item.getString(MainActivity.KEY_CODE);
                                        }
                                        mPaymentMethods.addView(ra);

                                    }
                                }

                                System.out.println(result);
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(progressDialog != null) {
                            progressDialog.setVisibility(View.GONE);
                        }
                    }
                });

                break;

            case R.id.select_shipping_method_imageview:
                //switch to shipping view
                footerShippingView.findViewById(R.id.order_ship_bottom_layout).setVisibility(View.VISIBLE);
                footerPaymentView.findViewById(R.id.order_payment_bottom_layout).setVisibility(View.GONE);
                break;

            case R.id.order_shipping_submit:
                if(!set_shipping) {
                    Toast toast = Toast.makeText(getActivity().getBaseContext(), getString(R.string.err_select_shipping), Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                if(!set_payment) {
                    Toast toast = Toast.makeText(getActivity().getBaseContext(), getString(R.string.err_select_payment), Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                postOrderToServer();

                break;

            case R.id.order_payment_submit:
                if(!set_shipping) {
                    Toast toast = Toast.makeText(getActivity().getBaseContext(), getString(R.string.err_select_shipping), Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                if(!set_payment) {
                    Toast toast = Toast.makeText(getActivity().getBaseContext(), getString(R.string.err_select_payment), Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                postOrderToServer();

                break;
        }
    }

    private void postOrderToServer() {

        if(ValidateInput()) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            String product_ids = "";
            String quantites = "";
            for(int i =0 ; i < CartCustomProductSaver.getInstance(this.getActivity()).getListOfCartProducts().size(); i ++)
            {
                CustomProduct pro = CartCustomProductSaver.getInstance(this.getActivity()).getListOfCartProducts().get(i);

                product_ids += pro.getId() + ",";
                quantites += pro.getQuantity() + ",";
            }

            params.add(new BasicNameValuePair("product_ids",  product_ids));
            params.add(new BasicNameValuePair("quantities",  quantites));

            params.add(new BasicNameValuePair("email", MainActivity.currentUser.getEmail()));
            params.add(new BasicNameValuePair("shipping_method",  mShippingTitle));
            params.add(new BasicNameValuePair("shipping_code",  mShippingCode));
            params.add(new BasicNameValuePair("payment_method", mPaymentTitle));
            params.add(new BasicNameValuePair("payment_code",  mPaymentCode));

            final RelativeLayout progressDialog = (RelativeLayout) myview.findViewById(R.id.productCheckoutLoadingPanel);
            progressDialog.setVisibility(View.VISIBLE);

            HttpPOSTProcess addOrdertAsync = new HttpPOSTProcess(MainActivity.getDataURL2, MainActivity.add_order_tag, params, new HttpPOSTOnTaskCompleted() {
                @Override
                public void onHttpPOSTOnTaskCompleted(JSONObject json) {

                    try {
                        if (json!=null && !json.isNull(MainActivity.KEY_RESULT)) {
                            Integer result = json.getInt(MainActivity.KEY_RESULT);

                            if(result == 1) {
                                MainActivity.currentUser.setFirstname(mFirstNameText);
                                MainActivity.currentUser.setLastname(mLastNameText);
                                MainActivity.currentUser.setAddress(mAddressText);
                                MainActivity.currentUser.setPhone(mPhoneText);
                                Country sel_country = (Country) mCountry.getSelectedItem();
                                MainActivity.currentUser.setCountryCode(sel_country.getCcode());
                                Zone sel_zone = (Zone) mCity.getSelectedItem();
                                MainActivity.currentUser.setCity(sel_zone.getZid());
                                //save to local
                                MainActivity.customerToXml(getActivity());
                                //clear cart
                                CartCustomProductSaver.getInstance(getActivity()).clearCart();
                                //show notice
                                Toast toast = Toast.makeText(getActivity().getBaseContext(), getString(R.string.complete_order), Toast.LENGTH_SHORT);
                                toast.show();

                                Fragment_Home_Product frag_home_product = new Fragment_Home_Product();
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.container, frag_home_product)
                                        .addToBackStack("addToBackStack fragment_home_product")
                                        .commit();
                            }

                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(progressDialog != null) {
                        progressDialog.setVisibility(View.GONE);
                    }
                    MainActivity.isGettingProduct = false;
                }
            });
        }
        else {
            Toast toast = Toast.makeText(getActivity().getBaseContext(), getString(R.string.err_complete_all_field), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public boolean ValidateInput() {
        boolean allchecked = true;
        mFirstNameText = mFirstName.getText().toString();
        mLastNameText = mLastName.getText().toString();
        mAddressText = mAddress.getText().toString();
        mPhoneText = mPhone.getText().toString();

        mFirstName.setHint("");
        mLastName.setHint("");
        mAddress.setHint("");
        mPhone.setHint("");

        if (!mFirstNameText.equals("")) {
            mFirstName.setBackgroundColor(Color.WHITE);
            mFirstName.setHint("");
        }
        if (!mLastNameText.equals("")) {
            mLastName.setBackgroundColor(Color.WHITE);
            mLastName.setHint("");
        }
        if (!mAddressText.equals("")) {
            mAddress.setBackgroundColor(Color.WHITE);
            mAddress.setHint("");
        }
        Zone sel_zone = (Zone) mCity.getSelectedItem();
        if (!sel_zone.getZname().equals(getString(R.string.spinner_select_zone))) {
            TextView sel_zone_txt = (TextView) mCity.getSelectedView();
            sel_zone_txt.setBackgroundColor(Color.WHITE);
            mCity.setBackgroundColor(Color.WHITE);
        }
        if (!mPhoneText.equals("")) {
            mPhone.setBackgroundColor(Color.WHITE);
            mPhone.setHint("");
        }
        Country sel_country = (Country) mCountry.getSelectedItem();
        if (!sel_country.getCname().equals(getString(R.string.spinner_select_country))) {
            TextView sel_country_txt = (TextView) mCountry.getSelectedView();
            sel_country_txt.setBackgroundColor(Color.WHITE);
        }

        if (mFirstNameText.equals("")) {
            allchecked = false;
            mFirstName.setHint(mFirstName.getHint().toString().replace(getString(R.string.error_input_emmpty), "").replace(": ", "") + ": " + getString(R.string.error_input_emmpty));
            mFirstName.setBackgroundResource(R.color.error_background_color);
        }
        if (mLastNameText.equals("")) {
            allchecked = false;
            mLastName.setHint(mLastName.getHint().toString().replace(getString(R.string.error_input_emmpty), "").replace(": ", "") + ": " + getString(R.string.error_input_emmpty));
            mLastName.setBackgroundResource(R.color.error_background_color);
        }
        if (mAddressText.equals("")) {
            allchecked = false;
            mAddress.setHint(getString(R.string.error_input_emmpty));
            mAddress.setBackgroundResource(R.color.error_background_color);
        }

        if (sel_zone == null || (sel_zone != null && sel_zone.getZname().equals(getString(R.string.spinner_select_zone)))) {
            allchecked = false;
            mCity.setBackgroundResource(R.color.error_background_color);
        }
        if (mPhoneText.equals("") || !CountryCode.isNumeric(mPhoneText.replace("+","").replace("-",""))) {
            allchecked = false;
            mPhone.setHint(getString(R.string.error_input_emmpty));
            mPhone.setBackgroundResource(R.color.error_background_color);
        }
        if (sel_country.getCname().equals(getString(R.string.spinner_select_country))) {
            allchecked = false;
            mCountry.setBackgroundResource(R.color.error_background_color);
            TextView sel_country_txt = (TextView) mCountry.getSelectedView();
            sel_country_txt.setBackgroundResource(R.color.error_background_color);
        }

        return allchecked;
    }

}
