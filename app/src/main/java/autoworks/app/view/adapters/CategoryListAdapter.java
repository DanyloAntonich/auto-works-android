package autoworks.app.view.adapters;

import autoworks.app.model.Category;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import autoworks.app.R;
import image_downloader.UrlImageViewCallback;
import image_downloader.UrlRectangleImageViewHelper;

// here's our beautiful adapter
public class CategoryListAdapter extends ArrayAdapter<Category> {

    Context mContext;
    int layoutResourceId;
    ArrayList<Category> data = new ArrayList<Category>();

    public CategoryListAdapter(Context mContext, int layoutResourceId, ArrayList<Category> data) {

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

        Category cat = data.get(position);

        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            //This must be done for performance reasons
            holder = new Holder();
            holder.categoryName = (TextView) convertView.findViewById(R.id.item_tittle);
            holder.categoryDescription = (TextView) convertView.findViewById(R.id.item_subtittle);
            holder.categoryImageView = (ImageView) convertView.findViewById(R.id.category_item_image);
            holder.categorytImage = null;

            // object item based on the position
            setContentView(holder,convertView, cat, position);

            convertView.setTag(holder);

        } else {
            holder = (Holder) convertView.getTag();
            setContentView(holder,convertView, cat, position);
        }


        return convertView;

    }

    private void setContentView(Holder holder, View convertView, Category cat, int position) {
        //get the TextView and then set the text (item name) and tag (item ID) values
        holder.categoryName.setText(cat.getmCategoryName());
        holder.categoryDescription.setText(cat.getmCategoryDescription().substring(0, ((cat.getmCategoryDescription().length() > 200) ? 200 : cat.getmCategoryDescription().length())));

//        new ImageDownloader(holder.categoryImageView).execute(cat.getmCategoryImageURL());
        if (!getItem(position).getmCategoryImageURL().equals("")) {
            UrlRectangleImageViewHelper.setUrlDrawable(holder.categoryImageView, getItem(position).getmCategoryImageURL(), R.drawable.no_product_image, new UrlImageViewCallback() {
                @Override
                public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                    if (!loadedFromCache) {
                        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
                        scale.setDuration(50);
                        scale.setInterpolator(new OvershootInterpolator());
                        imageView.startAnimation(scale);
                    }
                }
            });
        }
    }

    /** View holder for the views we need access to */
    private static class Holder {
        public TextView categoryName;
        public TextView categoryDescription;
        public ImageView categoryImageView;
        public Bitmap categorytImage;
    }

}

