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

    // בנאי דיפולטיבי (חובה בפרגמנטים)
    public EndGameFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // שליפת הודעת התוצאה שהועברה מ-MainTikTak
        if (getArguments() != null) {
            result = getArguments().getString("result_message");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_end_game, container, false);

        // חיבור רכיבי התצוגה
        TV_result = v.findViewById(R.id.TV_result);
        BTN_rematch = v.findViewById(R.id.BTN_rematch);
        RV_leaderboard = v.findViewById(R.id.RV_leaderboard);

        db = FirebaseDatabase.getInstance().getReference();
        playerList = new ArrayList<>();

        // הצגת תוצאת המשחק הנוכחי
        TV_result.setText(result);

        // הגדרת ה-RecyclerView עבור טבלת המובילים
        adapter = new PlayerAdapter(playerList);
        RV_leaderboard.setLayoutManager(new LinearLayoutManager(getActivity()));
        RV_leaderboard.setAdapter(adapter);
        RV_leaderboard.setHasFixedSize(true);

        // טעינת המשתמשים והניצחונות מה-Database
        loadLeaderboard();

        // מאזין לכפתור משחק חוזר - מאפס את מסך המשחק לחלוטין
        BTN_rematch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    android.content.Intent intent = getActivity().getIntent();
                    getActivity().finish(); // סוגר את המסך הישן והפרגמנט איתו
                    startActivity(intent);  // פותח מסך משחק חדש ונקי
                }
            }
        });

        return v;
    }

    /**
     * שולפת את כל המשתמשים מ-Firebase,
     * ממירה אותם לכינויים (Nicknames) וממיינת לפי כמות ניצחונות מהגבוה לנמוך.
     */
    private void loadLeaderboard() {
        db.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                playerList.clear();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    // שליפת הכינוי והניצחונות של המשתמש (במקום מייל)
                    String nickname = userSnapshot.child("nickname").getValue(String.class);
                    Long wins = userSnapshot.child("wins").getValue(Long.class);

                    if (nickname == null || nickname.trim().isEmpty()) {
                        nickname = "שחקן אלמוני"; // הגנה למקרה שאין כינוי
                    }

                    playerList.add(new PlayerItem(nickname, wins == null ? 0 : wins));
                }

                // מיון הרשימה בסדר יורד (מהניצחונות הגבוהים לנמוכים)
                Collections.sort(playerList, new Comparator<PlayerItem>() {
                    @Override
                    public int compare(PlayerItem a, PlayerItem b) {
                        return Long.compare(b.wins, a.wins);
                    }
                });

                // עדכון התצוגה בטבלה
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}