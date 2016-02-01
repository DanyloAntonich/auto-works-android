package autoworks.app.view;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import autoworks.app.R;
import autoworks.app.view.adapters.OrderHistoryAdapter;
import autoworks.app.dialogs.DatePickerFragment;
import autoworks.app.model.CustomProduct;
import autoworks.app.model.OrderItem;
import events.OnOrderItemClickListener;
import helpers.HttpPOSTOnTaskCompleted;
import helpers.HttpPOSTProcess;
import autoworks.app.dialogs.TransparentProgressDialog;
import helpers.UserFunctions;

public class Fragment_Order_History extends Fragment implements View.OnClickListener{

    private TextView mHistoryStart, mHistoryEnd;
    private ImageView mHistoryClear;
    private OrderHistoryAdapter adapter;

    public Fragment_Order_History() {
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
        Log.d("AutoWorks", "oCreateView fragment__order__history");
        myview = inflater.inflate(R.layout.fragment__order__history, container,false);

        mHistoryStart = (TextView) myview.findViewById(R.id.history_start);
        mHistoryStart.setOnClickListener(this);

        mHistoryEnd = (TextView) myview.findViewById(R.id.history_end);
        mHistoryEnd.setOnClickListener(this);

        mHistoryClear = (ImageView) myview.findViewById(R.id.history_clear);
        mHistoryClear.setOnClickListener(this);


        //set filter action
        ListView listViewItems = (ListView)myview.findViewById(R.id.order_list);
        listViewItems.setOnItemClickListener(new OnOrderItemClickListener(getFragmentManager()));
        mHistoryStart.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // our adapter instance
                adapter.getOderFilter().filter(s + "~@~" + mHistoryEnd.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        mHistoryEnd.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // our adapter instance
                adapter.getOderFilter().filter(mHistoryStart.getText().toString() + "~@~" + s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });

        //check login
        UserFunctions.isUserLoggedInThenSwitch(getActivity().getSupportFragmentManager());


        final Dialog order_progressDialog = TransparentProgressDialog.createProgressDialog(this.getActivity());
        order_progressDialog.show();

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("customer_id", MainActivity.currentUser.getCustomerID().toString()));
        params.add(new BasicNameValuePair("language_id", MainActivity.currentUser.getLanguageId().toString()));

        if(MainActivity.orderList.size() <= 0) {
            HttpPOSTProcess getOrdersAsync = new HttpPOSTProcess(MainActivity.getDataURL2, MainActivity.order_tag, params, new HttpPOSTOnTaskCompleted() {
                @Override
                public void onHttpPOSTOnTaskCompleted(JSONObject json) {
                    try {
                        if (json!=null && !json.isNull(MainActivity.KEY_ORDER)) {
                            JSONArray arr = json.getJSONArray(MainActivity.KEY_ORDER);

                            ArrayList<OrderItem> temp = new ArrayList<OrderItem>();

                            for(int i=0; i<arr.length(); i++)
                            {
                                JSONObject jsObj = arr.getJSONObject(i);

                                OrderItem order = new OrderItem();
                                order.setOrderID(jsObj.getInt(MainActivity.KEY_ORDER_ID));
                                order.setDate(jsObj.getString(MainActivity.KEY_ORDER_DATE));
                                order.setStatus(jsObj.getString(MainActivity.KEY_ORDER_STATUS));
                                order.setTotal(jsObj.getDouble(MainActivity.KEY_ORDER_TOTAL));

                                ArrayList<CustomProduct> orderProducts = new ArrayList<>();

                                JSONArray oProducts = jsObj.getJSONArray(MainActivity.KEY_PRODUCTS);

                                for(int j=0; j< oProducts.length(); j++)
                                {
                                    JSONObject product = oProducts.getJSONObject(j);

                                    CustomProduct pro = new CustomProduct();
                                    pro.setId(product.getInt(MainActivity.KEY_PRODUCT_ID));
                                    pro.setProductName(product.getString(MainActivity.KEY_PRODUCT_NAME));
                                    pro.setProductImage(product.getString(MainActivity.KEY_PRODUCT_PICTURE));
                                    pro.setProductPrice(product.getString(MainActivity.KEY_PRODUCT_PRICE));
                                    pro.setQuantity(product.getInt(MainActivity.KEY_PRODUCT_QUANTITY));
                                    pro.setTotalPrice(product.getDouble(MainActivity.KEY_PRODUCT_TOTAL_PRICE));

                                    orderProducts.add(pro);
                                }

                                order.setOrderProducts(orderProducts);

                                MainActivity.orderList.add(order);
                                temp.add(order);
                            }

                            // our adapter instance
                            adapter = new OrderHistoryAdapter(getActivity(), R.layout.order_item, temp);
                            // create a new ListView, set the adapter and item click listener
                            final ListView listViewItems = (ListView)myview.findViewById(R.id.order_list);
                            listViewItems.setAdapter(adapter);
                            listViewItems.setOnItemClickListener(new OnOrderItemClickListener(getFragmentManager()));

                            if(order_progressDialog != null) {
                                order_progressDialog.dismiss();
                            }
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        else {
            ArrayList<OrderItem> temp = new ArrayList<OrderItem>(MainActivity.orderList);
            // our adapter instance
            adapter = new OrderHistoryAdapter(getActivity(), R.layout.order_item, temp);
            // create a new ListView, set the adapter and item click listener
            listViewItems = (ListView)myview.findViewById(R.id.order_list);
            listViewItems.setAdapter(adapter);
            listViewItems.setOnItemClickListener(new OnOrderItemClickListener(getFragmentManager()));

            if(order_progressDialog != null) {
                order_progressDialog.dismiss();
            }
        }


        return myview;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.history_start:{
                DialogFragment datepicker = new DatePickerFragment(getActivity(), mHistoryStart);
                datepicker.show(getActivity().getSupportFragmentManager(), "datePickerStart");

                break;
            }
            case R.id.history_end:{
                DialogFragment datepicker = new DatePickerFragment(getActivity(), mHistoryEnd);
                datepicker.show(getActivity().getSupportFragmentManager(), "datePickerEnd");

                break;
            }
            case R.id.history_clear: {
                mHistoryStart.setText("");
                mHistoryEnd.setText("");

                break;
            }
        }
    }

}
