package autoworks.app.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.GridView;

import org.apache.http.message.BasicNameValuePair;

import autoworks.app.view.MainActivity;
import autoworks.app.R;
import autoworks.app.view.adapters.SortItemAdapter;
import autoworks.app.model.SortItem;
import helpers.Global;

/**
 * Created by volyminhnhan on 2/20/15.
 */
public class FilterBySortItem extends DialogFragment {
    private FragmentManager mFragmentManager;
    View mRootView;

    public FilterBySortItem(){

    }

    @SuppressLint("ValidFragment")
    public FilterBySortItem(View rootView) {
        this.mRootView = rootView;
    }

    @SuppressLint("ValidFragment")
    public FilterBySortItem(FragmentManager fragmentManager) {
        this.mFragmentManager = fragmentManager;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // our adapter instance
        final SortItemAdapter adapter = new SortItemAdapter(getActivity(), R.layout.sort_item, Global.sortItems());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_title_select_sort)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        SortItem sort = adapter.getItem(0);
                        MainActivity.mpParamsSearchList.add(new BasicNameValuePair("sort_by", sort.getCode().split(",")[0]));
                        MainActivity.mpParamsSearchList.add(new BasicNameValuePair("sort", sort.getCode().split(",")[1]));

                        GridView gridViewItems = (GridView)mRootView.findViewById(R.id.product_list);
                        Global.sortSearchGridview(getActivity(),getFragmentManager(), mRootView, gridViewItems, adapter.getItem(position));
                    }
                });

        return builder.create();
    }
}