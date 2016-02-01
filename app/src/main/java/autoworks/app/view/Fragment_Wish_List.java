package autoworks.app.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import autoworks.app.R;
import autoworks.app.view.adapters.CartWishListCustomProductAdapter;
import autoworks.app.model.CustomProduct;
import helpers.WishCustomProductSaver;


public class Fragment_Wish_List extends Fragment implements View.OnClickListener {
    private View myview;

    private CartWishListCustomProductAdapter adapter;
    private ListView listProductCart;

   // private TextView total_price,total_quantity;
    private ArrayList<CustomProduct> customProductArrayList;
    public Fragment_Wish_List() {
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
        android.support.v7.app.ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        ImageView logo = (ImageView)actionBar.getCustomView().findViewById(R.id.actionBarLogo);
        logo.setVisibility(View.VISIBLE);

    }

    @Override
    public void onStop()
    {
        super.onStop();
        WishCustomProductSaver.getInstance(this.getActivity()).dataToXml();
        System.out.println("test onStop");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AutoWorks", "oCreateView fragment_register");
        myview = inflater.inflate(R.layout.fragment__wish__list, container,false);
        listProductCart = (ListView) myview.findViewById(R.id.cartListView);

        listProductCart.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (SCROLL_STATE_TOUCH_SCROLL == scrollState) {
                    View currentFocus = getActivity().getCurrentFocus();
                    if (currentFocus != null) {
                        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        customProductArrayList = WishCustomProductSaver.getInstance(this.getActivity()).getListOfCartProducts();
        adapter = new CartWishListCustomProductAdapter(this, R.layout.wishlist_product_item,customProductArrayList);
        listProductCart.setAdapter(adapter);
        WishCustomProductSaver.getInstance(this.getActivity()).setEventHandler(new WishCustomProductSaver.IEventHandler() {
            @Override
            public void onListChanged() {
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onTotalPriceChanged() {

            }
        });
        if (customProductArrayList.isEmpty())
        {
            final RelativeLayout progressDialog = (RelativeLayout) myview.findViewById(R.id.wishListLoadingPanel);
            WishCustomProductSaver.getInstance(this.getActivity()).loadListOfCartProducts(progressDialog);
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
