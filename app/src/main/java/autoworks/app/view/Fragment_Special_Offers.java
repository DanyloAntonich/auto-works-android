package autoworks.app.view;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
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
import autoworks.app.view.adapters.SpecialOffersListAdapter;
import autoworks.app.dialogs.TransparentProgressDialog;
import autoworks.app.model.CustomProduct;
import events.OnProductSearchItemClickListener;
import helpers.HttpPOSTOnTaskCompleted;
import helpers.HttpPOSTProcess;
import helpers.UserFunctions;


public class Fragment_Special_Offers extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match

    private static Dialog specialOffers_progressDialog;
    private ListView listViewItems;

    public Fragment_Special_Offers() {
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

        //show logo
        android.support.v7.app.ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        ImageView logo = (ImageView)actionBar.getCustomView().findViewById(R.id.actionBarLogo);
        logo.setVisibility(View.VISIBLE);


    }
    private View myview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AutoWorks", "oCreateView fragment_register");
        myview = inflater.inflate(R.layout.fragment__special__offers, container,false);

        //check login
        UserFunctions.isUserLoggedInThenSwitch(getActivity().getSupportFragmentManager());

        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("customer_group_id",MainActivity.currentUser.getCustomerGroupID().toString()));
        params.add(new BasicNameValuePair("language_id", MainActivity.currentUser.getLanguageId().toString()));

        listViewItems = (ListView) myview.findViewById(R.id.product_list);

        if(MainActivity.specialOffersList.size() <= 0) {
            specialOffers_progressDialog = TransparentProgressDialog.createProgressDialog(getActivity());
            specialOffers_progressDialog.show();

            HttpPOSTProcess getProductAsync = new HttpPOSTProcess(MainActivity.getDataURL2, MainActivity.get_special_offer, params, new HttpPOSTOnTaskCompleted() {
                @Override
                public void onHttpPOSTOnTaskCompleted(JSONObject json) {
                    ArrayList<CustomProduct> result = new ArrayList<CustomProduct>();
                    try {
                        if (json!=null && !json.isNull(MainActivity.KEY_PRODUCTS)) {
                            JSONArray arr = json.getJSONArray(MainActivity.KEY_PRODUCTS);

                            for(int i=0; i<arr.length(); i++)
                            {
                                JSONObject jsObj = arr.getJSONObject(i);

                                CustomProduct pro = new CustomProduct();
                                pro.setId(jsObj.getInt(MainActivity.KEY_PRODUCT_ID));
                                pro.setProductName(jsObj.getString(MainActivity.KEY_PRODUCT_NAME));
                                pro.setProductImage(jsObj.getString(MainActivity.KEY_PRODUCT_PICTURE));
                                pro.setProductPrice(jsObj.getString(MainActivity.KEY_PRODUCT_PRICE));
                                pro.setProductSpecialPrice(jsObj.getString(MainActivity.KEY_PRODUCT_SPECIAL));
                                pro.setProductShortDescription(jsObj.getString(MainActivity.KEY_PRODUCT_DESCRIPTION));

                                MainActivity.specialOffersList.add(pro);
                            }

                            if(arr.length() > 0) {
                                // our adapter instance
                                SpecialOffersListAdapter adapter = new SpecialOffersListAdapter(getActivity(), R.layout.special_offers_item, MainActivity.specialOffersList);
                                // create a new ListView, set the adapter and item click listener
                                //adapter.notifyDataSetChanged();
                                listViewItems.setAdapter(adapter);
                                listViewItems.invalidateViews();
                            }
                            else {
                                Toast.makeText(getActivity().getBaseContext(), R.string.error_no_products,
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(specialOffers_progressDialog != null) {
                        specialOffers_progressDialog.dismiss();
                    }
                }
            });
        }
        else {
            // our adapter instance
            SpecialOffersListAdapter adapter = new SpecialOffersListAdapter(getActivity(), R.layout.special_offers_item, MainActivity.specialOffersList);
            // create a new ListView, set the adapter and item click listener
            //adapter.notifyDataSetChanged();
            listViewItems.setAdapter(adapter);
            listViewItems.invalidateViews();
            listViewItems.setOnItemClickListener(new OnProductSearchItemClickListener(getFragmentManager()));
        }


        return myview;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {

    }

}
