package com.example.david_first_android.tiktak;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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

    Tile tile1_1, tile1_2, tile1_3;
    Tile tile2_1, tile2_2, tile2_3;
    Tile tile3_1, tile3_2, tile3_3;
    TextView TV_winner;
    TextView TV_turn;

    DatabaseReference db;
    String gameId;
    String uid;
    Game currentGame;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tik_tak);

        //
        db = FirebaseDatabase.getInstance().getReference();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        TV_winner = findViewById(R.id.TV_winner);
        TV_turn = findViewById(R.id.TV_turn);

        //חיבור הטיילים לID שלהם
        tile1_1 = new Tile(findViewById(R.id.tile1_1));
        tile1_2 = new Tile(findViewById(R.id.tile1_2));
        tile1_3 = new Tile(findViewById(R.id.tile1_3));
        tile2_1 = new Tile(findViewById(R.id.tile2_1));
        tile2_2 = new Tile(findViewById(R.id.tile2_2));
        tile2_3 = new Tile(findViewById(R.id.tile2_3));
        tile3_1 = new Tile(findViewById(R.id.tile3_1));
        tile3_2 = new Tile(findViewById(R.id.tile3_2));
        tile3_3 = new Tile(findViewById(R.id.tile3_3));

        findOrCreateGame();

        tile1_1.getIv().setOnClickListener(v -> handleClick(0));
        tile1_2.getIv().setOnClickListener(v -> handleClick(1));
        tile1_3.getIv().setOnClickListener(v -> handleClick(2));
        tile2_1.getIv().setOnClickListener(v -> handleClick(3));
        tile2_2.getIv().setOnClickListener(v -> handleClick(4));
        tile2_3.getIv().setOnClickListener(v -> handleClick(5));
        tile3_1.getIv().setOnClickListener(v -> handleClick(6));
        tile3_2.getIv().setOnClickListener(v -> handleClick(7));
        tile3_3.getIv().setOnClickListener(v -> handleClick(8));
    }

    // על משבצת בלוחהפעולה נקראת כל פעם שמתמש לוחץ
    private void handleClick(int index) {
        if (currentGame == null) return;
        if (currentGame.player2 == null) return;
        if (!currentGame.turn.equals(uid)) return;
        if (!currentGame.board.get(index).equals("")) return;

        String symbol = uid.equals(currentGame.player1) ? "X" : "O";
        currentGame.board.set(index, symbol);


        String nextTurn;
        if (uid.equals(currentGame.player1)) {
            nextTurn = currentGame.player2;
        } else {
            nextTurn = currentGame.player1;
        }

        db.child("games").child(gameId).child("board").setValue(currentGame.board);
        db.child("games").child(gameId).child("turn").setValue(nextTurn);

    }

    //בודקת אם מישהו מנצח
    private boolean isWinner(List<String> board, String symbol) {
        // שורות
        if (board.get(0).equals(symbol) && board.get(1).equals(symbol) && board.get(2).equals(symbol)) return true;
        if (board.get(3).equals(symbol) && board.get(4).equals(symbol) && board.get(5).equals(symbol)) return true;
        if (board.get(6).equals(symbol) && board.get(7).equals(symbol) && board.get(8).equals(symbol)) return true;
        // עמודות
        if (board.get(0).equals(symbol) && board.get(3).equals(symbol) && board.get(6).equals(symbol)) return true;
        if (board.get(1).equals(symbol) && board.get(4).equals(symbol) && board.get(7).equals(symbol)) return true;
        if (board.get(2).equals(symbol) && board.get(5).equals(symbol) && board.get(8).equals(symbol)) return true;
        // אלכסונים
        if (board.get(0).equals(symbol) && board.get(4).equals(symbol) && board.get(8).equals(symbol)) return true;
        if (board.get(2).equals(symbol) && board.get(4).equals(symbol) && board.get(6).equals(symbol)) return true;
        return false;
    }

    //
    private void findOrCreateGame() {
        Log.d("TIKTAK", "findOrCreateGame נקרא");

        db.child("games").orderByChild("status").equalTo("waiting")
                .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            gameId = snapshot.getChildren().iterator().next().getKey();
                            db.child("games").child(gameId).child("player2").setValue(uid);
                            db.child("games").child(gameId).child("status").setValue("active");
                            listenToGame();
                        } else {
                            gameId = db.child("games").push().getKey();
                            Game game = new Game(uid);
                            db.child("games").child(gameId).setValue(game);
                            listenToGame();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.d("TIKTAK", "שגיאה: " + error.getMessage());
                    }
                });
    }

    private void listenToGame() {
        db.child("games").child(gameId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                currentGame = snapshot.getValue(Game.class);
                if (currentGame == null) return;

                updateBoardUI(currentGame.board);

                String mySymbol = uid.equals(currentGame.player1) ? "X" : "O";
                String opponentSymbol = mySymbol.equals("X") ? "O" : "X";

                if (isWinner(currentGame.board, mySymbol)) {
                    updateWins();
                    showEndGameFragment("🎉 ניצחת!");
                } else if (isWinner(currentGame.board, opponentSymbol)) {
                    showEndGameFragment("😔 הפסדת");
                } else if (currentGame.player2 == null) {
                    TV_turn.setText("ממתין לשחקן שני...");
                } else if (currentGame.turn.equals(uid)) {
                    TV_turn.setText("התור שלך!");
                } else {
                    TV_turn.setText("התור של היריב");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("TIKTAK", "שגיאה: " + error.getMessage());
            }
        });

    }

    private void setTile(Tile tile, String value) {
        if (value == null || value.equals("")) {
            tile.getIv().setImageDrawable(null);
            tile.setType(0);
            return;
        }
        if (value.equals("X")) {
            tile.getIv().setImageResource(R.drawable.goodx);
            tile.setType(1);
        }
        if (value.equals("O")) {
            tile.getIv().setImageResource(R.drawable.ofortiktak);
            tile.setType(2);
        }
    }

    private void updateBoardUI(List<String> board) {
        setTile(tile1_1, board.get(0));
        setTile(tile1_2, board.get(1));
        setTile(tile1_3, board.get(2));
        setTile(tile2_1, board.get(3));
        setTile(tile2_2, board.get(4));
        setTile(tile2_3, board.get(5));
        setTile(tile3_1, board.get(6));
        setTile(tile3_2, board.get(7));
        setTile(tile3_3, board.get(8));
    }


    //בסוף המשחק מתווסף +1 לשדה נצחונות בפיירבייס עבור המשתמש הספציפי
    private void updateWins() {
        db.child("users").child(uid).child("wins")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Long currentWins = snapshot.getValue(Long.class);
                        long newWins = (currentWins == null ? 0 : currentWins) + 1;
                        db.child("users").child(uid).child("wins").setValue(newWins);
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }

    //מעבר לפרגמנט סיום המשחק
    private void showEndGameFragment(String result) {
        EndGameFragment fragment = EndGameFragment.newInstance(result);
        getSupportFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, fragment)
                .commit();
    }


}



