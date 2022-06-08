package com.yashrajsinh.pickbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.yashrajsinh.pickbook.Adapter.UploadBookDetailAdapter;
import com.yashrajsinh.pickbook.Model.BookDetail;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sdsmdg.tastytoast.TastyToast;

public class ShowUploadedBooks extends AppCompatActivity {

    private RecyclerView uploadBookRecycler;
    private ProgressDialog progressDialog;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_uploaded_books);

        uploadBookRecycler = findViewById(R.id.uploaded_books_list);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Processing...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        uploadBookRecycler.setHasFixedSize(true);
        uploadBookRecycler.setLayoutManager(new LinearLayoutManager(this));
        fetchBooks();

    }

    private void fetchBooks() {
        CollectionReference books = firestore.collection("BookDetails");
        final Query query = books.whereEqualTo("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            progressDialog.dismiss();
                            if(task.getResult().size() <= 0) {
                                TastyToast.makeText(ShowUploadedBooks.this, "There are no uploaded books yet!", TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
                            }
                        }
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        FirestoreRecyclerOptions<BookDetail> options = new FirestoreRecyclerOptions.Builder<BookDetail>()
                                .setQuery(query, BookDetail.class)
                                .build();

                        UploadBookDetailAdapter adapter = new UploadBookDetailAdapter(options);
                        adapter.assignContext(ShowUploadedBooks.this);
                        uploadBookRecycler.setAdapter(adapter);
                        Log.d("Length", adapter.getSnapshots().size() + "");
                        adapter.startListening();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        TastyToast.makeText(ShowUploadedBooks.this, e.getMessage(), TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                    }
                });
    }

}