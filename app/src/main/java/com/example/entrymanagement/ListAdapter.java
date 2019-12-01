package com.example.entrymanagement;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class ListAdapter extends BaseAdapter {
    ArrayList<Detail> details;
    Context ctx;
    Detail currentDetail;
    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();


    public ListAdapter(ArrayList<Detail> details,Context ctx) {
        this.details = details;
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return details.size();
    }

    @Override
    public Detail getItem(int i) {
        return details.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, final ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View inflatedView;
        if(view == null)
            inflatedView = layoutInflater.inflate(R.layout.list_adapter,viewGroup,false);
        else
            inflatedView = view;


        currentDetail = getItem(i);
        final TextView tvVname = inflatedView.findViewById(R.id.tvVname);
        final TextView tvVemail = inflatedView.findViewById(R.id.tvVemail);
        final TextView tvVphone = inflatedView.findViewById(R.id.tvVphone);
        final TextView tvHname = inflatedView.findViewById(R.id.tvHname);
        TextView tvHemail = inflatedView.findViewById(R.id.tvHemail);
        TextView tvHphone = inflatedView.findViewById(R.id.tvHphone);
        final TextView tvCheckIn = inflatedView.findViewById(R.id.tvCheckIn);
        final TextView tvCheckOut = inflatedView.findViewById(R.id.tvCheckOut);
        final Button btnCheckOut = inflatedView.findViewById(R.id.btnCheckOutList);

        tvVname.setText(currentDetail.getVname());
        tvVemail.setText(currentDetail.getVemail());
        tvVphone.setText(currentDetail.getVphone());
        tvHname.setText(currentDetail.getHname());
        tvHemail.setText(currentDetail.getHemail());
        tvHphone.setText(currentDetail.getHphone());
        tvCheckIn.setText("Check In: "+currentDetail.getCheckInTime());
        tvCheckOut.setText(currentDetail.getCheckOutTime());

        if(tvCheckOut.getText().equals("Check Out: "))
            btnCheckOut.setVisibility(View.VISIBLE);
        else
            btnCheckOut.setVisibility(View.INVISIBLE);

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    String checkOutTime = currentDate +" At "+currentTime;
                    tvCheckOut.setText("Check Out: "+checkOutTime);
                    btnCheckOut.setVisibility(View.INVISIBLE);
                    //substring 10 is used to remove "Check In: "
                    String checkInTime = tvCheckIn.getText().toString().substring(10);
                    setCheckOutToFireBase(checkInTime,
                            "Check Out: "+checkOutTime);
                    checkForSmsPermission();

                    String subject = "Thanks For Your Visit";
                    String message = "Visitor: "+tvVname.getText().toString()+"\n"+
                            "Phone: "+tvVphone.getText().toString()+"\n"+
                            tvCheckIn.getText().toString()+"\n"+
                            "Check-out : "+checkOutTime+"\n"+
                            "Host: "+tvHname.getText().toString()+"\n"+
                            "Address:"+currentDetail.getAddress();

                SmsAsyncTask smsAsyncTask = new SmsAsyncTask();
                smsAsyncTask.execute(new String[]{tvVphone.getText().toString(),message});
                GMailAsyncTask gMailAsyncTask = new GMailAsyncTask(ctx,tvVemail.getText().toString(),subject,message);
                gMailAsyncTask.execute();


                }
            });


        return inflatedView;
    }

    private void setCheckOutToFireBase(final String checkIn, final String CheckOut){
        dbref.child("Checked_In_List").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String check = dataSnapshot.child("checkInTime").getValue(String.class);
                if(check!=null && check.equals(checkIn)){
                    Map<String,Object> map = new HashMap<String, Object>() {};
                    map.put("checkOutTime",CheckOut);
                    dataSnapshot.getRef().updateChildren(map);
                }

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

    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(ctx,
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) ctx,
                    new String[]{Manifest.permission.SEND_SMS},
                    100);
        }
    }

    private class SmsAsyncTask extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String phone = strings[0];
            String message = strings[1];

            if((ActivityCompat.checkSelfPermission(ctx,
                    Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)){
                Intent intent = new Intent(ctx.getApplicationContext(),ctx.getClass());
                PendingIntent pi = PendingIntent.getActivity(ctx.getApplicationContext(),0,intent,0);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone,null,message,pi,null);
                return "SMS sent successfully to "+phone;
            }
            else
                return "Please enable SMS Permission in App Settings";

        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(ctx,aVoid+" ",Toast.LENGTH_SHORT).show();
        }
    }
}
