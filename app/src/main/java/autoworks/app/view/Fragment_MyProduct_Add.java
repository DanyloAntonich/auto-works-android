package autoworks.app.view;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import autoworks.app.R;
import autoworks.app.model.Product;
import helpers.HttpPOSTOnTaskCompleted;
import helpers.HttpPOSTProcess;
import helpers.JSONParser;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class Fragment_MyProduct_Add extends Fragment implements View.OnClickListener {

    private View myview;
    private Product myProduct;
    private Communicator comm;
    private Button saveButton, dismissButton;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private Uri mImageCaptureUri;
    public static JSONParser jsonParser;
    private Button addProductPicture1, addProductPicture2, addProductPicture3, addProductPicture4;
    //private Bitmap bitmap1, bitmap2, bitmap3, bitmap4;

    private EditText productNameEditText, productDescriptionEditText, productPriceEditText, productQuantityEditText;

    public static String addproduct_tag = "addproduct";

    public static String KEY_CUSTOMER_ID = "customer_id";
    public static String KEY_COLLECTION_ID = "collection_id";
    public static String KEY_QUANTITY = "quantity";
    public static String KEY_STOCKSTATUS_ID = "stock_status_id";
    public static String KEY_PRICE = "price";
    public static String KEY_PRODUCT_DESCRIPTION = "productDescriptionEditText";
    public static String KEY_PRODUCT_STORE = "product_store";
    public static String KEY_DESCRIPTION = "description";
    public static String KEY_NAME = "name";

    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";

    public Fragment_MyProduct_Add() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(9);
    }

    public void SetProduct(Product product) {
        this.myProduct = product;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addProductPicture1 = (Button) myview.findViewById(R.id.addProductPicture1);
        addProductPicture1.setTag(1);
        addProductPicture2 = (Button) myview.findViewById(R.id.addProductPicture2);
        addProductPicture2.setTag(2);
        addProductPicture3 = (Button) myview.findViewById(R.id.addProductPicture3);
        addProductPicture3.setTag(3);
        addProductPicture4 = (Button) myview.findViewById(R.id.addProductPicture4);
        addProductPicture4.setTag(4);

        UploadImageFromDevide(addProductPicture1);
        UploadImageFromDevide(addProductPicture2);
        UploadImageFromDevide(addProductPicture3);
        UploadImageFromDevide(addProductPicture4);

    }

    public void UploadImageFromDevide(final Button product_picture) {
        final String[] items = new String[]{"From Camera", "From SD Card"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

        builder.setTitle("Select Image");

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory(),
                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    mImageCaptureUri = Uri.fromFile(file);

                    try {
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, Integer.parseInt(product_picture.getTag().toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dialog.cancel();
                } else {
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), Integer.parseInt(product_picture.getTag().toString()));
                }
            }
        });

        final AlertDialog dialog = builder.create();
        product_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }

    @Override
    public void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        //String output = data.getStringExtra(android.provider.MediaStore.EXTRA_OUTPUT);
        Bitmap bitmap = null;
        String url ="";
        if (data != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), uri);
                url = uri.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), mImageCaptureUri);
            } catch (IOException e) {

            }
        }
        if (bitmap != null) {
            bitmap = rotateBitmap(bitmap, url);
            //upload(bitmap);
            Drawable d = new BitmapDrawable(getResources(), bitmap);

            switch (request) {
                case 1:
                    addProductPicture1.setBackground(d);
                    addProductPicture1.setWidth(100);
                    addProductPicture1.setHeight(150);
                    break;
                case 2:
                    addProductPicture2.setBackground(d);
                    addProductPicture2.setWidth(100);
                    addProductPicture2.setHeight(150);
                    break;
                case 3:
                    addProductPicture3.setBackground(d);
                    addProductPicture3.setWidth(100);
                    addProductPicture3.setHeight(150);
                    break;
                case 4:
                    addProductPicture4.setBackground(d);
                    addProductPicture4.setWidth(100);
                    addProductPicture4.setHeight(150);
                    break;
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("AutoWorks", "oCreateView fragment_product");
        myview = inflater.inflate(R.layout.fragment__add__product, container, false);

        saveButton = (Button) myview.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        dismissButton = (Button) myview.findViewById(R.id.dismissButton);
        dismissButton.setOnClickListener(this);
        productNameEditText = (EditText) myview.findViewById(R.id.productNameEditText);
        productDescriptionEditText = (EditText) myview.findViewById(R.id.productDescriptionEditText);
        productPriceEditText = (EditText) myview.findViewById(R.id.productPriceEditText);
        productQuantityEditText = (EditText) myview.findViewById(R.id.productQuantityEditText);

        return myview;
    }


    private Bitmap rotateBitmap(Bitmap bitmap, String url) {

        Bitmap result = null;
        try {
            ExifInterface exif = new ExifInterface(url);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            }
            else if (orientation == 3) {
                matrix.postRotate(180);
            }
            else if (orientation == 8) {
                matrix.postRotate(270);
            }
            result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
        }
        catch (Exception e) {

        }
        return result;
    }

    public void upload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        System.out.println("encodedImage " + encodedImage);

        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("encoded_image", encodedImage));

        JSONParser jsonParser = new JSONParser();
//        JSONObject json = jsonParser.getJSONFromUrl(MainActivity.getDataURL2, params);

        HttpPOSTProcess uploadAsync = new HttpPOSTProcess(MainActivity.getDataURL2, "addProduct", params, new HttpPOSTOnTaskCompleted() {
            @Override
            public void onHttpPOSTOnTaskCompleted(JSONObject json) {
                try {
                    if (json != null && !json.isNull(MainActivity.KEY_RESULT)) {
                        String result = json.getString(MainActivity.KEY_RESULT);
                        if (result.equals("SUCCESSFULLY")) {
                            Toast.makeText(getActivity(), "image uploaded successfully", Toast.LENGTH_SHORT).show();
                        }else
                        {
                            Toast.makeText(getActivity(), "image upload error!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {

                }
            }
        });


    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveButton:
                Bitmap bitmap1 =
                        drawableToBitmap(addProductPicture1.getBackground());
                upload(bitmap1);
                //addProductPicture1.getBackground()
                break;

        }
    }


}
