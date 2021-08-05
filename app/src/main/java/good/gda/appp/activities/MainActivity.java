package good.gda.appp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import good.gda.appp.R;
import good.gda.appp.fragments.InformationFragment;
import good.gda.appp.fragments.InsertDataToDataBaseFragment;
import good.gda.appp.fragments.SendErrorOrPlaceFragment;
import good.gda.appp.fragments.ShowMapFragment;
import good.gda.appp.other.Constants;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ShowMapFragment("All")).commit();
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.nav_Information:
                openFragment(new InformationFragment());
                break;
            case R.id.nav_ShowMap:
                openFragment(new ShowMapFragment("All"));
                break;
            case R.id.nav_monuments:
                openFragment(new ShowMapFragment("Monument"));
                break;
            case R.id.nav_sendErrorOrPlace:
                openFragment(new SendErrorOrPlaceFragment());
                break;
            case R.id.nav_Parks:
                openFragment(new ShowMapFragment("Park"));
                break;
            case R.id.nav_Places_for_a_rest:
                openFragment(new ShowMapFragment("Place for a rest"));
                break;
            case R.id.nav_Share:
                Constants.showMessage(getApplicationContext(), "Coming soon!");
                break;
            case R.id.nav_Add_Place:
                openFragment(new InsertDataToDataBaseFragment());
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

    private void openFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}
