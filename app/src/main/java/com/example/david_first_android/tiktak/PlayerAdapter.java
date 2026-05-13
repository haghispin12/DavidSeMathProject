package com.example.david_first_android.tiktak;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.david_first_android.R;

import java.util.ArrayList;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    ArrayList<PlayerItem> players;

    public PlayerAdapter(ArrayList<PlayerItem> players) {
        this.players = players;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        PlayerItem player = players.get(position);
        holder.TV_rank.setText("#" + (position + 1));
        holder.TV_email.setText(player.email);
        holder.TV_wins.setText(String.valueOf(player.wins));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView TV_rank, TV_email, TV_wins;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            TV_rank = itemView.findViewById(R.id.TV_rank);
            TV_email = itemView.findViewById(R.id.TV_player_email);
            TV_wins = itemView.findViewById(R.id.TV_player_wins);
        }
    }
}