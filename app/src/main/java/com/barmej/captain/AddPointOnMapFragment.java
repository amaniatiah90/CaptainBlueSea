package com.barmej.captain;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddPointOnMapFragment extends Fragment {
    private ImageView pinView;
    private Button selectPickupButton;
    private Button addPickupButton;
    private Button selectDestinationButton;
    private Button addDestinationButton;
    private AddPointCommunicationInterface pointCommunicationInterface;
    private int requestType;

//    public static AddPointOnMapFragment getInstance(int requestType) {
//        Bundle bundle = new Bundle();
//        bundle.putInt("request_type", requestType);
//        AddPointOnMapFragment addPointOnMapFragment = new AddPointOnMapFragment();
//        addPointOnMapFragment.setArguments(bundle);
//        return addPointOnMapFragment;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            requestType = getArguments().getInt("request_type", 0);
           // setRequestType(requestType);


        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_point, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        pinView = view.findViewById(R.id.image_view_location_pin);
        selectPickupButton = view.findViewById(R.id.button_select_pickup);
        addPickupButton = view.findViewById(R.id.button_add_pickup);
        selectDestinationButton = view.findViewById(R.id.button_select_destination);
        addDestinationButton = view.findViewById(R.id.button_add_destination);




        selectPickupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPickup();
            }
        });

        addPickupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPickup();
            }
        });

        selectDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDestination();
            }
        });

        addDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDestination();
            }
        });
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
        hideAllViews();
        pinView.setVisibility(View.VISIBLE);
        if (requestType == AddTripActivity.REQUEST_PICKUP_LOCATION) {
            selectPickupButton.setVisibility(View.VISIBLE);
        } else {
            selectDestinationButton.setVisibility(View.VISIBLE);
        }

    }

    public void selectPickup() {

        if (pointCommunicationInterface != null && pointCommunicationInterface.setPickup()) {
            hideAllViews();
           // pinView.setVisibility(View.VISIBLE);
            addPickupButton.setVisibility(View.VISIBLE);
        }

    }
    public void addPickup() {
        if (pointCommunicationInterface != null) {
            pointCommunicationInterface.addPickup();
      //      pinView.setVisibility(View.GONE);
        }
      //  getActivity().finish();
//
    }



    public void selectDestination(){
        if (pointCommunicationInterface != null && pointCommunicationInterface.setDestination()) {
            hideAllViews();
//            pinView.setVisibility(View.VISIBLE);
            addDestinationButton.setVisibility(View.VISIBLE);
        }
//
    }

    public void addDestination() {
        if (pointCommunicationInterface != null) {
            pointCommunicationInterface.addDestination();
        }

    }

    public void hideAllViews() {
        pinView.setVisibility(View.GONE);
        selectPickupButton.setVisibility(View.GONE);
        addPickupButton.setVisibility(View.GONE);
        selectDestinationButton.setVisibility(View.GONE);
        addDestinationButton.setVisibility(View.GONE);

    }

    public void setActionDelegates(AddPointCommunicationInterface pointCommunicationInterface) {
        this.pointCommunicationInterface = pointCommunicationInterface;
    }

}
