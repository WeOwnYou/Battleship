package com.company;

public class Ship {

    private int x0, x1, y0, y1, numberOfDecks;
    private boolean isHorizontal, isDestroyed;

    public Ship(int x0, int y0, int x1, int y1, boolean isHorizontal) {
        isDestroyed = false;
        this.isHorizontal = isHorizontal;
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        numberOfDecks = Math.max(Math.abs(this.x0-this.x1), Math.abs(this.y0 - this.y1)) + 1;

    }

    public int getY0() {
        return y0;
    }

    public int getX0() {
        return x0;
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public boolean getIsHorizontal() {
        return isHorizontal;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public void shipHitted() {
        numberOfDecks--;
//        System.out.println(numberOfDecks);
        if(numberOfDecks == 0)
            isDestroyed = true;
    }
}
