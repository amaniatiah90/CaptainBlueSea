package com.barmej.captain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import static com.barmej.captain.TripDetailsFragmet.TRIP_DATA;

public class TripDetailsActivity extends AppCompatActivity  {

    private FrameLayout frameLayout;
    private CaptianActionDelegates captianActionDelegates;
    private TripDetailsFragmet tripDetailsFragmet;
    private Trip trip;
    private StatusCallBack statusCallBack = getStatusCallBack();
    private LocationCallback locationCallback;
    private FusedLocationProviderClient location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        if (getIntent() != null && getIntent().getExtras() != null) {
            trip = getIntent().getExtras().getParcelable(TRIP_DATA);
        } else {
            throw new RuntimeException("You must pass trip object for this Activity");
        }

        frameLayout = findViewById(R.id.frame_layout);
        tripDetailsFragmet = new TripDetailsFragmet();
        Bundle bundle = new Bundle();
        bundle.putParcelable(TRIP_DATA, trip);
        tripDetailsFragmet.setArguments(bundle);
        setFragment(tripDetailsFragmet);

        getCaptainActionDelegates();
        tripDetailsFragmet.setCaptianActionDelegates(captianActionDelegates);


    }
//
    private StatusCallBack getStatusCallBack() {
        return new StatusCallBack() {
            @Override
            public void onUpdate(Trip trip) {
                String tripStatus = trip.getStatus();

                if (tripStatus.equals(Trip.Status.AVAILABLE.name())) {
                    tripDetailsFragmet.updateWithStatus(trip);
                } else if (tripStatus.equals(Trip.Status.START_TRIP.name())){
                    tripDetailsFragmet.showOnTripView(trip);
                    tripDetailsFragmet.checkLocationPermissionAndSetUpUserLocation();
                } else if (tripStatus.equals(Trip.Status.ARRIVED.name())) {
                    tripDetailsFragmet.showArrivedScreen(trip);
                }
            }
        };
    }

//    private void showArrivedScreen(Trip trip) {
//        tripDetailsFragmet.reset();
//        tripDetailsFragmet.updateWithStatus(trip);
//    }
//
//    private void showOnTripView(Trip trip) {
//        tripDetailsFragmet.setCurrentMarker(new LatLng(trip.getCurrentLat(), trip.getCurrentLng()));
//        tripDetailsFragmet.setDestinationMarker(new LatLng(trip.getDestinationLat(), trip.getDestinationLng()));
//        tripDetailsFragmet.setPickUpMarker(new LatLng(trip.getPickupLat(), trip.getPickupLng()));
//        tripDetailsFragmet.updateWithStatus(trip);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        TripManager.getInstance().startListeningForStatus(statusCallBack, trip.getId());
    }

    private void getCaptainActionDelegates() {
        captianActionDelegates = new CaptianActionDelegates() {
            @Override
            public void startTrip() {
                System.out.println("START TRIP");
                TripManager.getInstance().updateTrip();

            }

            @Override
            public void updateLocation() {
                // TODO: update trip using TripManager
            }

            @Override
            public void arrivedTrip() {
                TripManager.getInstance().updateTripToArrived();

            }

            @Override
            public void goOffline() {
                FirebaseAuth.getInstance().signOut();
                startActivity(LoginActivity.getStartIntent(TripDetailsActivity.this));
                finish();
            }
        };
    }


    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();

    }

    @Override
    protected void onStop() {
        super.onStop();
        TripManager.getInstance().stopListeningToStatus();
        tripDetailsFragmet.stopLocationUpdates();
    }


}
