package com.example.draft;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailText;
    private EditText loginPassText;
    private Button loginBtn;
    private Button loginRegBtn;

    private FirebaseAuth mAuth;

    private ProgressBar loginProgress;

        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            mAuth = FirebaseAuth.getInstance(); //allows to use mAuth instead of typing getinstance all the time

            loginEmailText = (EditText) findViewById(R.id.reg_email);
            loginPassText = (EditText) findViewById(R.id.reg_pass);
            loginBtn = (Button) findViewById(R.id.reg_btn);
            loginRegBtn = (Button) findViewById(R.id.reg_login_btn);

            loginProgress = (ProgressBar) findViewById(R.id.reg_progress);
            //LoginFeatures
            loginRegBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent regIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(regIntent);
                }
            });
            loginBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    //login process

                    String loginEmail = loginEmailText.getText().toString();
                    String loginPass = loginPassText.getText().toString();

                    if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)) {

                        loginProgress.setVisibility(View.VISIBLE);
                        mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    sendToMain();
                                }
                                else{
                                    String errorMessage= task.getException().getMessage();
                                    Toast.makeText(LoginActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                                }
                                loginProgress.setVisibility(View.INVISIBLE);

                            }
                        });



                    }
                }

            });
        }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToMain();
        }

    }

    private void sendToMain() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();//makes sure user does not go back, finished intent
    }
}
