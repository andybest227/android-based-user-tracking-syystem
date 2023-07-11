package com.example.usertrackingsystem;

import androidx.annotation.NonNull;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.usertrackingsystem.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private DatabaseReference reference;
    private LocationManager manager;
    private final int MIN_TIME = 1000;  //1sec
    private final int MIN_DISTANCE = 1; // 1 meter
    private String username;
    Marker myMaker;
    MyLocation location;
    private double defaultLat;
    private double defaultLong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get the user name from preview screen
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");


        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        reference = FirebaseDatabase.getInstance().getReference().child(username);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        //working with actionBar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setActionBar(myToolbar);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        getLocationUpdates();

        readChanges();
        defaultLat = Double.parseDouble(loadArray("myArray")[0]);
        defaultLong = Double.parseDouble(loadArray("myArray")[1]);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    // menu items response
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("About")
                    .setMessage(R.string.about)
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.cancel());
                AlertDialog alertAbout = alert.create();
                alertAbout.show();
                break;
            case R.id.logout:
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                reference1.child(username).removeValue();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                break;
            case R.id.exit:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Exit Application")
                        .setMessage("Are you sure you want to Exit?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> ActivityCompat.finishAffinity(this))
                        .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            case R.id.help:
                AlertDialog.Builder alerted = new AlertDialog.Builder(this);
                alerted.setTitle("Instructions")
                        .setMessage(R.string.help_str)
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.cancel());
                AlertDialog alertAbout2 = alerted.create();
                alertAbout2.show();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
    private void readChanges() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    try {
                        location = snapshot.getValue(MyLocation.class);
                        if (location != null) {
                            Double[] userLoc = {location.getLatitude(), location.getLongitude()};
                            saveDefLoc(userLoc, "myArray");
                            myMaker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                        }
                    }catch (Exception e){
                        Toast.makeText(MapsActivity.this, "No or poor internet connection, loading user location...", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLocationUpdates() {
        if (manager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            }else if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            }else{
                Toast.makeText(this, "No Provided Enabled", Toast.LENGTH_SHORT).show();
            }
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocationUpdates();
            }else{
                Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng userlocation = new LatLng(defaultLat, defaultLong);
        myMaker = mMap.addMarker(new MarkerOptions().position(userlocation).title("Last location of "+username));
        //mMap.setMinZoomPreference(12);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userlocation));
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Session session = new Session(this);
        String currentUser = session.getUsername();
        if (currentUser.equals(username)){
            saveLocation(location);
        }
    }

    private void saveLocation(Location location) {
        reference.setValue(location);
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    //retrieve default location
    public String[] loadArray(String arrayName){
        SharedPreferences preferences = getSharedPreferences("defaultLoc", MODE_PRIVATE);
        int size = preferences.getInt(arrayName+"_size", 0);
        String[] array = new String[size];
        for (int i=0; i<size; i++)
            array[i] = preferences.getString(arrayName+"_"+i, null);
        return array;
    }
    //alter default location
    public void saveDefLoc(Double[] array, String arrayName){
        SharedPreferences sharedPreferences = getSharedPreferences("defaultLoc", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(arrayName +"_size", array.length);
        for (int i = 0; i<array.length; i++)
            editor.putString(arrayName +"_"+i, String.valueOf(array[i]));
        editor.apply();
    }
}