package helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import autoworks.app.view.MainActivity;

/**
 * Created by INNOVATION on 3/24/2015.
 */
public class ImageUploader {

    public static void upload(Context context, Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("encoded_image", encodedImage));
        params.add(new BasicNameValuePair("tag", "addProduct"));


        JSONParser jsonParser = new JSONParser();
        JSONObject json = jsonParser.getJSONFromUrl(MainActivity.getDataURL2, params);

        try {
            if (json != null && !json.isNull(MainActivity.KEY_RESULT)) {
                String result = json.getString(MainActivity.KEY_RESULT);
                if (result.equals("SUCCESSFULLY")) {
                    Toast.makeText(context, "image uploaded successfully", Toast.LENGTH_SHORT);
                }
            }
        }catch (Exception e)
        {

        }

    }
}
