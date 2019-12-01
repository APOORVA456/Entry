package com.example.entrymanagement;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CheckInList extends AppCompatActivity {

    DatabaseReference dbref;
    ArrayList<Detail> details = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exit_checkout);

        ListView listView = findViewById(R.id.listView);
        final ListAdapter adapter = new ListAdapter(details,CheckInList.this);
        listView.setAdapter(adapter);

        dbref = FirebaseDatabase.getInstance().getReference();


        dbref.child("Checked_In_List").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Detail detail = new Detail(dataSnapshot.child("vname").getValue(String.class),
                        dataSnapshot.child("vphone").getValue(String.class),
                        dataSnapshot.child("vemail").getValue(String.class),
                        dataSnapshot.child("hname").getValue(String.class),
                        dataSnapshot.child("hphone").getValue(String.class),
                        dataSnapshot.child("hemail").getValue(String.class),
                        dataSnapshot.child("checkInTime").getValue(String.class),
                        dataSnapshot.child("address").getValue(String.class),
                        dataSnapshot.child("checkOutTime").getValue(String.class));
                details.add(detail);
                adapter.notifyDataSetChanged();
                Log.e("taggg",dataSnapshot.child("checkInTime").getValue(String.class)+" ");

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
