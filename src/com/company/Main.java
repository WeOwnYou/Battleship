package com.company;


public class Main {
    public static void main(String[] args) {
        MainWindow1 mv = new MainWindow1();
        while (true) {
            if(mv.isEndOfGame()) {
                mv.dispose();
                mv = new MainWindow1();
            }
        }
    }
}