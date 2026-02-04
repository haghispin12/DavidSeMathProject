package com.example.david_first_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter< UserAdapter.MyViewHolder> {

    private ArrayList<User> users;
    private InterOnItemClick interOnItemClick;



    //פעולה בונה של האדפטר שמקשר בין המערך לתצוגה בפרגמנט
    public UserAdapter(ArrayList<User> users, InterOnItemClick interOnItemClick) {
        this.users = users;
        this.interOnItemClick = interOnItemClick;
    }

    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);

        return new MyViewHolder(view);
    }

    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(users.get(position), interOnItemClick);
    }

    public int getItemCount() {
        return users.size();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView TV_username ;
        ImageView IV_userImage;

        // xml מבצע השמה של ערכי האוביקט עם הפקדים מה
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            TV_username = itemView.findViewById(R.id. TV_username);
            IV_userImage = itemView.findViewById(R.id. IV_userImage);
        }

        public void bind(final User item, final InterOnItemClick
                interOnItemClick)      {
            TV_username.setText(item.getUserName());
            IV_userImage.setImageBitmap(item.getBitmap());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    interOnItemClick.onItemClick(item);
                }
            });
        }

    }//end inner class



}

