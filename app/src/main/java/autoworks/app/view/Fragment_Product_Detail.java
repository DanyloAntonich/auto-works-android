package autoworks.app.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
// HEAD
import android.widget.RelativeLayout;
//=======
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meetme.android.horizontallistview.HorizontalListView;
//>>>>>>> master

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

//<<<<<<< HEAD
//=======
import autoworks.app.R;
import autoworks.app.dialogs.TransparentProgressDialog;
import autoworks.app.view.adapters.RelatedProductAdapter;
//>>>>>>> master
import autoworks.app.model.CustomProduct;
import events.OnProductSearchItemClickListener;
import helpers.CartCustomProductSaver;
import helpers.CustomProductCacher;
import helpers.HttpPOSTOnTaskCompleted;
import helpers.HttpPOSTProcess;
//<<<<<<< HEAD
import helpers.ImageDownloader2;
import helpers.WishCustomProductSaver;
//=======


public class Fragment_Product_Detail extends Fragment implements View.OnClickListener {

    private int mProductID = 0;
    private static CustomProduct mCurrentProduct = new CustomProduct();
    private RelativeLayout detail2_add_cart, detail2_add_wish;
    private NumberFormat formatter = new DecimalFormat("#0");


    private TextView mName, mPrice, mSpecialPrice, mDescription;
    private ImageView mImage;
    private HorizontalListView mRelatedProduct;

    public Fragment_Product_Detail() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public Fragment_Product_Detail(int productID) {
        // Required empty public constructor
        this.mProductID = productID;
    }

    @SuppressLint("ValidFragment")
    public Fragment_Product_Detail(CustomProduct product) {
        // Required empty public constructor
        this.mCurrentProduct = product;
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
        android.support.v7.app.ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        ImageView logo = (ImageView) actionBar.getCustomView().findViewById(R.id.actionBarLogo);
        logo.setVisibility(View.VISIBLE);

    }

    private static View myview;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AutoWorks", "oCreateView fragment__product_detail");
        myview = inflater.inflate(R.layout.fragment__product_detail, container, false);

//        final RelativeLayout progressDialog = (RelativeLayout) myview.findViewById(R.id.productDetailLoadingPanel);
        final Dialog progressDialog = TransparentProgressDialog.createProgressDialog(getActivity());
//        progressDialog.show();

        //get textview
        mName = (TextView) myview.findViewById(R.id.product_detail_tittle);
        mImage = (ImageView) myview.findViewById(R.id.product_detail_image);
        mPrice = (TextView) myview.findViewById(R.id.product_detail_oldprice);
        mSpecialPrice = (TextView) myview.findViewById(R.id.product_detail_newprice);
        mDescription = (TextView) myview.findViewById(R.id.product_detail_description);
        mRelatedProduct = (HorizontalListView) myview.findViewById(R.id.relatedProducts);

        detail2_add_cart = (RelativeLayout) myview.findViewById(R.id.detail2_add_cart);
        detail2_add_wish = (RelativeLayout) myview.findViewById(R.id.detail2_add_wish);
        detail2_add_cart.setOnClickListener(this);
        detail2_add_wish.setOnClickListener(this);


        mCurrentProduct.addIProductLoadingHandler(new CustomProduct.IProductLoadingHandler() {
            @Override
            public void onLoadProductCompleted() {
                if (progressDialog != null){
//                    progressDialog.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }
//

                mName.setText(mCurrentProduct.getProductName());
                // ImageDownloader2.getImageBitmap(mCurrentProduct.getProductImage(), mCurrentProduct, null);
                if (mCurrentProduct.getLoadedBitmap() == null)
                    ImageDownloader2.getImageBitmap(mCurrentProduct.getProductImage(), mCurrentProduct, new ImageDownloader2.ILoadingEvent() {
                        @Override
                        public void onLoaded(final Bitmap bm) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mImage.setImageBitmap(bm);
                                }
                            });

                        }
                    });
                else
                    mImage.setImageBitmap(mCurrentProduct.getLoadedBitmap());

