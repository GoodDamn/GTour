package good.gda.appp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import good.gda.appp.R;
import good.gda.appp.other.Constants;


public class ShowMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap gMap;
    private DatabaseReference FireDatabase;
    private FusedLocationProviderClient useLocationProviderClient;
    private Button view_Map;
    private final String type;
    // Get location user on google map. ///////

    public ShowMapFragment(String type)
    {
        this.type = type;
    }

    private void getLocationUser() {
        try {
            useLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
            useLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude()), 10));
                    }
                }
            });
        } catch (Exception ex) {
            Constants.showMessage(getContext(),"Application can't get data about your location. Please, turn on 'location'");
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_google_maps_activity, container, false);
        view_Map = v.findViewById(R.id.ButtonMonum_viewMap);
        return v;
    }

    // View created./////////

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
        getPermissionLocation();
    }

    // Data from database.///////

    private void ReceivingDataFromFireBase() {
        try {
            FireDatabase = FirebaseDatabase.getInstance().getReference("Places");
            FireDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String t = snapshot.child("type").getValue(String.class);
                        if (t.equals(type) || type.equals("All")) {
                            gMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(snapshot.child("positionX").getValue().toString()), Double.parseDouble(snapshot.child("positionY").getValue().toString())))
                                    .snippet("You can receive " + snapshot.child("exp").getValue().toString() + " EXP. ,if you visit this place.")
                                    .title(snapshot.child("name_Place").getValue().toString())
                                    .icon(BitmapDescriptorFactory.defaultMarker(CheckingType(t))));
                            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(snapshot.child("positionX").getValue().toString()), Double.parseDouble(snapshot.child("positionY").getValue().toString())), 12));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception ex) {
            Constants.showMessage(getContext(),"Error, when data is receiving from database. Error:" + ex.getMessage());
        }
    }

    // Get Permission ///////////////////////

    private Boolean locationGranted = false;

    private void getPermissionLocation() {
        try {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationGranted = true;
                } else ActivityCompat.requestPermissions(getActivity(), permissions, 1234);
            } else ActivityCompat.requestPermissions(getActivity(), permissions, 1234);
        } catch (Exception ex) {
            Constants.showMessage(getContext(),"Error, when application is receiving permission on using geolocation of user. Code error:" + ex.getMessage());
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationGranted = false;
        if (requestCode == 1234)
        {
            if (grantResults.length > 0) {
                locationGranted = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        locationGranted = false;
                        return;
                    }
                }
                locationGranted = true;
            }
        }
    }


    // Receiving data from Firebase database. ///////////

    @Override
    public void onStart() {
        super.onStart();
        ReceivingDataFromFireBase();
    }

    private float CheckingType(String typeOfPlace) {
        if (typeOfPlace.equals("Monument"))
            return BitmapDescriptorFactory.HUE_BLUE;
        else if (typeOfPlace.equals("Park")) return BitmapDescriptorFactory.HUE_YELLOW;
        else if (typeOfPlace.equals("Place for a rest")) return BitmapDescriptorFactory.HUE_GREEN;
        else return BitmapDescriptorFactory.HUE_ROSE;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        view_Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    view_Map.setText("Terrain");
                    gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else if (gMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
                    view_Map.setText("Normal");
                    gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                } else if (gMap.getMapType() == GoogleMap.MAP_TYPE_TERRAIN) {
                    view_Map.setText("Satellite");
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });

        if (locationGranted)
        {
            gMap.setMyLocationEnabled(true);
            gMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                        getLocationUser();
                    else {
                        DialogInterface.OnClickListener dialoginterface = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case Dialog.BUTTON_POSITIVE:
                                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        Constants.showMessage(getContext(),"Unfortunately, application can't determine your location.");
                                        break;
                                }
                            }
                        };

                        new AlertDialog.Builder(getContext())
                                .setTitle("GPS permission")
                                .setMessage("GPS necessary for a work with your geolocation. Please, turn on GPS.")
                                .setPositiveButton("YES", dialoginterface)
                                .setNegativeButton("NO", dialoginterface)
                                .show();
                    }
                    return false;
                }
            });
        }
    }

}
