package com.example.food;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class Food_Upload extends AppCompatActivity {

    EditText desc, food_name, price;
    ImageView upload_picture;
    TextView email, full_name, phone;
    Button upload;
    private Bitmap imgToStore;
    private Uri imgPath;
    FirebaseDatabase Rdata;
    DatabaseReference ref;
    FirebaseStorage foodDatabase;
    StorageReference storageReference;
    ProgressDialog pd;

    private FirebaseAuth ownerauth;
    private String oid, ownerid;

    Restaurant_Profile_model profile_model=new Restaurant_Profile_model();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_food_upload);

        pd = new ProgressDialog(Food_Upload.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading...");

        ownerauth=FirebaseAuth.getInstance();

        desc = findViewById(R.id.description);
        food_name = findViewById( R.id.food_name);
        price = findViewById(R.id.price);
        upload_picture = findViewById(R.id.upload_Picture);
        upload = findViewById(R.id.upload_button);
        email=findViewById(R.id.email);
        full_name=findViewById(R.id.fullname);
        phone=findViewById(R.id.phone);

        BottomNavigationView bottomNavigationView = findViewById( R.id.navigation_bottom );

        bottomNavigationView.setSelectedItemId(R.id.nav_upload);

        bottomNavigationView.setOnItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean result = false;
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity( new Intent( getApplicationContext(), Restaurant_Home_View.class ) );
                        overridePendingTransition( 0, 0 );
                        return true;

                    case R.id.nav_upload:
                        return true;

                    case R.id.nav_profile:
                        startActivity( new Intent( getApplicationContext(), R_Profile.class ) );
                        overridePendingTransition( 0, 0 );
                        return true;

                }
                return false;
            }
        });




        Bundle intent = getIntent().getExtras();
        if(intent != null){

            ownerid = intent.getString("Owner ID");

            food_name.setText(intent.getString("Food Name").toString());
            desc.setText(intent.getString("Description").toString());
            price.setText(intent.getString("Price").toString());
            Picasso.get().load(intent.getString("Food Image")).into(upload_picture);


            upload.setText("save");
        }

        Rdata=FirebaseDatabase.getInstance();
        ref=Rdata.getReference("food");
        foodDatabase=FirebaseStorage.getInstance();
        storageReference=foodDatabase.getReference("food pic");
        upload_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picker = new Intent(Intent.ACTION_PICK);
                picker.setType("image/*");
                startActivityForResult(picker,222);

            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (upload.getText().equals("Upload")){
                    String food = food_name.getText().toString().trim();
                    String Desc = desc.getText().toString().trim();
                    String pri = price.getText().toString().trim();
                    if (food.isEmpty()){
                        food_name.setError("Food Name is Required!");
                        food_name.requestFocus();
                        return;
                    }

                    if (Desc.isEmpty()){
                        desc.setError("Description is required!");
                        desc.requestFocus();
                        return;
                    }

                    if (pri.isEmpty()){
                        price.setError("Description is required!");
                        price.requestFocus();
                        return;
                    }
                    if (imgPath == null )
                    {
                        Toast.makeText(getApplicationContext(), "Image is required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    pd.show();
                    StorageReference food_store = storageReference.child(food_name.getText().toString());
                    food_store.putFile(imgPath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageReference.child(food_name.getText().toString()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String download = task.getResult().toString();
                                    foodmodel model = new foodmodel(full_name.getText().toString(), email.getText().toString(), food_name.getText().toString(),
                                            desc.getText().toString(),price.getText().toString(),phone.getText().toString(),  download, ownerauth.getCurrentUser().getUid());
                                    ref.push().setValue(model);

                                    Toast.makeText(getApplicationContext(), "successfull", Toast.LENGTH_LONG).show();
                                    pd.dismiss();

                                    Intent intent = new Intent(Food_Upload.this, Customer_Home.class);
                                    startActivity(intent);

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Food_Upload.this, Food_Upload.class);
                            startActivity(intent);
                        }
                    });
                }
                else {
                    pd.show();
                    Query query = ref.orderByKey().equalTo(ownerid);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds: snapshot.getChildren()){

                                //update data
                                ds.getRef().child("food_name").setValue(food_name.getText().toString());
                                ds.getRef().child("desc").setValue(desc.getText().toString());
                                ds.getRef().child("price").setValue(price.getText().toString());


                                pd.dismiss();

                                Toast.makeText(Food_Upload.this,"Updated Successfully",Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(Food_Upload.this, Customer_Home.class));
                                finish();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Food_Upload.this, Food_Upload.class));                        }
                    });
                }
            }
        });

        full_name=findViewById(R.id.fullname);
        phone=findViewById(R.id.phone);
        email=findViewById(R.id.email);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String Uid= user.getUid();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("User");
        ValueEventListener valueEventListener = ref.child( Uid ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                profile_model = snapshot.getValue( Restaurant_Profile_model.class );


                full_name.setText( profile_model.getFull_Name().toString() );
                phone.setText( profile_model.getPhone_Number().toString() );
                email.setText( profile_model.getEmail().toString() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });


    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 222 && resultCode == RESULT_OK)
        {
            imgPath = data.getData();
            upload_picture.setImageURI(imgPath);

            try {
                imgToStore = MediaStore.Images.Media.getBitmap(getContentResolver(),imgPath);
                upload_picture.setImageBitmap(imgToStore);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}