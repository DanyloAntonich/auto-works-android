package helpers;

import android.content.Context;
import android.util.Log;

import org.simpleframework.xml.Root;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by INNOVATION on 3/10/2015.
 */
@Root
public class WishCustomProductSaver extends  CartCustomProductSaver{

    private static WishCustomProductSaver customProductSaver;
    public WishCustomProductSaver()
    {
        this(null);
    }
    public WishCustomProductSaver(Context context)
    {
        super(context);
        fileName = "WishListSave3.xml";
    }


    @Override
    public void dataToXml() {
        // Log.d(PostsActivity.TAG, "saving postDataSource");

        // OutputStream outputStream = new FileOutputStream(fileName)
        try {
            OutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            persister.write(this, outputStream);
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void xmlToData(Context context) {
        //  Log.d(PostsActivity.TAG, "loading postDataSource");
        try {
            InputStream inputStream = context.openFileInput(fileName);
            customProductSaver = persister.read(WishCustomProductSaver.class, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (customProductSaver == null)
            customProductSaver = new WishCustomProductSaver(context);

        customProductSaver.context = context;
    }


    public static WishCustomProductSaver getInstance(Context context)
    {
        if (customProductSaver == null)
        {
            try {
                customProductSaver = new WishCustomProductSaver(context);
                customProductSaver.xmlToData(context);
            }
            catch (Exception ex) {
                Log.e("error", ex.getStackTrace().toString());
            }
        }
        return customProductSaver;
    }


}
