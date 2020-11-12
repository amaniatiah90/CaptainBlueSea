package com.barmej.captain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AddTripActivity extends AppCompatActivity {

    public static final String TRIP_REF_PATH = "trips";
    public static final int REQUEST_PICKUP_LOCATION = 0;
    public static final int REQUEST_DESTINATION_LOCATION = 1;

    private FirebaseDatabase database;

    private TextInputLayout mTripFromCountryTextInputLayout;
    private TextInputLayout mTripToCountryTextInputLayout;
    private TextInputLayout mTripAvailableSeatInputTextLayout;
    private TextInputEditText mTripFromCountryTextInputEditText;
    private TextInputEditText mTripToCountryTextInputEditText;
    private TextInputEditText mTripAvailableSeatTextInputEditText;
    private Button mAddTripButton;
    private Date  mTripDate;
    private DatePicker mDatePicker;
    private ProgressDialog mDialog;
    private ConstraintLayout mConstraintLayout;
    private Trip trip;
    private AddPointCommunicationInterface pointCommunicationInterface;
    private Location location;
    private LatLng pickup;
    private LatLng destination;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        mConstraintLayout = findViewById(R.id.add_trip_cord_layout);
        mTripFromCountryTextInputLayout = findViewById(R.id.input_layout_from_country);
        mTripToCountryTextInputLayout = findViewById(R.id.input_layout_to_country);
        mTripAvailableSeatInputTextLayout = findViewById(R.id.input_layout_available_seats);
        mTripFromCountryTextInputEditText = findViewById(R.id.edit_text_trip_from_country);
        mTripToCountryTextInputEditText = findViewById(R.id.edit_text_trip_to_country);
        mTripAvailableSeatTextInputEditText = findViewById(R.id.edit_text_available_seats);
        mAddTripButton = findViewById(R.id.button_add_trip);
        mDatePicker = findViewById(R.id.datePicker);

        database = FirebaseDatabase.getInstance();

        mDialog = new ProgressDialog(this);
        mDialog.setIndeterminate(true);
        mDialog.setTitle(R.string.app_name);
        mDialog.setMessage(getString(R.string.uploading_Data));

        mTripFromCountryTextInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view,boolean hasFocus) {
                if (hasFocus) {
                    showActivityMap(REQUEST_PICKUP_LOCATION);
                    view.clearFocus();
                }
            }
        });

        mTripToCountryTextInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view,boolean hasFocus) {
                if (hasFocus) {
                    showActivityMap(REQUEST_DESTINATION_LOCATION);
                    view.clearFocus();
                }
            }
        });

        mAddTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTripFromCountryTextInputLayout.setError(null);
                mTripToCountryTextInputLayout.setError(null);
                mTripAvailableSeatInputTextLayout.setError(null);
                if (TextUtils.isEmpty(mTripFromCountryTextInputEditText.getText())) {
                    mTripFromCountryTextInputLayout.setError(getString(R.string.error_msg_from_country));
                } else if (TextUtils.isEmpty(mTripToCountryTextInputEditText.getText())) {
                    mTripToCountryTextInputLayout.setError(getString(R.string.error_msg_to_country));
                } else if (TextUtils.isEmpty(mTripAvailableSeatTextInputEditText.getText())) {
                    mTripAvailableSeatInputTextLayout.setError(getString(R.string.error_msg_available_seat));
                } else if (mTripFromCountryTextInputLayout != null && mTripToCountryTextInputLayout != null && mTripAvailableSeatInputTextLayout != null) {
                    addTripToFirebase();
                }
            }
        });


    }

    private void showActivityMap(int requestCode) {
        Intent intent = new Intent(AddTripActivity.this, MapsActivity.class);
        intent.putExtra("request_type", requestCode);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_OK) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses;
            Address address;
            switch (requestCode) {
                case REQUEST_PICKUP_LOCATION:
                    pickup = data.getParcelableExtra("pickup");
                   // mTripFromCountryTextInputEditText.setText(pickup.latitude + " , " + pickup.longitude);
                    try {
                        addresses = geocoder.getFromLocation(pickup.latitude,pickup.longitude,1);
                        address = addresses.get(0);
                        mTripFromCountryTextInputEditText.setText(address.getFeatureName() + " - " + address.getLocality() + " - " + address.getCountryName());
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                    break;
                case REQUEST_DESTINATION_LOCATION:
                    destination = data.getParcelableExtra("destination");
                    try {
                        addresses = geocoder.getFromLocation(destination.latitude,destination.longitude,1);
                        address = addresses.get(0);
                        mTripToCountryTextInputEditText.setText(address.getFeatureName() + " - " + address.getLocality() + " - " + address.getCountryName());
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                    break;
            }
        }
    }

    public void addTripToFirebase() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
        calendar.set(Calendar.MONTH, mDatePicker.getMonth());
        calendar.set(Calendar.YEAR, mDatePicker.getYear());
        mTripDate = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        String dateString = dateFormat.format(mTripDate.getTime());
        String captainId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String id = captainId + " - " + dateString;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference(TRIP_REF_PATH).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Trip trip = snapshot.getValue(Trip.class);
                if(trip != null) {
                    // TODO: there is already a scheulded
                    Toast.makeText(AddTripActivity.this,"هناك رحلة في نفس الوقت",Toast.LENGTH_SHORT).show();
                } else {
                    addTrip(id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
    private void addTrip(String id) {
        trip = new Trip();

        trip.setFromCountry(mTripFromCountryTextInputEditText.getText().toString());
        trip.setToCountry(mTripToCountryTextInputEditText.getText().toString());

        trip.setDate(mTripDate);
        trip.setAvailableSeats(Integer.parseInt(mTripAvailableSeatTextInputEditText.getText().toString()));
        trip.setId(id);
        trip.setStatus(Trip.Status.AVAILABLE.name());
        trip.setPickupLat(pickup.latitude);
        trip.setPickupLng(pickup.longitude);
        trip.setDestinationLat(destination.latitude);
        trip.setDestinationLng(destination.longitude);
        mDialog.show();

        database.getReference(TRIP_REF_PATH).child(id).setValue(trip).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Snackbar.make(mConstraintLayout, R.string.add_trip_success, Snackbar.LENGTH_SHORT).addCallback(new Snackbar.Callback(){
                        @Override
                        public void onDismissed(Snackbar transientBottomBar,int event) {
                            super.onDismissed(transientBottomBar,event);
                            mDialog.dismiss();
                            finish();
                        }
                    }).show();

                } else {
                    Snackbar.make(mConstraintLayout, R.string.add_trip_failed, Snackbar.LENGTH_LONG).show();

                }
            }
        });

    }

}
