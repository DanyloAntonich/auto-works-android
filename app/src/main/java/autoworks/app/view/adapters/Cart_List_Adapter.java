package autoworks.app.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import autoworks.app.R;
import autoworks.app.model.Order;


public class Cart_List_Adapter extends BaseAdapter
{

    private Context mContext;
    List<Order> cartList;

    public Cart_List_Adapter(Context context, List<Order> data)
    {
        super();
        mContext=context;
        cartList = data;

    }

    public int getCount()
    {
        // return the number of records in cursor
        if(cartList!=null)
            return cartList.size();
        return 0;
    }

    // getView method is called for each item of ListView
    public View getView(int position,  View view, ViewGroup parent)
    {
        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.cart_list_view, null);
        ImageView order_icon = (ImageView) view.findViewById(R.id.order_icon);
        TextView product_name = (TextView) view.findViewById(R.id.product_name);
        TextView product_count = (TextView) view.findViewById(R.id.product_count);
        TextView order_price = (TextView) view.findViewById(R.id.order_price);
        switch(cartList.get(position).getStatus()){
            case 1: order_icon.setImageResource(R.drawable.order_confirmed);
                break;
            case 2: order_icon.setImageResource(R.drawable.order_paid);
                break;
            case 4: order_icon.setImageResource(R.drawable.order_success);
                break;
            case 3: order_icon.setImageResource(R.drawable.order_canceled);
                break;
        }
        product_name.setText(cartList.get(position).getProduct().getProductName());
        product_count.setText(String.valueOf(cartList.get(position).getCount()));
        order_price.setText(String.valueOf(cartList.get(position).getProduct().getProductPrice()*cartList.get(position).getCount()));

        return view;
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}