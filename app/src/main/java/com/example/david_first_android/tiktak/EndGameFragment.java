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

public class EndGameFragment extends Fragment {

    // רכיבי תצוגה
    TextView TV_result;
    Button BTN_rematch;
    RecyclerView RV_current_game, RV_global_leaderboard;

    // רשימות ואדפטרים נפרדים לשתי הטבלאות
    ArrayList<PlayerItem> globalList;
    ArrayList<PlayerItem> currentMatchList;
    PlayerAdapter globalAdapter;
    PlayerAdapter currentMatchAdapter;

    DatabaseReference db;
    String result;

    // מזהי השחקנים במשחק הנוכחי
    String p1Uid, p2Uid;

    public EndGameFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // שליפת הנתונים שהועברו ממסך המשחק
        if (getArguments() != null) {
            result = getArguments().getString("result_message");
            p1Uid = getArguments().getString("player1_uid");
            p2Uid = getArguments().getString("player2_uid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_end_game, container, false);

        // חיבור רכיבי טקסט וכפתור מה-XML
        TV_result = v.findViewById(R.id.TV_result);
        BTN_rematch = v.findViewById(R.id.BTN_rematch);
        TV_result.setText(result);

        // חיבור שני ה-RecyclerViews מה-XML החדש
        RV_current_game = v.findViewById(R.id.RV_current_game);
        RV_global_leaderboard = v.findViewById(R.id.RV_global_leaderboard);

        db = FirebaseDatabase.getInstance().getReference();

        // אתחול רשימות ואדפטרים (שימוש חוזר באדפטר הקיים)
        globalList = new ArrayList<>();
        currentMatchList = new ArrayList<>();

        globalAdapter = new PlayerAdapter(globalList);
        currentMatchAdapter = new PlayerAdapter(currentMatchList);

        // הגדרת לוח עולמי
        RV_global_leaderboard.setLayoutManager(new LinearLayoutManager(getActivity()));
        RV_global_leaderboard.setAdapter(globalAdapter);

        // הגדרת לוח ראש בראש
        RV_current_game.setLayoutManager(new LinearLayoutManager(getActivity()));
        RV_current_game.setAdapter(currentMatchAdapter);

        // טעינת הנתונים מהשרת
        loadLeaderboards();

        // מאזין לכפתור משחק חוזר - מאפס את האקטיביטי
        BTN_rematch.setOnClickListener(view -> {
            if (getActivity() != null) {
                // יוצרים Intent חדש לגמרי, לא משכפלים את הקיים
                android.content.Intent intent = new android.content.Intent(getActivity(), MainTikTak.class);

                // מנקים את כל הסטאק של האקטיביטיז הקודמים כדי שמשחק חוזר יהיה "נקי"
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);

                getActivity().finish();
                startActivity(intent);
            }
        });

        return v;
    }

    /**
     * שולפת את כל המשתמשים מ-Firebase ומפצלת אותם לשתי הטבלאות
     */
    private void loadLeaderboards() {
        db.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                globalList.clear();
                currentMatchList.clear();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userKey = userSnapshot.getKey();
                    String nickname = userSnapshot.child("nickname").getValue(String.class);
                    Long wins = userSnapshot.child("wins").getValue(Long.class);

                    if (nickname == null || nickname.trim().isEmpty()) {
                        nickname = "שחקן אלמוני";
                    }

                    PlayerItem player = new PlayerItem(nickname, wins == null ? 0 : wins);

                    // 1. הוספה קבועה לרשימה העולמית
                    globalList.add(player);

                    // 2. הוספה לרשימת הראש בראש רק אם ה-UID תואם לשחקני המשחק הנוכחי
                    if (userKey != null && (userKey.equals(p1Uid) || userKey.equals(p2Uid))) {
                        currentMatchList.add(player);
                    }
                }

                // 3. מיון שתי הרשימות בסדר יורד לפי כמות ניצחונות
                Collections.sort(globalList, (a, b) -> Long.compare(b.wins, a.wins));
                Collections.sort(currentMatchList, (a, b) -> Long.compare(b.wins, a.wins));

                // 4. עדכון שני האדפטרים להצגת השינויים במסך
                globalAdapter.notifyDataSetChanged();
                currentMatchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}