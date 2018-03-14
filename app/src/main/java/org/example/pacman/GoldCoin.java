package org.example.pacman;

/**
 * This class should contain information about a single GoldCoin.
 * such as x and y coordinates (int) and whether or not the goldcoin
 * has been taken (boolean)
 */

public class GoldCoin {
    private int gcx, gcy;
    private boolean coinTaken = false;

    public GoldCoin(){

    }
    public int getGcx(){
        return gcx;
    }
    public int getGcy(){
        return gcy;
    }
    public void setX(int x){
        gcx = x;
    }
    public void sety(int y){
        gcy = y;

    }
    public void takeCoin(){
        coinTaken = true;
    }
    public boolean isCoinTaken(){
        return coinTaken;
    }
}
