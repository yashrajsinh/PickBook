package com.yashrajsinh.pickbook.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yashrajsinh.pickbook.R;
import com.yashrajsinh.pickbook.ShowRequest;
import com.yashrajsinh.pickbook.ShowUploadedBooks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sdsmdg.tastytoast.TastyToast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AccountFragment extends Fragment {

    private Context context;
    private ProgressDialog progressDialog;

    private TextView name, email, address;
    private Button viewNotifications;
    private Button showUploadedBooks;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name = view.findViewById(R.id.user_name);
        email = view.findViewById(R.id.user_email);
        address = view.findViewById(R.id.user_address);

        viewNotifications = view.findViewById(R.id.show_request);
        showUploadedBooks = view.findViewById(R.id.show_uploaded_books);

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Processing...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        fetchUserAccountDetail();

        viewNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ShowRequest.class));
            }
        });

        showUploadedBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ShowUploadedBooks.class));
            }
        });

    }

    private void fetchUserAccountDetail() {
        CollectionReference reference = firestore.collection("ClientUsers");
        Query query = reference.whereEqualTo("email", firebaseAuth.getCurrentUser().getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()) {
                    for(DocumentSnapshot snapshot : task.getResult()) {
                        name.setText(snapshot.get("name").toString());
                        email.setText(snapshot.get("email").toString());
                        address.setText(snapshot.get("address").toString());
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.hide();
                TastyToast.makeText(context, e.getMessage(), TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
            }
        });

    }
}
