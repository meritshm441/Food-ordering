package com.example.food;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
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

public class R_Profile extends AppCompatActivity {



    ImageView propic;
    Button update, logout;
    TextView profile_fullname, profile_email;
    EditText fullname, password, email, phonenumber, location;

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


    Restaurant_Profile_model profile_model = new Restaurant_Profile_model();

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
        setContentView(R.layout.activity_rprofile);
        update=findViewById(R.id.update);
        logout = findViewById(R.id.log_out);
        pd = new ProgressDialog(R_Profile.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading...");
        fuser=FirebaseAuth.getInstance().getCurrentUser();
        uid=fuser.getUid();
        sref= FirebaseStorage.getInstance().getReference().child("profile picture");
        ref = FirebaseDatabase.getInstance().getReference("User");
        auth=FirebaseAuth.getInstance();


        logout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(R_Profile.this, Login_As.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        } );
        //Popup
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        BottomNavigationView bottomNavigationView = findViewById( R.id.nav_bottom);

        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean result = false;
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity( new Intent( getApplicationContext(), Restaurant_Home_View.class ) );
                        overridePendingTransition( 0, 0 );
                        return true;

                    case R.id.nav_profile:
                        return true;

                    case R.id.nav_upload:
                        startActivity( new Intent( getApplicationContext(), Food_Upload.class ) );
                        overridePendingTransition( 0, 0 );
                        return true;

                }
                return false;
            }
        });


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

        fullname = findViewById(R.id.name);
        email = findViewById(R.id.R_email);
        phonenumber = findViewById(R.id.rephone);
        location=findViewById(R.id.relocation);
        password = findViewById(R.id.repass);


        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String Uid= user.getUid();
        ref= FirebaseDatabase.getInstance().getReference("User");
        ref.child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                profile_model=snapshot.getValue(Restaurant_Profile_model.class);

//                profile_fullname.setText(profile_model.getRestaurant_name().toString());
//                profile_email.setText(profile_model.getEmail().toString());
//
                fullname.setText(profile_model.getFull_Name().toString());
                email.setText(profile_model.getEmail().toString());
                location.setText(profile_model.getLocation().toString());
                phonenumber.setText(profile_model.getPhone_Number().toString());
                password.setText(profile_model.getPassword().toString());

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
                        Toast.makeText(R_Profile.this,"Updating in progress",Toast.LENGTH_SHORT).show();
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
                                ds.getRef().child("full_Name").setValue(fullname.getText().toString().trim());
                                ds.getRef().child("email").setValue(email.getText().toString().trim());
                                ds.getRef().child("location").setValue(location.getText().toString().trim());
                                ds.getRef().child("phone_Number").setValue(phonenumber.getText().toString().trim());
                                ds.getRef().child("password").setValue(password.getText().toString().trim());



                            }
                            pd.dismiss();
                            Toast.makeText(R_Profile.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(R_Profile.this, R_Profile.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            pd.dismiss();
                            Toast.makeText(R_Profile.this,error.getMessage(),Toast.LENGTH_LONG).show();
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
                Toast.makeText(R_Profile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(R_Profile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
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
                            ds.getRef().child("full_Name").setValue(fullname.getText().toString().trim());
                            ds.getRef().child("email").setValue(email.getText().toString().trim());
                            ds.getRef().child("location").setValue(location.getText().toString().trim());
                            ds.getRef().child("phone_Number").setValue(phonenumber.getText().toString().trim());
                            ds.getRef().child("password").setValue(password.getText().toString().trim());

                            ds.getRef().child("propic").setValue(downloadurl);


                        }
                        pd.dismiss();
                        Toast.makeText(R_Profile.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        //tart the new activity after update
                        Intent i = new Intent(R_Profile.this, R_Profile.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        pd.dismiss();
                        Toast.makeText(R_Profile.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });



    }
}
