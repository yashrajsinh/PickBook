package com.yashrajsinh.pickbook.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yashrajsinh.pickbook.Model.BookDetail;
import com.yashrajsinh.pickbook.R;
import com.yashrajsinh.pickbook.SelectLibrary;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShowRequestAdapter extends FirestoreRecyclerAdapter<BookDetail, ShowRequestAdapter.ShowRequestViewHolder> {

    private Context context;

    public ShowRequestAdapter(@NonNull FirestoreRecyclerOptions<BookDetail> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ShowRequestViewHolder holder, final int position, @NonNull final BookDetail model) {

        if(model.getUserEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
            holder.bookName.setText(model.getBookName());
            holder.authorName.setText(model.getAuthorName());
            Picasso.with(context)
                    .load(model.getImagePath())
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .centerCrop()
                    .into(holder.bookImg);

            holder.approval.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.layout_alert_dialog, null);
                    final TextView category, requesterEmail, bestOfBook;

                    category = layout.findViewById(R.id.category_name);
                    requesterEmail = layout.findViewById(R.id.requester_email);
                    bestOfBook = layout.findViewById(R.id.best_of_book);

                    category.setText(model.getCategory());
                    requesterEmail.setText(model.getRequesterEmail());
                    bestOfBook.setText(model.getBestOfBook());

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(layout);
                    builder.setPositiveButton("Select Library for Pickup Point", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context, SelectLibrary.class);

                            intent.putExtra("bookName", model.getBookName());
                            intent.putExtra("docId", getSnapshots().getSnapshot(position).getId());
                            intent.putExtra("currentUserEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            intent.putExtra("requesterUserEmail", model.getRequesterEmail());
                            context.startActivity(intent);
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });

        }

    }

    @NonNull
    @Override
    public ShowRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_request_info, parent, false);
        return new ShowRequestViewHolder(view);
    }

    public void assignContext(Context context) {
        this.context = context;
    }

    public class ShowRequestViewHolder extends RecyclerView.ViewHolder {

        private ImageView bookImg;
        private TextView bookName, authorName;
        private Button approval;

        public ShowRequestViewHolder(@NonNull View itemView) {
            super(itemView);

            bookName = itemView.findViewById(R.id.book_name);
            authorName = itemView.findViewById(R.id.author_name);
            bookImg = itemView.findViewById(R.id.placeholder);

            approval = itemView.findViewById(R.id.approval);

        }
    }

}
