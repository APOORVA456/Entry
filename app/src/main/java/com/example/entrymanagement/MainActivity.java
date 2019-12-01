package com.example.entrymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    int check = 0;
    String vname,vemail,vphone,hname,hemail,hphone,address;
    DatabaseReference dbref ;
    Detail odetail;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkForSmsPermission();
        final TextInputEditText vName = findViewById(R.id.vName);
        final TextInputEditText vEmail = findViewById(R.id.vEmail);
        final TextInputEditText vPhone = findViewById(R.id.vPhone);
        final TextInputEditText hName = findViewById(R.id.hName);
        final TextInputEditText hEmail = findViewById(R.id.hEmail);
        final TextInputEditText hPhone = findViewById(R.id.hPhone);
        final TextInputEditText etaddress = findViewById(R.id.addressVisited);
        TextView tvVistor = findViewById(R.id.tvVistor);
        tvVistor.setPaintFlags(tvVistor.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        TextView tvHost = findViewById(R.id.tvHost);
        tvHost.setPaintFlags(tvHost.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        Button btnCheckIn = findViewById(R.id.btnCheckIn);
        dbref = FirebaseDatabase.getInstance().getReference().child("Checked_In_List");
        odetail = new Detail();

        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vName.getText()!=null)
                    vname = vName.getText().toString();
                if(vEmail.getText()!=null)
                    vemail = vEmail.getText().toString();
                if(vPhone.getText()!=null)
                    vphone = vPhone.getText().toString();
                if(hName.getText()!=null)
                    hname = hName.getText().toString();
                if(hEmail.getText()!=null)
                    hemail = hEmail.getText().toString();
                if(hPhone.getText()!=null)
                    hphone = hPhone.getText().toString();
                if(etaddress.getText()!=null)
                    address = etaddress.getText().toString();

                if(vname.isEmpty() || vemail.isEmpty() || vphone.isEmpty()
                        || hname.isEmpty()|| hemail.isEmpty()|| hphone.isEmpty() ||address.isEmpty()){
                    Toast.makeText(MainActivity.this,"First Enter the details",Toast.LENGTH_SHORT).show();
                }
                else {
                    sendDetailToHost(vname,vemail,vphone,hname,hemail,hphone,address);
                    check = 1;
                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.checkout_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== R.id.menu_checkout)
            startActivity(new Intent(MainActivity.this, CheckInList.class));
        return super.onOptionsItemSelected(item);
    }

    public void sendDetailToHost( String vname, String vemail, String vphone,
                                 String hname,  String hemail, String hphone,
                                 String address){

        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String subject = "New Visitor : " +vname;
        String message = "Visitor Name : "+vname+"\n" +
                "Email : "+vemail+"\n"+
                "Ph.No : "+vphone+"\n"+
                "Visited On : "+currentDate +" At "+currentTime;

        odetail.setVname(vname);
        odetail.setVemail(vemail);
        odetail.setVphone(vphone);
        odetail.setHname(hname);
        odetail.setHemail(hemail);
        odetail.setHphone(hphone);
        odetail.setCheckInTime(currentDate+" At "+currentTime);
        odetail.setAddress(address);
        odetail.setCheckOutTime("Check Out: ");
        dbref.push().setValue(odetail);

        runMultipleAsyncTask(hemail,hphone,message,subject);

    }

    private void runMultipleAsyncTask(String email,String phone,String msg,String sub){
        SmsAsyncTask smsAsyncTask = new SmsAsyncTask();
        smsAsyncTask.execute(new String[]{phone,msg});
        GMailAsyncTask gMailAsyncTask = new GMailAsyncTask(MainActivity.this,email,sub,msg);
        gMailAsyncTask.execute();
    }

    private class SmsAsyncTask extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String phone = strings[0];
            String message = strings[1];

            if((ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)){
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),0,intent,0);
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
            Toast.makeText(MainActivity.this,aVoid+" ",Toast.LENGTH_SHORT).show();
        }
    }


    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);

        }
    }

}
