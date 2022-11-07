package com.example.blogapp.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blogapp.R;
import com.example.blogapp.models.Blog;
import com.example.blogapp.models.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class MyBlogAdapter extends RecyclerView.Adapter<MyBlogAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<Blog> data;
    private OnBlogListener onBlogListener;
    private UserDatabaseAdapter db;

    public MyBlogAdapter(Context context, List<Blog> data, OnBlogListener onBlogListener) {
        this.layoutInflater = LayoutInflater.from(context);
        this.data = data;
        this.onBlogListener = onBlogListener;
        db = new UserDatabaseAdapter(context);
        db.open();
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_view, viewGroup, false);
        return new ViewHolder(view, onBlogListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Blog blog = data.get(position);
        User user = db.getUserWithId("" + blog.getUserId());
        holder.title.setText(blog.getTitle());
        holder.description.setText(blog.getDescription());
        holder.userName.setText(user.getName());
        String strDate = blog.getTime();
        holder.time.setText(strDate.substring(0, strDate.length() - 3));
        byte[] image = blog.getImage();
        Bitmap bitmap = getImage(image);
        holder.imageBlog.setImageBitmap(bitmap);
        image = user.getImage();
        bitmap = getImage(image);
        holder.imageProfile.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, description, userName, time;
        ImageView imageBlog, imageProfile;
        OnBlogListener onBlogListener;

        public ViewHolder(@NonNull View itemView, OnBlogListener onBlogListener) {
            super(itemView);
            title = itemView.findViewById(R.id.card_title);
            description = itemView.findViewById(R.id.card_desc);
            userName = itemView.findViewById(R.id.user_name);
            time = itemView.findViewById(R.id.time);
            imageBlog = itemView.findViewById(R.id.imageView);
            imageProfile = itemView.findViewById(R.id.profile);
            this.onBlogListener = onBlogListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onBlogListener.onBlogClick(getAdapterPosition());
        }
    }

    public interface OnBlogListener {
        void onBlogClick(int position);
    }

}
