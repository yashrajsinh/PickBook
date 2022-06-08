package com.yashrajsinh.pickbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yashrajsinh.pickbook.Common.Common;
import com.yashrajsinh.pickbook.Model.ClientUsers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sdsmdg.tastytoast.TastyToast;

public class SignUp extends AppCompatActivity {

    private EditText name, email, password, address, phone;
    private Button signup;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        address = findViewById(R.id.user_address);
        phone = findViewById(R.id.phone);

        signup = findViewById(R.id.signup);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Creating account, please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.validate(
                        name.getText().toString(),
                        email.getText().toString(),
                        password.getText().toString(),
                        address.getText().toString(),
                        phone.getText().toString()
                )) {
                    CollectionReference collectionReference = firestore.collection("ClientUsers");
                    Query query = collectionReference.whereEqualTo("email", email.getText().toString());
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            boolean flag = false;
                            if(task.isSuccessful()) {
                                for(DocumentSnapshot snapshot : task.getResult()) {
                                    String mail = snapshot.getString("email");
                                    if(mail.equals(email.getText().toString())) {
                                        flag = true;
                                    } else {
                                        flag = false;
                                    }
                                }

                                if(!flag) {
                                    progressDialog.show();
                                    firebaseAuth.createUserWithEmailAndPassword(
                                            email.getText().toString(),
                                            password.getText().toString()
                                    ).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            firestore.collection("ClientUsers")
                                                    .document(firebaseAuth.getCurrentUser().getUid())
                                                    .set(new ClientUsers(
                                                            name.getText().toString(),
                                                            email.getText().toString(),
                                                            password.getText().toString(),
                                                            address.getText().toString(),
                                                            firebaseAuth.getUid(),
                                                            phone.getText().toString()
                                                    ))
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()) {
                                                                progressDialog.dismiss();
                                                            } else {
                                                                TastyToast.makeText(SignUp.this, task.getException().getMessage(), TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
                                                            }
                                                        }
                                                    })
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            TastyToast.makeText(SignUp.this, "Registered successfully", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                                                            try {
                                                                Thread.sleep(100);
                                                                startActivity(new Intent(SignUp.this, MainActivity.class));
                                                                finish();
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressDialog.hide();
                                                            TastyToast.makeText(SignUp.this, e.getMessage(), TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                                                        }
                                                    });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            TastyToast.makeText(SignUp.this, e.getMessage(), TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                                        }
                                    });
                                } else {
                                    TastyToast.makeText(SignUp.this, "User account already exists", TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
                                }
                            }
                        }
                    });
                } else {
                    TastyToast.makeText(SignUp.this, "Please fill out all required fields properly", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                }
            }
        });

    }
}
