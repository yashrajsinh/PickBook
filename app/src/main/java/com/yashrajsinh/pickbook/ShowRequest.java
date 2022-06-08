package com.yashrajsinh.pickbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.yashrajsinh.pickbook.Adapter.ShowRequestAdapter;
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

public class ShowRequest extends AppCompatActivity {

    private RecyclerView recyclerView;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_request);

        recyclerView = findViewById(R.id.notification_recycler);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Processing...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        fetchRequest();

    }

    private void fetchRequest() {

        CollectionReference reference = FirebaseFirestore.getInstance().collection("BookDetails");
        final Query query = reference.whereEqualTo("status", false)
                .whereEqualTo("requested", true);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressDialog.dismiss();

                if(task.getResult().size() == 0)
                    TastyToast.makeText(ShowRequest.this, "No requests found yet!", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                FirestoreRecyclerOptions<BookDetail> data = new FirestoreRecyclerOptions.Builder<BookDetail>()
                        .setQuery(query, BookDetail.class)
                        .build();

                ShowRequestAdapter adapter = new ShowRequestAdapter(data);
                adapter.assignContext(ShowRequest.this);
                recyclerView.setAdapter(adapter);
                adapter.startListening();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
}
