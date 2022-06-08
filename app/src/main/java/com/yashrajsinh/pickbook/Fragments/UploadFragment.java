package com.yashrajsinh.pickbook.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.yashrajsinh.pickbook.Common.Common;
import com.yashrajsinh.pickbook.Model.BookDetail;
import com.yashrajsinh.pickbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UploadFragment extends Fragment {

    private Context context;

    private EditText bookName, authorName, bestOfBook;
    private Spinner categoryBind;
    private ImageView uploadImage;
    private Button uploadBook;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("books");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private StorageTask storageTask;

    private ProgressDialog progressDialog;

    private String item, userEmail;

    private Uri uri;

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    userEmail = user.getEmail();
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookName = view.findViewById(R.id.book_name);
        authorName = view.findViewById(R.id.author_name);
        bestOfBook = view.findViewById(R.id.best_of_book);

        categoryBind = view.findViewById(R.id.category_bind);
        uploadImage = view.findViewById(R.id.book_image);
        uploadBook = view.findViewById(R.id.upload_button);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Processing...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        categoryBind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fetchCategory();

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        uploadBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.validate(bookName.getText().toString(),
                        authorName.getText().toString(),
                        uri)) {
                    if(storageTask != null && storageTask.isInProgress()) {
                        uploadBookDetail();
                    } else {
                        uploadBookDetail();
                    }
                } else {
                    TastyToast.makeText(getContext(), "Please fill out necessary details! Such as book name, author name and image.", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                }
            }
        });

    }

    private void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private void fetchCategory() {
        final ArrayList<String> categories = new ArrayList<String>();
        CollectionReference reference = firestore.collection("CategoryType");
        Query query = reference.orderBy("name");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        categories.add(snapshot.get("name").toString());
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        R.layout.support_simple_spinner_dropdown_item, categories);
                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                categoryBind.setAdapter(adapter);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.hide();
                TastyToast.makeText(getContext(), e.getMessage(), TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK &&
                data != null && data.getData() != null) {
            uri = data.getData();
            Picasso.with(getContext()).load(uri).resize(700, 700).into(uploadImage);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadBookDetail() {
        if (uri != null) {
            progressDialog.show();
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
            storageTask = fileReference.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            final Uri downloadUri = urlTask.getResult();
                            Log.d("DOWNLOAD", downloadUri.toString());

                            firestore.collection("BookDetails")
                                    .add(new BookDetail(bookName.getText().toString(),
                                            authorName.getText().toString(),
                                            bestOfBook.getText().toString(),
                                            item,
                                            downloadUri.toString(),
                                            userEmail,
                                            firebaseAuth.getUid(),
                                            true,
                                            false))
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            progressDialog.hide();
                                            bookName.setText("");
                                            authorName.setText("");
                                            categoryBind.setSelection(0);
                                            bestOfBook.setText("");
                                            uploadImage.setImageResource(R.drawable.image_view);
                                            TastyToast.makeText(getContext(), "Book has been uploaded", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            TastyToast.makeText(getContext(), e.getMessage(), TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            TastyToast.makeText(getContext(), e.getMessage(), TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            TastyToast.makeText(getContext(), "Uploading : "+(int)progress, TastyToast.LENGTH_SHORT, TastyToast.DEFAULT).show();
                        }
                    });
        } else {
            TastyToast.makeText(getContext(), "Please select an Image!", TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
        }
    }

}
