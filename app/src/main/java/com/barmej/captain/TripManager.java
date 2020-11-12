package com.barmej.captain;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TripManager {
    private static final String TRIP_REF_PATH = "trips";
    private FirebaseDatabase database;
    private static TripManager instance;
    private Trip trip;
    private ValueEventListener tripListener;

    private StatusCallBack statusCallBack;

    public TripManager() {
        database = FirebaseDatabase.getInstance();
    }

    public static  TripManager getInstance() {
        if (instance == null) {
            instance = new TripManager();
        }
        return instance;
    }

    public void startListeningForStatus(StatusCallBack statusCallBack, String id) {
        this.statusCallBack = statusCallBack;
        tripListener = database.getReference(TRIP_REF_PATH).child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        trip = dataSnapshot.getValue(Trip.class);
                        if (trip != null) {
                            notifyListener(trip);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

//    private void getTripAndNotifyStatus() {
//        database.getReference(TRIP_REF_PATH).child(trip.getId())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        trip = dataSnapshot.getValue(Trip.class);
//
//                        if (trip != null) {
//
//                            notifyListener(trip);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }

    private void notifyListener(Trip trip) {
        if (statusCallBack != null) {
            statusCallBack.onUpdate(trip);
        }
    }

    // TODO: create new update trip method to update trip location


    public void updateTrip() {
        System.out.println("UPDATE TRIP");

        trip.setStatus(Trip.Status.START_TRIP.name());
        database.getReference(TRIP_REF_PATH).child(trip.getId()).setValue(trip);
        notifyListener(trip);



//         database.getReference(TRIP_REF_PATH).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                trip = snapshot.getValue(Trip.class);
//                trip.setStatus(Trip.Status.START_THE_TRIP.name());
//                database.getReference(TRIP_REF_PATH).child(trip.getId()).setValue(trip).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        System.out.println("TRIP UPDATED");
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        System.out.println("TRIP UPDATE FAILED");
//                        e.printStackTrace();
//                    }
//                });
//                notifyListener(trip);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }

    public void updateTripToArrived() {
        trip.setStatus(Trip.Status.ARRIVED.name());
        database.getReference(TRIP_REF_PATH).child(trip.getId()).setValue(trip);
        notifyListener(trip);




//         database.getReference(TRIP_REF_PATH).child(trip.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                     String tripStatus = trip.getStatus();
//                    // if (tripStatus.equals(Trip.Status.START_THE_TRIP)) {
//                         trip = snapshot.getValue(Trip.class);
//                         trip.setStatus(Trip.Status.TRIP_HAS_ARRIVED.name());
//                         database.getReference(TRIP_REF_PATH).child(trip.getId()).setValue(trip);
//                         notifyListener(trip);
//                     //}
//            }

//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    public void updateCurrentLocation(double lat, double lng) {
        trip.setCurrentLat(lat);
        trip.setCurrentLng(lng);
        database.getReference(TRIP_REF_PATH).child(trip.getId()).setValue(trip);
    }


    public void stopListeningToStatus() {
        if (tripListener != null) {
            database.getReference().child(trip.getId()).removeEventListener(tripListener);
        }
        statusCallBack = null;
    }


}
