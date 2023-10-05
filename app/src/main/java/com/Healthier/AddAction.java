package com.Healthier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class AddAction extends AppCompatActivity {

    EditText meal,dish,calories,img;
    Button addNew,cancelAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_action);

        meal = (EditText) findViewById(R.id.addMeal);
        dish = (EditText) findViewById(R.id.addDish);
        calories = (EditText) findViewById(R.id.addCalories);
        img = (EditText) findViewById(R.id.img);

        addNew = (Button)findViewById(R.id.addNewBtn);
        cancelAdd = (Button)findViewById(R.id.cancelAdd);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insert();
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        cancelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void insert()
    {
        Map<String,Object> map = new HashMap<>();
        map.put("meal",meal.getText().toString());
        map.put("dish",dish.getText().toString());
        map.put("calories",calories.getText().toString());
        map.put("imgSrc",img.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("food").push()
                .setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddAction.this, "Record added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddAction.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}