package com.yashrajsinh.pickbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;

import com.yashrajsinh.pickbook.Adapter.BookDetailAdpater;
import com.yashrajsinh.pickbook.Model.BookDetail;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sdsmdg.tastytoast.TastyToast;

public class BookInfo extends AppCompatActivity {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private TextView title;

    private String category;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        recyclerView = findViewById(R.id.book_info_recycler);
        title = findViewById(R.id.title);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Processing...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(getIntent().getStringExtra("category") != null) {
            category = getIntent().getStringExtra("category");
            title.setText(category);
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchBooks();

    }

    private void fetchBooks() {
        CollectionReference reference = firestore.collection("BookDetails");
        final Query query = reference.whereEqualTo("category", category).whereEqualTo("status", true);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    progressDialog.dismiss();
                    if(task.getResult().size() == 0) {
                        TastyToast.makeText(BookInfo.this, "There are no books available of type" + category, TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
                    }
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                FirestoreRecyclerOptions<BookDetail> data = new FirestoreRecyclerOptions.Builder<BookDetail>()
                        .setQuery(query, BookDetail.class)
                        .build();

                BookDetailAdpater adpater = new BookDetailAdpater(data);
                adpater.assignContext(BookInfo.this);
                recyclerView.setAdapter(adpater);
                adpater.startListening();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                TastyToast.makeText(BookInfo.this, e.getMessage(), TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
            }
        });
    }
}
