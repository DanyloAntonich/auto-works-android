package autoworks.app.view;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meetme.android.horizontallistview.HorizontalListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import autoworks.app.R;
import events.OnProductSearchItemClickListener;
import helpers.HttpPOSTOnTaskCompleted;
import helpers.HttpPOSTProcess;
import helpers.JSONParser;
import autoworks.app.model.CustomProduct;
import autoworks.app.view.adapters.CustomProductAdapter;

/**
 * Created by Tan on 6/13/2014.
 */
//AdapterView.OnItemClickListener
public class Fragment_HorizontalListview_Products extends Fragment implements AbsListView.OnItemClickListener {

    private View viewRoot;
    Bundle myBackupBundle;
    //private GridView mDrawerGridView;

    private HorizontalListView mProductSLideShow;
    public static JSONParser jsonParser;
    private ArrayList<CustomProduct> mCustomProducts = new ArrayList<CustomProduct>();
    private String mTitle;
    private String mProductTag;

    public Fragment_HorizontalListview_Products() {

    }

    @SuppressLint("ValidFragment")
    public Fragment_HorizontalListview_Products(String product_tag, String title){
        this.mCustomProducts = new ArrayList<CustomProduct>();
        this.mTitle = title;
        this.mProductTag = product_tag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(1);
    }

    private Toast toast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AutoWorks", "oCreateView fragment_home_product");
        viewRoot = inflater.inflate(R.layout.horizontal_listview_product_wrapper, container,false);

        mProductSLideShow = (HorizontalListView) viewRoot.findViewById(R.id.block_products);

        TextView title = (TextView) viewRoot.findViewById(R.id.block_title);
        title.setText(mTitle);

        getProducts();

        return viewRoot;
    }

    private void getProducts() {
        mCustomProducts = new ArrayList<CustomProduct>();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("page", MainActivity.page));
        params.add(new BasicNameValuePair("limit", "5"));
        params.add(new BasicNameValuePair("language_id", MainActivity.currentUser.getLanguageId().toString()));
        params.add(new BasicNameValuePair("keyword", ""));
        params.add(new BasicNameValuePair("'sort_by'", "date_added"));
        params.add(new BasicNameValuePair("'sort'", "DESC"));

//        if(mTitle == "SPECIAL PRODUCTS" && MainActivity.currentUser.getCustomerID() > 0) {
//            params.add(new BasicNameValuePair("product_type", "specialProduct"));
//        }
//        else {
//            params.add(new BasicNameValuePair("page", "0"));
//        }

        if(MainActivity.currentUser.getCustomerID() > 0) {
            params.add(new BasicNameValuePair("customer_id", MainActivity.currentUser.getCustomerID().toString()));
        }

        if(MainActivity.currentUser.getCustomerGroupID() > 0) {
            params.add(new BasicNameValuePair("customer_group_id", MainActivity.currentUser.getCustomerGroupID().toString()));
        }

        HttpPOSTProcess getProductsAsync = new HttpPOSTProcess(MainActivity.getDataURL2, mProductTag, params, new HttpPOSTOnTaskCompleted() {
            @Override
            public void onHttpPOSTOnTaskCompleted(JSONObject json) {
                try {
                    if (json!=null && !json.isNull(MainActivity.KEY_PRODUCTS)) {
                        JSONArray arr = json.getJSONArray(MainActivity.KEY_PRODUCTS);

                        int lenth = arr.length();
                        for(int i=0; i<arr.length(); i++)
                        {
                            JSONObject jsObj = arr.getJSONObject(i);

                            CustomProduct pro = new CustomProduct();
                            pro.setId(jsObj.getInt(MainActivity.KEY_PRODUCT_ID));
                            pro.setProductName(jsObj.getString(MainActivity.KEY_PRODUCT_NAME));
                            pro.setProductImage(jsObj.getString(MainActivity.KEY_PRODUCT_PICTURE));
                            pro.setProductPrice(jsObj.getString(MainActivity.KEY_PRODUCT_PRICE));
                            pro.setProductSpecialPrice(jsObj.getString(MainActivity.KEY_PRODUCT_SPECIAL));

                            mCustomProducts.add(pro);
                        }

                        setupSpecialProductSlideshow();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void resetDotSelectBackground(int viewId) {
        LinearLayout ll = (LinearLayout) viewRoot.findViewById(viewId);

        for (int i = 0; i < ll.getChildCount(); i ++) {
            ImageView img = (ImageView)ll.getChildAt(i);
            img.setBackgroundResource(R.drawable.dot_not_selected);
        }
    }

    private void setUpBlockDots(int nums, int rID) {
        LinearLayout ll = (LinearLayout) viewRoot.findViewById(rID);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 0, 0, 0);

        if(nums > 0) {
            for (int i = 0; i < nums; i ++) {
                ImageView img = new ImageView(getActivity());
                img.setId(i);
                if(i == 0) {
                    img.setBackgroundResource(R.drawable.dot_selected);
                }
                else {
                    img.setBackgroundResource(R.drawable.dot_not_selected);
                }
                img.setLayoutParams(lp);

                ll.addView(img);
            }
        }
    }

    private void setDotIsViewing(int rID, HorizontalListView list) {
        LinearLayout ll = (LinearLayout) viewRoot.findViewById(rID);
        ImageView img = (ImageView)ll.getChildAt(list.getFirstVisiblePosition());
        img.setBackgroundResource(R.drawable.dot_selected);
    }

    private void setupSpecialProductSlideshow() {
        // Make an array adapter using the built in android layout to render a list of strings
        CustomProductAdapter adapter = new CustomProductAdapter(getActivity(), mCustomProducts);
        //add more dots

        setUpBlockDots(mCustomProducts.size(), R.id.listSpecialProductDots);

        // Assign adapter to HorizontalListView
        //mHlvCustomList.setAdapter(adapter);
        mProductSLideShow.setAdapter(adapter);
        mProductSLideShow.setOnItemClickListener(new OnProductSearchItemClickListener(getFragmentManager()));
        mProductSLideShow.setOnScrollStateChangedListener(new HorizontalListView.OnScrollStateChangedListener() {
            @Override
            public void onScrollStateChanged(ScrollState scrollState) {
                if (scrollState.toString().equals(ScrollState.SCROLL_STATE_IDLE.toString())) {
                    resetDotSelectBackground(R.id.listSpecialProductDots);
                    setDotIsViewing(R.id.listSpecialProductDots, mProductSLideShow);

                    //System.out.println("STOPPEDDDDDDDDDDDDDDDDDDDDD:" + mHlvCustomListWithDividerAndFadingEdge.getFirstVisiblePosition());
                }
            }
        });

        mProductSLideShow.setOnItemClickListener(new OnProductSearchItemClickListener(getFragmentManager()));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
