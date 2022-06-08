package com.yashrajsinh.pickbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.yashrajsinh.pickbook.Model.PlaceBookAtLibrary;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.Random;

public class SelectLibrary extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

    private Spinner librarySelectionMenu;
    private Button submitButton;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private String libraryName;
    private String bookName, currentUserEmail, requesterUserEmail;
    private String docId;
    private String phoneNumber;
    private int otp;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_library);

        bookName = getIntent().getStringExtra("bookName");
        currentUserEmail = getIntent().getStringExtra("currentUserEmail");
        requesterUserEmail = getIntent().getStringExtra("requesterUserEmail");

        librarySelectionMenu = findViewById(R.id.library_names);
        submitButton = findViewById(R.id.place_to_lib);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Processing...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        docId = getIntent().getStringExtra("docId");
        librarySelectionMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                libraryName = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        fetchLibraryNameFromDB();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestore.collection("PlacedBooksAtLibrary")
                        .add(new PlaceBookAtLibrary(libraryName,
                                bookName,
                                currentUserEmail,
                                requesterUserEmail,
                                docId,
                                generateOTP(),
                                false))
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                TastyToast.makeText(SelectLibrary.this, "Book request has successfully placed at : "+libraryName,
                                        TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();

                                CollectionReference bookRequestRef = firestore.collection("BookDetails");
                                bookRequestRef.document(docId)
                                        .update("status", false);
                                bookRequestRef.document(docId)
                                        .update("requested", false);



                                startActivity(new Intent(SelectLibrary.this, ShowRequest.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                TastyToast.makeText(SelectLibrary.this, e.getMessage(), TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                            }
                        });
            }
        });

    }

    private int generateOTP () {
        Random r = new Random();
        int low = 1000;
        int high = 9999;
        int result = r.nextInt(high-low) + low;
        otp = result;
        return otp;
    }

    private void fetchLibraryNameFromDB() {
        final ArrayList<String> names = new ArrayList<>();
        CollectionReference libraryCollection = firestore.collection("Library");
        Query query = libraryCollection.orderBy("name");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        names.add(snapshot.get("name").toString());
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectLibrary.this,
                        R.layout.support_simple_spinner_dropdown_item, names);
                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                librarySelectionMenu.setAdapter(adapter);
                progressDialog.hide();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        TastyToast.makeText(SelectLibrary.this, e.getMessage(), TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                        progressDialog.dismiss();
                    }
                });
    }

}