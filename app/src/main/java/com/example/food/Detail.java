package com.example.food;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Detail extends AppCompatActivity {

    TextView food_name, Restaurant_name, Email, Location, phone, Desc, price;
    ImageView food_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail);
       DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int )(width*0.8),(int )(height*0.7));

        food_name = findViewById(R.id.Food_Name1);
        Restaurant_name = findViewById(R.id.Restaurant_name1);
//        Location = findViewById(R.id.location1);
        Email = findViewById(R.id.Email1);
        phone = findViewById(R.id.Phone_Number);
        Desc = findViewById(R.id.Desc);
        price = findViewById(R.id.Price1);
        food_image = findViewById(R.id.food_image);


        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            Restaurant_name.setText(intent.getString("Restaurant Name"));
            Email.setText(intent.getString("Email"));
            food_name.setText(intent.getString("Food Name"));
            phone.setText(intent.getString("Phone Number"));
            Desc.setText(intent.getString("Description"));
            price.setText(intent.getString("Price"));
            Picasso.get().load(intent.getString("Food Image").toString()).error(R.drawable.ic_launcher_background).fit().centerCrop().into(food_image);



        }
    }
}
