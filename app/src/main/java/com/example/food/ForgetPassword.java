package com.example.food;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {
    private EditText email_reset;
    private Button button_reset;

    private ProgressBar reset_progressBar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_forget_password);

        email_reset = findViewById(R.id.reset_email);
        button_reset = findViewById(R.id.reset_button);

        reset_progressBar=(ProgressBar) findViewById(R.id.reset_progressbar);

        auth = FirebaseAuth.getInstance();

        button_reset.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetpass();
            }
        }));

    }
    private void resetpass(){
        String email = email_reset.getText().toString().trim();

        if(email.isEmpty()){
            email_reset.setError("email required");
            email_reset.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_reset.setError("please provide a valid email");
            email_reset.requestFocus();
            return;
        }

        reset_progressBar.setVisibility(View.VISIBLE);

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(ForgetPassword.this,"A password has been sent to your email!!!",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgetPassword.this, com.example.food.Log_In.class));

                    reset_progressBar.setVisibility(View.GONE);

                }
                else {
                    String error = task.getException().getMessage();
                    Toast.makeText(ForgetPassword.this,"try again! something wrong happened "+ error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}