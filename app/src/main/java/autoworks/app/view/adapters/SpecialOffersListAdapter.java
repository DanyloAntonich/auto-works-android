package autoworks.app.view.adapters;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import autoworks.app.R;
import autoworks.app.model.CustomProduct;
import helpers.CartCustomProductSaver;
import helpers.WishCustomProductSaver;
import image_downloader.UrlImageViewCallback;
import image_downloader.UrlRectangleImageViewHelper;

// here's our beautiful adapter
public class SpecialOffersListAdapter extends ArrayAdapter<CustomProduct> {

    Context mContext;
    int layoutResourceId;
    ArrayList<CustomProduct> data = new ArrayList<CustomProduct>();
    private Toast toast;

    public SpecialOffersListAdapter(Context mContext, int layoutResourceId, ArrayList<CustomProduct> data) {
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
        final CustomProduct pro = data.get(position);

        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new Holder();
            holder.productName = (TextView) convertView.findViewById(R.id.product_tittle);
            holder.productImage = (ImageView) convertView.findViewById(R.id.product_image);
            holder.productPrice = (TextView) convertView.findViewById(R.id.product_price);
            holder.productPriceSpecial = (TextView) convertView.findViewById(R.id.product_price_special);
            holder.productShortDescription = (TextView) convertView.findViewById(R.id.product_short_description);

            holder.addToCart = (RelativeLayout) convertView.findViewById(R.id.product_add_to_cart);
            holder.addToWishList = (RelativeLayout) convertView.findViewById(R.id.product_add_to_wish_list);


//            holder.salePercentage = (TextView) convertView.findViewById(R.id.product_sale_percentage);
//            holder.productSale = (ImageView) convertView.findViewById(R.id.product_sale);
//            holder.productSuperSale = (ImageView) convertView.findViewById(R.id.product_super_sale);

            setContentView(holder, convertView, pro, position);

            convertView.setTag(holder);
        }
        else {
            holder = (Holder) convertView.getTag();

            setContentView(holder, convertView, pro, position);

        }

        return convertView;

    }

    private void setContentView(Holder holder, View convertView, final CustomProduct pro, int position) {
        //get the TextView and then set the text (item name) and tag (item ID) values
        holder.productName.setText(pro.getProductName());
        holder.productShortDescription.setText(pro.getProductShortDescription());

        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = CartCustomProductSaver.getInstance(mContext).addCustomProduct(true, pro);
                if (toast!=null)
                    toast.cancel();
                toast = Toast.makeText(mContext, "Product id " + String.valueOf(pro.getId()) + " added to cart (" + String.valueOf(quantity) + ")", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        holder.addToWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = WishCustomProductSaver.getInstance(mContext).addCustomProduct(false, pro);
                if (toast!=null)
                    toast.cancel();
                toast = Toast.makeText(mContext, "Product id " + String.valueOf(pro.getId()) +" added to wish list ("+String.valueOf(quantity) +")", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        if(!pro.getProductImage().isEmpty() && holder.productImage .getBackground() == null) {
//            new ImageDownloader(holder.productImage ).execute(pro.getProductImage());
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
        }

        String special = pro.getProductSpecialPrice();
        if(!special.isEmpty() && special != "null"  && special != "0.0" ) {
            //Long percentage = Math.round((Double.parseDouble(pro.getProductPrice()) - Double.parseDouble(pro.getProductSpecialPrice())) / Double.parseDouble(getItem(position).getProductPrice()) * 100);

            holder.productPrice.setText(pro.getProductPrice());
            holder.productPriceSpecial.setText(pro.getProductSpecialPrice());
//            holder.salePercentage.setText("SALE " + percentage + "%");

            //set sales tags
            //if percentage > 50 => super sale

//            if(percentage > 50) {
//                holder.productSale.setVisibility(View.INVISIBLE);
//                holder.productSuperSale.setVisibility(View.VISIBLE);
//            }
//            else {
//                holder.productSale.setVisibility(View.VISIBLE);
//                holder.productSuperSale.setVisibility(View.INVISIBLE);
//            }

        }
        else {
            holder.productPriceSpecial.setText(getItem(position).getProductPrice());
            holder.productPrice.setVisibility(View.INVISIBLE);
//            holder.salePercentage.setVisibility(View.INVISIBLE);
//            holder.productSuperSale.setVisibility(View.INVISIBLE);
//            holder.productSale.setVisibility(View.INVISIBLE);
        }
    }

    /** View holder for the views we need access to */
    private static class Holder {
        public TextView productName;
        public TextView productPrice;
        public TextView productPriceSpecial;
        public TextView productShortDescription;
        public TextView salePercentage;
        public ImageView productImage;
        public ImageView productSuperSale;
        public ImageView productSale;

        public RelativeLayout addToCart;
        public RelativeLayout addToWishList;
    }
}