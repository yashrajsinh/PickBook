package com.yashrajsinh.pickbook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yashrajsinh.pickbook.BookInfo;
import com.yashrajsinh.pickbook.Model.CategoryType;
import com.yashrajsinh.pickbook.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryTypeAdapter extends FirestoreRecyclerAdapter<CategoryType, CategoryTypeAdapter.CategoryTypeViewHolder> {

    private Context context;

    public CategoryTypeAdapter(@NonNull FirestoreRecyclerOptions<CategoryType> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryTypeViewHolder holder, int position, @NonNull final CategoryType model) {
        holder.categoryName.setText(model.getName());
        holder.categoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BookInfo.class);
                intent.putExtra("category", model.getName());
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public CategoryTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category_type, parent, false);
        return new CategoryTypeViewHolder(view);
    }

    public void assignContext(Context context) { this.context = context; }

    public class CategoryTypeViewHolder extends RecyclerView.ViewHolder {

        private TextView categoryName;
        private LinearLayout categoryLayout;

        public CategoryTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryLayout = itemView.findViewById(R.id.category_layout);
        }
    }

}
