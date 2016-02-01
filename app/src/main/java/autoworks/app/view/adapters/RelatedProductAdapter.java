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
import android.widget.TextView;

import java.util.ArrayList;

import autoworks.app.R;
import autoworks.app.model.CustomProduct;
import image_downloader.UrlImageViewCallback;
import image_downloader.UrlRectangleImageViewHelper;

/** An array adapter that knows how to render views when given CustomData classes */
public class RelatedProductAdapter extends ArrayAdapter<CustomProduct> {
    private LayoutInflater mInflater;

    public RelatedProductAdapter(Context context, ArrayList<CustomProduct> values) {
        super(context, R.layout.horizontal_product_item, values);
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            // Inflate the view since it does not exist
            convertView = mInflater.inflate(R.layout.horizontal_product_item_relative_related, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            //This must be done for performance reasons
            holder = new Holder();
            holder.productName = (TextView) convertView.findViewById(R.id.product_name);
            holder.productPrice = (TextView) convertView.findViewById(R.id.product_price);
            holder.productPriceSpecial = (TextView) convertView.findViewById(R.id.product_price_special);
            holder.salePercentage = (TextView) convertView.findViewById(R.id.salePercentage);
            holder.productImage = (ImageView) convertView.findViewById(R.id.product_image);
            holder.productSuperSale = (ImageView) convertView.findViewById(R.id.product_super_sale);
            holder.productSale = (ImageView) convertView.findViewById(R.id.product_sale);

            // Set the content
            holder.productName.setText(getItem(position).getProductName());

//            new ImageDownloader(holder.productImage).execute(getItem(position).getProductImage());
            if (!getItem(position).getProductImage().equals("")) {
                UrlRectangleImageViewHelper.setUrlDrawable(holder.productImage, getItem(position).getProductImage(), R.drawable.no_product_image, new UrlImageViewCallback() {
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
            //System.out.println(getItem(position).getProductImage());

            String special = getItem(position).getProductSpecialPrice();
            if(!special.isEmpty() && special != "null" && special != "0.0") {
                Long percentage = Math.round((Double.parseDouble(getItem(position).getProductPrice()) - Double.parseDouble(getItem(position).getProductSpecialPrice())) / Double.parseDouble(getItem(position).getProductPrice()) * 100);
                holder.productPrice.setText(getItem(position).getProductPrice());
                holder.productPriceSpecial.setText(getItem(position).getProductSpecialPrice());
                holder.salePercentage.setText("SALE " + percentage + "%");

                //if percentage > 50 => super sale
                if(percentage > 50) {
//                img.setBackgroundResource(R.drawable.product_super_sale);
//                rl.addView(img, params);
                    holder.productSale.setVisibility(View.INVISIBLE);
                    holder.productSuperSale.setVisibility(View.VISIBLE);
                }
                else {
//                img.setBackgroundResource(R.drawable.product_sale);
//                rl.addView(img, params);
                    holder.productSale.setVisibility(View.VISIBLE);
                    holder.productSuperSale.setVisibility(View.INVISIBLE);
                }

            }
            else {
                holder.productPriceSpecial.setText(getItem(position).getProductPrice());
                holder.productPrice.setVisibility(View.INVISIBLE);
                holder.salePercentage.setVisibility(View.INVISIBLE);
                holder.productSuperSale.setVisibility(View.INVISIBLE);
                holder.productSale.setVisibility(View.INVISIBLE);
            }

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        //convertView.setBackgroundColor(getItem(position).getBackgroundColor());

        return convertView;
    }

    /** View holder for the views we need access to */
    private static class Holder {
        public TextView productName;
        public TextView productPrice;
        public TextView productPriceSpecial;
        public TextView salePercentage;
        public ImageView productImage;
        public ImageView productSuperSale;
        public ImageView productSale;
    }
}
