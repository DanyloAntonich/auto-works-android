package helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import autoworks.app.R;

/**
 * Created by volyminhnhan on 4/11/15.
 */
public class MapRouteHelper {

    private GoogleMap googleMap;
    private Activity mActivity;

    public MapRouteHelper(Activity activity, SupportMapFragment mapFragment, int rID, LatLng currentPos, LatLng targetPos){

        googleMap = mapFragment.getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        googleMap.setMyLocationEnabled(true);

        this.mActivity = activity;

        TelephonyManager tm = (TelephonyManager) this.mActivity.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();
        Log.d("Country code", countryCode);

        updateLocation(currentPos, targetPos);


    }
    private Marker target;
    public void updateLocation(LatLng currentPos, LatLng targetPos) {

        TelephonyManager tm = (TelephonyManager) this.mActivity.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();
        Log.d("Country code", countryCode);

        //add marker
        target = googleMap.addMarker(new MarkerOptions()
                .position(targetPos)
                .title("Auto-Works Store")
                        //.snippet("Population: 4,137,400")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));


        if(countryCode.toUpperCase().equals("SA")) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 20));
            String url = getMapsApiDirectionsUrl(currentPos, targetPos);
            ReadTask downloadTask = new ReadTask();
            downloadTask.execute(url,"0");
        }
        else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetPos, 20));
        }
    }

    public static String getMapsApiDirectionsUrl(LatLng startPos, LatLng endPost) {
        String waypoints = "waypoints=optimize:true|"
                + startPos.latitude + "," + startPos.longitude
                + "|" + "|" + endPost.latitude + ","
                + endPost.longitude;

        String sensor = "sensor=false";
        String params = "origin=" + startPos.latitude + "," + startPos.longitude + "&destination="  + endPost.latitude + "," + endPost.longitude + "&" + waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;

        Log.e("url", url);
        return url;
    }

    public class ReadTaskResult
    {
        public String result;
    }

    public class ParserTaskResult
    {
        public List<List<HashMap<String, String>>> routes;
    }

    private class ReadTask extends AsyncTask<String, Void, ReadTaskResult> {

        @Override
        protected ReadTaskResult doInBackground(String... url) {
            ReadTaskResult umem = new ReadTaskResult();
            try {
                HttpConnection http = new HttpConnection();
                umem.result = http.readUrl(url[0]);

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return umem;
        }

        @Override
        protected void onPostExecute(ReadTaskResult result) {
            super.onPostExecute(result);
            new ParserTask().execute(result.result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, ParserTaskResult> {

        @Override
        protected ParserTaskResult doInBackground(
                String... jsonData) {

            JSONObject jObject;
            ParserTaskResult result = new ParserTaskResult();

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                result.routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(ParserTaskResult result) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            if(result.routes != null) {
                // traversing through routes
                for (int i = 0; i < result.routes.size(); i++) {
                    points = new ArrayList<LatLng>();
                    polyLineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = result.routes.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    polyLineOptions.addAll(points);
                    polyLineOptions.width(10);
                    polyLineOptions.color(Color.BLUE);
                }
            }

            if(polyLineOptions != null) {
                googleMap.addPolyline(polyLineOptions);
            }
        }
    }
}
