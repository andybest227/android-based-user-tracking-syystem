package com.example.usertrackingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //launch the track self interface
        Button selfTracking = findViewById(R.id.track_self);

        //get the user name from
        Session session = new Session(this);
        username = session.getUsername();

        selfTracking.setOnClickListener(view -> {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        //launch user tracking interface
        Button userTracking = findViewById(R.id.track_users);
        userTracking.setOnClickListener(view ->{
            final  EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setHint("Enter Admin Access Code");
            input.setPadding(30, 20, 0, 20);
            input.setAllCaps(true);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Admin Authentication");
            builder.setView(input);
            builder.setPositiveButton("Authenticate", (dialogInterface, i) -> {
                Toast.makeText(HomeActivity.this, "Authenticating...", Toast.LENGTH_LONG).show();
                if (input.getText().toString().equals("12345")){
                    Intent intent = new Intent(HomeActivity.this, Admin.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(HomeActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                dialogInterface.cancel();
            });
            builder.show();

        });
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
            case R.id.logout:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.settings:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("About")
                        .setMessage(R.string.about)
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.cancel());
                AlertDialog alertAbout = alert.create();
                alertAbout.show();
                break;
            case R.id.exit:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Exit Application")
                        .setMessage("Are you sure you want to Exit?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            ActivityCompat.finishAffinity(this);
                        })
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

}