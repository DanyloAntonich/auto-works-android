package helpers;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import autoworks.app.view.MainActivity;
import autoworks.app.R;
import autoworks.app.view.adapters.ProductSearchListAdapter;
import autoworks.app.dialogs.TransparentProgressDialog;
import autoworks.app.model.Category;
import autoworks.app.model.CustomProduct;
import autoworks.app.model.OrderItem;
import autoworks.app.model.SortItem;
import events.OnProductSearchItemClickListener;

/**
 * Created by Vo Ly Minh Nhan on 10/22/14.
 */
public class Global {
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private static Dialog searching_progressDialog;
    public static void getProductsByParams(final Activity activity, final FragmentManager fragmentManager, final View rootView, final GridView gridViewItems, final Integer page, final List<NameValuePair> params) {
        if(MainActivity.isGettingProduct) {
            return;
        }

        params.add(new BasicNameValuePair("page", page.toString()));

        //create loading progress bar
        if(MainActivity.productSearchList.size() <= 0) {
            searching_progressDialog = TransparentProgressDialog.createProgressDialog(activity);
            searching_progressDialog.show();
        }

        MainActivity.isGettingProduct = true;
        HttpPOSTProcess getProductAsync = new HttpPOSTProcess(MainActivity.getDataURL2, MainActivity.get_product_tag, params, new HttpPOSTOnTaskCompleted() {
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

                            MainActivity.productSearchList.add(pro);
                        }

                        if(!json.isNull(MainActivity.KEY_CATEGORIES)) {
                            JSONArray categories = json.getJSONArray(MainActivity.KEY_CATEGORIES);

                            if(categories.length() > 0) {
                                for(int i=0; i< categories.length(); i++)
                                {
                                    JSONObject jsObj = categories.getJSONObject(i);

                                    Category cat = new Category();
                                    cat.setmCategoryID(jsObj.getInt(MainActivity.KEY_CATEGORY_ID));
                                    cat.setmCategoryName(jsObj.getString(MainActivity.KEY_CATEGORY_NAME));
                                    cat.setmCategoryImageURL(jsObj.getString(MainActivity.KEY_CATEGORY_IMAGE));
                                    cat.setmCategoryDescription("");

                                    if(!isInCategoriesList(MainActivity.categoryList, cat.getmCategoryID())) {
                                        MainActivity.categoryList.add(cat);
                                    }
                                }
                            }
                        }

                        if(arr.length() > 0) {
                            // our adapter instance
                            ProductSearchListAdapter adapter = new ProductSearchListAdapter(activity, R.layout.product_search_item, MainActivity.productSearchList);
                            // create a new ListView, set the adapter and item click listener
                            //adapter.notifyDataSetChanged();
                            gridViewItems.setAdapter(adapter);
                            gridViewItems.invalidateViews();
                            gridViewItems.setOnItemClickListener(new OnProductSearchItemClickListener(fragmentManager));

                            //set onscroll listner
                            gridViewItems.setOnScrollListener(new StaggeredGridView.OnScrollListener() {
                                public void onScrollStateChanged(AbsListView view, int scrollState) {
                                    System.out.println("scrollState:" + scrollState);
                                }

                                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                    if (totalItemCount > 0 && visibleItemCount > 0 && (totalItemCount - visibleItemCount) < 3) {
                                        getProductsByParams(activity, fragmentManager, rootView, gridViewItems, page + 1, params);
                                        System.out.println("visibleItemCount: " + visibleItemCount + ", totalItemCount: " + totalItemCount + ", page: " + (page + 1));
                                        System.out.println("Is getting product: " + ((MainActivity.isGettingProduct) ? "true" : "false"));
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(activity.getBaseContext(), R.string.error_no_products,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                if(searching_progressDialog != null) {
                    searching_progressDialog.dismiss();
                }
                MainActivity.isGettingProduct = false;
            }
        });
    }

    private static boolean isInCategoriesList(ArrayList<Category> categories, Integer category_id) {
        for(Category cat: categories) {
            if(cat.getmCategoryID() == category_id) {
                return true;
            }
        }

        return false;
    }

    public static void sortSearchGridview(final Activity activity, final FragmentManager fragmentManager, final View rootView, final GridView gridViewItems, final SortItem sortItem) {
        if(MainActivity.productSearchList.size() > 0) {
            // our adapter instance
            ProductSearchListAdapter adapter = new ProductSearchListAdapter(activity, R.layout.product_search_item, MainActivity.productSearchList);
            // create a new ListView, set the adapter and item click listener
            if(sortItem.getCode().equals("name,ASC")) {
                adapter.sort(new Comparator<CustomProduct>() {
                    public int compare(CustomProduct arg0, CustomProduct arg1) {
                        return arg0.getProductName().compareTo(arg1.getProductName());
                    }
                });
            }
            else if(sortItem.getCode().equals("name,DESC")){
                adapter.sort(new Comparator<CustomProduct>() {
                    public int compare(CustomProduct arg0, CustomProduct arg1) {
                        return arg1.getProductName().compareTo(arg0.getProductName());
                    }
                });
            }
            else if(sortItem.getCode().equals("price,ASC")) {
                adapter.sort(new Comparator<CustomProduct>() {
                    public int compare(CustomProduct arg0, CustomProduct arg1) {
                        return (Double.parseDouble(arg0.getProductPrice()) > Double.parseDouble(arg1.getProductPrice())) ? 1 : -1;
                    }
                });
            }
            else if(sortItem.getCode().equals("price,DESC")){
                adapter.sort(new Comparator<CustomProduct>() {
                    public int compare(CustomProduct arg0, CustomProduct arg1) {
                        return (Double.parseDouble(arg1.getProductPrice()) > Double.parseDouble((arg0.getProductPrice()))) ? 1 : -1;
                    }
                });
            }

            gridViewItems.setAdapter(adapter);
            gridViewItems.invalidateViews();
        }
        else {
            Toast.makeText(activity.getBaseContext(), R.string.error_no_products,
                    Toast.LENGTH_LONG).show();
        }
    }

    public static List<NameValuePair> createBasicProductByCategorySearchParams(Category currentCat) {
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("limit", MainActivity.limitItemPerLoading.toString()));
        params.add(new BasicNameValuePair("category_id", currentCat.getmCategoryID().toString()));
        params.add(new BasicNameValuePair("language_id", MainActivity.currentUser.getLanguageId().toString()));
        params.add(new BasicNameValuePair("customer_group_id", MainActivity.currentUser.getCustomerGroupID().toString()));

        if(!MainActivity.keyword.equals("")) {
            params.add(new BasicNameValuePair("keyword", MainActivity.keyword));
        }

        return params;
    }

    public static List<NameValuePair> createBasicProductsSearchParams(String keyword) {
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("limit", MainActivity.limitItemPerLoading.toString()));
        params.add(new BasicNameValuePair("customer_group_id", MainActivity.currentUser.getCustomerGroupID().toString()));
        params.add(new BasicNameValuePair("language_id", MainActivity.currentUser.getLanguageId().toString()));
        params.add(new BasicNameValuePair("keyword", keyword));
        params.add(new BasicNameValuePair("sort_by", "date_added"));
        params.add(new BasicNameValuePair("sort", "DESC"));

        return params;
    }


    public static ArrayList<SortItem> sortItems() {
        ArrayList<SortItem> items = new ArrayList<SortItem>();
        items.add(new SortItem("name,ASC","Name A-Z"));
        items.add(new SortItem("name,DESC","Name Z-A"));
        items.add(new SortItem("price,ASC","Price Ascending"));
        items.add(new SortItem("price,DESC","Price Descending"));

        return items;
    }


    public static OrderItem getOrderByOrderID(int orderId) {
        for(int i =0; i < MainActivity.orderList.size(); i ++) {
            OrderItem order = MainActivity.orderList.get(i);
            if(order.getOrderID().equals(orderId)) {
                return order;
            }
        }
        return null;
    }
}
