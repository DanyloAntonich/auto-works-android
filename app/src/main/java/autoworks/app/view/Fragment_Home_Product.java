package autoworks.app.view;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import autoworks.app.R;

import com.etsy.android.grid.StaggeredGridView;
import com.meetme.android.horizontallistview.HorizontalListView;

import autoworks.app.Utilities.UIUtility;
import helpers.Global;
import helpers.HttpPOSTOnTaskCompleted;
import helpers.HttpPOSTProcess;
import helpers.JSONParser;
import autoworks.app.view.adapters.CustomArrayAdapter;
import autoworks.app.model.CustomData;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Tan on 6/13/2014.
 */
//AdapterView.OnItemClickListener
public class Fragment_Home_Product extends Fragment implements View.OnClickListener {

    private View viewRoot;
    private EditText inputSearch;
    Bundle myBackupBundle;
    public static JSONParser jsonParser;
    private RelativeLayout update_profile_button, order_history_button;
    private StaggeredGridView mDrawerGridView;
    private boolean is_search = false;
    //private GridView mDrawerGridView;


    private HorizontalListView mHlvCustomListWithDividerAndFadingEdge;
    private HorizontalListView mSpecialProductSLideShow;

    private FragmentManager fragmentManager;

    private ArrayList<CustomData> mCustomData = new ArrayList<CustomData>();

    public Fragment_Home_Product(){
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(2);
    }

    private Toast toast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AutoWorks", "oCreateView fragment_home_product");
        viewRoot = inflater.inflate(R.layout.fragment_home_product, container,false);

        inputSearch   = (EditText)viewRoot.findViewById(R.id.inputSearch);

        inputSearch.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    doSearch();

                    return true;
                }
                return false;
            }
        });

        //add search icon to search bar
        RelativeLayout rl = (RelativeLayout) viewRoot.findViewById(R.id.search_box_container);
        ImageView iv = new ImageView(getActivity());
        iv.setImageDrawable(getResources().getDrawable(R.drawable.search_icon));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(UIUtility.pxToDp(getContext(), 60), UIUtility.pxToDp(getContext(), 60));
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.rightMargin = 30;
        params.topMargin = 30;
        rl.addView(iv, params);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch();
            }
        });


        //show logo
        android.support.v7.app.ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        ImageView logo = (ImageView)actionBar.getCustomView().findViewById(R.id.actionBarLogo);
        logo.setVisibility(View.VISIBLE);


        // Get references to UI widgets
        mHlvCustomListWithDividerAndFadingEdge = (HorizontalListView) viewRoot.findViewById(R.id.hlvCustomListWithDividerAndFadingEdge);

        getSlideShowImages();
        showProducts();


        update_profile_button = (RelativeLayout)viewRoot.findViewById(R.id.update_profile_button);
        update_profile_button.setOnClickListener(this);
        order_history_button = (RelativeLayout)viewRoot.findViewById(R.id.order_history_button);
        order_history_button.setOnClickListener(this);

        return viewRoot;
    }
    private void getSlideShowImages() {
        //get Slideshow images
        HttpPOSTProcess getSlideshowImagesAsync = new HttpPOSTProcess(MainActivity.getDataURL2, MainActivity.slideshow_image, new LinkedList<NameValuePair>(), new HttpPOSTOnTaskCompleted() {
            @Override
            public void onHttpPOSTOnTaskCompleted(JSONObject json) {
                try {
                    ArrayList<CustomData> result = new ArrayList<CustomData>();
                    if (json!=null && !json.isNull(MainActivity.KEY_IMAGES)) {
                        JSONArray arr = json.getJSONArray(MainActivity.KEY_IMAGES);
                        mCustomData.clear();
                        int lenth = arr.length();

                        for(int i=0; i<arr.length(); i++)
                        {
                            JSONObject jsObj = arr.getJSONObject(i);

                            CustomData pro = new CustomData();
                            pro.setImgURL(jsObj.getString(MainActivity.KEY_IMAGE));

                            mCustomData.add(pro);
                        }

                        setupHomeSlideShow();
                    }

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showProducts() {
        //set up latest products
        Fragment_HorizontalListview_Products fragment_latest_product = new Fragment_HorizontalListview_Products(MainActivity.get_product_tag, getString(R.string.latest_product));
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.latestProducts, fragment_latest_product)
                .addToBackStack("addToBackStack get latest products")
                .commit();

        //set up most sold products
        Fragment_HorizontalListview_Products fragment_most_sold_product = new Fragment_HorizontalListview_Products(MainActivity.get_most_sold_product, getString(R.string.most_sold));
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mostSoldProducts, fragment_most_sold_product)
                .addToBackStack("addToBackStack get special products")
                .commit();

        //set up most sold products
        if(MainActivity.currentUser.getCustomerID() > 0) {
            Fragment_HorizontalListview_Products fragment_also_bought_product = new Fragment_HorizontalListview_Products(MainActivity.get_related_products_customer_bought, getString(R.string.people_also_bought));
            fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.alsoBoughtProducts, fragment_also_bought_product)
                    .addToBackStack("addToBackStack get also bought products")
                    .commit();
        }
    }

    public void doSearch() {
        MainActivity.keyword = inputSearch.getText().toString();

        //getProductsFromServer();
        Fragment_Searching fragment_searching = new Fragment_Searching(Global.createBasicProductsSearchParams(MainActivity.keyword));
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment_searching)
                .addToBackStack("addToBackStack fragment_searching")
                .commit();

        Toast toast = Toast.makeText(getActivity().getBaseContext(), getString(R.string.search_text) + MainActivity.keyword, Toast.LENGTH_LONG);
        toast.show();
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

    private void setupHomeSlideShow() {
        // Make an array adapter using the built in android layout to render a list of strings
        CustomArrayAdapter adapter = new CustomArrayAdapter(getActivity(), mCustomData);
        //add more dots

        setUpBlockDots(mCustomData.size(), R.id.listViewDots);

        // Assign adapter to HorizontalListView
        //mHlvCustomList.setAdapter(adapter);
        mHlvCustomListWithDividerAndFadingEdge.setAdapter(adapter);
        mHlvCustomListWithDividerAndFadingEdge.setOnScrollStateChangedListener(new HorizontalListView.OnScrollStateChangedListener() {
            @Override
            public void onScrollStateChanged(ScrollState scrollState) {
                if(scrollState.toString().equals(HorizontalListView.OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE.toString())) {
                    if(mCustomData.size() > 0) {
                        resetDotSelectBackground(R.id.listViewDots);
                        setDotIsViewing(R.id.listViewDots, mHlvCustomListWithDividerAndFadingEdge);
                    }

                    //System.out.println("STOPPEDDDDDDDDDDDDDDDDDDDDD:" + mHlvCustomListWithDividerAndFadingEdge.getFirstVisiblePosition());
                }
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myBackupBundle=savedInstanceState;

    }

    @Override
    public final void onClick(View v) {
        switch (v.getId()){
            case R.id.order_history_button:{
                Log.d("AutoWorks","onSelected fragment_order_history");
                Fragment_Order_History fragment_order_history = new Fragment_Order_History();
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.container, fragment_order_history)
                        .addToBackStack("addToBackStack fragment_order_history")
                        .commit();
                break;
            }
            case R.id.update_profile_button:{
                Log.d("AutoWorks","onSelected fragment_update_profile");
                Fragment_Update_Profile fragment_update_profile = new Fragment_Update_Profile();
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.container, fragment_update_profile)
                        .addToBackStack("addToBackStack fragment_update_profile")
                        .commit();
                break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
