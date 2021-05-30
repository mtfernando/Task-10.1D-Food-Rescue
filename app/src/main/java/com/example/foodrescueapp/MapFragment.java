package com.example.foodrescueapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodrescueapp.data.DatabaseHelper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback{
    public static final String TAG = "MapFragment";
    DatabaseHelper db;
    GoogleMap mMap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //Initialize Map Fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        //Async map
        supportMapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //When map is loaded
        mMap = googleMap;

        if(getArguments()!=null){
            //Latitude and Longitude from Arguments
            double lat = getArguments().getDouble("latitude");
            double lon = getArguments().getDouble("longitude");

            LatLng locationLatLng = new LatLng(lat, lon);
            //Info Log - Longitude and Latitude of the food_location
            Log.i(TAG, "Latitude: " + lat);
            Log.i(TAG, "Longitude: " + lon);

            mMap.addMarker(new MarkerOptions()
                    .position(locationLatLng)
                    .title("Pick up location"));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 12.0f));
        }
    }
}