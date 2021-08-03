package good.gda.appp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private DrawerLayout drawerLayout;
    SupportMapFragment supportMapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportMapFragment = SupportMapFragment.newInstance();
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View nav_header = navigationView.getHeaderView(0);
        TextView navEmail_User = nav_header.findViewById(R.id.EmailText_nav),
                navNickname_User = nav_header.findViewById(R.id.NicknameText_nav);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        navEmail_User.setText(user.getEmail());
        navNickname_User.setText(user.getDisplayName());


        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        supportMapFragment.getMapAsync(this);


        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ShowMapFragment()).commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.nav_Information:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new InformationFragment()).commit();
                break;
            case R.id.nav_ShowMap:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ShowMapFragment()).commit();
                break;
            case R.id.nav_monuments:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MonumentsMapsActivity()).commit();
                break;

            case R.id.nav_sendErrorOrPlace:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SendErrorOrPlace()).commit();
                break;

            case R.id.nav_Parks:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Parks()).commit();
                break;

            case R.id.nav_Places_for_a_rest:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RestPlaces()).commit();
                break;

            case R.id.nav_Share:
                Toast toast = Toast.makeText(getApplicationContext(),"Coming soon!",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.nav_Add_Place:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new InsertDataToDataBaseActivity()).commit();
                break;
            case R.id.nav_Exit:
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
