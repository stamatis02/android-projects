package com.example.ergasia2dikiamou;

import static android.view.View.GONE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class signup extends AppCompatActivity {
    EditText emaillog, passwordlog, username;
    Button login, signup ;
     FirebaseAuth auth;
     DatabaseReference mDatabase;
      //we made a function to check if the credentials are valid before sign up or login
    //the username is mandatory in the signup only so after the signup is succesfull we are making the textview gone.
     private boolean isinputvalid(String email, String password , String username) {
         if (email.isEmpty()) {
             Toast.makeText(signup.this, "email is mandatory", Toast.LENGTH_SHORT).show();
             return false;
         } else if (password.isEmpty()) {
             Toast.makeText(signup.this, "password is mandatory", Toast.LENGTH_SHORT).show();
             return false;
         } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
             Toast.makeText(signup.this, "Η μορφή του Email δεν είναι έγκυρη", Toast.LENGTH_SHORT).show();
             return false;
         }
         else if(username.isEmpty()){
             Toast.makeText(signup.this, "useername mandatory", Toast.LENGTH_SHORT).show();
             return false;
         }
         return true;
     }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mDatabase = FirebaseDatabase.getInstance().getReference("chatuser");
        signup = findViewById(R.id.signupbutton);
        auth = FirebaseAuth.getInstance();
        login = findViewById(R.id.loginbutt);
        emaillog = findViewById(R.id.emailsign);
        passwordlog = findViewById(R.id.passsignup);
        username = findViewById(R.id.usernameid);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emaillog.getText().toString().trim();
                String password = passwordlog.getText().toString().trim();
                String username1 = username.getText().toString().trim();
                if (!isinputvalid(email,password,username1) ){//we check the validations
                    return;
                }
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(signup.this,"signup succesfdul", Toast.LENGTH_SHORT).show();
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String uid = firebaseUser.getUid();
                             chatuser newUser = new chatuser(uid, username1 , email);
                             mDatabase.child(uid).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void unused) {
                                     // if signin is succsful we hide the username textview
                                     username.setVisibility(GONE);
                                 }
                             }).addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {
                                     Toast.makeText(signup.this, "Απέτυχε η αποθήκευση!", Toast.LENGTH_SHORT).show();
                                 }
                             });
                        }
                        else {
                            Toast.makeText(signup.this, "signup failed try again later", Toast.LENGTH_SHORT).show();
                        }


                    }
                });}
            });
                login.setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {
                        String email = emaillog.getText().toString().trim();
                        String password = passwordlog.getText().toString().trim();
                        String username1 = username.getText().toString().trim();
                        if (!isinputvalid(email,password,"ooo")){//we check again the validations
                            return ;
                        }
                        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(signup.this, "login succesfull",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(signup.this , chat.class);
                                    startActivity(intent);
                                    finish();
                                }

                                else {

                                    Toast.makeText(signup.this, "login failed try again",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

            }


    }




