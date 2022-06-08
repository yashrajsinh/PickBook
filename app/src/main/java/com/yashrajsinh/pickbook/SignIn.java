package com.yashrajsinh.pickbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yashrajsinh.pickbook.Common.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sdsmdg.tastytoast.TastyToast;

public class SignIn extends AppCompatActivity {

    private EditText email, password;
    private TextView forgotPassword;
    private FirebaseAuth firebaseAuth;
    private Button signin;
    private FirebaseAuth.AuthStateListener stateListener;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signin = findViewById(R.id.signin);
        forgotPassword = findViewById(R.id.forgotpassword);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Processing...");
        progressDialog.setCanceledOnTouchOutside(false);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.layout_forgot_password, null);
                final EditText frgtEmail;
                frgtEmail = layout.findViewById(R.id.frgt_user_email);

                AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
                builder.setView(layout);
                builder.setPositiveButton("Send Password Reset Link", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(frgtEmail.getText().toString().isEmpty())
                            return;
                        if(Common.validate(frgtEmail.getText().toString())) {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(frgtEmail.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                TastyToast.makeText(SignIn.this,
                                                        "Reset link is sent to : " + frgtEmail.getText().toString(),
                                                        TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                                            } else {
                                                TastyToast.makeText(SignIn.this,
                                                        "Something went wrong!",
                                                        TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            TastyToast.makeText(SignIn.this,
                                                    e.getMessage(),
                                                    TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                                        }
                                    });
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        stateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    TastyToast.makeText(SignIn.this,"Logging in...", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                    Intent intent = new Intent(SignIn.this, Home.class);
                    intent.putExtra("email", user.getEmail());
                    startActivity(intent);
                    finish();
                }
            }
        };

        email = findViewById(R.id.name);
        password = findViewById(R.id.password);


            signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Common.validate(email.getText().toString(),
                            password.getText().toString())) {
                        progressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),
                            password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        progressDialog.dismiss();
                                    }
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    TastyToast.makeText(SignIn.this, "Successfully logged in...", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                                    Intent intent = new Intent(SignIn.this, Home.class);
                                    intent.putExtra("email", firebaseAuth.getCurrentUser().getEmail());
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.hide();
                                    TastyToast.makeText(SignIn.this, e.getMessage(), TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                                }
                            });
                    } else {
                        TastyToast.makeText(SignIn.this, "Email or password is empty", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                    }
                }
            });


    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(stateListener);
    }
}
