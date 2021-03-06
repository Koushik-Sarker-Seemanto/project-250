package com.koushik.health_kit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoctorScheduleCheck extends AppCompatActivity {

    private static  String TAG="DoctorScheduleEdit";
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private DatabaseReference doctorReference;


    private TextView dayNameText;
    String doctorUID;
    public String Day;


    private FirebaseAuth patientAuth;

    private RecyclerView recyclerView;
    List<DoctorScheduleEditCardview> patientList;
    DoctorScheduleCheckCardviewadapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_schedule_check);

        drawerLayout = (DrawerLayout) findViewById(R.id.dpDrawerId);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.Open,R.string.Close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.dpNavigationViewId);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();


                if (id == R.id.menuBMIbtnId) {
                    Toast.makeText(DoctorScheduleCheck.this, "BMI CLICKED", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(getApplicationContext(), BmiCalculator.class);
                    startActivity(intent1);
                }
                else if (id == R.id.menuPrescriptionbtnId)
                {
                    startActivity(new Intent(getApplicationContext(), MyPrescriptionList.class));
                }
                else if (id == R.id.menuSearchbtnId)
                {
                    Intent intent = new Intent(getApplicationContext(), SearchDoctor.class);
                    startActivity(intent);
                }
                else if (id == R.id.menuDiabetesbtnId)
                {
                    Toast.makeText(DoctorScheduleCheck.this, "Diabetes CLICKED", Toast.LENGTH_SHORT).show();
                    Intent intent2 = new Intent(getApplicationContext(), DiabetesCalculator.class);
                    startActivity(intent2);
                }
                else if (id == R.id.menuAboutustnId)
                {
                    Toast.makeText(DoctorScheduleCheck.this, "About Us CLICKED", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), AboutUs.class));
                }
                else if (id == R.id.menuLogoutbtnId)
                {
                    Toast.makeText(DoctorScheduleCheck.this, "Log Out Clicked", Toast.LENGTH_SHORT).show();
                    finish();
                    patientAuth.signOut();
                }
                else if (id == R.id.menuEmergencybtnId)
                {
                    startActivity(new Intent(getApplicationContext(), EmergencyMapsActivity.class));
                }
                return true;
            }
        });

        ///Drawer & NavigationBar ends.

        patientAuth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getExtras();
        if(bundle==null)
        {
            return;
        }
        String dayName = (String) bundle.get("day");
        doctorUID = (String) bundle.get("doctorUID");

        Log.d(TAG, "getExtra: "+dayName);
        dayNameText = (TextView) findViewById(R.id.DaynameId);
        dayNameText.setText(dayName);
        Day = dayName;

        doctorReference = FirebaseDatabase.getInstance().getReference().child("Doctors").child(doctorUID);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initializing the productlist
        patientList = new ArrayList<>();

        //creating recyclerview adapter
        adapter = new DoctorScheduleCheckCardviewadapter(this,patientList,Day,doctorUID);

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);

        doctorReference = FirebaseDatabase.getInstance().getReference("Doctors").child(doctorUID).child("schedule").child(dayNameText.getText().toString());
        doctorReference.addListenerForSingleValueEvent(valueEventListener);

    }


    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            patientList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DoctorScheduleEditCardview patientlist = snapshot.getValue(DoctorScheduleEditCardview.class);
                    patientList.add(patientlist);
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item) ||super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            startActivity(new Intent(getApplicationContext(),DoctorProfileFromPatient.class));
            //super.onBackPressed();
        }
    }
}
