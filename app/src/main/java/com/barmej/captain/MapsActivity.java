package com.barmej.captain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.google.android.gms.maps.model.LatLng;

public class MapsActivity extends AppCompatActivity {
    private MapFragment mapsFragment;
    private FrameLayout frameLayout;
    private AddPointOnMapFragment addPointOnMapFragment;
    private AddPointCommunicationInterface pointCommunicationInterface;

    private LatLng pickUpLatLng;
    private LatLng destinationLatLng;
//    private Trip trip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        frameLayout = findViewById(R.id.frame_layout);

        FragmentManager manager = getSupportFragmentManager();

        mapsFragment = (MapFragment) manager.findFragmentById(R.id.map_container_fragment);
        addPointOnMapFragment = (AddPointOnMapFragment) manager.findFragmentById(R.id.add_point);



        addPointOnMapFragment.setRequestType(getIntent().getIntExtra("request_type", 0));
        initListenerDelegates();
        addPointOnMapFragment.setActionDelegates(pointCommunicationInterface);


       // mapsFragment.checkLocationPermissionAndSetUpUserLocation();



    }

    private void initListenerDelegates() {
        pointCommunicationInterface = new AddPointCommunicationInterface() {
            @Override
            public boolean setPickup() {
                pickUpLatLng = mapsFragment.captureCenter();
                if (pickUpLatLng != null) {
                    mapsFragment.setPickUpMarker(pickUpLatLng);
                   // tripDetailsFragmet.setPickUpMarker(pickUpLatLng);
                    return true;
                }
                return false;
            }

            @Override
            public boolean setDestination() {
                destinationLatLng = mapsFragment.captureCenter();
                if (destinationLatLng != null) {
                    mapsFragment.setDestinationMarker(destinationLatLng);
                    return true;
                }
                return false;
            }

            @Override
            public void addPickup() {
                Intent intent = new Intent();
                intent.putExtra("pickup",pickUpLatLng);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void addDestination() {
                Intent intent = new Intent();
                intent.putExtra("destination",destinationLatLng);
                setResult(RESULT_OK, intent);
                finish();

            }
        };
    }

    public void reset() {
        destinationLatLng = null;
        pickUpLatLng = null;
    }



//
//    private void addpickup(LatLng pickup) {
//        trip = new Trip();
//        trip.setPickupLat(pickup.latitude);
//
//        finish();
//    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();

    }
}