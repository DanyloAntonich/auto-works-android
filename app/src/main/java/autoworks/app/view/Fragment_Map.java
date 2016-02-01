package autoworks.app.view;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import autoworks.app.R;
import helpers.MapRouteHelper;


public class Fragment_Map extends Fragment implements LocationListener {

    LocationManager locationManager;
    private Location location;

    MapRouteHelper mapRouteHelper = null;

    public Fragment_Map() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(4);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //show logo
        android.support.v7.app.ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        ImageView logo = (ImageView)actionBar.getCustomView().findViewById(R.id.actionBarLogo);
        logo.setVisibility(View.VISIBLE);

    }

   LatLng auto_works_store = new LatLng(21.545135, 39.215966);
    //LatLng hcmc = new LatLng(10.82310, 106.62966);

    private View myview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AutoWorks", "oCreateView fragment_register");
        myview = inflater.inflate(R.layout.fragment__map, container, false);

        try {
            locationManager = (LocationManager)getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,   this, Looper.getMainLooper());
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,   this, Looper.getMainLooper());

            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        catch (Exception ex) {
            Log.d("error", ex.getMessage());
        }

        SupportMapFragment mapFragment =  (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if(location != null) {
//            mapRouteHelper =  new MapRouteHelper(getActivity(),mapFragment, R.id.map, new LatLng(location.getLatitude(), location.getLongitude()), auto_works_store);
            mapRouteHelper = new MapRouteHelper(getActivity(),mapFragment, R.id.map, auto_works_store, auto_works_store);
        }
        else {
            mapRouteHelper = new MapRouteHelper(getActivity(),mapFragment, R.id.map, auto_works_store, auto_works_store);
        }

        return myview;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void onLocationChanged(Location location) {
        // Called when a new location is found by the network location provider.
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(location != null) {
//            mapRouteHelper.updateLocation(new LatLng(location.getLatitude(), location.getLongitude()), auto_works_store);
        }

        //Log.i("Message: ","Location changed, " + location.getAccuracy() + " , " + location.getLatitude()+ "," + location.getLongitude());
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {}
    public void onProviderEnabled(String provider) {}
    public void onProviderDisabled(String provider) {}

}
