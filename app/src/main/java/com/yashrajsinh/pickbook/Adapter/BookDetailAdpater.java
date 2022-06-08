package com.yashrajsinh.pickbook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yashrajsinh.pickbook.Model.BookDetail;
import com.yashrajsinh.pickbook.R;
import com.yashrajsinh.pickbook.ViewDetail;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BookDetailAdpater extends FirestoreRecyclerAdapter<BookDetail, BookDetailAdpater.BookDetailViewHolder> {

    private Context context;

    public BookDetailAdpater(@NonNull FirestoreRecyclerOptions<BookDetail> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BookDetailViewHolder holder, int position, @NonNull final BookDetail model) {

        if(model.isStatus()) {
            holder.bookName.setText(model.getBookName());
            holder.authorName.setText(model.getAuthorName());
            Picasso.with(context)
                    .load(model.getImagePath())
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(holder.bookholder);

            holder.viewDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewDetail.class);
                    intent.putExtra("OwnerEmail", model.getUserEmail());
                    intent.putExtra("BookName", model.getBookName());
                    intent.putExtra("AuthorName", model.getAuthorName());
                    intent.putExtra("Category", model.getCategory());
                    intent.putExtra("BestOfBook", model.getBestOfBook());
                    intent.putExtra("ImagePath", model.getImagePath());
                    intent.putExtra("UserID", model.getUserId());
                    context.startActivity(intent);
                }
            });
        }

    }

    @NonNull
    @Override
    public BookDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_book_info, parent, false);
        return new BookDetailViewHolder(view);
    }

    public void assignContext(Context context) {
        this.context = context;
    }

    public class BookDetailViewHolder extends RecyclerView.ViewHolder {

        private ImageView bookholder;
        private TextView bookName, authorName;
        private Button viewDetail;

        public BookDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            bookholder = itemView.findViewById(R.id.placeholder);
            bookName = itemView.findViewById(R.id.book_name);
            authorName = itemView.findViewById(R.id.author_name);
            viewDetail = itemView.findViewById(R.id.details);

        }
    }

}
