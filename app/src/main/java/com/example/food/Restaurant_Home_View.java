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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Restaurant_Home_View extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Restaurant_View_Adaptor restaurantViewAdaptor;
    private List<foodmodel> foodmodelList;
    private EditText search;
    private FirebaseUser fuser;
    private String userId;
  //  private custompb pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_restaurant_recycler_view);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        userId = fuser.getUid();
        recyclerView = findViewById(R.id.recycler_categories);
        recyclerView.setHasFixedSize(true);
        int number_columns = 2;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, number_columns));

        foodmodelList = new ArrayList<>();
        restaurantViewAdaptor = new Restaurant_View_Adaptor (this, foodmodelList);
        recyclerView.setAdapter(restaurantViewAdaptor);




//search end

        BottomNavigationView bottomNavigationView = findViewById( R.id.bottom_navigation );

        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean result = false;
                switch (item.getItemId()) {
                    case R.id.nav_upload:
                        startActivity( new Intent( getApplicationContext(), Food_Upload.class ) );
                        overridePendingTransition( 0, 0 );
                        return true;

                    case R.id.nav_home:
                        return true;

                    case R.id.nav_profile:
                        startActivity( new Intent( getApplicationContext(), R_Profile.class ) );
                        overridePendingTransition( 0, 0 );
                        return true;

                }
                return false;
            }
        });





        //   pb.show();

        Query query = FirebaseDatabase.getInstance().getReference("food")
                .orderByChild("ownerid") // column
                .equalTo(userId);

        query.addListenerForSingleValueEvent(companyjobvalueEventListener);

        //Search
        search = findViewById( R.id.search );

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
        restaurantViewAdaptor.filterlist(filter);
    }
    //search end
    ValueEventListener companyjobvalueEventListener = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            foodmodelList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    foodmodel jobModel = snapshot.getValue(foodmodel.class);
                    jobModel.setOwnerid(snapshot.getKey());
                    foodmodelList.add(jobModel);
                }

                restaurantViewAdaptor.notifyDataSetChanged();
              //  pb.dismiss();
            }
            else
            {
              //  pb.dismiss();
                Toast.makeText(getApplicationContext(),"No Available foods",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

          //  pb.dismiss();

            startActivity(new Intent(Restaurant_Home_View.this, Restaurant_Home_View.class));
        }


    };
}