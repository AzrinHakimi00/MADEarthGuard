package com.example.madearthguard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignUpPage extends AppCompatActivity {

    EditText ETemail, ETpassword, ETconfirmpassword;
    Button signUp;
    TextView goToLoginPage;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        ETemail = findViewById(R.id.ETemail);
        ETpassword = findViewById(R.id.ETpassword);
        ETconfirmpassword = findViewById(R.id.ETconfirmPassword);

        signUp = findViewById(R.id.signUpBtn);
        goToLoginPage = findViewById(R.id.loginBtn);

        firebaseAuth = FirebaseAuth.getInstance();

        goToLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpPage.this,LoginPage.class));
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = ETemail.getText().toString();
                String password = ETpassword.getText().toString();
                String confirmpassword = ETconfirmpassword.getText().toString();

                if(email.isEmpty()){
                    ETemail.setError("Please provide your email address");
                    ETemail.requestFocus();
                }
                else if(password.isEmpty()){
                    ETpassword.setError("Please provide a password");
                    ETpassword.requestFocus();
                }
                else if(confirmpassword.isEmpty()){
                    ETconfirmpassword.setError("Please retype your password");
                    ETconfirmpassword.requestFocus();
                }
                else if(!password.equals(confirmpassword)){
                    ETconfirmpassword.setError("The password does not match");
                    ETconfirmpassword.requestFocus();
                }
                else if(!email.isEmpty() && !password.isEmpty() && !confirmpassword.isEmpty() && password.equals(confirmpassword)){
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Sign Up Unsuccessful",Toast.LENGTH_LONG).show();
                            }
                            else{
                                Objects.requireNonNull(firebaseAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(), "Sign up successful. Please verfy you email.", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignUpPage.this,VerificationPage.class));
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(),"Sign Up Unsuccessful",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            }
                        }
                    });
                }

            }
        });
    }
}