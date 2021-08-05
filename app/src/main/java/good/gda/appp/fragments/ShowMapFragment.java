package good.gda.appp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import good.gda.appp.R;
import good.gda.appp.other.Constants;


public class ShowMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap gMap;
    private DatabaseReference database, databaseUser;
    private FusedLocationProviderClient useLocationProviderClient;
    private Button view_Map;
    private final String type;
    private ArrayList<Places> places;
    private ArrayList<Marker> markers;
    private ArrayList<Circle> circles;
    private static final double RADIUS = 25.0;
    private String visitedPlaces = "";
    private int exp = 0;

    public ShowMapFragment(String type)
    {
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_google_maps_activity, container, false);
        view_Map = v.findViewById(R.id.ButtonMonum_viewMap);
        places = new ArrayList<>();
        markers = new ArrayList<>();
        circles = new ArrayList<>();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseUser = FirebaseDatabase.getInstance().getReference("Users/" + firebaseUser.getUid());
        databaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("visited").exists())
                {
                    visitedPlaces = dataSnapshot.child("visited").getValue().toString();
                    exp = Integer.parseInt(dataSnapshot.child("exp").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

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
            database = FirebaseDatabase.getInstance().getReference("Places");
            database.addListenerForSingleValueEvent(vel);
        } catch (Exception ex) {
            Constants.showMessage(getContext(),"Error, when data is receiving from database. Error:" + ex.getMessage());
        }
    }

    private final ValueEventListener vel = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            LatLng latLng = new LatLng(0,0);
            for (DataSnapshot snapshot : dataSnapshot.getChildren())
            {
                String t = snapshot.child("type").getValue(String.class),
                    placeId = snapshot.child("placeId").getValue().toString();
                if ((t.equals(type) || type.equals("All")) && !visitedPlaces.contains(placeId))
                {
                    double posX = Double.parseDouble(snapshot.child("positionX").getValue().toString()),
                            posY = Double.parseDouble(snapshot.child("positionY").getValue().toString()),
                            exp = Double.parseDouble(snapshot.child("exp").getValue().toString());
                    latLng = new LatLng(posX, posY);
                    String namePlace = snapshot.child("name_Place").getValue().toString();
                    places.add(new Places(placeId, namePlace, posX,
                            posY, exp ,t, ""));
                    circles.add(gMap.addCircle(new CircleOptions()
                            .center(latLng)
                            .clickable(true)
                            .radius(RADIUS)
                            .fillColor(Color.parseColor("#808E97FD"))
                            .strokeWidth(1.0f)));
                    markers.add(gMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .snippet("You can receive " + (int) exp + " EXP. ,if you visit this place.")
                            .title(namePlace)
                            .icon(BitmapDescriptorFactory.defaultMarker(CheckingType(t)))));
                }
            }
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

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

    // Get location user on google map. ///////
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
        gMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                for (short i = 0; i < places.size(); i++)
                {
                    float lat = (float) places.get(i).positionX,
                        lon = (float) places.get(i).positionY;
                    int z = (int) (Math.pow(Math.abs(location.getLatitude() - lat), 2) * 1000000000),
                        z1 = (int) (Math.pow(Math.abs(location.getLongitude() - lon), 2) * 1000000000),
                        rad = (int) (Math.pow(RADIUS+12, 2)*0.1f);
                    if (z1 + z < rad)
                    {
                        Dialog dialog = new Dialog(getActivity());
                        dialog.setCancelable(true);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_description);
                        ((TextView) dialog.findViewById(R.id.dialog_textView_name)).setText(places.get(i).Name_Place);
                        ((TextView) dialog.findViewById(R.id.dialog_textView_description)).setText(places.get(i).desc);
                        Log.d("123456", z + " " + z1 + " " + rad);
                        Constants.showMessage(getContext(), places.get(i).Name_Place);
                        exp += places.get(i).EXP;
                        databaseUser.child("exp").setValue(exp);
                        visitedPlaces += (places.get(i).placeId + "|");
                        databaseUser.child("visited").setValue(visitedPlaces);
                        places.remove(i);
                        markers.get(i).remove();
                        markers.remove(i);
                        circles.get(i).remove();
                        circles.remove(i);
                        break;
                    }
                }
            }
        });


        view_Map.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
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
