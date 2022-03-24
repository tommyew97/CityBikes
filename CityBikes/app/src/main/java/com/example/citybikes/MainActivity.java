package com.example.citybikes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.view.MenuItem;

import com.example.citybikes.ui.favorites.FavoritesFragment;
import com.example.citybikes.ui.map.MapFragment;
import com.example.citybikes.ui.list.ListFragment;
import com.google.android.material.navigation.NavigationView;


/**
 * Class that handles the functionality of the app on one activity. It holds the drawer menu
 * and its different (fragment) sections.
 *
 * Sources:
 *      vid 1: Fragment vs Activity: 'https://www.youtube.com/watch?v=4r8FvGADzF4'
 *      vid 2: Usar Navigation Drawer con Fragmentos: 'https://www.youtube.com/watch?v=W-Os-qa_t_8'
 *      vid 3: Crear un menú inferior con Button Navigation:'https://www.youtube.com/watch?v=pHNXlQXpi2s&t=580s'
 *
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    private double userLat;
    private double userLong;
    private boolean locationAllowed = false;

    /**
     * creates the activity and loads al the content
     *
     *  NOTE: might need to implement functions to handle the view if user orientation changes
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UI
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        getSupportFragmentManager().beginTransaction().add(R.id.content, new ListFragment()).commit();
        setTitle("List");

        //Setup toolbar
        setSupportActionBar(toolbar);

        toggle = setUpDrawerToggle();
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, this);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                userLat = location.getLatitude();
                userLong = location.getLongitude();
                locationAllowed = true;
            }
            catch (Exception e) {
                e.printStackTrace();
                locationAllowed = false;
            }
        }

    }

    /**
     * Method to keep code organized. It only handles the different references to open and close
     * the drawer
     * @return
     */
    private ActionBarDrawerToggle setUpDrawerToggle(){
        return new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
    }

    /**
     * Handles the action of keeping the menu open in case of switching
     * orientation
     * @param savedInstanceState
     */
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    /**
     * Handles different configuration changes when switching orientation view
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    /**
     * Complements the action of switching sections/menu options
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        selectItemNav(item);
        return true;
    }

    /**
     * Handles switching between different section/menu options
     * @param item
     */
    private void selectItemNav(MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch ((item.getItemId())){
            case R.id.nav_map:
                fragmentTransaction.replace(R.id.content, new MapFragment()).commit();
                break;
            case R.id.nav_favorites:
                fragmentTransaction.replace(R.id.content, new FavoritesFragment()).commit();

                break;
            case R.id.nav_list:
                fragmentTransaction.replace(R.id.content, new ListFragment()).commit();
                break;
        }
        setTitle(item.getTitle());
        drawerLayout.closeDrawers();
    }

    /**
     * Method used to handling the event of option menu i.e. which menu action
     * is triggered and what should be the outcome of that action
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public double getUserLong() {
        return userLong;
    }

    public double getUserLat() {
        return userLat;
    }

    public boolean getLocationAllowed() {
        return locationAllowed;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        userLat = location.getLatitude();
        userLong = location.getLongitude();
    }
}