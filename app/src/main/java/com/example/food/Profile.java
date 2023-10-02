package com.example.food;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class Profile extends AppCompatActivity {

    TextView profile_fullname, profile_email, email, phonenumber;

    ImageView propic;
    Button update, logout;
    EditText fullname, password;

    private Bitmap imgHolder;
    private Uri path;
    ProgressDialog pd;
    DatabaseReference ref;
    StorageReference sref;
    StorageTask stask;
    FirebaseAuth auth;
    FirebaseUser fuser;
    String uid;
    String upcimage;

    com.example.food.customer_profile_model profile_model = new com.example.food.customer_profile_model();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 222 && resultCode == RESULT_OK)
        {
            path = data.getData();
            propic.setImageURI(path);

            try {
                imgHolder = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                propic.setImageBitmap(imgHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);
        update=findViewById(R.id.update);
        logout= findViewById(R.id.logout);
        pd = new ProgressDialog(Profile.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading...");
        fuser=FirebaseAuth.getInstance().getCurrentUser();
        uid=fuser.getUid();
        sref= FirebaseStorage.getInstance().getReference().child("profile picture");
        ref = FirebaseDatabase.getInstance().getReference("User");
        auth=FirebaseAuth.getInstance();
        BottomNavigationView bottomNavigationView = findViewById( R.id.nav_bottom_customer );

        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean result = false;
                switch (item.getItemId()) {
                    case R.id.nav_cust_home:
                        startActivity( new Intent( getApplicationContext(), Customer_Home.class ) );
                        overridePendingTransition( 0, 0 );
                        return true;

                    case R.id.nav_cust_profile:
                        return true;


                }
                return false;
            }
        });


        logout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(Profile.this, Login_As.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        } );
        //Popup
        //popup end

        profile_fullname=findViewById(R.id.profile_name);
        profile_email=findViewById(R.id.profile_email);
        propic = findViewById(R.id.propic);
        propic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 222);
            }
        });

        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        phonenumber = findViewById(R.id.phonenumber);
        password = findViewById(R.id.password);


        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String Uid= user.getUid();
        ref= FirebaseDatabase.getInstance().getReference("User");
        ref.child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                profile_model=snapshot.getValue(com.example.food.customer_profile_model.class);

               profile_fullname.setText(profile_model.getFname().toString());
               profile_email.setText(profile_model.getEmail().toString());

                fullname.setText(profile_model.getFname().toString());
                email.setText(profile_model.getEmail().toString());
                phonenumber.setText(profile_model.getPhone().toString());
                password.setText(profile_model.getPass().toString());

                upcimage =  profile_model.getPropic().toString();
                Picasso.get().load(upcimage).into( propic );
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(path != null)
                {
                    if(stask !=null && stask.isInProgress()) // prevent button clicked multiple times
                    {
                        Toast.makeText(Profile.this,"Updating in progress",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        pd.show();
                        Remove();
                    }
                }

                else
                {


                    Query query = ref.orderByKey().equalTo(uid);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull  DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                //update data

                                ds.getRef().child("Fname").setValue(fullname.getText().toString().trim());
                                ds.getRef().child("Phone").setValue(phonenumber.getText().toString().trim());
                                ds.getRef().child("Email").setValue(email.getText().toString().trim());
                                ds.getRef().child("Pass").setValue(password.getText().toString().trim());



                            }
                            pd.dismiss();
                            Toast.makeText(Profile.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(Profile.this, Profile.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            pd.dismiss();
                            Toast.makeText(Profile.this,error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void Remove() {
        StorageReference imageref = FirebaseStorage.getInstance().getReferenceFromUrl(upcimage);
        imageref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //deleted
                uploadNewimage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                //failed get and show error
                Toast.makeText(Profile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadNewimage() {
        if(path != null)
        {
            String mDownload = auth.getCurrentUser().getUid();
            StorageReference storeFile = sref.child(mDownload);
            stask= storeFile.putFile(path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    editprofile();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(Profile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(this,"No file Image selected",Toast.LENGTH_SHORT).show();
        }
    }

    private void editprofile() {
        //getdownload url here
        sref.child(auth.getCurrentUser().getUid()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                //get download url

                String downloadurl = task.getResult().toString();

                Query q = ref.orderByKey().equalTo(auth.getCurrentUser().getUid());
                q.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            //update data
                            ds.getRef().child("Fname").setValue(fullname.getText().toString().trim());
                            ds.getRef().child("Phone").setValue(phonenumber.getText().toString().trim());
                            ds.getRef().child("Email").setValue(email.getText().toString().trim());
                            ds.getRef().child("Pass").setValue(password.getText().toString().trim());
                            ds.getRef().child("propic").setValue(downloadurl);


                        }
                        pd.dismiss();
                        Toast.makeText(Profile.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        //tart the new activity after update
                        Intent i = new Intent(Profile.this, Profile.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        pd.dismiss();
                        Toast.makeText(Profile.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });



    }
}
