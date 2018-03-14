package org.example.pacman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.button;
import static android.R.attr.textViewStyle;

public class MainActivity extends Activity {
    //reference to the main view
    GameView gameView;
    //reference to the game class.
    Game game;

    TextView textViewPoints, textViewTimer;

    //Pacmans mover timer
    private Timer pacTimer, gameTimer, enemyTimer;
    private int timeCounter;


    //is the app running?
    private boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //saying we want the game to run in one mode only
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        gameView =  findViewById(R.id.gameView);
        textViewPoints = findViewById(R.id.points);
        textViewTimer = findViewById(R.id.timer);

        //timer for Pacman, the Game, Enemy and init.
        pacTimer = new Timer();
        gameTimer = new Timer();
        enemyTimer = new Timer();
        //counteren bliver sat til 60, så gamelogikken er at spilleren har 60 seconder
        //til at komme så langt som muligt.
        timeCounter = 60;

        running = true; //should the game be running?
        // how often/fast should pacman move
        pacTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }
        },0, 50);

        enemyTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                EnemyTimerMethod();
            }
        },0,50);
        //first parameter indicates the start point, we start now.
        //second parameter is the number of milliseconds between the calls

        gameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                CounterMethod();
            }
        },0,1000);
        game = new Game(this,textViewPoints);
        game.setGameView(gameView);
        gameView.setGame(game);

        game.newGame();
        if(timeCounter == 0){
            buildAlert();
        }
        // BUTTONS
        //PAUSE
        final Button pauseButton = findViewById(R.id.pause);
        pauseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View p){
                if(!game.isGamePaused()){
                    game.pauseGame();
                    pauseButton.setText("C");
                } else if(game.isGamePaused()){
                    game.resumeGame();
                    pauseButton.setText("P");
                }
            }
        });
        //RIGHT
        Button buttonRight = findViewById(R.id.moveRight);
        //listener of our pacman, when somebody clicks it
        buttonRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                game.changeDirectionToRight();
            }
        });
        //LEFT
        Button buttonLeft = findViewById(R.id.moveLeft);
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.changeDirectionToLeft();
            }
        });
        // UP
        Button buttonUp = findViewById(R.id.moveUp);
        buttonUp.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                game.changeDirectionToUp();

            }
        });
        // DOWN
        Button buttonDown = findViewById(R.id.moveDown);
        buttonDown.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                game.changeDirectionToDown();
            }
        });
}
    @Override
    protected void onStop(){
        super.onStop();
        pacTimer.cancel();
        gameTimer.cancel();
        enemyTimer.cancel();
    }
    private void CounterMethod(){
        this.runOnUiThread(Counter_Tick);
    }
    private Runnable Counter_Tick = new Runnable() {
        @Override
        public void run() {
            textViewTimer.setText("Time Left: " + timeCounter);
            if(!game.isGamePaused()){
                if(running){
                    if(game.getDirection() != 0)
                        timeCounter--;
                    if(timeCounter == 0){
                        game.pauseGame();
                        buildAlert();
                    }
                }
            }
        }
    };

    private void EnemyTimerMethod(){
        this.runOnUiThread(Enemy_Tick);
    }
    private Runnable Enemy_Tick = new Runnable() {
        @Override
        public void run() {
            if(!game.isGamePaused()){
                if(running) {
                    if(game.getDirection()!= 0){
                    if (game.getEnemyX() < game.getPacx()) {
                        game.moveEnemyRight();
                    } else if (game.getEnemyY() < game.getPacy()) {
                        game.moveEnemyDown();
                    } else if (game.getEnemyX() > game.getPacx()) {
                        game.moveEnemyLeft();
                    } else if (game.getEnemyY() > game.getPacy()) {
                        game.moveEnemyUp();
                    }
                }
                }
            }
        }
    };
    private void TimerMethod(){
        this.runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable() {
        @Override
        public void run() {
            if(!game.isGamePaused()){
            if(running){
                if(game.getDirection()==1){
                    game.movePacmanRight(20);
                } else if (game.getDirection() == 2){
                    game.movePacmanLeft(20);
                } else if (game.getDirection() == 3){
                    game.movePacmanUp(20);
                } else if (game.getDirection() == 4){
                    game.movePacmanDown(20);
                }
            }
        }
    }
};

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(this,"settings clicked",Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_newGame) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void buildAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/text");
                email.putExtra(Intent.EXTRA_SUBJECT, "Look at my score!");
                email.putExtra(Intent.EXTRA_TEXT, "This was my score, " + game.getPoints() + " in the amazing Pac-man game by the students from EAAA");
                getApplicationContext().startActivity(Intent.createChooser(email,"Send mail"));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNeutralButton("New Game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                getApplicationContext().startActivity(intent);
            }
        });



        AlertDialog dialog = builder.create();
        dialog.setTitle("Times Up");
        dialog.setMessage("Your score was: "
                + game.getPoints()
                + ", and current level is: "
                + game.getGameLevel()
                + ". Do you want to share it?");
        dialog.show();




    }
}
