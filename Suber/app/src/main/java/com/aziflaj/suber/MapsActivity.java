package com.aziflaj.suber;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    public static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap;
    private Marker mMarker;
    private LocationManager mLocationManager;
    private String mBestProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED

                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(MapsActivity.this, "Don't have location permission", Toast.LENGTH_LONG).show();
            return;
        }

        mLocationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED

                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(MapsActivity.this, "Don't have location permission", Toast.LENGTH_LONG).show();
            return;
        }

        if (mLocationManager != null) {
            mLocationManager.requestLocationUpdates(mBestProvider, 400, 0, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED

                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(MapsActivity.this, "Don't have location permission", Toast.LENGTH_LONG).show();
            return;
        }

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mBestProvider = mLocationManager.getBestProvider(new Criteria(), true);
        Location hereNow = mLocationManager.getLastKnownLocation(mBestProvider);
        if (hereNow != null) {
            Log.d(TAG, "Location: Lat " + hereNow.getLatitude());
            Log.d(TAG, "Location: Long " + hereNow.getLongitude());
            onLocationChanged(hereNow);
        }

        mLocationManager.requestLocationUpdates(mBestProvider, 400, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mMarker != null) {
            mMarker.remove();
        }

        double lat = location.getLatitude();
        double lng = location.getLongitude();
        LatLng hereNow = new LatLng(lat, lng);
        mMarker = mMap.addMarker(new MarkerOptions().position(hereNow));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hereNow, 17));
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

    public void getTaxi(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED

                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(MapsActivity.this, "Don't have location permission", Toast.LENGTH_LONG).show();
            return;
        }

        Location hereNow = mLocationManager.getLastKnownLocation(mBestProvider);
        if (hereNow != null) {
            String status = String.format("You are at %.4f Lat, %.4f Long", hereNow.getLatitude(), hereNow.getLongitude());
            Toast.makeText(MapsActivity.this, status, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MapsActivity.this, "Location is null", Toast.LENGTH_SHORT).show();
        }
    }
}
