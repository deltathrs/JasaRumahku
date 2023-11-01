package com.example.jasarumahku;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText editTextemail,editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;

    ProgressBar progressBar;
    TextView textView;

    Button googleAuth;
    FirebaseDatabase database;
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 20;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        editTextemail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        googleAuth = findViewById(R.id.btn_google);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.signUpNow);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("623155254752-jcm4c5tm1mn1d9imha21kf4k1f9leqp3.apps.googleusercontent.com")
                        .requestEmail()
                        .build();

        googleSignInClient = GoogleSignIn.getClient(this,gso);

        googleAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });

//        if (mAuth.getCurrentUser() != null){
//
//            Intent intent = new Intent(LoginActivity.this, HomeGoogleActivity.class);
//            startActivity(intent);
//            finish();
//        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity2.class);
                startActivity(intent);
                finish();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email,password;
                email = String.valueOf(editTextemail.getText());
                password = String.valueOf(editTextPassword.getText());

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }

    private void googleSignIn() {

        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());

            }
            catch (Exception e){
                Toast.makeText(this, e.getMessage() , Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void firebaseAuth(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id",user.getUid());
                            map.put("name",user.getDisplayName());
                            map.put("profile",user.getPhotoUrl().toString());

                            database.getReference().child("users").child(user.getUid()).setValue(map);

                            Intent intent = new Intent(LoginActivity.this, HomeGoogleActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}