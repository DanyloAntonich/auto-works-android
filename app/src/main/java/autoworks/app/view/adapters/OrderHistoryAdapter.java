package autoworks.app.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import autoworks.app.view.MainActivity;
import autoworks.app.R;
import autoworks.app.model.OrderItem;

// here's our beautiful adapter
public class OrderHistoryAdapter extends ArrayAdapter<OrderItem> {

    Context mContext;
    int layoutResourceId;
    ArrayList<OrderItem> data = new ArrayList<OrderItem>();
    private OrderItemFilter mFilter = new OrderItemFilter();

    public OrderHistoryAdapter(Context mContext, int layoutResourceId, ArrayList<OrderItem> data) {
        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        /*
         * The convertView argument is essentially a "ScrapView" as described is Lucas post
         * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
         * It will have a non-null value when ListView is asking you recycle the row layout.
         * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
         */
        // object item based on the position
        OrderItem order = data.get(position);
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new Holder();
            holder.orderID = (TextView) convertView.findViewById(R.id.order_id);
            holder.orderDateAdded = (TextView) convertView.findViewById(R.id.order_date);
            holder.orderStatus = (TextView) convertView.findViewById(R.id.order_status);
            holder.orderTotal = (TextView) convertView.findViewById(R.id.order_total);

            setContentView(holder, convertView, order, position);

            convertView.setTag(holder);

        }
        else {
            holder = (Holder) convertView.getTag();

            setContentView(holder, convertView, order, position);
        }


        return convertView;
    }

    private void setContentView(Holder holder, View convertView, OrderItem order, int position) {
        holder.orderID.setText(order.getOrderID().toString());
        holder.orderDateAdded.setText(order.getDate().toString());
        holder.orderStatus.setText(order.getStatus().toString());
        holder.orderTotal.setText(order.getTotal().toString());
    }

    public Filter getOderFilter() {
        return mFilter;
    }

    private class OrderItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();
            String[] filters = filterString.split("~@~");

            java.util.Date startDate;
            try {
                startDate = Date.valueOf(filters[0]);
            }
            catch (Exception ex) {
                startDate = Date.valueOf("2000-01-01");
            }

            java.util.Date endDate;
            try {
                endDate = Date.valueOf(filters[1]);
            }
            catch (Exception ex) {
                endDate = new java.util.Date();
            }

            FilterResults results = new FilterResults();

            final ArrayList<OrderItem> nlist = new ArrayList<OrderItem>();

            OrderItem orderItem ;

            data.clear();
            data.addAll(new ArrayList<>( MainActivity.orderList));

            for (int i = 0; i <  data.size(); i++) {
                orderItem = data.get(i);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date orderDate;
                try {
                    orderDate = dateFormat.parse(orderItem.getDate());
                }
                catch (ParseException ex) {
                    orderDate = new java.util.Date();
                }

                if (orderDate.after(startDate) && orderDate.before(endDate)) {
                    nlist.add(orderItem);
                }
            }

            if(!constraint.equals("~@~")) {
                results.values = nlist;
                results.count = nlist.size();
            }
            else {
                ArrayList<OrderItem> temp = new ArrayList<>(MainActivity.orderList);

                results.values = temp;
                results.count = temp.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
//            ArrayList<OrderItem> temp = (ArrayList<OrderItem>) results.values;
            data.clear();
            data.addAll( (ArrayList<OrderItem>) results.values);
            notifyDataSetChanged();
        }

    }

    /** View holder for the views we need access to */
    private static class Holder {
        public TextView orderID;
        public TextView orderDateAdded;
        public TextView orderStatus;
        public TextView orderTotal;
    }
}