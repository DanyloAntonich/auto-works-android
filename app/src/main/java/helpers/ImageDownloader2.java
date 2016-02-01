package helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by INNOVATION on 3/13/2015.
 */
public class ImageDownloader2 {

    public static void getImageBitmap(final String url, final ItemBitmap itemBitmap, final ILoadingEvent event) {
        if (itemBitmap.getLoadedBitmap() == null) {
            if (!itemBitmap.isBitmapLoading()) {
                itemBitmap.setBitmapLoading(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bm = null;
                        try {
                            URL aURL = new URL(url);
                            URLConnection conn = aURL.openConnection();


                            conn.connect();
                            InputStream is = conn.getInputStream();
                            BufferedInputStream bis = new BufferedInputStream(is);
                            bm = BitmapFactory.decodeStream(bis);
                            itemBitmap.setLoadedBitmap(bm);
                            bis.close();
                            is.close();
                            if (event != null)
                                event.onLoaded(bm);
                        } catch (IOException e) {
                            Log.e("TAG", "Error getting bitmap", e);
                        }
                    }
                }).start();
            }
        }

    }

    public static abstract class ItemBitmap
    {
        protected String imageUrl;
        protected Bitmap loadedBitmap;
        private Object objectLock = new Object();
        protected boolean isBitmapLoading;


        public boolean isBitmapLoading() {
            synchronized (objectLock) {
                return isBitmapLoading;
            }
        }

        public void setBitmapLoading(boolean isLoading) {
            synchronized (objectLock) {
                this.isBitmapLoading = isLoading;
            }
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public Bitmap getLoadedBitmap() {
            return loadedBitmap;
        }

        public void setLoadedBitmap(Bitmap loadedBitmap) {
            this.loadedBitmap = loadedBitmap;
        }
    }

    public static interface ILoadingEvent
    {
        void onLoaded(Bitmap bitmap);
    }
}
