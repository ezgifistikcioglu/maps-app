package com.example.mapsapp.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.mapsapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements LocationListener  {

    MapView mMapView;
    private GoogleMap googleMap;
    Marker marker;
    SharedPreferences prefs;
    boolean firstPositionUpdate = true;
    LatLng start_position = null;
    boolean toast_is_show = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        //Get Shared Preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //Init Google Maps
        mMapView = rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Handle Google Maps
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                onMapReadyHandler(mMap);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void onMapReadyHandler(GoogleMap mMap) {
        googleMap = mMap;

        //Check Permissions
        checkAndRequestPermission();
    }

    public void checkAndRequestPermission() {
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, getTargetRequestCode());
        }
        else
        {
            mapActions();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(getActivity().getApplicationContext(), "Konum izni verilmeli!", Toast.LENGTH_LONG).show();
        }
        checkAndRequestPermission();
    }

    @SuppressLint("MissingPermission")
    public void mapActions() {
        //Get User Account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());

        //Add Marker
        MarkerOptions marker_options = new MarkerOptions()
                .position(new LatLng(10, 10))
                .title(account.getDisplayName());
        marker = googleMap.addMarker(marker_options);
        marker.setVisible(false);

        //Current Location Subscription
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 1000, 1, this);
        Toast.makeText(getActivity().getApplicationContext(), "Konum bilginiz alınıyor!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        checkDistance(new LatLng(location.getLatitude(), location.getLongitude()));
        marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        marker.setVisible(true);
        float zoom_level = (firstPositionUpdate) ? 22 : googleMap.getCameraPosition().zoom;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoom_level));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    public void checkDistance(LatLng loc) {
        if(start_position==null) {
            start_position = loc;
        }
        float[] results = new float[1];
        Location.distanceBetween(start_position.latitude, start_position.longitude, loc.latitude, loc.longitude, results);
        float distance = results[0];
        Log.i("distance", "konum farkı: "+results[0]);
        Integer alert_distance = prefs.getInt("alert_distance", 5);
        if(distance > alert_distance) {
            if(!toast_is_show) {
                toast_is_show = true;
                Toast.makeText(getActivity(), "Lütfen boşlangıç konuuna dönün!", Toast.LENGTH_LONG).show();
            }
        } else {
            if(toast_is_show) {
                toast_is_show = false;
                Toast.makeText(getActivity(), "Başlangıç konuuna yaklaştınız!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
