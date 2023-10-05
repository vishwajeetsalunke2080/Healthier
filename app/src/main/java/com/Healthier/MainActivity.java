package com.Healthier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int totalsize = 0;
    RecyclerView rv;

    TextView blankV, calorieView;
    ProgressBar calorieBar;
    ImageView blankIV;
    FloatingActionButton fb;
    mainAdapter x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
//        blankV = (TextView) findViewById(R.id.blankText);
//        blankIV = (ImageView) findViewById(R.id.blankImage);

        calorieView = (TextView) findViewById(R.id.calorieView);
        calorieBar = (ProgressBar) findViewById(R.id.calorieBar);
        FirebaseRecyclerOptions<MainModel> options =
                new FirebaseRecyclerOptions.Builder<MainModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("food").orderByChild("dish"), MainModel.class)
                        .build();
        x = new mainAdapter(options);
        rv.setAdapter(x);
        fb = (FloatingActionButton) findViewById(R.id.addRecord);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddAction.class));
            }
        });
        sebar();
    }


    @Override
    protected void onStart() {
        super.onStart();
        x.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        x.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                txtSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                txtSearch(s);
                return false;
            }
        });
        sebar();
        return super.onCreateOptionsMenu(menu);
    }

    private void txtSearch(String str) {
        FirebaseRecyclerOptions<MainModel> options =
                new FirebaseRecyclerOptions.Builder<MainModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("food").orderByChild("dish").startAt(str).endAt(str + "~"), MainModel.class)
                        .build();

        mainAdapter y = new mainAdapter(options);
        y.startListening();
        rv.setAdapter(y);
    }

    public void sebar() {
        FirebaseDatabase.getInstance().getReference().child("food").orderByChild("calories").addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> s = snapshot.getChildren();
                for (DataSnapshot x : s) {
                    MainModel m = x.getValue(MainModel.class);
                    totalsize = totalsize + Integer.parseInt(m.calories);
                }
                calorieBar.setMax(250);
                if (totalsize > 250) {


                    NotificationChannel channel = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        channel = new NotificationChannel("My Notification","Notification", NotificationManager.IMPORTANCE_HIGH);
                    }
                    NotificationManager manager = getSystemService(NotificationManager.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        manager.createNotificationChannel(channel);
                    }


                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "My Notification");
                    builder.setContentTitle("Daily Calorie Limit Exceeded !");
                    builder.setContentText("Dear user you have exceeded your calorie target your current calorie count is "+totalsize);
                    builder.setSmallIcon(R.drawable.ic_launcher_foreground);
                    builder.setAutoCancel(true);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    notificationManager.notify(1, builder.build());
                }

                calorieBar.setProgress(totalsize);
                calorieView.setText(""+totalsize+" / "+calorieBar.getMax());
                if(totalsize>calorieBar.getMax()){ calorieView.setTextColor(getColor(R.color.red));}
                else{calorieView.setTextColor(getColor(R.color.white));}
                totalsize =0;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    //    private void checkState() {
//            FirebaseDatabase.getInstance().getReference("food").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                totalsize = (int)snapshot.getChildrenCount();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        if(totalsize==0)
//        {
//            rv.setVisibility(View.INVISIBLE);
//            blankIV.setVisibility(View.VISIBLE);
//            blankV.setVisibility(View.VISIBLE);
//        }
//        else
//        {
//            rv.setVisibility(View.VISIBLE);
//            blankV.setVisibility(View.INVISIBLE);
//            blankIV.setVisibility(View.INVISIBLE);
//            getApplication().onCreate();
//        }
//
//    }
}