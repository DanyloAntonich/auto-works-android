package autoworks.app.view.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import autoworks.app.view.Fragment_Product_Detail;
import autoworks.app.view.Fragment_Shopping_Cart;
import autoworks.app.view.Fragment_Wish_List;
import autoworks.app.R;
import autoworks.app.model.CustomProduct;
import helpers.CartCustomProductSaver;
import helpers.ImageDownloader2;
import helpers.WishCustomProductSaver;

/**
 * An array adapter that knows how to render views when given CustomData classes
 */
public class CartWishListCustomProductAdapter extends ArrayAdapter<CustomProduct> {
    private LayoutInflater mInflater;
    private Fragment fragment;
    private int layout;
    // private ArrayList<Holder> listHolders = new ArrayList<>();
    private NumberFormat formatter = new DecimalFormat("#0");
    private Toast toast;
    public CartWishListCustomProductAdapter(Fragment fragment, int layout, ArrayList<CustomProduct> values) {
        super(fragment.getActivity(), layout, values);
        this.layout = layout;
        this.fragment = fragment;
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      /*  View view = mInflater.inflate(R.layout.shopping_product_item, null, false);
        EditText item1_quantity = (EditText) view.findViewById(R.id.item1_quantity);
        item1_quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

    }
    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final Holder holder;
        //this.getItem()
        if (convertView == null) {
            // Inflate the view since it does not exist
            convertView = mInflater.inflate(layout, null);
        }

        if (convertView.getTag() == null) {

            holder = new Holder();
            holder.item1_tittle = (TextView) convertView.findViewById(R.id.item1_tittle);
            holder.item1_oldprice = (TextView) convertView.findViewById(R.id.item1_oldprice);
            holder.item1_newprice = (TextView) convertView.findViewById(R.id.item1_price);
            holder.item1_alert = (TextView) convertView.findViewById(R.id.item1_alert);
            holder.quantityRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.quantityRelativeLayout);
            holder.cart_item1_image = (ImageView) convertView.findViewById(R.id.cart_item1_image);
            holder.item1_info = (ImageView) convertView.findViewById(R.id.item1_info);
            holder.buy_now = (RelativeLayout) convertView.findViewById(R.id.buy_now);
            holder.increase_quantity = (ImageButton)convertView.findViewById(R.id.increase_quantity);
            holder.decrease_quantity = (ImageButton)convertView.findViewById(R.id.decrease_quantity);
            if (convertView.findViewById(R.id.item1_quantity) instanceof EditText)
                holder.item1_quantity_edit = (EditText) convertView.findViewById(R.id.item1_quantity);
            else
                holder.item1_quantity = (TextView) convertView.findViewById(R.id.item1_quantity);
            holder.item1_totalprice = (TextView) convertView.findViewById(R.id.item1_amount);
            holder.item1_delete = (ImageView) convertView.findViewById(R.id.item1_delete);
            convertView.setTag(holder);

            if(getItem(holder.position).getQuantity() == 1) {
                holder.item1_alert.setText(getContext().getString(R.string.item_last));
            }
            else if(getItem(holder.position).getQuantity() > 1 && getItem(holder.position).getQuantity() <= 5) {
                holder.item1_alert.setText(getContext().getString(R.string.item_almost_gone));
            }

            if (holder.item1_delete!=null) {
                holder.item1_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AlertDialog.Builder(fragment.getActivity())
                                .setTitle("Delete Product")
                                .setMessage("Do you really want to delete this product?, id =  " + getItem(holder.position).getId())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Toast.makeText(fragment.getActivity(), "deleted product " + getItem(holder.position).getId(), Toast.LENGTH_SHORT).show();
                                        if (fragment instanceof Fragment_Shopping_Cart)
                                            CartCustomProductSaver.getInstance(getContext()).removeCustomProduct(getItem(holder.position).getId());
                                        else if (fragment instanceof Fragment_Wish_List)
                                            WishCustomProductSaver.getInstance(getContext()).removeCustomProduct(getItem(holder.position).getId());


                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();


                    }
                });
            }
            if (holder.item1_info!=null) {
                holder.item1_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Fragment_Product_Detail fragment_product_detail = new Fragment_Product_Detail(getItem(holder.position));
                        fragment.getFragmentManager().beginTransaction()
                                .replace(R.id.container, fragment_product_detail)
                                .addToBackStack("addToBackStack fragment_product_detail")
                                .commit();
                    }
                });
            }
            if ( holder.cart_item1_image!=null) {
                holder.cart_item1_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment_Product_Detail fragment_product_detail = new Fragment_Product_Detail(getItem(holder.position));
                        fragment.getFragmentManager().beginTransaction()
                                .replace(R.id.container, fragment_product_detail)
                                .addToBackStack("addToBackStack fragment_product_detail")
                                .commit();
                    }
                });
            }


          /*  if (holder.quantityRelativeLayout!=null) {
                holder.quantityRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        holder.item1_quantity_edit.requestFocusFromTouch();
                        holder.item1_quantity_edit.requestFocus();
                        holder.item1_quantity_edit.selectAll();
                        InputMethodManager imm = (InputMethodManager) fragment.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(holder.item1_quantity_edit, InputMethodManager.SHOW_IMPLICIT);
                        return false;
                    }
                });
            }*/

            if (holder.item1_quantity_edit!=null) {
                holder.item1_quantity_edit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int newQuantity = 1;

                        try {
                            newQuantity = Integer.parseInt(s.toString());

                        } catch (Exception e) {
                            System.out.println(position);
                        }
                        //int holderPosition = (Integer) holder.item1_quantity.getTag();//use this to get the right position
                        if (fragment instanceof Fragment_Shopping_Cart) {
                            CartCustomProductSaver.getInstance(getContext()).changeQuantity(getItem(holder.position).getId(), newQuantity);
                        } else {
                            WishCustomProductSaver.getInstance(getContext()).changeQuantity(getItem(holder.position).getId(), newQuantity);
                        }
                        //getItem(position).setQuantity(newQuantity);

                        getItem(holder.position).setQuantity(newQuantity);
                        // System.out.println("holder-tag = " +holder.item1_quantity.getTag() +", position " + position);

                        //change total price
                        try {
                            double totalPrice = Double.parseDouble(getItem(holder.position).getProductPrice()) * getItem(holder.position).getQuantity();
                            getItem(holder.position).setTotalPrice(totalPrice);
                            holder.item1_totalprice.setText("$"+formatter.format(totalPrice));//???

                        } catch (Exception e) {
                        }
                        if (CartCustomProductSaver.getInstance(getContext()).getEventHandler() != null)
                            CartCustomProductSaver.getInstance(getContext()).getEventHandler().onTotalPriceChanged();
                        if (WishCustomProductSaver.getInstance(getContext()).getEventHandler() != null)
                            WishCustomProductSaver.getInstance(getContext()).getEventHandler().onTotalPriceChanged();

                    }
                });
            }

            if (holder.buy_now!=null)
            {
                holder.buy_now.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quantity =CartCustomProductSaver.getInstance(getContext()).addCustomProduct(true,getItem(position));
                        if (toast!=null)
                            toast.cancel();
                        toast = Toast.makeText(getContext(), "Product id " + getItem(position).getId() +" added to cart ("+quantity+")", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
            if (holder.increase_quantity!=null && holder.decrease_quantity!=null)
            {
                holder.increase_quantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int newQuantity = getItem(holder.position).getQuantity() + 1;
                        holder.item1_quantity_edit.setText(newQuantity +"");

                        //int holderPosition = (Integer) holder.item1_quantity.getTag();//use this to get the right position
                        if (fragment instanceof Fragment_Shopping_Cart) {
                            CartCustomProductSaver.getInstance(getContext()).changeQuantity(getItem(holder.position).getId(), newQuantity);
                        } else {
                            WishCustomProductSaver.getInstance(getContext()).changeQuantity(getItem(holder.position).getId(), newQuantity);
                        }

                        getItem(holder.position).setQuantity(newQuantity);
                        // System.out.println("holder-tag = " +holder.item1_quantity.getTag() +", position " + position);

                        //change total price
                        try {
                            getItem(holder.position).setTotalPrice(Double.parseDouble(getItem(holder.position).getProductPrice()) * getItem(holder.position).getQuantity());
                            holder.item1_totalprice.setText(getItem(holder.position).getTotalPrice() + "");//???

                        } catch (Exception e) {
                        }
                        if (CartCustomProductSaver.getInstance(getContext()).getEventHandler() != null)
                            CartCustomProductSaver.getInstance(getContext()).getEventHandler().onTotalPriceChanged();
                        if (WishCustomProductSaver.getInstance(getContext()).getEventHandler() != null)
                            WishCustomProductSaver.getInstance(getContext()).getEventHandler().onTotalPriceChanged();
                    }
                });
                holder.decrease_quantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int newQuantity = getItem(holder.position).getQuantity() - 1;
                        if (newQuantity < 1)
                            newQuantity = 1;
                        holder.item1_quantity_edit.setText(newQuantity +"");
                        //int holderPosition = (Integer) holder.item1_quantity.getTag();//use this to get the right position
                        if (fragment instanceof Fragment_Shopping_Cart) {
                            CartCustomProductSaver.getInstance(getContext()).changeQuantity(getItem(holder.position).getId(), newQuantity);
                        } else {
                            WishCustomProductSaver.getInstance(getContext()).changeQuantity(getItem(holder.position).getId(), newQuantity);
                        }

                        getItem(holder.position).setQuantity(newQuantity);
                        // System.out.println("holder-tag = " +holder.item1_quantity.getTag() +", position " + position);

                        //change total price
                        try {
                            getItem(holder.position).setTotalPrice(Double.parseDouble(getItem(holder.position).getProductPrice()) * getItem(holder.position).getQuantity());
                            holder.item1_totalprice.setText(getItem(holder.position).getTotalPrice() + "");//???

                        } catch (Exception e) {
                        }
                        if (CartCustomProductSaver.getInstance(getContext()).getEventHandler() != null)
                            CartCustomProductSaver.getInstance(getContext()).getEventHandler().onTotalPriceChanged();
                        if (WishCustomProductSaver.getInstance(getContext()).getEventHandler() != null)
                            WishCustomProductSaver.getInstance(getContext()).getEventHandler().onTotalPriceChanged();
                    }
                });
            }

        } else {
            holder = (Holder) convertView.getTag();
        }
        if (getItem(position).getImageUrl()!=null) {
            ImageDownloader2.getImageBitmap(getItem(position).getImageUrl(), getItem(position), new ImageDownloader2.ILoadingEvent() {
                @Override
                public void onLoaded(Bitmap bm) {
                    fragment.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            notifyDataSetChanged();
                        }
                    });

                }
            });
        }

        holder.position = position;
        if (holder.item1_tittle!=null)
            holder.item1_tittle.setText(getItem(position).getProductName());

        if (holder.item1_oldprice!=null && holder.item1_newprice!=null) {
           try
           {
               Double specialPrice = Double.parseDouble(getItem(position).getProductSpecialPrice());
               Double oldPrice = Double.parseDouble(getItem(position).getProductPrice());
               holder.item1_oldprice.setVisibility(View.VISIBLE);
               holder.item1_oldprice.setText("$" + formatter.format(oldPrice));
               holder.item1_newprice.setText("$" + formatter.format(specialPrice));

               RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.item1_newprice.getLayoutParams();
               params.addRule(RelativeLayout.RIGHT_OF, R.id.item1_oldprice);
               holder.item1_newprice.setLayoutParams(params);

           }catch (Exception e)
           {
               try {
                   Double oldPrice = Double.parseDouble(getItem(position).getProductPrice());
                   holder.item1_oldprice.setVisibility(View.GONE);

                   RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.item1_newprice.getLayoutParams();
                   params.addRule(RelativeLayout.RIGHT_OF, R.id.cart_item1_image);
                   holder.item1_newprice.setLayoutParams(params);
                   holder.item1_newprice.setText("$" + formatter.format(oldPrice));

               }catch (Exception e1)
               {}
           }
          /*  if (TextUtils.isEmpty(getItem(position).getProductSpecialPrice())) {
                holder.item1_oldprice.setVisibility(View.GONE);
                holder.item1_newprice.setText("$" + getItem(position).getProductPrice());
            } else {
                holder.item1_oldprice.setVisibility(View.VISIBLE);
                holder.item1_oldprice.setText("$" + getItem(position).getProductPrice());
                holder.item1_newprice.setText("$" + getItem(position).getProductSpecialPrice());
            }*/

        }
        if (holder.cart_item1_image!=null) {
            if (getItem(position).getLoadedBitmap() == null)
                holder.cart_item1_image.setImageResource(R.drawable.no_product_image);
            else
                holder.cart_item1_image.setImageBitmap(getItem(position).getLoadedBitmap());
        }
       // holder.item1_quantity.setTag(position);
       // holder.item1_delete.setTag(position);
        if (holder.item1_quantity!=null)
            holder.item1_quantity.setText(getItem(position).getQuantity() + "");
        if (holder.item1_quantity_edit!=null)
            holder.item1_quantity_edit.setText(getItem(position).getQuantity() + "");


        try {
            getItem(position).setTotalPrice(Double.parseDouble(getItem(position).getProductPrice()) * getItem(position).getQuantity());
        } catch (Exception e) {

        }
        if (holder.item1_totalprice!=null)
            holder.item1_totalprice.setText("$"+getItem(position).getTotalPrice());

        //Log.d("TestEditText ", "item1_quantity.setText " + getItem(position).getQuantity());
        // Log.d("TestEditText ", "item1_totalprice.getText " + item1_totalprice.getText());

        //if (!getItem(position).isLoaded()) {


        //convertView.setBackgroundColor(getItem(position).getBackgroundColor());

        return convertView;
    }

    private class Holder {
        TextView item1_tittle;
        TextView item1_oldprice;
        TextView item1_alert;
        TextView item1_newprice;
        RelativeLayout quantityRelativeLayout;
        ImageView cart_item1_image;

        ImageView item1_info;
        TextView item1_quantity;
        EditText item1_quantity_edit;
        TextView item1_totalprice;
        ImageView item1_delete;

        RelativeLayout buy_now;
        ImageButton increase_quantity;
        ImageButton decrease_quantity;
        int position;
    }

}
