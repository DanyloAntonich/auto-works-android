package autoworks.app.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import autoworks.app.R;
import autoworks.app.view.adapters.OrderDetailAdapter;
import autoworks.app.model.OrderItem;
import events.OnOrderItemClickListener;
import helpers.Global;
import helpers.UserFunctions;


public class Fragment_Order_Detail extends Fragment implements View.OnClickListener{

    private int mOrderID;
    private OrderItem currentOrder;
    private TextView mOrderDate, mOrderTotal, mOrderTotalItem;

    public Fragment_Order_Detail() {
        // Required empty public constructor

    }

    @SuppressLint("ValidFragment")
    public Fragment_Order_Detail(int orderId) {
        this.mOrderID = orderId;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(4);
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
    private View myview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AutoWorks", "oCreateView fragment__order__detail");
        myview = inflater.inflate(R.layout.fragment__order__detail, container,false);

        //check login
        UserFunctions.isUserLoggedInThenSwitch(getActivity().getSupportFragmentManager());

        currentOrder = Global.getOrderByOrderID(mOrderID);

        mOrderDate = (TextView) myview.findViewById(R.id.order_detail_date);
        mOrderDate.setText(currentOrder.getDate());

        mOrderTotal = (TextView) myview.findViewById(R.id.order_detail_total);
        mOrderTotal.setText(currentOrder.getTotal().toString());

        mOrderTotalItem = (TextView) myview.findViewById(R.id.order_detail_quantity);
        mOrderTotalItem.setText(String.valueOf(currentOrder.getOrderProducts().size()));


        // our adapter instance
        final OrderDetailAdapter adapter = new OrderDetailAdapter(getActivity(), R.layout.order_detail_item, currentOrder.getOrderProducts());
        // create a new ListView, set the adapter and item click listener
        final ListView listViewItems = (ListView)myview.findViewById(R.id.order_detail_item_list);
        listViewItems.setAdapter(adapter);
        listViewItems.setOnItemClickListener(new OnOrderItemClickListener(getFragmentManager()));

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
