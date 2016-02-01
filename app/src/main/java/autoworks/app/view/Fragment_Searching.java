package autoworks.app.view;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.apache.http.NameValuePair;

import java.util.LinkedList;
import java.util.List;

import autoworks.app.R;
import autoworks.app.Utilities.UIUtility;
import autoworks.app.dialogs.FilterByCategory;
import autoworks.app.dialogs.FilterBySortItem;
import helpers.Global;

public class Fragment_Searching extends Fragment implements View.OnClickListener{

    private GridView gridViewItems;

    public Fragment_Searching() {
        // Required empty public constructor
        MainActivity.mpParamsSearchList = new LinkedList<NameValuePair>();
    }

    @SuppressLint("ValidFragment")
    public Fragment_Searching(List<NameValuePair> params){
        MainActivity.mpParamsSearchList = params;
        MainActivity.productSearchList.clear();
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
        ActionBar actionBar = getActivity().getActionBar();
        if(actionBar != null) {
            ImageView logo = (ImageView)actionBar.getCustomView().findViewById(R.id.actionBarLogo);
            if(logo != null) {
                logo.setVisibility(View.VISIBLE);
            }
        }

    }
    private EditText inputSearch;
    private View myview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AutoWorks", "oCreateView fragment_searching");
        myview = inflater.inflate(R.layout.fragment__searching, container,false);

        inputSearch  = (EditText)myview.findViewById(R.id.inputSearch);

        if(!MainActivity.keyword.equals("")) {
            inputSearch.setText(MainActivity.keyword);
            MainActivity.categoryList.clear();
        }

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
        RelativeLayout rl = (RelativeLayout) myview.findViewById(R.id.search_box_container);
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

        //get product by params1
        gridViewItems = (GridView)myview.findViewById(R.id.product_list);
        Global.getProductsByParams(getActivity(),getFragmentManager(),myview,gridViewItems,0,MainActivity.mpParamsSearchList);

        //set click for category filter
        RelativeLayout filter_category = (RelativeLayout)myview.findViewById(R.id.searching_filter_category);
        filter_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterByCategory filter_cate = new FilterByCategory(getFragmentManager());
                filter_cate.show(getActivity().getSupportFragmentManager(), "searching_filter_category");
            }
        });

        //set click for category sort
        RelativeLayout filter_sortitem = (RelativeLayout)myview.findViewById(R.id.searching_filter_sort);
        filter_sortitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterBySortItem filter_sort = new FilterBySortItem(myview);
                filter_sort.show(getActivity().getSupportFragmentManager(), "searching_sort_products");
            }
        });

        return myview;
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {

    }

}
