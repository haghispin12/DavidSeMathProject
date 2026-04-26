package com.example.david_first_android.tiktak;




import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.david_first_android.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MainTikTak extends AppCompatActivity {

    Tile tile1_1, tile1_2, tile1_3;
    Tile tile2_1, tile2_2, tile2_3;
    Tile tile3_1, tile3_2, tile3_3;
    TextView TV_winner;

    FirebaseFirestore fb;

    String gameId;
    String uid;

    Game currentGame;




    private boolean isWinner(int playerType) {
        // שורות
        if (tile1_1.getType() == playerType && tile1_2.getType() == playerType && tile1_3.getType() == playerType)
            return true;
        if (tile2_1.getType() == playerType && tile2_2.getType() == playerType && tile2_3.getType() == playerType)
            return true;
        if (tile3_1.getType() == playerType && tile3_2.getType() == playerType && tile3_3.getType() == playerType)
            return true;

        // עמודות
        if (tile1_1.getType() == playerType && tile2_1.getType() == playerType && tile3_1.getType() == playerType)
            return true;
        if (tile1_2.getType() == playerType && tile2_2.getType() == playerType && tile3_2.getType() == playerType)
            return true;
        if (tile1_3.getType() == playerType && tile2_3.getType() == playerType && tile3_3.getType() == playerType)
            return true;

        // אלכסונים
        if (tile1_1.getType() == playerType && tile2_2.getType() == playerType && tile3_3.getType() == playerType)
            return true;
        if (tile1_3.getType() == playerType && tile2_2.getType() == playerType && tile3_1.getType() == playerType)
            return true;

        return false; // אין מנצח
    }


    private void createGame() {

        gameId = fb.collection("games").document().getId();

        Game game = new Game(uid); // משתמש במחלקה שלך

        fb.collection("games")
                .document(gameId)
                .set(game)
                .addOnSuccessListener(unused -> {

                    listenToGame();
                });
    }

    private void listenToGame() {

        fb.collection("games").document(gameId)
                .addSnapshotListener((snapshot, error) -> {

                    if (snapshot == null || !snapshot.exists()) return;

                    currentGame = snapshot.toObject(Game.class);

                    updateBoardUI(currentGame.board);
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


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_tik_tak);

        fb = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        createGame();

        TV_winner = findViewById(R.id.TV_winner);
        //כל המשבצות של הלוח
        tile1_1 = new Tile(findViewById(R.id.tile1_1));
        tile1_2 = new Tile(findViewById(R.id.tile1_2));
        tile1_3 = new Tile(findViewById(R.id.tile1_3));

        tile2_1 = new Tile(findViewById(R.id.tile2_1));
        tile2_2 = new Tile(findViewById(R.id.tile2_2));
        tile2_3 = new Tile(findViewById(R.id.tile2_3));

        tile3_1 = new Tile(findViewById(R.id.tile3_1));
        tile3_2 = new Tile(findViewById(R.id.tile3_2));
        tile3_3 = new Tile(findViewById(R.id.tile3_3));



// --- ONCLICK ---

        tile1_1.getIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tile1_1.getType() == 0) {
                    tile1_1.getIv().setImageResource(R.drawable.goodx);
                    tile1_1.setType(1);
                }
                if (isWinner(tile1_1.getType())) {
                    TV_winner.setText("YOU WIN!!!");
                }






            }
        });

        tile1_2.getIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tile1_2.getType() == 0) {
                    tile1_2.getIv().setImageResource(R.drawable.goodx);
                    tile1_2.setType(1);
                }
                if (isWinner(tile1_2.getType())) {
                    TV_winner.setText("YOU WIN!!!");
                }
            }
        });

        tile1_3.getIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tile1_3.getType() == 0) {
                    tile1_3.getIv().setImageResource(R.drawable.goodx);
                    tile1_3.setType(1);
                }
                if (isWinner(tile1_3.getType())) {
                    TV_winner.setText("YOU WIN!!!");
                }
            }
        });

        tile2_1.getIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tile2_1.getType() == 0) {
                    tile2_1.getIv().setImageResource(R.drawable.goodx);
                    tile2_1.setType(1);
                }
                if (isWinner(tile2_1.getType())) {
                    TV_winner.setText("YOU WIN!!!");
                }
            }
        });

        tile2_2.getIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tile2_2.getType() == 0) {
                    tile2_2.getIv().setImageResource(R.drawable.goodx);
                    tile2_2.setType(1);
                }
                if (isWinner(tile2_2.getType())) {
                    TV_winner.setText("YOU WIN!!!");
                }
            }
        });

        tile2_3.getIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tile2_3.getType() == 0) {
                    tile2_3.getIv().setImageResource(R.drawable.goodx);
                    tile2_3.setType(1);
                }
                if (isWinner(tile2_3.getType())) {
                    TV_winner.setText("YOU WIN!!!");
                }
            }
        });

        tile3_1.getIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tile3_1.getType() == 0) {
                    tile3_1.getIv().setImageResource(R.drawable.goodx);
                    tile3_1.setType(1);
                }
                if (isWinner(tile3_1.getType())) {
                    TV_winner.setText("YOU WIN!!!");
                }
            }
        });

        tile3_2.getIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tile3_2.getType() == 0) {
                    tile3_2.getIv().setImageResource(R.drawable.goodx);
                    tile3_2.setType(1);
                }
                if (isWinner(tile3_2.getType())) {
                    TV_winner.setText("YOU WIN!!!");
                }
            }
        });

        tile3_3.getIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tile3_3.getType() == 0) {
                    tile3_3.getIv().setImageResource(R.drawable.goodx);
                    tile3_3.setType(1);
                }
                if (isWinner(tile3_3.getType())) {
                    TV_winner.setText("YOU WIN!!!");
                }
            }
        });

    }
}