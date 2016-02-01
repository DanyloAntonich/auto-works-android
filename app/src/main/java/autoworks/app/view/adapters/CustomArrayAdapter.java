package autoworks.app.view.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import autoworks.app.R;
import autoworks.app.model.CustomData;
import image_downloader.UrlImageViewCallback;
import image_downloader.UrlRectangleImageViewHelper;

/** An array adapter that knows how to render views when given CustomData classes */
public class CustomArrayAdapter extends ArrayAdapter<CustomData> {
    private LayoutInflater mInflater;

    public CustomArrayAdapter(Context context, ArrayList<CustomData> values) {
        super(context, R.layout.horizontal_slideshow_item, values);
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            // Inflate the view since it does not exist
            convertView = mInflater.inflate(R.layout.horizontal_slideshow_item, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            // This must be done for performance reasons
            holder = new Holder();
            holder.horizontal_list_item = (ImageView) convertView.findViewById(R.id.horizontal_list_item);
            // Populate the text
            String uri = "";

            // int imageResource = R.drawable.icon;
//        int imageResource = convertView.getResources().getIdentifier(uri + getItem(position).getImgURL() , "drawable", MainActivity.class.getPackage().getName());
//
//        Drawable image = convertView.getResources().getDrawable(imageResource);
//        holder.horizontal_list_item.setImageDrawable(image);
            //holder.horizontal_list_item.setBackgroundResource(R.drawable.no_image_available);

//            new ImageDownloader(holder.horizontal_list_item).execute(getItem(position).getImgURL());

            if (!getItem(position).getImgURL().equals("")) {
                UrlRectangleImageViewHelper.setUrlDrawable(holder.horizontal_list_item, getItem(position).getImgURL(), R.drawable.no_image_available, new UrlImageViewCallback() {
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
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }



        // Set the color
        //convertView.setBackgroundColor(getItem(position).getBackgroundColor());

        return convertView;
    }

    /** View holder for the views we need access to */
    private static class Holder {
        public ImageView horizontal_list_item;
    }
}
