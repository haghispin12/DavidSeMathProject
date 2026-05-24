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

    // --- משתני תצוגה ---
    Tile tile1_1, tile1_2, tile1_3;
    Tile tile2_1, tile2_2, tile2_3;
    Tile tile3_1, tile3_2, tile3_3;
    TextView TV_winner; // מציג הודעת ניצחון/הפסד
    TextView TV_turn;   // מציג את התור הנוכחי

    // --- משתני Firebase ---
    DatabaseReference db; // חיבור לשורש מסד הנתונים בענן

    // --- משתני משחק ---
    String gameId;        // מזהה ייחודי של המשחק הנוכחי בFirebase
    String uid;           // מזהה ייחודי של המשתמש המחובר
    String myNickname;    // כאן נשמור את הכינוי שנשלוף מהשרת
    Game currentGame;     // האובייקט שמכיל את מצב המשחק הנוכחי
    boolean gameOver = false; // מונע פעולות כפולות אחרי סיום המשחק

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tik_tak);

        // אתחול Firebase - חיבור למסד הנתונים ושליפת ה-uid של המשתמש המחובר
        db = FirebaseDatabase.getInstance().getReference();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // חיבור רכיבי התצוגה ל-XML
        TV_winner = findViewById(R.id.TV_winner);
        TV_turn = findViewById(R.id.TV_turn);

        // חיבור כל משבצת בלוח לרכיב ה-ImageView המתאים לה ב-XML
        tile1_1 = new Tile(findViewById(R.id.tile1_1));
        tile1_2 = new Tile(findViewById(R.id.tile1_2));
        tile1_3 = new Tile(findViewById(R.id.tile1_3));
        tile2_1 = new Tile(findViewById(R.id.tile2_1));
        tile2_2 = new Tile(findViewById(R.id.tile2_2));
        tile2_3 = new Tile(findViewById(R.id.tile2_3));
        tile3_1 = new Tile(findViewById(R.id.tile3_1));
        tile3_2 = new Tile(findViewById(R.id.tile3_2));
        tile3_3 = new Tile(findViewById(R.id.tile3_3));

        // חיפוש משחק קיים שמחכה לשחקן, אם לא קיים - יוצר חדש
        findOrCreateGame();

        // הגדרת מאזין לחיצה לכל משבצת - כל משבצת מעבירה את האינדקס שלה (0-8)
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

    /**
     * מטפלת בלחיצה על משבצת בלוח.
     * מבצעת 4 בדיקות לפני ביצוע המהלך:
     * 1. האם המשחק נטען?
     * 2. האם שחקן שני הצטרף?
     * 3. האם זה התור של המשתמש הנוכחי?
     * 4. האם המשבצת ריקה?
     * אם כל הבדיקות עוברות - מעדכנת את הלוח בFirebase
     */
    private void handleClick(int index) {
        if (currentGame == null) return;
        if (currentGame.player2 == null) return;
        if (!currentGame.turn.equals(uid)) return;
        if (!currentGame.board.get(index).equals("")) return;

        // קביעת הסימבול
        String symbol = uid.equals(currentGame.player1) ? "X" : "O";
        currentGame.board.set(index, symbol);

        // בדיקה: האם המהלך הזה הרגע הביא לי ניצחון?
        if (isWinner(currentGame.board, symbol)) {
            gameOver = true; // חוסם כניסה כפולה למאזינים כדי למנוע בלאגן

            // 1. מעדכנים בשרת באופן ממוקד שהמשחק נגמר, מה הלוח הסופי ומי המנצח
            db.child("games").child(gameId).child("board").setValue(currentGame.board);
            db.child("games").child(gameId).child("winner").setValue(uid);
            db.child("games").child(gameId).child("status").setValue("finished");

            // 2. קוראים לפונקציה המעודכנת ומעבירים לה את פתיחת הפרגמנט כמשימה (Runnable)
            // זה פותר את השגיאה בשורה 105 ומבטיח סנכרון מושלם לטבלה!
            updateWins(new Runnable() {
                @Override
                public void run() {
                    showEndGameFragment("🎉 ניצחת!");
                }
            });
            return; // עוצרים כאן, אין צורך להעביר תור
        }

        // אם לא היה ניצחון, המשחק ממשיך כרגיל ומעבירים תור:
        String nextTurn = uid.equals(currentGame.player1) ? currentGame.player2 : currentGame.player1;

        db.child("games").child(gameId).child("board").setValue(currentGame.board);
        db.child("games").child(gameId).child("turn").setValue(nextTurn);
    }

    /**
     * בודקת אם שחקן מסוים ניצח.
     * בודקת את כל 8 הצירופים האפשריים לניצחון:
     * 3 שורות, 3 עמודות, 2 אלכסונים.
     */
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

    /**
     * מחפשת משחק קיים עם status="waiting" בFirebase.
     * אם נמצא - מצטרפת אליו כשחקן 2.
     * אם לא נמצא - יוצרת משחק חדש עם status="waiting" ומחכה לשחקן שני.
     * בשני המקרים קוראת ל-listenToGame() להתחיל האזנה.
     */
    private void findOrCreateGame() {
        Log.d("TIKTAK", "findOrCreateGame נקרא");

        db.child("games").orderByChild("status").equalTo("waiting")
                .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // נמצא משחק - מצטרף כשחקן 2
                            gameId = snapshot.getChildren().iterator().next().getKey();

                            db.child("games").child(gameId).child("player2").setValue(uid);
                            // שומרים את הכינוי של שחקן 2 בשרת בזמן ההצטרפות
                            db.child("games").child(gameId).child("player2Nickname").setValue(myNickname);
                            db.child("games").child(gameId).child("status").setValue("active");

                            listenToGame();
                        } else {
                            // לא נמצא משחק - יוצר משחק חדש ומעביר לבנאי גם את ה-UID וגם את הכינוי
                            gameId = db.child("games").push().getKey();
                            Game game = new Game(uid, myNickname); // מעבירים את הכינוי והID לבנאי

                            db.child("games").child(gameId).setValue(game);
                            db.child("games").child(gameId).onDisconnect().removeValue();
                            listenToGame();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.d("TIKTAK", "שגיאה: " + error.getMessage());
                    }
                });
    }

    /**
     * מצרפת listener קבוע למסמך המשחק בFirebase.
     * כל שינוי במשחק (לחיצה על משבצת, הצטרפות שחקן) מפעיל את onDataChange
     * אוטומטית על שני הטלפונים בו זמנית.
     * gameOver מונע מהלוגיקה לרוץ שוב אחרי סיום המשחק.
     */
    /**
     * מצרפת listener קבוע למסמך המשחק בFirebase.
     * כל שינוי במשחק (לחיצה על משבצת, הצטרפות שחקן) מפעיל את onDataChange
     * אוטומטית על שני הטלפונים בו זמנית.
     * gameOver מונע מהלוגיקה לרוץ שוב אחרי סיום המשחק.
     */
    private void listenToGame() {
        db.child("games").child(gameId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                currentGame = snapshot.getValue(Game.class);
                if (currentGame == null) return;

                if ("active".equals(currentGame.status)) {
                    db.child("games").child(gameId).onDisconnect().cancel();
                }

                // עדכון הלוח
                updateBoardUI(currentGame.board);

                // ניהול ה-TextView של התורות
                if ("waiting".equals(currentGame.status)) {
                    TV_turn.setText("⏳ ממתין לשחקן שני שיצטרף...");
                } else if ("active".equals(currentGame.status)) {
                    if (currentGame.turn.equals(uid)) {
                        TV_turn.setText("🟢 תורך לשחק!");
                    } else {
                        TV_turn.setText("🔴 תור היריב, ממתין למהלך...");
                    }
                } else if ("finished".equals(currentGame.status)) {
                    TV_turn.setText("🏁 המשחק הסתיים");
                }

                // 🔥 ניהול סיום המשחק על סמך מה שנקבע ב-Firebase
                if ("finished".equals(currentGame.status) && !gameOver) {
                    gameOver = true;

                    if (uid.equals(currentGame.winner)) {
                        // המכשיר של המנצח יציג את זה (הוא כבר קיבל את ה-+1 ב-handleClick)
                        showEndGameFragment("🎉 ניצחת!");
                    } else {
                        // המכשיר של המפסיד יציג את זה
                        showEndGameFragment("😔 הפסדת");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("TIKTAK", "שגיאה: " + error.getMessage());
            }
        });
    }

    /**
     * מעדכנת את תצוגת המשבצת לפי הערך שלה.
     * "" = ריק, "X" = תמונת איקס, "O" = תמונת עיגול
     */
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

    /**
     * עוברת על כל 9 המשבצות ומעדכנת את התצוגה
     * לפי הערכים שנמצאים ב-board מהFirebase
     */
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

    /**
     * מוסיפה +1 לשדה wins של המשתמש בFirebase.
     * קודם שולפת את הערך הנוכחי, מוסיפה 1, ושומרת בחזרה.
     * אם השדה לא קיים עדיין - מתחיל מ-1.
     */
    /**
     * מוסיפה +1 לשדה wins של המשתמש בFirebase.
     * מפעילה את ה-Runnable שנשלח אליה רק לאחר שהשמירה בענן הסתיימה בהצלחה.
     */
    private void updateWins(Runnable onComplete) {
        db.child("users").child(uid).child("wins")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Long currentWins = snapshot.getValue(Long.class);
                        long newWins = (currentWins == null ? 0 : currentWins) + 1;

                        // שמירה בשרת עם מאזין (OnCompleteListener) לסיום הכתיבה הפיזית בענן
                        db.child("users").child(uid).child("wins").setValue(newWins)
                                .addOnCompleteListener(task -> {
                                    if (onComplete != null) {
                                        onComplete.run(); // מפעיל את פתיחת הפרגמנט רק אחרי שהשמירה הסתיימה!
                                    }
                                });
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.d("TIKTAK", "שגיאה בעדכון הניצחונות: " + error.getMessage());
                    }
                });
    }

    /**
     * פותחת את ה-EndGameFragment על גבי מסך המשחק.
     * מעבירה את תוצאת המשחק (ניצחון/הפסד) כפרמטר.
     * ה-Fragment מציג לוח תוצאות ואפשרות למשחק חוזר.
     */
    private void showEndGameFragment(String result) {
        // 1. יצירת מופע חדש וחלק של הפרגמנט
        EndGameFragment fragment = new EndGameFragment();

        // 2. יצירת (Bundle) להעברת כל הנתונים יחד
        Bundle args = new Bundle();
        args.putString("result_message", result); // מעביר את "🎉 ניצחת!" או "😔 הפסדת"

        // 3. שליפת הכינויים מתוך אובייקט המשחק שמעודכן מה-Firebase
        if (currentGame != null) {
            args.putString("player1_name", currentGame.player1Nickname != null ? currentGame.player1Nickname : "שחקן 1");
            args.putString("player2_name", currentGame.player2Nickname != null ? currentGame.player2Nickname : "שחקן 2");
        } else {
            // גיבוי במקרה חריג שהאובייקט ריק
            args.putString("player1_name", "שחקן 1");
            args.putString("player2_name", "שחקן 2");
        }

        // 4. הצמדת הנתונים לפרגמנט
        fragment.setArguments(args);

        // 5. הצגת הפרגמנט על המסך
        getSupportFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, fragment)
                .commit();
    }

    /**
     * נקראת אוטומטית כשהמשתמש עוזב את המסך.
     * מסמנת את המשתמש כלא מחובר בFirebase
     * כדי שיוכל להתחבר שוב בפעם הבאה.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 1. מעדכן בשרת שהמשתמש התנתק
        if (uid != null) {
            db.child("users").child(uid).child("online").setValue(false);
        }

        // 2. אם שחקן 1 התחרט ויצא ממסך ההמתנה - מוחק את המשחק כדי שלא יישאר "משחק רפאים"
        if (currentGame != null && gameId != null) {
            if ("waiting".equals(currentGame.status) && uid.equals(currentGame.player1)) {
                db.child("games").child(gameId).removeValue();
            }
        }

    }
}