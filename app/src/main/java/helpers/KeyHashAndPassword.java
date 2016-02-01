package helpers;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import autoworks.app.R;

/**
 * Created by INNOVATION on 2/11/2015.
 */

//help to get key hash of android environment
public class KeyHashAndPassword {
    public static String GetKeyHash(Context context)
    {
        String keyHash= "";
        try {

            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "autoworks.app",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                keyHash= Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        return keyHash;
    }

    //Use when register with social account
    public static String encodeUsername(Context context, String value)
    {
        String key = context.getString(R.string.hash_secret_salt);
        String result = base64Encode(xorWithKey(value.getBytes(), key.getBytes()));

      //  String original =  decode(result, key);
        return result;
    }

    public static String decode(String s, String key) {
        return new String(xorWithKey(base64Decode(s), key.getBytes()));
    }

    private static byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ key[i%key.length]);
        }
        return out;
    }

    private static String base64Encode(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT).toString().replaceAll("\\s", "");//remove white spaces

    }

    private static byte[] base64Decode(String s) {
        try {
            return Base64.decode(s, Base64.DEFAULT);
        } catch (Exception e)
        {
        }
        return null;
    }
   /* private void test()
    {

    }

    private static byte[] hashWithSalt(String input, String salt)
    {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest((input + salt).getBytes("UTF-8"));
        } catch (Exception e)
        {
            return null;
        }
    }
    private static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }*/
}
