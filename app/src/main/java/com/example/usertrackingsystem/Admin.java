package com.example.usertrackingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Admin extends AppCompatActivity {
    private ListView mListView;
    ArrayList<String> usersArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        mListView = findViewById(R.id.active_users);
        usersArrayList = new ArrayList<>();
        initializeListView();

        mListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(Admin.this, MapsActivity.class);
            intent.putExtra("username", usersArrayList.get(i));
            startActivity(intent);
        });
    }

    private void initializeListView() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, usersArrayList);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    String parent = childSnapshot.getKey();
                    usersArrayList.add(parent);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mListView.setAdapter(adapter);
    }
}