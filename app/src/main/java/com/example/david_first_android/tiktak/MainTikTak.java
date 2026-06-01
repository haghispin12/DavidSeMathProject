package com.example.david_first_android.tiktak;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.david_first_android.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainTikTak extends AppCompatActivity {

    // --- רכיבי ממשק משתמש (UI) ---
    Tile tile1_1, tile1_2, tile1_3;
    Tile tile2_1, tile2_2, tile2_3;
    Tile tile3_1, tile3_2, tile3_3;
    TextView TV_turn, TV_timer;

    // --- חיבורים ל-Firebase ---
    DatabaseReference db;

    // --- ניהול מצב המשחק (State) ---
    String gameId;
    String uid;
    String myNickname;
    Game currentGame;
    boolean gameOver = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tik_tak);

        db = FirebaseDatabase.getInstance().getReference();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        TV_turn = findViewById(R.id.TV_turn);
        TV_timer = findViewById(R.id.TV_timer);

        // אתחול אריחי המשחק
        tile1_1 = new Tile(findViewById(R.id.tile1_1));
        tile1_2 = new Tile(findViewById(R.id.tile1_2));
        tile1_3 = new Tile(findViewById(R.id.tile1_3));
        tile2_1 = new Tile(findViewById(R.id.tile2_1));
        tile2_2 = new Tile(findViewById(R.id.tile2_2));
        tile2_3 = new Tile(findViewById(R.id.tile2_3));
        tile3_1 = new Tile(findViewById(R.id.tile3_1));
        tile3_2 = new Tile(findViewById(R.id.tile3_2));
        tile3_3 = new Tile(findViewById(R.id.tile3_3));

        // מאזיני לחיצה
        tile1_1.getIv().setOnClickListener(v -> handleClick(0));
        tile1_2.getIv().setOnClickListener(v -> handleClick(1));
        tile1_3.getIv().setOnClickListener(v -> handleClick(2));
        tile2_1.getIv().setOnClickListener(v -> handleClick(3));
        tile2_2.getIv().setOnClickListener(v -> handleClick(4));
        tile2_3.getIv().setOnClickListener(v -> handleClick(5));
        tile3_1.getIv().setOnClickListener(v -> handleClick(6));
        tile3_2.getIv().setOnClickListener(v -> handleClick(7));
        tile3_3.getIv().setOnClickListener(v -> handleClick(8));

        findOrCreateGame();
    }

    private void listenToGame() {
        db.child("games").child(gameId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                currentGame = snapshot.getValue(Game.class);
                if (currentGame == null) return;

                // 1. מצב המתנה לשחקן שני
                if ("waiting".equals(currentGame.status)) {
                    TV_turn.setText("⏳ ממתין לשחקן שני שיצטרף...");
                    TV_timer.setVisibility(View.GONE);
                    return;
                }

                // 2. עדכון הלוח ב-UI
                updateBoardUI(currentGame.board);

                // 3. עדכון הטיימר מהשרת
                if (snapshot.hasChild("timer")) {
                    Long secondsLeft = snapshot.child("timer").getValue(Long.class);
                    if (secondsLeft != null) {
                        TV_timer.setVisibility(View.VISIBLE);
                        TV_timer.setText("⏳ זמן נותר: " + secondsLeft);
                    }
                }

                // 4. ניהול תור השחקנים
                if ("active".equals(currentGame.status)) {
                    TV_turn.setText(currentGame.turn.equals(uid) ? "🟢 תורך לשחק!" : "🔴 תור היריב...");

                    // הפעלת שירות הטיימר
                    android.content.Intent timerIntent = new android.content.Intent(MainTikTak.this, TurnTimerService.class);
                    timerIntent.putExtra("game_id", gameId);
                    timerIntent.putExtra("current_turn_uid", currentGame.turn);
                    timerIntent.putExtra("player1_uid", currentGame.player1);
                    timerIntent.putExtra("player2_uid", currentGame.player2);
                    startService(timerIntent);
                }

                // 5. ניהול סיום משחק (ניצחון/הפסד או תיקו)
                if ("finished".equals(currentGame.status) && !gameOver) {
                    gameOver = true;
                    stopService(new android.content.Intent(MainTikTak.this, TurnTimerService.class));
                    TV_timer.setVisibility(View.GONE);

                    if (currentGame.winner != null) {
                        if (currentGame.winner.equals(uid)) {
                            updateWins(() -> showEndGameFragment("🎉 ניצחת!"));
                        } else if ("tie".equals(currentGame.winner)) {
                            showEndGameFragment("🤝 זה תיקו!");
                        } else {
                            showEndGameFragment("😔 הפסדת");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("TIKTAK", "שגיאה ב-Firebase: " + error.getMessage());
            }
        });
    }

    // --- פונקציות עזר (handleClick, updateWins, וכו' נשארות כפי שהיו) ---
    private void handleClick(int index) {
        if (currentGame == null || currentGame.player2 == null || !currentGame.turn.equals(uid) || !currentGame.board.get(index).equals("")) return;

        String symbol = uid.equals(currentGame.player1) ? "X" : "O";
        currentGame.board.set(index, symbol);

        // א. בדיקת ניצחון
        if (isWinner(currentGame.board, symbol)) {
            db.child("games").child(gameId).child("board").setValue(currentGame.board);
            db.child("games").child(gameId).child("winner").setValue(uid);
            db.child("games").child(gameId).child("status").setValue("finished");
            return;
        }

        // ב. בדיקת תיקו
        if (isBoardFull(currentGame.board)) {
            db.child("games").child(gameId).child("board").setValue(currentGame.board);
            db.child("games").child(gameId).child("winner").setValue("tie");
            db.child("games").child(gameId).child("status").setValue("finished");
            return;
        }

        // ג. העברת תור רגילה
        String nextTurn = uid.equals(currentGame.player1) ? currentGame.player2 : currentGame.player1;
        db.child("games").child(gameId).child("board").setValue(currentGame.board);
        db.child("games").child(gameId).child("turn").setValue(nextTurn);
    }

    private void findOrCreateGame() {
        // שלב 1: הצגת טקסט המתנה מידי כדי לתת פידבק למשתמש
        TV_turn.setText("⏳ מחפש משחק זמין...");

        // שלב 2: חיפוש משחק פתוח בסטטוס "waiting"
        db.child("games").orderByChild("status").equalTo("waiting")
                .limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // מצאנו חדר קיים! מתחברים אליו
                            DataSnapshot gameSnapshot = snapshot.getChildren().iterator().next();
                            gameId = gameSnapshot.getKey();

                            // עדכון שחקן שני בחדר והעברת סטטוס ל-active
                            db.child("games").child(gameId).child("player2").setValue(uid);
                            db.child("games").child(gameId).child("status").setValue("active");

                            Log.d("TIKTAK", "הצטרפות לחדר קיים: " + gameId);
                            listenToGame();
                        } else {
                            // לא מצאנו חדר, יוצרים חדר חדש
                            gameId = db.child("games").push().getKey();
                            Game newGame = new Game(uid, myNickname);

                            // שמירה ב-Firebase
                            db.child("games").child(gameId).setValue(newGame);

                            // עדכון טקסט שהמתנה החלה
                            TV_turn.setText("⏳ ממתין לשחקן שני שיצטרף...");

                            Log.d("TIKTAK", "יצירת חדר חדש: " + gameId);
                            listenToGame();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("TIKTAK", "שגיאה בחיפוש משחק: " + error.getMessage());
                        TV_turn.setText("❌ שגיאה בחיבור לשרת");
                    }
                });
    }

    private void updateBoardUI(List<String> board) {
        setTile(tile1_1, board.get(0)); setTile(tile1_2, board.get(1)); setTile(tile1_3, board.get(2));
        setTile(tile2_1, board.get(3)); setTile(tile2_2, board.get(4)); setTile(tile2_3, board.get(5));
        setTile(tile3_1, board.get(6)); setTile(tile3_2, board.get(7)); setTile(tile3_3, board.get(8));
    }

    private void setTile(Tile tile, String value) {
        if (value.equals("X")) { tile.getIv().setImageResource(R.drawable.goodx); tile.setType(1); }
        else if (value.equals("O")) { tile.getIv().setImageResource(R.drawable.ofortiktak); tile.setType(2); }
        else { tile.getIv().setImageDrawable(null); tile.setType(0); }
    }

    private boolean isWinner(List<String> board, String symbol) {
        return (board.get(0).equals(symbol) && board.get(1).equals(symbol) && board.get(2).equals(symbol)) ||
                (board.get(3).equals(symbol) && board.get(4).equals(symbol) && board.get(5).equals(symbol)) ||
                (board.get(6).equals(symbol) && board.get(7).equals(symbol) && board.get(8).equals(symbol)) ||
                (board.get(0).equals(symbol) && board.get(3).equals(symbol) && board.get(6).equals(symbol)) ||
                (board.get(1).equals(symbol) && board.get(4).equals(symbol) && board.get(7).equals(symbol)) ||
                (board.get(2).equals(symbol) && board.get(5).equals(symbol) && board.get(8).equals(symbol)) ||
                (board.get(0).equals(symbol) && board.get(4).equals(symbol) && board.get(8).equals(symbol)) ||
                (board.get(2).equals(symbol) && board.get(4).equals(symbol) && board.get(6).equals(symbol));
    }

    private void updateWins(Runnable onComplete) {
        db.child("users").child(uid).child("wins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snapshot) {
                Long current = snapshot.getValue(Long.class);
                db.child("users").child(uid).child("wins").setValue((current == null ? 0 : current) + 1)
                        .addOnCompleteListener(task -> onComplete.run());
            }
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    private void showEndGameFragment(String result) {
        EndGameFragment fragment = new EndGameFragment();
        Bundle args = new Bundle();
        args.putString("result_message", result);

        // בדיקה קריטית: האם ה-currentGame קיים?
        if (currentGame != null) {
            Log.d("DEBUG_FRAGMENT", "שולח UIDs: " + currentGame.player1 + " ו-" + currentGame.player2);
            args.putString("player1_uid", currentGame.player1);
            args.putString("player2_uid", currentGame.player2);
        } else {
            Log.e("DEBUG_FRAGMENT", "שגיאה: currentGame הוא null!");
        }

        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, fragment)
                .commit();
    }


    private boolean isBoardFull(List<String> board) {
        for (String cell : board) {
            if (cell == null || cell.equals("")) {
                return false; // יש עוד מקום פנוי, המשחק ממשיך
            }
        }
        return true; // הלוח מלא, יצאנו לתיקו
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new android.content.Intent(this, TurnTimerService.class));
    }
}