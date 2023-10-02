package com.example.food;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class Register extends AppCompatActivity {

    private FirebaseAuth rAuth;
    FirebaseDatabase food_delivery_db;
    DatabaseReference userreference;
    FirebaseStorage food_delivery_storage;
    StorageReference storageReference;
    EditText Fname, Email, Phone, Pass;
    TextView signIn;

    private Uri imgPath;
    CircleImageView profile_image;
    private CircleImageView profile_picture;
    private Bitmap imgToStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_register);

        Fname = findViewById(R.id.txtFName);
        Email = findViewById(R.id.txtEmail);
        Phone = findViewById(R.id.txtphone);
        Pass = findViewById(R.id.txtpassword);
        signIn = findViewById(R.id.register_customer);
        Button signup = findViewById(R.id.signup_button);

        profile_image = findViewById( R.id.profile_image );
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 222);
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Log_In.class);
                startActivity(intent);
            }
        });
        //image reg
        food_delivery_storage= FirebaseStorage.getInstance();
        storageReference=food_delivery_storage.getReference("profile picture");
        //end

        rAuth = FirebaseAuth.getInstance();
        food_delivery_db = FirebaseDatabase.getInstance();
        DatabaseReference userReference;

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    private void register() {

        String full_name =Fname.getText().toString().trim();
        String E_mail =Email.getText().toString().trim();
        String phone =Phone.getText().toString().trim();
        String pass =Pass.getText().toString().trim();

        if(full_name.isEmpty()){
            Fname.setError("Full Name required");
            Fname.requestFocus();
            return;
        }

        if(E_mail.isEmpty()){
            Email.setError("email required");
            Email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(E_mail).matches()){
            Email.setError("please provide valid email");
            Email.requestFocus();
            return;
        }

        if(phone.isEmpty()){
            Phone.setError("Phone Number required");
            Phone.requestFocus();
            return;
        }

        if(pass.isEmpty()){
            Pass.setError("Password required");
            Pass.requestFocus();
            return;
        }

        if(pass.length()<6){
            Pass.setError("Password must be more than 6!");
            Pass.requestFocus();
            return;
        }
        if (imgPath==null){
            Toast.makeText( getApplicationContext(), "Profile Picture is required",Toast.LENGTH_LONG ).show();
            return;
        }
        rAuth.createUserWithEmailAndPassword(Email.getText().toString().trim(), Pass.getText().toString().trim())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {


                        StorageReference storeFile = storageReference.child( rAuth.getCurrentUser().getUid() );
                        storeFile.putFile( imgPath ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference.child( rAuth.getCurrentUser().getUid() ).getDownloadUrl().addOnCompleteListener( new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        String url=task.getResult().toString();
                                        com.example.food.customer_profile_model profilemodel = new com.example.food.customer_profile_model(url, Fname.getText().toString().trim(),Email.getText().toString().trim(),Phone.getText().toString().trim(),Pass.getText().toString().trim(),0);
                                        userreference=food_delivery_db.getReference("User");
                                        userreference.child(rAuth.getCurrentUser().getUid()).setValue(profilemodel) .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(Register.this,"User registered successfully",Toast.LENGTH_SHORT).show();

                                                startActivity(new Intent(getApplicationContext(), Log_In.class));


                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
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