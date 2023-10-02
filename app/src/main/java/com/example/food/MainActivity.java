package com.example.food;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Switch open;
    FirebaseAuth foodAuth;
    private FirebaseDatabase minlesraDatabase;
    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        foodAuth = FirebaseAuth.getInstance();
        checklogin();
//        open = findViewById(R.id.open);
//        open.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Intent intent = new Intent(MainActivity.this, Log_In.class);
//                                        startActivity(intent);
//
//                                    }
//                                });



    /*    new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (foodAuth.getUid()==null) {
                    Intent intent = new Intent( MainActivity.this, Login_As.class );
                    startActivity( intent );
                    finish();
                }
                else{
                    Intent intentlogin = new Intent( MainActivity.this, Display.class );
                    startActivity( intentlogin );
                    finish();
                }
            }
        },MainActivity);*/

    }
        public void checklogin(){
            FirebaseUser fuser = foodAuth.getCurrentUser();
            if (fuser != null) {
                //there is some one loged  in
                uid = fuser.getUid();
                minlesraDatabase = FirebaseDatabase.getInstance();
                FirebaseDatabase firebaseD = FirebaseDatabase.getInstance();
                firebaseD.getReference().child("User").child(uid).child("usertype").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int userRole = snapshot.getValue(Integer.class);

                        if (userRole == 0) {


                            Intent i = new Intent(MainActivity.this, Customer_Home.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);

                        } else {


                            Intent i = new Intent(MainActivity.this, Restaurant_Home_View.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        startActivity(new Intent(getApplicationContext(), Log_In.class));
                    }
                });

            }
            else
            {
                // no one logged in
                Intent i = new Intent(MainActivity.this, Log_In.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        }

    }
