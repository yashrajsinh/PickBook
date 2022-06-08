package com.yashrajsinh.pickbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.yashrajsinh.pickbook.Common.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateBookDetail extends AppCompatActivity {

    private EditText bookName, authorName;
    private Spinner categoryList;
    private EditText bestFromBook;
    private ImageView bookImg;
    private Button updateButton;

    private ProgressDialog progressDialog;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("books");
    private StorageTask storageTask;

    private Uri uri;
    private String item, docPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_book_detail);

        bookName = findViewById(R.id.book_name);
        authorName = findViewById(R.id.author_name);
        categoryList = findViewById(R.id.category_bind);
        bestFromBook = findViewById(R.id.best_of_book);
        bookImg = findViewById(R.id.book_image);
        updateButton = findViewById(R.id.update_button);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Processing...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        docPath = getIntent().getStringExtra("documentPath");
        bookName.setText(getIntent().getStringExtra("bookName"));
        authorName.setText(getIntent().getStringExtra("authorName"));
        bestFromBook.setText(getIntent().getStringExtra("bestFromBook"));
        Picasso.with(this)
                .load(getIntent().getStringExtra("imageLink"))
                .resize(700, 700)
                .placeholder(R.drawable.placeholder)
                .into(bookImg);
        uri = Uri.parse(getIntent().getStringExtra("imageLink"));
        fetchCategoryList();

        categoryList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        bookImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePicker();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.validate(bookName.getText().toString(),
                        authorName.getText().toString(), uri)) {
                    updateBookDetails();
                }
            }
        });

    }

    private void updateBookDetails() {
        Log.d("URI", uri.toString());
        if(uri != null && !uri.toString().equals(getIntent().getStringExtra("imageLink"))) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
            storageTask = fileReference.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            final Uri downloadUri = urlTask.getResult();
                            Log.d("DOWNLOAD", downloadUri.toString());

                            HashMap<String, Object> data = new HashMap<>();
                            data.put("authorName", authorName.getText().toString());
                            data.put("bookName", bookName.getText().toString());
                            data.put("bestOfBook", bestFromBook.getText().toString());
                            data.put("category", item);
                            data.put("imagePath", downloadUri.toString());

                            StorageReference deleteRef = FirebaseStorage.getInstance().getReferenceFromUrl(getIntent().getStringExtra("imageLink"));
                            deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("DELETE OLD IMAGE", "SUCCESS!");
                                }
                            });

                            firestore.collection("BookDetails")
                                    .document(docPath)
                                    .update(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            TastyToast.makeText(UpdateBookDetail.this, "Book detail updated",
                                                    TastyToast.LENGTH_SHORT, TastyToast.SUCCESS)
                                                    .show();
                                            startActivity(new Intent(UpdateBookDetail.this, ShowUploadedBooks.class));
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            TastyToast.makeText(UpdateBookDetail.this, e.getMessage(),
                                                    TastyToast.LENGTH_SHORT, TastyToast.ERROR)
                                                    .show();
                                        }
                                    });

                        }
                    });
        } else {
            HashMap<String, Object> data = new HashMap<>();
            data.put("authorName", authorName.getText().toString());
            data.put("bookName", bookName.getText().toString());
            data.put("bestOfBook", bestFromBook.getText().toString());
            data.put("category", item);
            data.put("imagePath", getIntent().getStringExtra("imageLink"));

            firestore.collection("BookDetails")
                    .document(docPath)
                    .update(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            TastyToast.makeText(UpdateBookDetail.this, "Book detail updated",
                                    TastyToast.LENGTH_SHORT, TastyToast.SUCCESS)
                                    .show();
                            startActivity(new Intent(UpdateBookDetail.this, ShowUploadedBooks.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            TastyToast.makeText(UpdateBookDetail.this, e.getMessage(),
                                    TastyToast.LENGTH_SHORT, TastyToast.ERROR)
                                    .show();
                        }
                    });
        }
        finish();
    }

    private void imagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK &&
                data != null && data.getData() != null) {
            uri = data.getData();
            Picasso.with(getApplicationContext()).load(uri).resize(700, 700).into(bookImg);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void fetchCategoryList() {

        final ArrayList<String> categories = new ArrayList<>();
        CollectionReference catRef = firestore.collection("CategoryType");
        Query query = catRef.orderBy("name");
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(DocumentSnapshot snapshot : task.getResult()) {
                                categories.add(snapshot.get("name").toString());
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateBookDetail.this,
                                R.layout.support_simple_spinner_dropdown_item, categories);
                        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        categoryList.setAdapter(adapter);
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        TastyToast.makeText(UpdateBookDetail.this, e.getMessage(), TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                    }
                });

    }

}