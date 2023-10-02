package com.example.food;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Restaurant_Registration extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseDatabase fooddatabase;
    DatabaseReference userReference;
    FirebaseStorage foodStorage;
    StorageReference storageReference;
    EditText fullname, email, phonenumber, password, location;
    CircleImageView profile_image;
    ProgressBar register_progressbar;
    TextView signin;
    private Uri imgPath;

    private Bitmap imgToStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_restaurant_registration);

        profile_image = findViewById( R.id.profile_image );
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 222);
            }
        });

        signin = findViewById(R.id.signin_txt);
        register_progressbar =findViewById(R.id.register_progressbar);
//        signin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Restaurant_Registration.this,R_Log_In.class);
//                startActivity(intent);
//            }
//        });

        profile_image = findViewById( R.id.profile_image );
        fullname = (EditText) findViewById(R.id.fullname);
        email = (EditText) findViewById(R.id.email);
        location = (EditText) findViewById(R.id.location);
        phonenumber = findViewById(R.id.phonenumber);
        password = (EditText) findViewById(R.id.password);
        Button register =(Button) findViewById(R.id.register);

//image reg
        foodStorage=FirebaseStorage.getInstance();
        storageReference=foodStorage.getReference("profile picture");
        //end

        mAuth = FirebaseAuth.getInstance();
        fooddatabase = FirebaseDatabase.getInstance();
        DatabaseReference userReference;

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    private void register() {

        String full_name =fullname.getText().toString().trim();
        String E_mail =email.getText().toString().trim();
        String phone =phonenumber.getText().toString().trim();
        String pass =password.getText().toString().trim();

        if(full_name.isEmpty()){
            fullname.setError("Full Name required");
            fullname.requestFocus();
            return;
        }

        if(E_mail.isEmpty()){
            email.setError("email required");
            email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(E_mail).matches()){
            email.setError("please provide valid email");
            email.requestFocus();
            return;
        }

        if(phone.isEmpty()){
            phonenumber.setError("Phone Number required");
            phonenumber.requestFocus();
            return;
        }

        if(pass.isEmpty()){
            password.setError("Password required");
            password.requestFocus();
            return;
        }

        if(pass.length()<6){
            password.setError("Password must be more than 6!");
            password.requestFocus();
            return;
        }
        if (imgPath==null){
            Toast.makeText( getApplicationContext(), "Profile Picture is required",Toast.LENGTH_LONG ).show();
            return;
        }
        register_progressbar.setVisibility(View.VISIBLE);
        //  create user
        mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        StorageReference storeFile = storageReference.child( mAuth.getCurrentUser().getUid() );
                        storeFile.putFile( imgPath ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference.child( mAuth.getCurrentUser().getUid() ).getDownloadUrl().addOnCompleteListener( new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        String url=task.getResult().toString();
                                        Restaurant_Profile_model profilemodel = new Restaurant_Profile_model(url, fullname.getText().toString().trim(),email.getText().toString().trim(),location.getText().toString().trim(),phonenumber.getText().toString().trim(),password.getText().toString().trim(),1);
                                        userReference=fooddatabase.getReference("User");
                                        userReference.child(mAuth.getCurrentUser().getUid()).setValue(profilemodel) .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                register_progressbar.setVisibility(View.GONE);
                                                Toast.makeText(Restaurant_Registration.this,"User registered successfully",Toast.LENGTH_SHORT).show();

                                              //  startActivity(new Intent(getApplicationContext(),R_Log_In.class));


                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                register_progressbar.setVisibility(View.GONE);
                                                Toast.makeText(getApplicationContext(),"Failed to register user because"+e,Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                } );
                            }
                        } );




                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        register_progressbar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 222 && resultCode == RESULT_OK)
        {
            imgPath = data.getData();
            profile_image.setImageURI(imgPath);

            try {
                imgToStore = MediaStore.Images.Media.getBitmap(getContentResolver(),imgPath);
                profile_image.setImageBitmap(imgToStore);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}