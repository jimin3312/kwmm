package com.kwmm0.Restaurant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kwmm0.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationInfo extends AppCompatActivity implements OnMapReadyCallback {
    double x,y;
    String restaurantName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_map_layout);

        setInfo();
        setMap();
    }

    private void setInfo() {
        x = getIntent().getDoubleExtra("x",0);
        y = getIntent().getDoubleExtra("y",0);
        restaurantName = getIntent().getStringExtra("restaurantName");
    }

    private void setMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_location);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng Point = new LatLng(x, y);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(Point);
        markerOptions.title(restaurantName);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(Point));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        googleMap.addMarker(markerOptions).showInfoWindow();

    }
}
