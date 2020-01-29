package com.example.mapsapp.ui.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mapsapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        return view ;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) { //harita hazır old. yapilacak islemler
        mMap = googleMap;

        LatLng eiffel = new LatLng(48.8587795,2.2923783);
        mMap.addMarker(new MarkerOptions().position(eiffel).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eiffel,15));
    }
}