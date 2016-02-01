package autoworks.app.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import autoworks.app.R;

/**
 * Created by Tan on 6/13/2014.
 */
public class Fragment_Home_Collection extends Fragment implements AdapterView.OnItemClickListener {

    private View myview;
    Bundle myBackupBundle;
    GridView mDrawerGridView;

    Communicator comm;

    //Set GridView Adapter
    Fragment_Home_Collection_CollectionDetail myadapter = null;

    public Fragment_Home_Collection(){

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AutoWorks", "oCreateView fragment_home_collection");
        myview = inflater.inflate(R.layout.fragment_home_collection, container,false);
        return myview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myBackupBundle=savedInstanceState;
        mDrawerGridView = (GridView) myview.findViewById(R.id.gridView);

        comm = (Communicator) getActivity();


        myadapter = new Fragment_Home_Collection_CollectionDetail(getActivity(), MainActivity.collectionList);
        mDrawerGridView.setAdapter(myadapter);

        mDrawerGridView.setOnItemClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ShowDetail(position);
    }

    public void ShowDetail(int position)
    {
        comm.GetCollectionDetail(MainActivity.collectionList.get(position).getProductCollectionID());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


}
