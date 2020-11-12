package com.barmej.captain;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.barmej.captain.AddTripActivity.TRIP_REF_PATH;

public class TripListFragment extends Fragment implements TripListAdapter.OnTripClickListener {
    private RecyclerView mRecyclerViewTrip;
    private TripListAdapter mTripsListAdapter;
    private ArrayList<Trip> mTrips;
    private Button mAddTripButton;
    private FrameLayout parentFrameLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trips_list,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        parentFrameLayout = getActivity().findViewById(R.id.frame_layout);

        mRecyclerViewTrip = view.findViewById(R.id.recycler_view_trip);
        mAddTripButton = view.findViewById(R.id.add_trip_button);
        mRecyclerViewTrip.setLayoutManager(new LinearLayoutManager(getContext()));

        mTrips = new ArrayList<>();
        mTripsListAdapter = new TripListAdapter(mTrips, TripListFragment.this);
        mRecyclerViewTrip.setAdapter(mTripsListAdapter);


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference(TRIP_REF_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mTrips.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    if(trip.getId().startsWith(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        mTrips.add(trip);
                    }

                }
                mTripsListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mAddTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddTripActivity.class));

            }
        });
    }


    @Override
    public void onTripClick(Trip trip) {
        Intent intent = new Intent(getContext(), TripDetailsActivity.class);
        intent.putExtra(TripDetailsFragmet.TRIP_DATA, trip);
        startActivity(intent);

    }
}
