package com.example.food;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Customer_Home extends AppCompatActivity {

    DisplayAdaptor displayAdaptor;
    RecyclerView display;
    List<foodmodel> foodmodelList;
    DatabaseReference food_db;
    EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_customer_home);

        display= findViewById(R.id.display);
        search = findViewById(R.id.search_C);
        int number_columns = 2;
        display.setHasFixedSize(true);
        display.setLayoutManager(new GridLayoutManager(this, number_columns));


        foodmodelList = new ArrayList<>();
        displayAdaptor = new DisplayAdaptor( this, foodmodelList);
        display.setAdapter(displayAdaptor);
        food_db = FirebaseDatabase.getInstance().getReference("food");
        food_db.addListenerForSingleValueEvent(foodValueEventListener);

        BottomNavigationView bottomNavigationView = findViewById( R.id.bottom_cust_navigation);

        bottomNavigationView.setSelectedItemId(R.id.nav_cust_home);

        bottomNavigationView.setOnItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean result = false;
                switch (item.getItemId()) {

                    case R.id.nav_cust_home:
                        return true;

                    case R.id.nav_cust_profile:
                        startActivity( new Intent( getApplicationContext(), Profile.class ) );
                        overridePendingTransition( 0, 0 );
                        return true;

                }
                return false;
            }
        });




        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty()){

                    search(editable.toString());
                }
                else
                {
                    search("");
                }

            }



        });

    }
    //search 2

    private void search(String s) {
        ArrayList<foodmodel> filter = new ArrayList<>();
        for(foodmodel j: foodmodelList ){
            if (j.getFood_name().toLowerCase().contains(s.toLowerCase()))
            {
                filter.add(j);
            }
        }
        displayAdaptor.filterlist(filter);
    }
    ValueEventListener foodValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            foodmodelList.clear();
            if(dataSnapshot.exists())
            {
                for (DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    foodmodel food_model =  snapshot.getValue(foodmodel.class);
                    foodmodelList.add(food_model);

                }
                displayAdaptor.notifyDataSetChanged();

            }
            else
                {
                    Toast.makeText(getApplicationContext(), "No available food", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
}