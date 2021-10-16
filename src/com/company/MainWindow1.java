package com.company;

import javax.swing.*;

public class MainWindow1 extends JFrame {

    private Game game;

    public MainWindow1() {
        setSize(1000, 1000);
        setTitle("1");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(true);
        game = new Game(this);
        this.add(game);
        setVisible(true);
    }

    public boolean isEndOfGame(){
        return game.isEndOfGame();
    }
}