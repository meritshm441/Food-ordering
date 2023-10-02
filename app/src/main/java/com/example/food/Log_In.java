package com.example.food;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Log_In extends AppCompatActivity {

    EditText email, password;
    ProgressBar login_progressbar;
    TextView login;
    TextView signup;
    TextView forgetpassword;
    FirebaseAuth mAuth;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_log_in);
        login_progressbar = findViewById(R.id.login_progressbar);
        signup = findViewById(R.id.register_txt);
        forgetpassword = findViewById(R.id.forgetpassword_txt);

        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Log_In.this,ForgetPassword.class);
                startActivity(intent);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Log_In.this,Login_As.class);
                startActivity(intent);
            }
        });


        mAuth = FirebaseAuth.getInstance();
        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.password);

    }

    private void login() {
        String login_email = email.getText().toString().trim();
        String login_password = password.getText().toString().trim();

        if (login_email.isEmpty()) {
            email.setError("email required");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(login_email).matches()) {
            email.setError("please provide valid email");
            email.requestFocus();
            return;
        }
        if (login_password.isEmpty()) {
            password.setError("password required");
            password.requestFocus();
            return;
        }
        if (login_password.length() < 6) {
            password.setError("Min password length is 6 characters");
            password.requestFocus();
            return;
        }

        login_progressbar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(login_email, login_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getUid();

                            FirebaseDatabase firebaseD = FirebaseDatabase.getInstance();
                            firebaseD.getReference().child("User").child(uid).child("usertype").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int userRole = snapshot.getValue(Integer.class);

                                    if (userRole == 0) {
                                        login_progressbar.setVisibility(View.INVISIBLE);

                                        Intent i = new Intent(Log_In.this, Customer_Home.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);

                                    } else {
                                        login_progressbar.setVisibility(View.INVISIBLE);

                                        Intent i = new Intent(Log_In.this, Restaurant_Home_View.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);

                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    login_progressbar.setVisibility(View.INVISIBLE); }

                            });

                        } else {
                            login_progressbar.setVisibility(View.GONE);
                            Toast.makeText(Log_In.this, "Incorrect email or password!!!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}