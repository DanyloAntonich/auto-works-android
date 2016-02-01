package autoworks.app.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import autoworks.app.R;
import autoworks.app.model.Customer;
import imageloader.ImageLoader;

/**
 * Created by Tan on 7/15/2014.
 */
public class Fragment_Home_Member_MemberDetail extends BaseAdapter {

    private View myview;
    private Context context;
    private ArrayList<Customer> userList;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;


    private class ViewHolder {
        ImageView profile_picture, rating1, rating2, rating3, rating4, rating5;
        TextView user_name;
    }

    public Fragment_Home_Member_MemberDetail(Context context, ArrayList<Customer> uList){
        // TODO Auto-generated constructor stub
        this.context = context;
        this.userList = uList;
        this.inflater = LayoutInflater.from(this.context);
        imageLoader=new ImageLoader(context,100);
    }

    @Override
    public int getCount() {
        if(userList!=null)
            return userList.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_home_member_memberdetail, null);
            mViewHolder = new ViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.profile_picture = (ImageView) convertView.findViewById(R.id.profile_picture);
        mViewHolder.user_name = (TextView) convertView.findViewById(R.id.user_name);
        mViewHolder.rating1 = (ImageView) convertView.findViewById(R.id.rating1);
        mViewHolder.rating2 = (ImageView) convertView.findViewById(R.id.rating2);
        mViewHolder.rating3 = (ImageView) convertView.findViewById(R.id.rating3);
        mViewHolder.rating4 = (ImageView) convertView.findViewById(R.id.rating4);
        mViewHolder.rating5 = (ImageView) convertView.findViewById(R.id.rating5);

        mViewHolder.user_name.setText(userList.get(position).getFullname());
        imageLoader.DisplayImage(userList.get(position).getProfile_picture(), mViewHolder.profile_picture);

        int i = 0;
        while (i < 5) {
            ImageView i_rating;
            if (i == 0)
                i_rating = (ImageView) convertView.findViewById(R.id.rating1);
            else  if (i == 1)
                i_rating = (ImageView) convertView.findViewById(R.id.rating2);
            else  if (i == 2)
                i_rating = (ImageView) convertView.findViewById(R.id.rating3);
            else  if (i == 3)
                i_rating = (ImageView) convertView.findViewById(R.id.rating4);
            else
                i_rating = (ImageView) convertView.findViewById(R.id.rating5);

            Rating(i,4,i_rating);
            i++;
        }
        return convertView;
    }

    public void Rating(int count, int rating, ImageView i_rating)
    {
        if(count <= rating) {
            i_rating.setImageResource(R.drawable.star_enable);
        }
        else
            i_rating.setImageResource(R.drawable.star_disable);
    }

}
