package events;

/**
 * Created by volyminhnhan on 12/02/2015.
 */
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import autoworks.app.view.Fragment_Product_Detail;
import autoworks.app.R;
import autoworks.app.model.CustomProduct;

/*
 * Here you can control what to do next when the user selects an item
 */
public class OnProductSearchItemClickListener implements OnItemClickListener {

    private FragmentManager fragmentManager;

    public OnProductSearchItemClickListener(final FragmentManager fManager) {
        this.fragmentManager = fManager;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Context context = view.getContext();

        CustomProduct pro = (CustomProduct)parent.getItemAtPosition(position);

        Fragment_Product_Detail fragment_product_detail = new Fragment_Product_Detail(pro.getId());
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment_product_detail)
                .addToBackStack("addToBackStack fragment_product_detail")
                .commit();

    }

}