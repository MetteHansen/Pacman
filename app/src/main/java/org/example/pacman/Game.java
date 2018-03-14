package org.example.pacman;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Random;

public class Game {
    //context is a reference to the activity
    private Context context;
    private int direction, pacx, pacy, coinCounter;
    private int gameLevel = 1;
    private int points = 0;
    //how points do we have
    //bitmap of the pacman
    //husk at tilføje en up og down mere til højre og venstre ups and downs
    private Bitmap pacBitmapRight, pacBitmapLeft, pacBitmapUp, pacBitmapDown, goldCoin, ghostPicture;
    //textview reference to points
    private TextView pointsView;
    private Enemy enemy;
    //the list of goldcoins - initially empty
    private ArrayList<GoldCoin> coins = new ArrayList<>();
    //a reference to the gameview
    private GameView gameView;
    private int h,w; //height and width of screen
    private boolean gamePaused, coinsInit, levelCompleted;

    public Game(Context context, TextView view) {
        this.context = context;
        this.pointsView = view;
        pacBitmapRight = BitmapFactory.decodeResource(context.getResources(), R.drawable.pacman_right);
        pacBitmapLeft = BitmapFactory.decodeResource(context.getResources(), R.drawable.pacman_left);
        pacBitmapUp = BitmapFactory.decodeResource(context.getResources(), R.drawable.pacman_right_up);
        pacBitmapDown = BitmapFactory.decodeResource(context.getResources(), R.drawable.pacman_right_down);
        goldCoin = BitmapFactory.decodeResource(context.getResources(), R.drawable.gold_coin);
        ghostPicture = BitmapFactory.decodeResource(context.getResources(), R.drawable.ghost);
    }

    public void setGameView(GameView view)
    {
        this.gameView = view;
    }
    //New game.
    public void newGame() {
        Random r = new Random(System.currentTimeMillis());

        pacx = 50;
        pacy = 400; //just some starting coordinates
        //reset the points
        direction = 0;
        gamePaused = false;
        coinsInit = false;
        levelCompleted = false;
        pointsView.setText(context.getResources().getString(R.string.points)+" "+ getPoints());

        //vi er nød til at tømme coins hver gang vi skifter level, fordi ellers ligger de tagede mønter stadig i
        //hukommelsen og derfor vil vores counter tjeck ikke blive fortaget korrekt.
        coins.clear();
        for(int i = 0; i<gameLevel*1; i++){
            coins.add(new GoldCoin());
        }
        coinCounter = coins.size();

        enemy = new Enemy();
        enemy.setY(10);
        enemy.setX(10);

        gameView.invalidate(); //redraw screen
    }
    //Increments level and resets board to show this.
    public void nextLevel(){
        gameLevel++;
        newGame();
    }

    public void setSize(int h, int w){
        this.h = h;
        this.w = w;
    }
    //Pacman move methods
    public void movePacmanRight(int pixels){
        //still within our boundaries?
        if (pacx+pixels+pacBitmapRight.getWidth()<w) {
            pacx = pacx + pixels;
            direction = 1;
            doCollisionCheck();
            gameView.invalidate();
        }
    }
    public void movePacmanLeft (int pixels){
        if(pacx-pixels >= 0){
            pacx = pacx - pixels;
            direction = 2;
            doCollisionCheck();
            gameView.invalidate();
        }
    }
    public void movePacmanUp (int pixels){
        if(pacy-pixels >= 0){
            pacy = pacy - pixels;
            direction = 3;
            doCollisionCheck();
            gameView.invalidate();
        }
    }
    public void movePacmanDown (int pixels){
        if(pacy+pixels+pacBitmapDown.getHeight()<h){
            pacy = pacy + pixels;
            direction = 4;
            doCollisionCheck();
            gameView.invalidate();
        }
    }
    //Pause function and check.
    public void pauseGame(){
        gamePaused = true;
    }
    public void resumeGame(){
        gamePaused = false;
    }
    public boolean isGamePaused(){
        return gamePaused;
    }

    //Enemy movement er sat til at gange 2 med det nuværende level.
    //Dette skal testes for at finde den rigtige værdi.

    public void moveEnemyUp(){

        enemy.moveEnemyUp(2*gameLevel);
        enemyCollision();
    }
    public void moveEnemyDown(){
        enemy.moveEnemyDown(2*gameLevel);
        enemyCollision();
    }
    public void moveEnemyLeft(){
        enemy.moveEnemyLeft(2*gameLevel);
        enemyCollision();
    }
    public void moveEnemyRight(){
        enemy.moveEnemyRight(2*gameLevel);
        enemyCollision();
    }
    //TODO check if the pacman touches a gold coin
    //and if yes, then update the neccesseary data
    //for the gold coins and the points
    //so you need to go through the arraylist of goldcoins and
    //check each of them for a collision with the pacman
    public void doCollisionCheck(){
        for(GoldCoin g: coins) {
            int pacXmid = getPacx() + (pacBitmapRight.getWidth() / 2);
            int pacYmid = getPacy() + (pacBitmapRight.getHeight() / 2);
            int gcXmid = g.getGcx() + (goldCoin.getWidth() / 2);
            int gcYmid = g.getGcy() + (goldCoin.getHeight() / 2);
            double colliosion = Math.sqrt(((pacXmid - gcXmid)*(pacXmid - gcXmid)) + ((pacYmid - gcYmid)*(pacYmid - gcYmid)));

            if(colliosion <= (pacBitmapRight.getWidth()/2) && colliosion <= (pacBitmapRight.getHeight()/2) && !g.isCoinTaken()){
                g.takeCoin();
                coinCounter--;
                points += 10;
                pointsView.setText(context.getResources().getString(R.string.points)+" "+ getPoints());
            }
        }
            gameView.invalidate();
        }

