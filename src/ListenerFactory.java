package com.company;


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class ListenerFactory {

    public static final int MOUSE_LISTENER_WHILE_PLACING = 1;
    public static final int MOUSE_LISTENER_WHILE_FIGHTNG = 2;
    public static final int MOUSE_LISTENER_FOR_BUTTONS = 3;
    private static ArrayList<Pair> mouseFactory;

    public ListenerFactory() {
        mouseFactory = new ArrayList<Pair>();
    }

    public static MouseListener createMouseListener(int type, Game game) {
        MouseListener result = null;
        try {
            switch (type) {
                case MOUSE_LISTENER_WHILE_PLACING:
                    result = new MLWPlacing(game);
                    Pair<Integer, MouseListener> temp1 = new Pair<>(MOUSE_LISTENER_WHILE_PLACING, result);
                    mouseFactory.add(temp1);
                case MOUSE_LISTENER_WHILE_FIGHTNG:
                    result = new MLWFighting(game);
            }
        } catch (NullPointerException e) {
//            e.printStackTrace();
        }
        return result;
    }

    public static void deleteMouseListener(int type, Game game) {
        if (!game.isBattleCondition())
            return;

        try {
            switch (type) {
                case MOUSE_LISTENER_FOR_BUTTONS:
                    for (int i = 0; i < mouseFactory.size(); i++)
                        if ((int) mouseFactory.get(i).getFirst() == MOUSE_LISTENER_FOR_BUTTONS) {
                            game.getMv().removeMouseListener((MouseListener) mouseFactory.get(i).getSecond());
                            mouseFactory.remove(i);
                            break;
                        }
                case MOUSE_LISTENER_WHILE_PLACING:
                    for (int i = 0; i < mouseFactory.size(); i++)
                        if ((int) mouseFactory.get(i).getFirst() == MOUSE_LISTENER_WHILE_PLACING) {
                            game.getMv().removeMouseListener((MouseListener) mouseFactory.get(i).getSecond());
                            mouseFactory.remove(i);
                            break;
                        }
            }
        } catch (NullPointerException e) {
//            e.printStackTrace();
        }
    }

    public static MouseListener createListenerForButton(int type, int x0, int y0, int width, int height, Game game) {
        MouseListener result = null;
        switch (type) {
            case Button.PLAY_GAME_BUTTON:
                result = new MLForButton(Button.START_SCREEN_BUTTON, game, Button.PLAY_GAME_BUTTON);
                return result;
            case Button.EXIT_GAME_BUTTON:
                result = new MLForButton(Button.START_SCREEN_BUTTON, game, Button.EXIT_GAME_BUTTON);
                return result;
            case Button.ACCEPT_PLACING_BUTTON:
                result = new MLForButton(Button.PLACE_SCREEN_BUTTON, game, Button.ACCEPT_PLACING_BUTTON);
                return result;
            case Button.RESET_PLACING_BUTTON:
                result = new MLForButton(Button.PLACE_SCREEN_BUTTON, game, Button.RESET_PLACING_BUTTON);
        }
        return result;
    }

    public static class MLForButton implements MouseListener {


        private int typeOfScreenButton, typeOfButton;
        private Game game;

        public MLForButton(int typeOfScreenButton, Game game, int typeOfButton) {
            this.typeOfScreenButton = typeOfScreenButton;
            this.game = game;
            this.typeOfButton = typeOfButton;
        }

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {

        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
//            if (conditionForNotUsingListener(new ConditionForButtons(typeOfScreenButton, game)))
            if (Button.conditionForNotUsing(typeOfScreenButton))
                return;

            int x0 = 0, y0 = 0, width = 0, height = 0;
            ArrayList<Button> Buttons = game.getButtons();

            for (int i = 0; i < Buttons.size(); i++) {
                Button b = Buttons.get(i);
                if (b.getTypeOfButton() == typeOfButton) {
                    x0 = b.getX0();
                    y0 = b.getY0();
                    width = b.getWidth();
                    height = b.getHeight();
                    break;
                } else continue;
            }
            int x = mouseEvent.getX() - 10, y = mouseEvent.getY() - 30;
            if (x0 <= x && (x0 + width) >= x && y0 <= y && (y0 + height) >= y) {
//                actionIfButtonPressed(new ActionIfButtonPressed(typeOfButton, game));
                Button.ationIfButtonIsPressed(typeOfButton);
            }

        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {

        }
    }


    public static class MLWPlacing implements MouseListener {

        private int x0, y0, x1, y1;
        private Game game;

        private MLWPlacing(Game game) {
            this.game = game;
        }

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            if (!(game.isBattleCondition() || game.isGameStarted()) && !game.isReady())
                return;

            x0 = mouseEvent.getX() - 10;                                                                                   //эксепты для неправльного ввода
            y0 = mouseEvent.getY() - 30;
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            if (!(game.isBattleCondition() || game.isGameStarted()))
                return;

            x1 = mouseEvent.getX() - 10;
            y1 = mouseEvent.getY() - 30;

            int cellSize = this.game.countCellSize();
            int x0 = (int) ((double) this.x0 / (double) cellSize) - 1;
            int y0 = (int) ((double) this.y0 / (double) cellSize) - 1;
            int x1 = (int) ((double) this.x1 / (double) cellSize) - 1;
            int y1 = (int) ((double) this.y1 / (double) cellSize) - 1;

            try {
                if (x1 - x0 == 0) {
                    this.game.createShip(x0, y0, x1, y1, false);
                } else {
                    if (y1 - y0 == 0) {
                        this.game.createShip(x0, y0, x1, y1, true);
                    }
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {

        }
    }

    public static class MLWFighting implements MouseListener {

        private Game game;

        public MLWFighting(Game game) {
            this.game = game;
        }


        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!game.isBattleCondition() || !game.isPlayersTurn())
                return;
            int cellSize = this.game.countCellSize();
            int x0 = (int) ((e.getX() - 10) / (float) cellSize) - 1;
            int y0 = (int) ((e.getY() - 30) / (float) cellSize) - 1;
//            System.out.println((!game.isBattleCondition() || !game.isPlayersTurn()));
            if (!(x0 <= 21 && x0 >= 12 && y0 <= 9 && y0 >= 0))
                return;
            x0 -= 12;
//            System.out.println(x0 + " " +y0);
            String[][] opponentsField = game.getOpponentsField();
            System.out.println(opponentsField[y0][x0] + " " + y0 + " " + x0);
            if(!opponentsField[y0][x0].equals("0"))
                return;
            game.setShotCommitted(true);
            game.setPlayersTurn(!game.isPlayersTurn());
//            System.out.println("Not_U");
            game.setShotPlacement(new Pair<>(x0, y0));
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
}
