package good.gda.appp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.solver.widgets.Snapshot;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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

public class RestPlaces extends Fragment  implements OnMapReadyCallback {

    GoogleMap gMap;
    DatabaseReference reference;
    FusedLocationProviderClient fusedLocationProviderClient;
    Button view_Map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rest_places, container, false);
        view_Map = v.findViewById(R.id.ButtonRest_viewMap);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment fragment = (SupportMapFragment)  getChildFragmentManager().findFragmentById(R.id.mapRestPlaces);
        fragment.getMapAsync(this);
    }


    @Override
    public void onStart() {
        super.onStart();
        reference = FirebaseDatabase.getInstance().getReference("Places");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    if (snapshot.child("type").getValue().toString().equals("Place for a rest"))
                    {
                        gMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(snapshot.child("positionX").getValue().toString()),Double.parseDouble(snapshot.child("positionY").getValue().toString())))
                                .snippet("You can receive " + snapshot.child("exp").getValue().toString() + " EXP. ,if you visit this place.")
                                .title(snapshot.child("name_Place").getValue().toString())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(snapshot.child("positionX").getValue().toString()), Double.parseDouble(snapshot.child("positionY").getValue().toString())), 13));

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void getLocationUser()
    {
        try
        {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(task.getResult().getLatitude(),task.getResult().getLongitude()),12));

                }
            });
        }
        catch (Exception ex)
        {
            Message("Error, when location user was receiving. Code: " + ex.getMessage());
        }
    }

    private void Message(String text)
    {
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        gMap = googleMap;

        view_Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
                {
                    view_Map.setText("Terrain");
                    gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
                else if (gMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE)
                {
                    view_Map.setText("Normal");
                    gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                }
                else if (gMap.getMapType() == GoogleMap.MAP_TYPE_TERRAIN)
                {
                    view_Map.setText("Satellite");
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });

        gMap.setMyLocationEnabled(true);
        gMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) getLocationUser();
                else
                {
                    DialogInterface.OnClickListener dialoginterface = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which)
                            {
                                case Dialog.BUTTON_POSITIVE:
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    Message("Unfortunately, application can't determine your location.");
                                    break;
                            }
                        }
                    };

                    new AlertDialog.Builder(getContext())
                            .setTitle("GPS permission")
                            .setMessage("GPS necessary for a work with your geolocation. Please, turn on GPS.")
                            .setPositiveButton("YES",dialoginterface)
                            .setNegativeButton("NO", dialoginterface)
                            .show();
                }
                return false;
            }
        });

    }
}
