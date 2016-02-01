package autoworks.app.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.TextView;

import java.util.ArrayList;

import autoworks.app.R;
import autoworks.app.view.adapters.CartWishListCustomProductAdapter;
import autoworks.app.model.CustomProduct;
import helpers.CartCustomProductSaver;


public class Fragment_Shopping_Cart extends Fragment implements View.OnClickListener {
    private View myview;

    private CartWishListCustomProductAdapter adapter;
    private ListView listProductCart;

    private TextView total_price, total_quantity;
    private ArrayList<CustomProduct> customProductArrayList;

    private RelativeLayout mButtonCheckout;

    public Fragment_Shopping_Cart() {
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
        myview = inflater.inflate(R.layout.fragment__shopping__cart, container, false);

        mButtonCheckout = (RelativeLayout) myview.findViewById(R.id.cart_checkout);
        mButtonCheckout.setOnClickListener(this);

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
        total_price = (TextView) myview.findViewById(R.id.total_price);
        total_quantity = (TextView) myview.findViewById(R.id.total_quantity);
        Bundle bundle =new Bundle();

        customProductArrayList = CartCustomProductSaver.getInstance(this.getActivity()).getListOfCartProducts();
        adapter = new CartWishListCustomProductAdapter(this,R.layout.shopping_product_item, customProductArrayList);

        listProductCart.setAdapter(adapter);

       /* listProductCart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment_Product_Detail fragment_product_detail = new Fragment_Product_Detail(customProductArrayList.get(position));
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment_product_detail)
                        .addToBackStack("addToBackStack fragment_product_detail")
                        .commit();

            }
        });*/

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
            final  RelativeLayout progressDialog = (RelativeLayout) myview.findViewById(R.id.shoppingCartLoadingPanel);
            CartCustomProductSaver.getInstance(this.getActivity()).loadListOfCartProducts(progressDialog);
        }
        updateTotalPrice();

        return myview;
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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.cart_checkout) {
            Fragment_Order_Checkout_Shipping fragment_order_checkout_shipping = new Fragment_Order_Checkout_Shipping();
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.container, fragment_order_checkout_shipping)
                    .addToBackStack("addToBackStack fragment_order_checkout_shipping")
                    .commit();
        }
    }

}



