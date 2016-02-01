package autoworks.app.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import autoworks.app.view.Fragment_Searching;
import autoworks.app.view.MainActivity;
import autoworks.app.R;
import autoworks.app.view.adapters.CategoryListAdapter;
import helpers.Global;

/**
 * Created by volyminhnhan on 2/20/15.
 */
public class FilterByCategory extends DialogFragment {
    private FragmentManager mFragmentManager;

    public FilterByCategory() {

    }

    @SuppressLint("ValidFragment")
    public FilterByCategory(FragmentManager fragmentManager) {
        this.mFragmentManager = fragmentManager;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // our adapter instance
        CategoryListAdapter adapter = new CategoryListAdapter(getActivity(), R.layout.category_item, MainActivity.categoryList);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_title_select_category)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        MainActivity.productSearchList.clear();
                        Fragment_Searching fragment_searching = new Fragment_Searching(Global.createBasicProductByCategorySearchParams(MainActivity.categoryList.get(position)));
                        mFragmentManager.beginTransaction()
                                .replace(R.id.container, fragment_searching)
                                .addToBackStack("addToBackStack fragment_searching")
                                .commit();
                    }
                });
        return builder.create();
    }
}