package autoworks.app.model;

import android.content.Context;
import android.location.Geocoder;
import android.util.Log;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

/**
 * Created by INNOVATION on 2/12/2015.
 */
public class CountryCode {

    public static void test(Context context)
    {
        Geocoder geoCoder = new Geocoder(context, Locale.getDefault());



        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
       // phoneUtil.isValidNumberForRegion()

        Set<String> regions = phoneUtil.getSupportedRegions();
        for (String region : regions)
        {
            Locale locale = new Locale("en", region);
            try {
                String city = geoCoder.getFromLocationName(region, 100).get(0).getLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }

            int countryCode = phoneUtil.getCountryCodeForRegion(region);
            Log.d("CountryCode", "Region = " + region +", locale " + locale.getDisplayCountry(locale) + ", code " + countryCode);

        }
    }

    public static boolean isNumeric(String str)
    {
        for (char c : str.toCharArray())
        {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
}
