package events;

/**
 * Created by volyminhnhan on 12/02/2015.
 */
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import autoworks.app.view.Fragment_Order_Detail;
import autoworks.app.R;
import autoworks.app.model.OrderItem;

/*
 * Here you can control what to do next when the user selects an item
 */
public class OnOrderItemClickListener implements OnItemClickListener {

    private FragmentManager fragmentManager;

    public OnOrderItemClickListener(final FragmentManager fManager) {
        this.fragmentManager = fManager;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Context context = view.getContext();

        OrderItem order = (OrderItem)parent.getItemAtPosition(position);

        Fragment_Order_Detail fragment_order_detail = new Fragment_Order_Detail(order.getOrderID());
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment_order_detail)
                .addToBackStack("addToBackStack fragment_order_detail")
                .commit();


    }

}