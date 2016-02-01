package autoworks.app.view.adapters;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import autoworks.app.R;
import autoworks.app.model.CustomProduct;

/**
 * Created by INNOVATION on 3/17/2015.
 */
public class CheckoutCustomProductAdapter  extends CartWishListCustomProductAdapter{

    ArrayList<CustomProduct> data = new ArrayList<CustomProduct>();

    public CheckoutCustomProductAdapter(Fragment fragment, int layout, ArrayList<CustomProduct> values) {
        super(fragment, layout, values);

        this.data = values;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {


        convertView = super.getView(position, convertView, parent);

        CustomProduct pro = data.get(position);

//        if (convertView == null) {
//
//
//
//            convertView.setTag(holder);
//        }
//        else {
//            holder = (Holder) convertView.getTag();
//        }

        Holder holder;
        holder = new Holder();
        holder.productName = (TextView) convertView.findViewById(R.id.item1_tittle);
        holder.productPrice = (TextView) convertView.findViewById(R.id.item1_price);
        holder.productQuantity = (TextView) convertView.findViewById(R.id.item1_quantity);
        holder.productTotal = (TextView) convertView.findViewById(R.id.item1_amount);

        Double price = Double.valueOf(pro.getProductSpecialPrice()) > 0 ? Double.valueOf(pro.getProductSpecialPrice()) : Double.valueOf(pro.getProductPrice());
        Double total = price*pro.getQuantity();

        holder.productName.setText(pro.getProductName());
        holder.productPrice.setText(price.toString());
        holder.productQuantity.setText(pro.getQuantity());
        holder.productTotal.setText(total.toString());

        return convertView;
    }

    /** View holder for the views we need access to */
    private static class Holder {
        public TextView productName;
        public TextView productPrice;
        public TextView productQuantity;
        public TextView productTotal;
    }
}
