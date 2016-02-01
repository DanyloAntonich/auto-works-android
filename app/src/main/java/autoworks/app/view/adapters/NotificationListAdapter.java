package autoworks.app.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import autoworks.app.R;
import autoworks.app.model.Notification;

// here's our beautiful adapter
public class NotificationListAdapter extends ArrayAdapter<Notification> {

    Context mContext;
    int layoutResourceId;
    ArrayList<Notification> data = new ArrayList<Notification>();

    public NotificationListAdapter(Context mContext, int layoutResourceId, ArrayList<Notification> data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

        }
        RelativeLayout item_background = (RelativeLayout)convertView.findViewById(R.id.item_background);
        TextView item_tittle = (TextView)convertView.findViewById(R.id.item_tittle);
        TextView item_message = (TextView)convertView.findViewById(R.id.item_message);

        // object item based on the position
        Notification notification = data.get(position);
        item_tittle.setText(notification.getTitle());
        item_message.setText(notification.getMessage());

        if (notification.isRead())
        {
            item_tittle.setTextColor(Color.GRAY);
            item_message.setTextColor(Color.GRAY);
            item_background.setBackgroundColor(Color.TRANSPARENT);
        }else
        {
            item_tittle.setTextColor(Color.BLACK);
            item_message.setTextColor(Color.BLACK);
            item_background.setBackgroundColor(Color.rgb(177,255,250));
        }

        return convertView;

    }

}