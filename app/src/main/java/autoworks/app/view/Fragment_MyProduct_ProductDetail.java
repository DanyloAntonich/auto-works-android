package autoworks.app.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import autoworks.app.R;
import autoworks.app.model.Customer;
import autoworks.app.model.Order;
import helpers.JSONParser;
import imageloader.ImageLoader;

/**
 * Created by Tan on 7/5/2014.
 */
public class Fragment_MyProduct_ProductDetail extends BaseAdapter {

    private View myview;
    private Context context;
    private Customer myuser;
    private LayoutInflater inflater;
    public ImageLoader imageLoader;
    Communicator comm;

    public static JSONParser jsonParser;

    public static String deleteproduct_tag = "deleteproduct";

    public static String KEY_PRODUCT_ID = "product_id";

    private static String KEY_SUCCESS = "success";

    private class ViewHolder {
        ImageView product_picture;
//        ImageView profile_picture;
        TextView user_name, product_name, product_description, product_price;
        Button remove_button, edit_button;
    }

    public Fragment_MyProduct_ProductDetail(Context context, Customer user) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.myuser = user;
        this.inflater = LayoutInflater.from(this.context);
        imageLoader=new ImageLoader(context,320);
        comm = (Communicator) context;
        jsonParser = new JSONParser();
    }

    @Override
    public int getCount() {
        if(myuser!=null && myuser.getProductList()!=null)
            return myuser.getProductList().size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_myproduct_productdetail, null);
            mViewHolder = new ViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.product_picture = (ImageView) convertView.findViewById(R.id.product_picture);
//        mViewHolder.profile_picture = (ImageView) convertView.findViewById(R.id.profile_picture);
        mViewHolder.user_name = (TextView) convertView.findViewById(R.id.user_name);
        mViewHolder.product_name = (TextView) convertView.findViewById(R.id.product_name);
        mViewHolder.product_description = (TextView) convertView.findViewById(R.id.product_description);
        mViewHolder.product_price = (TextView) convertView.findViewById(R.id.product_price);
        mViewHolder.remove_button = (Button) convertView.findViewById(R.id.remove_button);
        mViewHolder.edit_button = (Button) convertView.findViewById(R.id.edit_button);

        mViewHolder.user_name.setText(myuser.getFullname());
//        imageLoader.DisplayImage(myuser.getProfile_picture(), mViewHolder.profile_picture);

        mViewHolder.product_name.setText(myuser.getProductList().get(position).getProductName());
        mViewHolder.product_description.setText(myuser.getProductList().get(position).getProductDescription());
        if(myuser.getProductList().get(position).getProductPicture()!=null)
        mViewHolder.product_price.setText(String.valueOf(myuser.getProductList().get(position).getProductPrice()));
        if(myuser.getProductList().get(position).getProductPicture()!=null && myuser.getProductList().get(position).getProductPicture().length()>0)
            imageLoader.DisplayImage(myuser.getProductList().get(position).getProductPicture(), mViewHolder.product_picture);


        mViewHolder.remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int productID = myuser.getProductList().get(position).getProductID();

                new AlertDialog.Builder(context)
                        .setTitle("Confirmation")
                        .setMessage("Do you really want to remove?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                for(int i=0; i< MainActivity.productList.size(); i++)
                                {
                                    if(MainActivity.productList.get(i).getProductID() == productID)
                                    {
                                        new DeleteProductToServer().execute(String.valueOf(productID));
                                        MainActivity.productList.remove(i);

                                        break;

                                    }
                                }
                                notifyDataSetChanged();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        mViewHolder.edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int productID = myuser.getProductList().get(position).getProductID();

                comm.EditProduct(productID);
            }
        });

        return convertView;
    }

    private class DeleteProductToServer extends AsyncTask<String, Void, JSONObject> {
        HttpGet uri = null;
        StatusLine status = null;
        HttpResponse resp = null;
        DefaultHttpClient client = null;
        @Override
        protected JSONObject doInBackground(String... productid) {
            JSONObject json=null;
            ArrayList<Order> result = new ArrayList<Order>();
            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair("tag", deleteproduct_tag));

                params.add(new BasicNameValuePair(KEY_PRODUCT_ID, productid[0]));

                json = jsonParser.getJSONFromUrl(MainActivity.getDataURL, params);
            }
            catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return json;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(JSONObject json) {
            if(json!=null) {
                try {
                    if (json!=null && json.getString(KEY_SUCCESS) != null) {
                        //registerErrorMsg.setText("");
                        String res = json.getString(KEY_SUCCESS);
                        if(Integer.parseInt(res) == 1){

                            comm.SaveProduct();

                        }else{
                            // Error in registration
                            //registerErrorMsg.setText("Error occured in registration");
                            Toast.makeText(context, "Không thể xóa sản phẩm!", Toast.LENGTH_SHORT).show();
                            Log.d("EditProduct", "Can't delete product");
                        }
                    }
                    else
                    {
                        //registerErrorMsg.setText("Can't connect server");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
