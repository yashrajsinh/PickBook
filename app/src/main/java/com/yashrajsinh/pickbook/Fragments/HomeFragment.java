package com.yashrajsinh.pickbook.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yashrajsinh.pickbook.Adapter.CategoryTypeAdapter;
import com.yashrajsinh.pickbook.Model.CategoryType;
import com.yashrajsinh.pickbook.R;
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
import com.onesignal.OneSignal;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private RecyclerView recyclerView;

    private Context context;
    private ProgressDialog progressDialog;

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.category_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Processing...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        OneSignal.startInit(getContext()).init();
        OneSignal.setSubscription(true);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                CollectionReference reference = FirebaseFirestore.getInstance().collection("ClientUsers");
                Map<String, Object> params = new HashMap<>();
                params.put("notificationKey", userId);
                reference.document(firebaseAuth.getCurrentUser().getUid())
                        .update(params);
            }
        });

        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);

        fetchCategoryType();

    }

    private void fetchCategoryType() {
        CollectionReference reference = firestore.collection("CategoryType");
        final Query query = reference.orderBy("name", Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    progressDialog.dismiss();
                    if(task.getResult().size() == 0) {
                        TastyToast.makeText(getContext(), "Seems likes its empty!", TastyToast.LENGTH_SHORT, TastyToast.DEFAULT).show();
                    }
                } else {
                    progressDialog.hide();
                    TastyToast.makeText(getContext(), task.getException().getMessage(), TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                FirestoreRecyclerOptions<CategoryType> data = new FirestoreRecyclerOptions.Builder<CategoryType>()
                        .setQuery(query, CategoryType.class)
                        .build();

                CategoryTypeAdapter adapter = new CategoryTypeAdapter(data);
                adapter.assignContext(context);
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
