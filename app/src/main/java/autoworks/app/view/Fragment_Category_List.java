package autoworks.app.view;

import android.annotation.SuppressLint;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import autoworks.app.R;
import autoworks.app.view.adapters.CategoryListAdapter;
import autoworks.app.model.Category;
import helpers.HttpPOSTOnTaskCompleted;
import helpers.HttpPOSTProcess;
import events.OnCategoryItemClickListener;
import autoworks.app.dialogs.TransparentProgressDialog;


public class Fragment_Category_List extends Fragment implements View.OnClickListener{

    private ArrayList<Category> mCategories = new ArrayList<>();

    public Fragment_Category_List() {
        MainActivity.categoryList = new ArrayList<>();
    }
    // TODO: Rename parameter arguments, choose names that match

    @SuppressLint("ValidFragment")
    public Fragment_Category_List(ArrayList<Category> categoriesList){
        MainActivity.categoryList = categoriesList;
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

    private int getParentCategoryIdx(ArrayList<Category> parentCategories, int parentID){
        for(int i =0; i < parentCategories.size(); i ++) {
            if(parentCategories.get(i).getmCategoryParentID() == parentID) {
                return i;
            }
        }

        return -1;
    }

    private View myview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AutoWorks", "oCreateView fragment_category_list");
        myview = inflater.inflate(R.layout.fragment__category__list, container,false);


//        final RelativeLayout progressDialog = (RelativeLayout) myview.findViewById(R.id.categoryListLoadingPanel);
//        progressDialog.setVisibility(View.VISIBLE);
        final Dialog progressDialog = TransparentProgressDialog.createProgressDialog(getActivity());
        progressDialog.show();

        if(MainActivity.categoryList.size() <= 0) {
            if(MainActivity.rootCategoriesList.size() <= 0 ) {
                //create loading progress bar
//                progressDialog.setVisibility(View.VISIBLE);
//                progressDialog.dismiss();

                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("parent_id", "0"));
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

                                    MainActivity.allCategories.add(cat);

                                    MainActivity.rootCategoriesList.add(cat);
                                }

                                MainActivity.categoryList = MainActivity.rootCategoriesList;
                                MainActivity.currentCateogoryLevel = 0;
                                // our adapter instance
                                CategoryListAdapter adapter = new CategoryListAdapter(getActivity(), R.layout.category_item, MainActivity.rootCategoriesList);
                                // create a new ListView, set the adapter and item click listener
                                ListView listViewItems = (ListView)myview.findViewById(R.id.category_list);
                                listViewItems.setAdapter(adapter);
                                listViewItems.setOnItemClickListener(new OnCategoryItemClickListener(getFragmentManager(), getActivity()));

                                if(progressDialog != null) {
//                                    progressDialog.setVisibility(View.GONE);
                                    progressDialog.dismiss();
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
                MainActivity.categoryList = MainActivity.rootCategoriesList;
                MainActivity.currentCateogoryLevel = 0;
                // our adapter instance
                CategoryListAdapter adapter = new CategoryListAdapter(getActivity(), R.layout.category_item, MainActivity.rootCategoriesList);
                // create a new ListView, set the adapter and item click listener
                ListView listViewItems = (ListView)myview.findViewById(R.id.category_list);
                listViewItems.setAdapter(adapter);
                listViewItems.setOnItemClickListener(new OnCategoryItemClickListener(getFragmentManager(), getActivity()));
            }
        }
        else {
            // our adapter instance
            CategoryListAdapter adapter = new CategoryListAdapter(getActivity(), R.layout.category_item, MainActivity.categoryList);
            // create a new ListView, set the adapter and item click listener
            ListView listViewItems = (ListView) myview.findViewById(R.id.category_list);
            listViewItems.setAdapter(adapter);
            listViewItems.setOnItemClickListener(new OnCategoryItemClickListener(getFragmentManager(), getActivity()));
        }
        if(progressDialog != null) {
//                                    progressDialog.setVisibility(View.GONE);
            progressDialog.dismiss();
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
