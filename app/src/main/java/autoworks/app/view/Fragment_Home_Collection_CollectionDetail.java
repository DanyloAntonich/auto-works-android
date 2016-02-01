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
import autoworks.app.model.Product;
import imageloader.ImageLoader;

/**
 * Created by Tan on 7/15/2014.
 */
public class Fragment_Home_Collection_CollectionDetail extends BaseAdapter {

    private View myview;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<ProductCollection> collectionList;
    private ImageLoader imageLoader;

    private class ViewHolder {
        ImageView product_picture_1,product_picture_2,product_picture_3,product_picture_4,profile_picture;
        TextView user_name, collection_name;
    }

    public Fragment_Home_Collection_CollectionDetail(Context context, ArrayList<ProductCollection> cList){
        // TODO Auto-generated constructor stub
        this.context = context;
        this.collectionList = cList;
        this.inflater = LayoutInflater.from(this.context);
        imageLoader=new ImageLoader(context,100);
    }

    @Override
    public int getCount() {
        if(collectionList!=null)
        {
            return collectionList.size();
        }
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
            convertView = inflater.inflate(R.layout.fragment_home_collection_collectiondetail, null);
            mViewHolder = new ViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }



        mViewHolder.product_picture_1 = (ImageView) convertView.findViewById(R.id.product_picture_1);
        mViewHolder.product_picture_2 = (ImageView) convertView.findViewById(R.id.product_picture_2);
        mViewHolder.product_picture_3 = (ImageView) convertView.findViewById(R.id.product_picture_3);
        mViewHolder.product_picture_4 = (ImageView) convertView.findViewById(R.id.product_picture_4);
        mViewHolder.profile_picture = (ImageView) convertView.findViewById(R.id.profile_picture);
        mViewHolder.user_name = (TextView) convertView.findViewById(R.id.user_name);
        mViewHolder.collection_name = (TextView) convertView.findViewById(R.id.collection_name);

        mViewHolder.collection_name.setText(collectionList.get(position).getProductCollectionName());

        ArrayList<Product> productList = collectionList.get(position).getProductList();
        if(productList!=null)
        {
            for(int i=0; i<4 && i<productList.size(); i++)
            {
                switch(i){
                    case 0: imageLoader.DisplayImage(productList.get(i).getProductPicture(), mViewHolder.product_picture_1);
                        break;
                    case 1: imageLoader.DisplayImage(productList.get(i).getProductPicture(), mViewHolder.product_picture_2);
                        break;
                    case 2: imageLoader.DisplayImage(productList.get(i).getProductPicture(), mViewHolder.product_picture_3);
                        break;
                    case 3: imageLoader.DisplayImage(productList.get(i).getProductPicture(), mViewHolder.product_picture_4);
                        break;
                    default: break;
                }
            }
        }

        if(collectionList.get(position).getProductCollectionOwner()!=null) {
            mViewHolder.user_name.setText(collectionList.get(position).getProductCollectionOwner().getFullname());
            imageLoader.DisplayImage(collectionList.get(position).getProductCollectionOwner().getProfile_picture(), mViewHolder.profile_picture);
        }

        return convertView;
    }
}
