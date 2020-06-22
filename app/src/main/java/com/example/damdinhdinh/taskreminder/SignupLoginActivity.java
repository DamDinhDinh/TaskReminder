package com.example.damdinhdinh.taskreminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupLoginActivity extends AppCompatActivity {
    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnSignup;
    private Button btnLogin;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_login);

        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnSignup = findViewById(R.id.btn_signup);
        btnLogin = findViewById(R.id.btn_login);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Toast.makeText(SignupLoginActivity.this, user.getEmail()+" "+user.getUid(), Toast.LENGTH_SHORT).show();
                }else{

                }
            }
        };

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if(validateForm(email, password)){
                    logIn(email, password);
                }else{
                    Toast.makeText(SignupLoginActivity.this, "You must enter both email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if(validateForm(email, password)){
                    signUp(email, password);
                }else{
                    Toast.makeText(SignupLoginActivity.this, "You must enter both email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null){
            firebaseAuth.addAuthStateListener(authStateListener);
        }
    }

    private boolean validateForm(String email, String password){
        if (email.isEmpty() || password.isEmpty()){
            return false;
        }
        return true;
    }

    public void signUp(String email, String password){
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignupLoginActivity.this, "Sign up success", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(SignupLoginActivity.this, "Sign up fail "+task.getResult(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void logIn(String email, String password){
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignupLoginActivity.this, "Sign in success", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(SignupLoginActivity.this, "Sign in fail ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
