package events;

/**
 * Created by volyminhnhan on 12/02/2015.
 */
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import autoworks.app.view.Fragment_Category_List;
import autoworks.app.view.Fragment_Searching;
import autoworks.app.view.MainActivity;
import autoworks.app.R;
import autoworks.app.dialogs.TransparentProgressDialog;
import autoworks.app.model.Category;
import helpers.Global;
import helpers.HttpPOSTOnTaskCompleted;
import helpers.HttpPOSTProcess;

/*
 * Here you can control what to do next when the user selects an item
 */
public class OnCategoryItemClickListener implements OnItemClickListener {

    private FragmentManager fragmentManager;
    private Activity mActivity;

    public OnCategoryItemClickListener(final FragmentManager fManager, Activity activity) {
        this.fragmentManager = fManager;
        this.mActivity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Context context = view.getContext();

        Category currentCat = (Category)parent.getItemAtPosition(position);

        if(currentCat.getmCategoryParentID() == 0) {
            MainActivity.currentCateogoryLevel = 0;
        }

        ArrayList<Category> tempCategories = getCategoriesWithChilds(currentCat.getmCategoryID());

        if(tempCategories.size() > 0 && MainActivity.currentCateogoryLevel < 2) {
            MainActivity.categoryList = tempCategories;
            MainActivity.currentCateogoryLevel += 1;
            Fragment_Category_List fragment_category_list = new Fragment_Category_List(MainActivity.categoryList);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment_category_list)
                    .addToBackStack("addToBackStack fragment_category_list")
                    .commit();
        }
        else if(MainActivity.currentCateogoryLevel < 2) {
            final Dialog category_progressDialog = TransparentProgressDialog.createProgressDialog(mActivity);
            category_progressDialog.show();

            MainActivity.categoryList.clear();

            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("parent_id", currentCat.getmCategoryID().toString()));
            params.add(new BasicNameValuePair("language_id", MainActivity.currentUser.getLanguageId().toString()));

            HttpPOSTProcess getCategoriesAsync = new HttpPOSTProcess(MainActivity.getDataURL2, MainActivity.getCategoriesByParent, params, new HttpPOSTOnTaskCompleted() {
                @Override
                public void onHttpPOSTOnTaskCompleted(JSONObject json) {
                    try {
                        if (json!=null && !json.isNull(MainActivity.KEY_CATEGORIES)) {
                            JSONArray arr = json.getJSONArray(MainActivity.KEY_CATEGORIES);

                            for(int i=0; i<arr.length(); i++)
                            {
                                JSONObject jsObj = arr.getJSONObject(i);

                                Category cat = new Category();
                                cat.setmCategoryID(jsObj.getInt(MainActivity.KEY_CATEGORY_ID));
                                cat.setmCategoryName(jsObj.getString(MainActivity.KEY_CATEGORY_NAME));
                                cat.setmCategoryDescription(jsObj.getString(MainActivity.KEY_CATEGORY_DESCRIPTION));
                                cat.setmCategoryImageURL(jsObj.getString(MainActivity.KEY_CATEGORY_IMAGE));
                                cat.setmCategoryParentID(jsObj.getInt(MainActivity.KEY_CATEGORY_PARENT_ID));

                                MainActivity.categoryList.add(cat);
                            }

//                            if(category_progressDialog != null) {
//                                category_progressDialog.dismiss();
//                            }

                            MainActivity.currentCateogoryLevel += 1;
                            Fragment_Category_List fragment_category_list = new Fragment_Category_List(MainActivity.categoryList);
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, fragment_category_list)
                                    .addToBackStack("addToBackStack fragment_category_list")
                                    .commit();

                        }
                        if(category_progressDialog != null) {
                            category_progressDialog.dismiss();
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        else {
            Fragment_Searching fragment_searching = new Fragment_Searching(Global.createBasicProductByCategorySearchParams(currentCat));
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment_searching)
                    .addToBackStack("addToBackStack fragment_searching")
                    .commit();
        }

    }

    private ArrayList<Category> getCategoriesWithChilds(int categoriesID) {
        ArrayList<Category> childCategories = new ArrayList<Category>();
        Category cat = new Category();
        for(int i=0; i< MainActivity.allCategories.size(); i++)
        {
            cat = MainActivity.allCategories.get(i);

            if(cat.getmCategoryParentID() == categoriesID){
                childCategories.add(cat);
            }
        }

        if(childCategories.size() > 0) {
            for(int i=0; i< childCategories.size(); i++) {
                cat = childCategories.get(i);

                cat.setChildCategories(getCategoriesWithChilds(cat.getmCategoryID()));
            }
        }

        return childCategories;
    }

}