package autoworks.app.Utilities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import autoworks.app.R;

/**
 * Created by Administrator on 1/30/2016.
 */
public class LocationUtility {
    public static String countryName = "";
    public static String countryCode = "";
    public static String phoneCode = "";
    public static String cityName = "";
    //// get address from location
    public static boolean getAddressFromCoordination(Context mContext) {
        String searchKey = "";
        GPSTracker gpsTracker = new GPSTracker(mContext);
        Geocoder geocoder = new Geocoder( mContext, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(), 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                countryName = address.getCountryName();
                cityName = address.getAdminArea();
                countryCode = address.getCountryCode();
                phoneCode = getPhoneCode(mContext, countryCode);
                return true;
            }
        } catch (IOException e) {
//                    Log.e(TAG, "Unable connect to Geocoder", e);
            e.printStackTrace();
        }
        return false;
    }
    public static String getPhoneCode(Context context, String country_code) {
        String pc = "";
        String[] strCountrys = context.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < strCountrys.length; i ++ ) {
            if (strCountrys[i].contains(country_code)) {
                pc = strCountrys[i].substring(0, strCountrys[i].lastIndexOf(","));
                break;
            }
        }
        return pc;
    }

}
