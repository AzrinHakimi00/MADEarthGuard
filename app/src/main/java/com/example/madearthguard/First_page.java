package com.example.madearthguard;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;


public class First_page extends AppCompatActivity {

    Button loginBtn;
    Button SignUpUsingEmailBtn;
    Button googleSignUpBtn;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 20;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        loginBtn = findViewById(R.id.loginButton);
        SignUpUsingEmailBtn = findViewById(R.id.SignUpUsingEmailButton);
        googleSignUpBtn = findViewById(R.id.GoogleButton);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(First_page.this,LoginPage.class));
            }
        });

        SignUpUsingEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(First_page.this,SignUpPage.class));
            }
        });




        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();


        googleSignInClient = GoogleSignIn.getClient(this,gso);
//--------------------------------------------------------------------------------------------------------------------------------------------------
        googleSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                googleSignUp();
            }
        });


        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(First_page.this,HomePage.class));
            finish();
        }

    }

    private void googleSignUp() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());

            }catch (Exception e){
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }


    }

    private void firebaseAuth(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    FirebaseUser user = firebaseAuth.getCurrentUser();


                    System.out.println(user.getDisplayName());
                    System.out.println(user.getUid());
                    System.out.println(user.getPhotoUrl().toString());

                    HashMap<String,Object>  map = new HashMap<>();

                    System.out.println(user.getUid());
                    System.out.println(user.getPhotoUrl().toString());
                    System.out.println(user.getDisplayName());

                    map.put("id",user.getUid());
                    map.put("name",user.getDisplayName());
                    map.put("profile",user.getPhotoUrl().toString());

                    database.getReference().child("Users").child(user.getUid()).setValue(map);

                    startActivity(new Intent(First_page.this,HomePage.class));
                }
                else{
                    Toast.makeText(First_page.this,"Something went wrong",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}