                String special = mCurrentProduct.getProductSpecialPrice();
                if (!special.isEmpty() && special != "null" && special != "0.0") {
                    try {
                        mPrice.setText("$" + formatter.format(Double.parseDouble(mCurrentProduct.getProductPrice())));
                    } catch (Exception e) {
                    }
                    try {
                        mSpecialPrice.setText("$" + formatter.format(Double.parseDouble(mCurrentProduct.getProductSpecialPrice())));
                    } catch (Exception e) {
                    }
                } else {
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    mPrice.setVisibility(View.INVISIBLE);
                    try {
                        mSpecialPrice.setText("$" + formatter.format(Double.parseDouble(mCurrentProduct.getProductPrice())));
                    } catch (Exception e) {
                    }
                    mSpecialPrice.setLayoutParams(lp);
                }
                mDescription.setText(Html.fromHtml(mCurrentProduct.getProductDescription()));


                //set related products
                System.out.println("getActivity() = " + getActivity() + ", " + mCurrentProduct.getRelatedProducts());
                RelatedProductAdapter adapter = new RelatedProductAdapter(getActivity(), mCurrentProduct.getRelatedProducts());
                //add more dots
                setUpBlockDots(mCurrentProduct.getRelatedProducts().size(), R.id.listRelatedProductDots);
                // Assign adapter to HorizontalListView
                // mHlvCustomList.setAdapter(adapter);
                mRelatedProduct.setAdapter(adapter);
                mRelatedProduct.setOnItemClickListener(new OnProductSearchItemClickListener(getFragmentManager()));
                mRelatedProduct.setOnScrollStateChangedListener(new HorizontalListView.OnScrollStateChangedListener() {
                    @Override
                    public void onScrollStateChanged(ScrollState scrollState) {
                        if (scrollState.toString().equals(ScrollState.SCROLL_STATE_IDLE.toString())) {
                            resetDotSelectBackground(R.id.listRelatedProductDots);
                            setDotIsViewing(R.id.listRelatedProductDots, mRelatedProduct);

                            //System.out.println("STOPPEDDDDDDDDDDDDDDDDDDDDD:" + mHlvCustomListWithDividerAndFadingEdge.getFirstVisiblePosition());
                        }
                    }
                });
            }
        });

        loadProduct( mProductID, false, mCurrentProduct, this.getActivity());

        if (mCurrentProduct.isLoading() &&!mCurrentProduct.isLoaded())
        {
//            progressDialog.setVisibility(View.VISIBLE);
            progressDialog.show();
        }

        return myview;
    }

    private void resetDotSelectBackground(int viewId) {
        LinearLayout ll = (LinearLayout) myview.findViewById(viewId);

        for (int i = 0; i < ll.getChildCount(); i ++) {
            ImageView img = (ImageView)ll.getChildAt(i);
            img.setBackgroundResource(R.drawable.dot_not_selected);
        }
    }

    private void setUpBlockDots(int nums, int rID) {
        LinearLayout  ll = (LinearLayout) myview.findViewById(rID);
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
        LinearLayout ll = (LinearLayout) myview.findViewById(rID);
        ImageView img = (ImageView)ll.getChildAt(list.getFirstVisiblePosition());
        img.setBackgroundResource(R.drawable.dot_selected);
    }
    public static void loadProduct(int id,boolean showDialog, final CustomProduct product, Context context) {
        System.out.println("got here 1");

        if (product == null)
            return;

        System.out.println("got here 2");

        if (product.isLoading()) {
            return;
        }

        System.out.println("got here 3");

        product.setLoading(true);

//        progressDialog.setVisibility(View.VISIBLE);

        CustomProduct cachedProduct = CustomProductCacher.getRawCustomProduct(id);
        if (cachedProduct !=null)
        {
            product.copy(cachedProduct);
            product.setLoaded(true);
            product.setLoading(false);
            return;
        }

        System.out.println("got here 4");

        //product has not been loaded
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("product_id", String.valueOf(id)));
        params.add(new BasicNameValuePair("customer_group_id", MainActivity.currentUser.getCustomerGroupID().toString()));
        params.add(new BasicNameValuePair("language_id", MainActivity.currentUser.getLanguageId().toString()));

        System.out.println("got here 5");

        MainActivity.isGettingProduct = false;

        HttpPOSTProcess getProductDetailAsync = new HttpPOSTProcess(MainActivity.getDataURL2, MainActivity.get_product_detail_tag, params, new HttpPOSTOnTaskCompleted() {
            @Override
            public void onHttpPOSTOnTaskCompleted(JSONObject json) {
                ArrayList<CustomProduct> result = new ArrayList<CustomProduct>();
                try {
                    if (json != null && !json.isNull(MainActivity.KEY_PRODUCT)) {
                        JSONObject jsObj = json.getJSONObject(MainActivity.KEY_PRODUCT);
                        // CustomProduct product = new CustomProduct();
                        // product = pro;
                        product.setId(jsObj.getInt(MainActivity.KEY_PRODUCT_ID));
                        product.setProductName(jsObj.getString(MainActivity.KEY_PRODUCT_NAME));
                        product.setProductImage(jsObj.getString(MainActivity.KEY_PRODUCT_PICTURE));
                        product.setProductPrice(jsObj.getString(MainActivity.KEY_PRODUCT_PRICE));
                        product.setProductSpecialPrice(jsObj.getString(MainActivity.KEY_PRODUCT_SPECIAL));
                        // pro.setProductDiscountPrice(jsObj.getString(MainActivity.KEY_PRODUCT_DISCOUNT)); //ERROR HERE
                        product.setProductShortDescription(jsObj.getString(MainActivity.KEY_PRODUCT_SHORT_DESCRIPTION));
                        product.setProductDescription(jsObj.getString(MainActivity.KEY_PRODUCT_DESCRIPTION));


                        //images
                        if (!jsObj.isNull(MainActivity.KEY_PRODUCT_PICTURES)) {
                            try {
                                JSONArray arr = json.getJSONArray(MainActivity.KEY_PRODUCT_PICTURES);
                                ArrayList<String> images = new ArrayList<>();

                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject image = arr.getJSONObject(i);

                                    images.add(image.getString(MainActivity.KEY_PRODUCT_ID));
                                }

                                product.setProductImages(images);
                            } catch (Exception ex) {

                            }
                        }

                        //realted products
                        if (!jsObj.isNull(MainActivity.KEY_RELATED_PRODUCTS)) {
                            ArrayList<CustomProduct> related_products = new ArrayList<>();
                            try {
                                JSONArray arr = jsObj.getJSONArray(MainActivity.KEY_RELATED_PRODUCTS);

                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject related_pros = arr.getJSONObject(i);

                                    CustomProduct r_pro = new CustomProduct();
                                    r_pro.setId(related_pros.getInt(MainActivity.KEY_PRODUCT_ID));
                                    r_pro.setProductName(related_pros.getString(MainActivity.KEY_PRODUCT_NAME));
                                    r_pro.setProductImage(related_pros.getString(MainActivity.KEY_PRODUCT_PICTURE));
                                    r_pro.setProductPrice(related_pros.getString(MainActivity.KEY_PRODUCT_PRICE));
                                    r_pro.setProductSpecialPrice(related_pros.getString(MainActivity.KEY_PRODUCT_SPECIAL));

                                    related_products.add(r_pro);
                                }

                            } catch (Exception ex) {

                            }
                            product.setRelatedProducts(related_products);
                        }

                        CustomProductCacher.addCustomProductCache(product);
                        product.setLoading(false);
                        product.setLoaded(true);

                        //mCurrentProduct = product;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    product.setLoading(false);
                    product.setLoaded(true);
                }
                MainActivity.isGettingProduct = false;
            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    Toast toast;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.detail2_add_cart:
                if (mCurrentProduct != null) {
                    int quantity =CartCustomProductSaver.getInstance(this.getActivity()).addCustomProduct(true, mCurrentProduct);
                    if (toast!=null)
                        toast.cancel();
                    toast = Toast.makeText(getActivity(), "Product id " + mCurrentProduct.getId() +" added to cart ("+quantity+")", Toast.LENGTH_SHORT);
                    toast.show();
                }

                break;

            case R.id.detail2_add_wish:

                if (mCurrentProduct != null) {
                    int quantity = WishCustomProductSaver.getInstance(this.getActivity()).addCustomProduct(false, mCurrentProduct);
                    if (toast!=null)
                        toast.cancel();
                    toast = Toast.makeText(getActivity(), "Product id " + mCurrentProduct.getId() +" added to wish list ("+quantity+")", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;

        }
    }


}
