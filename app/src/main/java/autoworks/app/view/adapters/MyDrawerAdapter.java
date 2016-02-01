package autoworks.app.view.adapters;

/**
 * Created by Tan on 7/4/2014.
 */
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import autoworks.app.R;

public class MyDrawerAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> titles;
    private ArrayList<Integer> images;
    private LayoutInflater inflater;
    private int[] selectedposition;

    public MyDrawerAdapter(Context context, ArrayList<String> titles, ArrayList<Integer> images,
                           int[] selectedposition) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.titles = titles;
        this.images = images;
        this.inflater = LayoutInflater.from(this.context);
        this.selectedposition = selectedposition;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return titles.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder mViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.drawer_list_navigation, null);
            mViewHolder = new ViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.tvTitle = (TextView) convertView
                .findViewById(R.id.navigation_text);
        mViewHolder.ivIcon = (ImageView) convertView
                .findViewById(R.id.navigation_icon);

        mViewHolder.tvTitle.setText(titles.get(position));
        mViewHolder.ivIcon.setImageResource(images.get(position));

        //Highlight the selected list item
        if (position == selectedposition[0]) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.navigation_selected));
            mViewHolder.tvTitle.setTextColor(context.getResources().getColor(R.color.navigation_text_hover));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
            mViewHolder.tvTitle.setTextColor(context.getResources().getColor(R.color.navigation_text));
        }

        return convertView;
    }



    private class ViewHolder {
        TextView tvTitle;
        ImageView ivIcon;
    }

}
