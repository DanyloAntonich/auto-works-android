package autoworks.app.view;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import autoworks.app.R;

public class Fragment_Home extends Fragment implements TabHost.OnTabChangeListener {

    private TabHost tabhost;
    private FragmentTabHost mTabHost;
    private View viewRoot;
    private int selectedTab = 0;
    private TextView frg_home_product_textView;

    public Fragment_Home(){
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            Log.d("AutoWorks", "onGet savedInstanceState fragment_home");
            this.setRetainInstance(true);
            selectedTab = savedInstanceState.getInt("selectedTab");
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AutoWorks", "onCreateView fragment_home");
        viewRoot = inflater.inflate(R.layout.fragment_home, container,false);

        return viewRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tabhost = (TabHost) viewRoot.findViewById(R.id.tabHost);
        SetupTabs();
        tabhost.setOnTabChangedListener(this);
    }

    // auto exec when changing rotation
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d("AutoWorks", "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);

        // Lock rotation
        /*switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            default:
                break;
        }
        */
        switch (selectedTab) {
            case 0: // do something for layout-0
                updateTab(R.id.fragment_home_product);
                break;
            case 1: // do something for layout-1
                updateTab(R.id.fragment_home_collection);
                break;
            case 2: // do something for layout-2
                updateTab(R.id.fragment_home_member);
                break;
        }
    }

    private void SetupTabs(){
        tabhost.setup();
        Log.d("AutoWorks", "onTabSetup fragment_home");
        //tabhost.addTab(tabhost.newTabSpec(getString(R.string.home_tab_caption_1)).setIndicator(getString(R.string.home_tab_caption_1)), Fragment_Home_Product.class, null);

        TabHost.TabSpec tabspec;

        tabspec = tabhost.newTabSpec(getString(R.string.home_tab_caption_1));
        tabspec.setContent(R.id.fragment_home_product);
        Log.d("AutoWorks", "onSetContent fragment_home_product");
        tabspec.setIndicator(getString(R.string.home_tab_caption_1), getResources().getDrawable(R.drawable.home));
        tabhost.addTab(tabspec);

        tabspec = tabhost.newTabSpec(getString(R.string.home_tab_caption_2));
        tabspec.setContent(R.id.fragment_home_collection);
        Log.d("AutoWorks", "onSetContent fragment_home_collection");
        tabspec.setIndicator(getString(R.string.home_tab_caption_2), getResources().getDrawable(R.drawable.home));
        tabhost.addTab(tabspec);

        tabspec = tabhost.newTabSpec(getString(R.string.home_tab_caption_3));
        tabspec.setContent(R.id.fragment_home_member);
        Log.d("AutoWorks", "onSetContent fragment_home_member");
        tabspec.setIndicator(getString(R.string.home_tab_caption_3), getResources().getDrawable(R.drawable.home));
        tabhost.addTab(tabspec);

        //tabhost.setCurrentTab(selectedTab);

        switch (selectedTab) {
            case 0: // do something for layout-0
                updateTab(R.id.fragment_home_product);
                break;
            case 1: // do something for layout-1
                updateTab(R.id.fragment_home_collection);
                break;
            case 2: // do something for layout-2
                updateTab(R.id.fragment_home_member);
                break;
        }
    }

    private void ChangeBottomLineTabhost(){
        TabWidget widget = tabhost.getTabWidget();
        for(int i = 0; i < widget.getChildCount(); i++) {
            View v = widget.getChildAt(i);
            // Look for the title view to ensure this is an indicator and not a divider.
            TextView tv = (TextView)v.findViewById(android.R.id.title);
            tv.setTextColor(getResources().getColor(R.color.tabhost_text));
            if(tv == null) {
                continue;
            }
            if(i == tabhost.getCurrentTab())
                v.setBackgroundResource(R.drawable.tab_selected_pressed_holo);
            else
                v.setBackgroundResource(R.drawable.tab_unselected_pressed_holo);

        }
    }

    @Override
    public void onTabChanged(String tabId) {
        Log.d("AutoWorks", "onTabChanged fragment_home " + selectedTab);
        selectedTab = tabhost.getCurrentTab();
        switch (selectedTab) {
            case 0: // do something for layout-0
                updateTab(R.id.fragment_home_product);
                break;
            case 1: // do something for layout-1
                updateTab(R.id.fragment_home_collection);
                break;
            case 2: // do something for layout-2
                updateTab(R.id.fragment_home_member);
                break;
        }
    }

    private void updateTab(int placeholder) {
        if (selectedTab == 0){
            Log.d("AutoWorks","onSelected fragment_home_product");
            Fragment_Home_Product fragment_home_product = new Fragment_Home_Product();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(placeholder, fragment_home_product)
                    .addToBackStack("addToBackStack fragment_home_product")
                    .commit();
        }
        else if (selectedTab == 1){
            Log.d("AutoWorks","onSelected fragment_home_collection");
            Fragment_Home_Collection fragment_home_collection = new Fragment_Home_Collection();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(placeholder, fragment_home_collection)
                    .addToBackStack("addToBackStack fragment_home_collection")
                    .commit();
        }
        else if (selectedTab == 2){
            Log.d("AutoWorks","onSelected fragment_home_member");
            Fragment_Home_Member fragment_home_member = new Fragment_Home_Member();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(placeholder, fragment_home_member)
                    .addToBackStack("addToBackStack fragment_home_member")
                    .commit();
        }
        ChangeBottomLineTabhost();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedTab",selectedTab);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