    //Does the same as coins except kills the pac man if the ghost touches.
    public void enemyCollision(){
            int enemyXmid = getEnemyX() + (ghostPicture.getWidth()/2);
            int enemyYmid = getEnemyY() + (ghostPicture.getHeight()/2);
            int pacXmid = getPacx() + (pacBitmapRight.getWidth()/2);
            int pacYmid = getPacy() + (pacBitmapRight.getHeight()/2);

            double colliosion = Math.sqrt(((enemyXmid - pacXmid)*(enemyXmid - pacXmid)) + ((enemyYmid - pacYmid)*(enemyYmid - pacYmid)));

            if(colliosion <= pacBitmapRight.getWidth() && colliosion <= pacBitmapRight.getHeight()){

                pauseGame();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            final Intent email = new Intent(Intent.ACTION_SEND);
                            email.setType("plain/text");
                            email.putExtra(Intent.EXTRA_SUBJECT, "Look at my score!");
                            email.putExtra(Intent.EXTRA_TEXT, "This was my score, " + getPoints() + " in the amazing Pac-man game by the students from EAAA");
                            context.startActivity(Intent.createChooser(email,"Send mail"));
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
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setTitle("Game Over");
                dialog.setMessage("Your score was: " + getPoints() + ", and current level is: " + gameLevel + ". Do you want to share it?");
                dialog.show();
            }
            gameView.invalidate();
        }

    public void intializeCoin(){
        Random r = new Random(System.currentTimeMillis());

        for (GoldCoin g: coins) {
            g.setX(r.nextInt(w - pacBitmapRight.getWidth()));
            g.sety(r.nextInt(h - pacBitmapRight.getHeight()));
        }
        coinsInit = true;


    }
    public boolean isInit(){
        if(coinsInit == true)
        return true;
        else{
            return false;
        }
    }
    public int getPacx()
    {
        return pacx;
    }
    public int getPacy()
    {
        return pacy;
    }
    public int getPoints()
    {
        return points;
    }
    public ArrayList<GoldCoin> getCoins()
    {
        return coins;
    }
    public Bitmap getPacBitmapRight()
    {
        return pacBitmapRight;
    }
    public Bitmap getPacBitmapLeft()
    {
        return pacBitmapLeft;
    }
    public Bitmap getPacBitmapDown()
    {
        return pacBitmapDown;
    }
    public Bitmap getPacBitmapUp()
    {
        return pacBitmapUp;
    }
    public int getDirection (){
        return direction;
    }
    public Bitmap getGoldCoin(){
        return goldCoin;
    }
    public void changeDirectionToRight(){
        direction = 1;
    }
    public void changeDirectionToLeft(){
        direction = 2;
    }
    public void changeDirectionToUp(){
        direction = 3;
    }
    public void changeDirectionToDown(){
        direction = 4;
    }
    public Bitmap getGhost(){
        return ghostPicture;
    }
    public int getEnemyX(){
        return enemy.getX();
    }
    public int getEnemyY(){
        return enemy.getY();
    }
    public void completeLevel(){
        levelCompleted = true;
        pauseGame();
        Toast.makeText(context, "Level " + gameLevel +  " complete!", Toast.LENGTH_SHORT).show();
        nextLevel();
    }
    public boolean areAllCoinsTaken(){
        if(coinCounter == 0){
            return true;
        } else
        return false;
    }
    public int getGameLevel(){
        return gameLevel;
    }

private class Enemy {
    private int x,y;

    public Enemy(){}

    public int getX(){
        return x;
    }
    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }

    public void moveEnemyUp(int pixels){
        if(y-pixels >= 0){
            y = y-pixels;
            gameView.invalidate();
        }
    }
    public void moveEnemyDown(int pixels){
        if(y+pixels+ghostPicture.getHeight()<h){
            y = y+pixels;
            gameView.invalidate();
        }
    }
    public void moveEnemyRight(int pixels){
        if(x+pixels+ghostPicture.getWidth()<w ){
            x = x+pixels;
            gameView.invalidate();
        }
    }
    public void moveEnemyLeft(int pixels){
        if(x-pixels >= 0){
            x = x - pixels;
            gameView.invalidate();
            }
        }
    }
}
