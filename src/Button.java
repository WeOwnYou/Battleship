package com.company;

import java.awt.*;

public class Button {

    private double x0Factor, y0Factor, widthFactor, heightFactor;
    private int typeOfScreen, typeOfButton, x0, y0, width, height;
    private Image buttonImg;
    public static final int START_SCREEN_BUTTON = 0, PLACE_SCREEN_BUTTON = 1, FIGHT_SCREEN_BUTTON = 2;
    public static final int PLAY_GAME_BUTTON = 3, EXIT_GAME_BUTTON = 4, ACCEPT_PLACING_BUTTON = 5, RESET_PLACING_BUTTON = 6;
    private static Game game;

    public Button(double x0Factor, double y0Factor, double widthFactor, double heightFactor, int typeOfScreen, int typeOfButton, Image img, Game game){
        this.x0Factor = x0Factor;
        this.y0Factor = y0Factor;
        this.widthFactor = widthFactor;
        this.heightFactor = heightFactor;
        this.buttonImg = img;
        this.typeOfButton = typeOfButton;
        this.typeOfScreen = typeOfScreen;
        this.game = game;
    }


    public Image getButtonImg() {
        return buttonImg;
    }

    public int getX0() {
        return x0;
    }

    public void setCoordinates(int screenWidth, int screenHeight){
        x0 = (int)(screenWidth * x0Factor);
        y0 = (int)(screenHeight * y0Factor);
        width =(int)(screenWidth * widthFactor);
        height = (int)(screenHeight * heightFactor);
    }


    public int getY0() {
        return y0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTypeOfScreen() {
        return typeOfScreen;
    }

    public int getTypeOfButton() {
        return typeOfButton;
    }

    public static void ationIfButtonIsPressed(int typeOfButton) {
        switch (typeOfButton) {
            case Button.PLAY_GAME_BUTTON:                                                                               //action
                game.findOpponent();
                game.setPlayPressed(true);                                                                              //action
                return;
            case Button.EXIT_GAME_BUTTON:
                game.closeGame();
                return;
            case Button.ACCEPT_PLACING_BUTTON:
                boolean isAllShipsPlaced = true;
                int t[] = game.getNumberOfShips();
                for (int i = 0; i < 5; i++)
                    if (t[i] != 0) {
                        isAllShipsPlaced = false;
                        break;
                    }
                if (isAllShipsPlaced && !game.isReady()) {
                    game.setReady(true);
                    game.setIsStartOfGame(true);
                    ThreadFactory.createThread(ThreadFactory.THREAD_FOR_LOADING, game);
//                    System.out.println("!!");
                }
                return;
            case Button.RESET_PLACING_BUTTON:
//                System.out.println("OO");
                game.resetGameField();
                game.resetNumberOfShips();
                game.resetShips();
                game.repaint();
                return;
        }
    }

    public static boolean conditionForNotUsing(int typeOfScreen) {
        switch (typeOfScreen) {
            case Button.START_SCREEN_BUTTON:
                return game.isGameStarted();
            case Button.PLACE_SCREEN_BUTTON:
                return (game.isBattleCondition() == game.isLocalConnection()) || game.isReady();//(!(game.isBattleCondition() || game.isGameStarted()) && !game.isReady());
            case Button.FIGHT_SCREEN_BUTTON:
                return true;
        }
        return true;
    }
}
