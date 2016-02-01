package autoworks.app.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import autoworks.app.R;
import autoworks.app.model.SortItem;

// here's our beautiful adapter
public class SortItemAdapter extends ArrayAdapter<SortItem> {

    Context mContext;
    int layoutResourceId;
    ArrayList<SortItem> data = new ArrayList<SortItem>();

    public SortItemAdapter(Context mContext, int layoutResourceId, ArrayList<SortItem> data) {
        super(mContext, layoutResourceId, data);

        this.mContext = mContext;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
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
        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new Holder();
            holder.sortName = (TextView) convertView.findViewById(R.id.item_tittle);
            holder.sortName.setText(data.get(position).getName());

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        return convertView;
    }

    /** View holder for the views we need access to */
    private static class Holder {
        public TextView sortName;
    }
}

