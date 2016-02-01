package autoworks.app.Utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import autoworks.app.controller.Constant;

/**
 * Created by Administrator on 1/4/2016.
 */
public class MediaUtility {
    public static boolean checkFileExist(String fileName, String PATH){

        File file = new File(PATH + fileName);
        if(file.exists()){
            return true;
        }else
            return false;
    }
    public static void setImageViewSize(ImageView imageview, int screenWidth){

        imageview.setMaxWidth(screenWidth);
        imageview.setMaxHeight(screenWidth);

    }

    public static void setRelativeLayoutSize(RelativeLayout relativeLayout, int screenWidth){
        ViewGroup.LayoutParams layoutParams = null;
        try{
            layoutParams =  relativeLayout.getLayoutParams();
        }catch (Exception e){
            e.printStackTrace();
        }
        layoutParams.width = screenWidth ;
        layoutParams.height = screenWidth;


        relativeLayout.setLayoutParams(layoutParams);


    }
    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }
    //get bitmap from path
    public static Bitmap getBitmap(String path, int bounds) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        //////
//        options.inSampleSize = 7;
        options.inSampleSize = bounds;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }
    //////rotate bitmap accoring to it's orientation
    public static Bitmap adjustBitmap(String photopath) {
        Uri uri = Uri.parse(photopath);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(uri.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //get current rotation
        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        ///Convert exif rotation to degrees:
        int rotationInDegrees = exifToDegrees(rotation);
        ///Then use the image's actual rotation as a reference point to rotate the image using a Matrix
        Matrix matrix = new Matrix();
        if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}

        //get BitmapFactory option
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        //get bitmap with local path
        Bitmap yourSelectedImage = BitmapFactory.decodeFile(photopath, bmOptions);
        ////create a new rotated image
        Bitmap adjustedBitmap = Bitmap.createBitmap(yourSelectedImage, 0, 0, yourSelectedImage.getWidth(), yourSelectedImage.getHeight(), matrix, true);
        return adjustedBitmap;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }
//    convert bitmap to byte[] from Uri============================================start
    public static byte[] convertImageToBytes(Context context, Uri uri){
        byte[] data = null;
        try {
            ContentResolver cr = context.getContentResolver();
            InputStream inputStream = cr.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            data = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }
//    convert bitmap to byte[] from Uri============================================end
//    convert video to byte[] from Uri============================================start
    public static byte[] convertVideoToBytes(Context context, Uri uri){
        byte[] videoBytes = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            FileInputStream fis = new FileInputStream(new File(getRealPathFromURI(context, uri)));
            FileInputStream fis = new FileInputStream(new File(String.valueOf(uri)));
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);

            videoBytes = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return videoBytes;
    }
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Video.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    //    convert video to byte[] from Uri============================================end

    public final static boolean isValidCharacter (String txtInput){
        Pattern pattern;
        Matcher matcher;

        final String USERNAME_PATTERN = "^[a-z0-9A-Z]{2,25}$";
        pattern = Pattern.compile(USERNAME_PATTERN);

        matcher = pattern.matcher(txtInput);
        return matcher.matches();
    }

    public static String saveProgressimageToSDCARD(Bitmap bitmap, String fileName){
        File sdCardDirectory = new File(Environment.getExternalStorageDirectory().toString() + Constant.INDECATOR);
        if (!sdCardDirectory.exists()) {
            sdCardDirectory.mkdirs();
        }
        String sdCardDirectoryPath = sdCardDirectory.getPath() ;
        File image = new File(sdCardDirectoryPath, fileName);
        if (image.exists()) {
            image.delete();
        }

        boolean success = false;

        // Encode the file as a PNG image.
//        FileOutputStream outStream;
        try {
//            bitmap = rotateImage(bitmap, 90);
            FileOutputStream outStream = new FileOutputStream(image);
            if (bitmap.getHeight() < 2400) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            } else if (bitmap.getHeight() > 2400 && bitmap.getHeight() < 3500) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            } else if (bitmap.getHeight() > 3500) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            }


            outStream.flush();
            outStream.close();
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        if (success) {
            return sdCardDirectoryPath + fileName;
        } else {
            return "";
        }
    }

    public static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
//        img.recycle();
        return rotatedImg;
    }

    public static boolean saveProgressFromToGallery(Bitmap bitmap, String fileName){

        File sdCardDirectory = Environment.getExternalStorageDirectory();
        String sdCardDirectoryPath = sdCardDirectory.getPath() + Constant.INDECATOR;
        File image = new File(sdCardDirectoryPath, fileName);
        boolean success = false;

        // Encode the file as a PNG image.
        FileOutputStream outStream;
        try {
            if (bitmap.getHeight() >= bitmap.getWidth()){

            }else {
//               bitmap = rotateImage(bitmap, 90);
            }
            outStream = new FileOutputStream(image);
            if (bitmap.getHeight() < 2400) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            } else if (bitmap.getHeight() > 2400 && bitmap.getHeight() < 3500) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            } else if (bitmap.getHeight() > 3500) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            }


            outStream.flush();
            outStream.close();
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (success) {
            return true;
        } else {
            return false;
        }
    }

    public static void garbageCollect() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    //Given the bitmap size and View size calculate a subsampling size (powers of 2)
    public static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int inSampleSize = 1;	//Default subsampling size
        // See if image raw height and width is bigger than that of required view
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            //bigger
            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }
}
