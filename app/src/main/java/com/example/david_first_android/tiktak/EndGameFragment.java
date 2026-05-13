package com.example.david_first_android.tiktak;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.david_first_android.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EndGameFragment extends Fragment {

    TextView TV_result;
    Button BTN_rematch;
    RecyclerView RV_leaderboard;
    DatabaseReference db;
    ArrayList<PlayerItem> playerList;
    PlayerAdapter adapter;
    String result;

    public static EndGameFragment newInstance(String result) {
        EndGameFragment fragment = new EndGameFragment();
        Bundle args = new Bundle();
        args.putString("result", result);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            result = getArguments().getString("result");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_end_game, container, false);

        TV_result = v.findViewById(R.id.TV_result);
        BTN_rematch = v.findViewById(R.id.BTN_rematch);
        RV_leaderboard = v.findViewById(R.id.RV_leaderboard);

        db = FirebaseDatabase.getInstance().getReference();
        playerList = new ArrayList<>();

        TV_result.setText(result);

        adapter = new PlayerAdapter(playerList);
        RV_leaderboard.setLayoutManager(new LinearLayoutManager(getActivity()));
        RV_leaderboard.setAdapter(adapter);
        RV_leaderboard.setHasFixedSize(true);

        loadLeaderboard();

        BTN_rematch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // נמשיך בשלב הבא
            }
        });

        return v;
    }

    private void loadLeaderboard() {
        db.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                playerList.clear();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);
                    Long wins = userSnapshot.child("wins").getValue(Long.class);

                    if (email != null) {
                        playerList.add(new PlayerItem(email, wins == null ? 0 : wins));
                    }
                }

                Collections.sort(playerList, new Comparator<PlayerItem>() {
                    @Override
                    public int compare(PlayerItem a, PlayerItem b) {
                        return Long.compare(b.wins, a.wins);
                    }
                });

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}