package com.yashrajsinh.pickbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yashrajsinh.pickbook.Notification.SendNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

public class ViewDetail extends AppCompatActivity {

    private TextView bookName, authorName, category, bestOfBook, ownerEmail;
    private ImageView bookImg;
    private Button sendRequest;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail);

        bookName = findViewById(R.id.book_name);
        authorName = findViewById(R.id.author_name);
        category = findViewById(R.id.title);
        ownerEmail = findViewById(R.id.owner_email);
        bestOfBook = findViewById(R.id.best_of_book);

        bookImg = findViewById(R.id.book_img);

        sendRequest = findViewById(R.id.request_button);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Processing...");
        progressDialog.setCanceledOnTouchOutside(false);

        if (getIntent().getStringExtra("OwnerEmail") != null &&
                getIntent().getStringExtra("BookName") != null &&
                getIntent().getStringExtra("AuthorName") != null &&
                getIntent().getStringExtra("Category") != null &&
                getIntent().getStringExtra("ImagePath") != null) {
            bookName.setText(getIntent().getStringExtra("BookName"));
            authorName.setText(getIntent().getStringExtra("AuthorName"));
            category.setText(getIntent().getStringExtra("Category"));
            bestOfBook.setText(getIntent().getStringExtra("BestOfBook"));
            ownerEmail.setText(getIntent().getStringExtra("OwnerEmail"));

            Picasso.with(this).load(getIntent().getStringExtra("ImagePath"))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(bookImg);
        }

        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ownerEmail.getText().toString().equals(firebaseAuth.getCurrentUser().getEmail())) {
                    TastyToast.makeText(ViewDetail.this, "Cannot send the request to yourself!", TastyToast.LENGTH_SHORT,
                            TastyToast.CONFUSING).show();
                } else {
                    bookDetail();
                }
            }
        });
    }

    private void bookDetail() {
        CollectionReference reference = FirebaseFirestore.getInstance().collection("ClientUsers");
        final Query query = reference.whereEqualTo("email", ownerEmail.getText().toString());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(DocumentSnapshot snapshot : task.getResult()) {
                        String notificationKey = snapshot.getString("notificationKey");

                        CollectionReference bookRef = FirebaseFirestore.getInstance().collection("BookDetails");
                        final Query bookQuery = bookRef.whereEqualTo("userId", getIntent().getStringExtra("UserID"));
                        bookQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for(DocumentSnapshot bookSnap : task.getResult()) {
                                        DocumentReference doc = bookSnap.getReference();
                                        doc.update("status", false);
                                        doc.update("requested", true);
                                        doc.update("requesterEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    }
                                }
                            }
                        });

                        new SendNotification("A new request from \n " + FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                "Book Request",
                                notificationKey);
                        TastyToast.makeText(ViewDetail.this, "Request has been placed", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                        try {
                            Thread.sleep(100);
                            startActivity(new Intent(ViewDetail.this, Home.class));
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                TastyToast.makeText(ViewDetail.this, e.getMessage(), TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
            }
        });
    }

}
