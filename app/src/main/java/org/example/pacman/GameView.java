package org.example.pacman;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class GameView extends View {

	Game game;
    int h,w; //used for storing our height and width of the view

	public void setGame(Game game)
	{
		this.game = game;
	}
	public GameView(Context context) {
		super(context);

	}
	public GameView(Context context, AttributeSet attrs) {
		super(context,attrs);
	}
	public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr);
	}

	//In the onDraw we put all our code that should be
	//drawn whenever we update the screen.
	@Override
	protected void onDraw(Canvas canvas) {
		//Here we get the height and weight
		h = canvas.getHeight();
		w = canvas.getWidth();
		//update the size for the canvas to the game.
		game.setSize(h,w);
		Log.d("GAMEVIEW","h = "+h+", w = "+w);
		//Making a new paint object
		Paint paint = new Paint();
		canvas.drawColor(Color.WHITE); //clear entire canvas to white color
		canvas.drawBitmap(game.getGhost(),game.getEnemyX(), game.getEnemyY(), paint);

		//draw the pacman
		if(game.getDirection() == 0){
			canvas.drawBitmap(game.getPacBitmapRight(),game.getPacx(), game.getPacy(), paint);
		} else if(game.getDirection() == 1){
		canvas.drawBitmap(game.getPacBitmapRight(), game.getPacx(),game.getPacy(), paint);
		} else if(game.getDirection() == 2){
			canvas.drawBitmap(game.getPacBitmapLeft(), game.getPacx(), game.getPacy(), paint);
		} else if (game.getDirection() == 3){
			canvas.drawBitmap(game.getPacBitmapUp(), game.getPacx(), game.getPacy(), paint);
		}else if (game.getDirection() == 4){
			canvas.drawBitmap(game.getPacBitmapDown(), game.getPacx(), game.getPacy(), paint);
		}
		if(!game.isInit()) {
			game.intializeCoin();
		}
		if(game.isInit()) {
			for (GoldCoin g : game.getCoins()) {
				if(!g.isCoinTaken()) {
					canvas.drawBitmap(game.getGoldCoin(), g.getGcx(), g.getGcy(), paint);
				}
			}
			if(game.areAllCoinsTaken()){
				game.completeLevel();
			}
		}
	super.onDraw(canvas);
	}
}
