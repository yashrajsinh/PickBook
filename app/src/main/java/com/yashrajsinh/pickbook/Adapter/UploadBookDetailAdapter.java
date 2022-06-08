package com.yashrajsinh.pickbook.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.yashrajsinh.pickbook.Model.BookDetail;
import com.yashrajsinh.pickbook.R;
import com.yashrajsinh.pickbook.UpdateBookDetail;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

public class UploadBookDetailAdapter extends FirestoreRecyclerAdapter<BookDetail, UploadBookDetailAdapter.UploadeBookDetailViewHolder> {

    private Context context;

    public UploadBookDetailAdapter(@NonNull FirestoreRecyclerOptions<BookDetail> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UploadeBookDetailViewHolder holder, final int position, @NonNull final BookDetail model) {
        holder.bookName.setText(model.getBookName());
        holder.authorName.setText(model.getAuthorName());
        Picasso.with(context)
                .load(model.getImagePath())
                .fit().centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(holder.bookImg);
        holder.updateBookDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference ref = getSnapshots().getSnapshot(position).getReference();
                ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        Log.d("status", value.getBoolean("status") + "");
                        if(!value.getBoolean("status")) {
                            TastyToast.makeText(context, "You cannot edit this record, request is already placed!", TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
                        } else {
                            Intent intent = new Intent(context, UpdateBookDetail.class);
                            intent.putExtra("bookName", model.getBookName());
                            intent.putExtra("authorName", model.getAuthorName());
                            intent.putExtra("category", model.getCategory());
                            intent.putExtra("bestFromBook", model.getBestOfBook());
                            intent.putExtra("imageLink", model.getImagePath());
                            intent.putExtra("userId", model.getUserId());
                            intent.putExtra("documentPath", getSnapshots().getSnapshot(position).getId());
                            context.startActivity(intent);
                        }
                    }
                });
            }
        });

        holder.deleteBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference ref = getSnapshots().getSnapshot(position).getReference();
                ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(!value.getBoolean("status")) {
                            TastyToast.makeText(context, "You cannot edit this record, request is already placed!", TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
                        } else {
                            final AlertDialog.Builder deleteConfirmationDialog = new AlertDialog.Builder(context);
                            deleteConfirmationDialog.setTitle("Delete this book?");
                            deleteConfirmationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteItem(position, model.getImagePath());
                                }
                            })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            deleteConfirmationDialog.show();
                        }
                    }
                });
            }
        });

    }

    public void deleteItem(final int position, final String imagePath) {
        getSnapshots().getSnapshot(position).getReference().delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imagePath);
                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                TastyToast.makeText(context, "Record has been deleted successfully", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                TastyToast.makeText(context, e.getMessage(), TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        TastyToast.makeText(context, e.getMessage(), TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                    }
                });
    }

    public void assignContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public UploadeBookDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_upload_book_listview, parent, false);
        return new UploadeBookDetailViewHolder(view);
    }

    public class UploadeBookDetailViewHolder extends RecyclerView.ViewHolder {

        private TextView bookName, authorName;
        private Button updateBookDetail, deleteBook;
        private ImageView bookImg;

        public UploadeBookDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            bookName = itemView.findViewById(R.id.book_name);
            authorName = itemView.findViewById(R.id.author_name);
            updateBookDetail = itemView.findViewById(R.id.update_details);
            deleteBook = itemView.findViewById(R.id.delete_book);
            bookImg = itemView.findViewById(R.id.placeholder);

        }
    }

}
