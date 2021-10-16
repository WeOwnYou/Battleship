package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Game extends JPanel {

    private String[][] gameField, opponentsField;
    private ArrayList<Ship> Ships;
    private ArrayList<Ship> destroyedShips;
    private ArrayList<Button> Buttons;
    private MainWindow1 mv;
    private int numberOfShips[], indent, cellSize, numberOfPlayersShipsDestroyed, numberOFOpponentsShipsDestroyed;
    private double rotationOfLoadingMark;
    private Pair shotPlacement;
    private Image yourTurnImg, holeImg, blowImg, loseImg, victoryImg, acceptImg, resetImg, startScreenImg, playImg, exitImg, loadingImg, shipImg, destroyedShipImg;
    private boolean isPlayPressed, isShotCommitted, isShotRegistered, isReady, isStartOfGame, isPlayersTurn, isResultOfShotExpected, IPDetected, sideIPDetected, battleCondition, localConnection, gameStarted, shipWasDestroyed, isFirst, isVictoryCondition, isLoseCondition, isEndOfGame;
    private String IPAddressOfPlayer, sideIPAddress, isShotHit;
    private int destroyedIndex;


    public Game(MainWindow1 mv) {

        preLoadImages();

        battleCondition = isShotCommitted = isPlayPressed = isShotRegistered = isResultOfShotExpected = isStartOfGame = gameStarted = IPDetected = sideIPDetected = localConnection = isReady = shipWasDestroyed = isVictoryCondition = isLoseCondition = isEndOfGame = false;
//        gameStarted = isStartOfGame = IPDetected = sideIPDetected = true;                                                                                                                                        //для теста расстановки
        isFirst = true;
        numberOfPlayersShipsDestroyed = numberOFOpponentsShipsDestroyed = 0;
        numberOfShips = new int[]{0, 4, 3, 2, 1};
        gameField = new String[10][10];
        opponentsField = new String[10][10];
        Ships = new ArrayList<Ship>(10);
        destroyedShips = new ArrayList<Ship>();
        Buttons = new ArrayList<Button>();


        this.mv = mv;
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++) {
                gameField[i][j] = "0";
                opponentsField[i][j] = "0";
            }
        mv.addMouseListener(ListenerFactory.createMouseListener((ListenerFactory.MOUSE_LISTENER_WHILE_PLACING), this));
    }

    public void createButtons() {
        if (isFirst)
            isFirst = false;
        else
            return;

        Button b = new Button(0.25, 0.4, 0.5, 0.1, Button.START_SCREEN_BUTTON, Button.EXIT_GAME_BUTTON, exitImg, this);
        Buttons.add(b);
        b.setCoordinates(getWidth(), getHeight());
        mv.addMouseListener(ListenerFactory.createListenerForButton(b.getTypeOfButton(), b.getX0(), b.getY0(), b.getWidth(), b.getHeight(), this));

        b = new Button(0.25, 0.3, 0.5, 0.1, Button.START_SCREEN_BUTTON, Button.PLAY_GAME_BUTTON, playImg, this);
        Buttons.add(b);
        b.setCoordinates(getWidth(), getHeight());
        mv.addMouseListener(ListenerFactory.createListenerForButton(b.getTypeOfButton(), b.getX0(), b.getY0(), b.getWidth(), b.getHeight(), this));

        b = new Button(0.85, 0.85, 0.03, 0.03, Button.PLACE_SCREEN_BUTTON, Button.ACCEPT_PLACING_BUTTON, acceptImg, this);
        Buttons.add(b);
        b.setCoordinates(getWidth(), getHeight());
        mv.addMouseListener(ListenerFactory.createListenerForButton(b.getTypeOfButton(), b.getX0(), b.getY0(), b.getWidth(), b.getHeight(), this));


        b = new Button(0.85, 0.85, 0.03, 0.03, Button.PLACE_SCREEN_BUTTON, Button.ACCEPT_PLACING_BUTTON, acceptImg, this);
        Buttons.add(b);
        b.setCoordinates(getWidth(), getHeight());
        mv.addMouseListener(ListenerFactory.createListenerForButton(b.getTypeOfButton(), b.getX0(), b.getY0(), b.getWidth(), b.getHeight(), this));

        b = new Button(0.91, 0.85, 0.03, 0.03, Button.PLACE_SCREEN_BUTTON, Button.RESET_PLACING_BUTTON, resetImg, this);
        Buttons.add(b);
        b.setCoordinates(getWidth(), getHeight());
        mv.addMouseListener(ListenerFactory.createListenerForButton(b.getTypeOfButton(), b.getX0(), b.getY0(), b.getWidth(), b.getHeight(), this));

    }

    public int countCellSize() {                                                                                         //установка размеров клеток и поля в целом работает в отношении с полем
        int cellSize;
        if (getWidth() <= getHeight()) {
            cellSize = (int) (getWidth() * 2.2) / 13;
            cellSize = (getWidth() - 2 * cellSize) / 11;
        } else {
            cellSize = (int) (getHeight() * 2.2) / 13;
            cellSize = (getHeight() - 2 * cellSize) / 11;
        }
        if (battleCondition) {
            cellSize = (int) (cellSize / 1.5);
            isReady = false;
        }
        return cellSize;
    }

    public void createShip(int x0, int y0, int x1, int y1, boolean isHorizontal) {                                      //запихивание информации о корабле в корбаль, а также в поле и колличество кораблей
        if (battleCondition)
            return;

        int len = Math.max(Math.abs(x1 - x0) + 1, Math.abs(y1 - y0) + 1);
        if (!(numberOfShips[len] > 0))                                                                                  //кораблей такого вида очень много
            return;

        if (isHorizontal) {                                                                                             //(может можно убрать) разная отрисовка горизонтальных и вертикальных кораблей
            int ix1 = Math.max(x0, x1), ix0 = Math.min(x0, x1);
            int iy1 = Math.max(y0, y1), iy0 = Math.min(y0, y1);
            for (int i = ix0; i <= ix1; i++)                                                                            //проверка на возможность поставить
                if (gameField[iy0][i] == "1" || gameField[iy0][i] == "2") {
                    return;
                }

            for (int i = ix0 - 1; i <= ix1 + 1; i++)                                                                    //запрет на постановку кораблей около данного
                for (int j = iy0 - 1; j <= iy1 + 1; j++) {
                    try {
                        if (ix0 <= i && i <= ix1 && iy0 <= j && iy1 >= j)
                            gameField[j][i] = "1";
                        else
                            gameField[j][i] = "2";
                    } catch (IndexOutOfBoundsException e) {
//                        e.printStackTrace();
                    }
                }
        } else {
            int ix1 = Math.max(x0, x1), ix0 = Math.min(x0, x1);
            int iy1 = Math.max(y0, y1), iy0 = Math.min(y0, y1);

            for (int i = iy0; i <= iy1; i++)
                if (gameField[i][ix0] == "1" || gameField[i][ix0] == "2") {
                    return;
                }
            for (int i = iy0 - 1; i <= iy1 + 1; i++)
                for (int j = ix0 - 1; j <= ix1 + 1; j++) {
                    try {
                        if (iy0 <= i && iy1 >= i && ix0 <= j && ix1 >= j)
                            gameField[i][j] = "1";
                        else
                            gameField[i][j] = "2";
                    } catch (IndexOutOfBoundsException e) {
//                        e.printStackTrace();
                    }
                }
        }

        numberOfShips[len] -= 1;                                                                                        //фактическое создание корабля и отрисовка
        Ship s = new Ship(x0, y0, x1, y1, isHorizontal);
        Ships.add(s);
        repaint();
    }

    public void shooting(int x, int y) {                                                                                //произведение боя
        setShotRegistered(true);
        if (gameField[y][x].equals("1")) {
            gameField[y][x] = "-1";
            setIsShotHit("-1");
        } else if (gameField[y][x].equals("0") || gameField[y][x].equals("2")) {
            gameField[y][x] = "-2";
            setIsShotHit("-2");
        }

        for (int i = 0; i < 10; i++) {
            Ship ship = Ships.get(i);
            int x0 = Math.min(ship.getX0(), ship.getX1()), x1 = Math.max(ship.getX0(), ship.getX1()),
                    y0 = Math.min(ship.getY0(), ship.getY1()), y1 = Math.max(ship.getY0(), ship.getY1());
            if (x0 <= x && x1 >= x && y0 <= y && y1 >= y) {
//                System.out.println("x " + x0 + " " + x + " " + x1);
//                System.out.println("y " + y0 + " " + y + " " + y1);
                ship.shipHitted();
                if (ship.isDestroyed()) {
                    playersShipDestroyed();
                    if (numberOfPlayersShipsDestroyed == 10)
                        isLoseCondition = true;
                    shipWasDestroyed = true;
                    for (int ix = x0; ix <= x1; ix++)
                        for (int iy = y0; iy <= y1; iy++)
                            gameField[iy][ix] = "-3";
                    destroyedIndex = i;
                } else
                    shipWasDestroyed = false;
            }
            Ships.set(i, ship);
        }
        repaint();
    }

    public void closeGame() {
        System.exit(0);
    }

    public void findOpponent() {
        if (!isPlayPressed) {
            ThreadFactory.createThread(ThreadFactory.THREAD_FOR_SERVER, this);
            ThreadFactory.createThread(ThreadFactory.THREAD_FOR_SERVER_DETECTION, this);
            ThreadFactory.createThread(ThreadFactory.THREAD_FOR_BROADCAST, this);
            ThreadFactory.createThread(ThreadFactory.THREAD_FOR_LOADING, this);
        }
    }

    public void paint(Graphics g) {                                                                                     //отрисовка
        cellSize = countCellSize();
        createButtons();
        super.paint(g);
        if (gameStarted)
            paintSelfField(g);
        else
            paintStartWindow(g);
        if (battleCondition)
            paintOpponentField(g);
        if (isReady)
            paintLoadingMark(g);
        paintButtons(g);
//        System.out.println(numberOFOpponentsShipsDestroyed + " " + numberOfPlayersShipsDestroyed + " .!. ");
        if (isLoseCondition) {
            isEndOfGame = false;
            paintLose(g);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new MainWindow1();
            mv.dispose();
        } else if (isVictoryCondition) {
            isEndOfGame = false;
            paintVictory(g);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new MainWindow1();
            mv.dispose();
        }
    }

    public void paintSelfField(Graphics g) {                                                                            //свое поле
        indent = 0;
        paintField(g);
        paintShips(g);
        paintHoles(g);
    }

    public void paintOpponentField(Graphics g) {                                                                        //поле оппонента
        indent = 12 * cellSize;
        paintField(g);
        paintShips(g);
        paintHoles(g);
        if (isPlayersTurn())
            paintYourTurn(g);
    }

    public void paintField(Graphics g) {                                                                                 //отрисовка поля (пока линии, потом ещё кнопки с акшон листенерами)
        g.setColor(Color.BLACK);
        for (int i = 0; i < 2; i++)
            for (int j = 0; j <= 10; j++) {
                if (i == 0) {
                    g.drawLine(indent + cellSize + j * cellSize, cellSize, indent + cellSize + j * cellSize, cellSize * 11);
                } else
                    g.drawLine(indent + cellSize, cellSize + j * cellSize, indent + cellSize * 11, cellSize + j * cellSize);
            }
    }

    public void paintYourTurn(Graphics g) {                                                                                                                //рисование надписи очередь
        g.drawImage(getYourTurnImg(), getWidth() - 4 * cellSize, getHeight() - 4 * cellSize, 4 * cellSize, 4 * cellSize, null);
    }

    public void paintShips(Graphics g) {                                                                                                                    //отрисовка кораблей
        if (indent != 0) {
            for (int i = 0; i < destroyedShips.size(); i++) {
                int lenX = destroyedShips.get(i).getX1() - destroyedShips.get(i).getX0() + 1;
                int lenY = destroyedShips.get(i).getY1() - destroyedShips.get(i).getY0() + 1;
                paintShip(g, destroyedShipImg, indent + cellSize + destroyedShips.get(i).getX0() * cellSize, cellSize + destroyedShips.get(i).getY0() * cellSize,
                        lenX * cellSize, lenY * cellSize, destroyedShips.get(i).getIsHorizontal());
            }
            return;
        }
        for (int i = 0; i < Ships.size(); i++) {
            int lenX = Ships.get(i).getX1() - Ships.get(i).getX0() + 1;
            int lenY = Ships.get(i).getY1() - Ships.get(i).getY0() + 1;
            if (Ships.get(i).isDestroyed())
                paintShip(g, destroyedShipImg, cellSize + Ships.get(i).getX0() * cellSize, cellSize + Ships.get(i).getY0() * cellSize,
                        lenX * cellSize, lenY * cellSize, Ships.get(i).getIsHorizontal());
            else
                paintShip(g, shipImg, cellSize + Ships.get(i).getX0() * cellSize, cellSize + Ships.get(i).getY0() * cellSize,
                        lenX * cellSize, lenY * cellSize, Ships.get(i).getIsHorizontal());
        }
    }

    public void paintHoles(Graphics g) {
        String[][] field;

        if (indent == 0) {
            field = gameField;
        } else {
            field = opponentsField;
        }

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                try {
                    if (field[i][j].equals("-2")) {
                        g.drawImage(getHoleImg(), indent + j * cellSize + cellSize, i * cellSize + cellSize, cellSize, cellSize, null);
                    } else if (field[i][j].equals("-1")) {
                        g.drawImage(getBlowImg(), indent + j * cellSize + cellSize, i * cellSize + cellSize, cellSize, cellSize, null);
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        }
    }

    public void paintLose(Graphics g) {
        g.drawImage(getLoseImg(), getWidth() / 4, getHeight() / 4, getWidth() / 2, getHeight() / 2, null);
    }

    public void paintVictory(Graphics g) {
        g.drawImage(getVictoryImg(), getWidth() / 4, getHeight() / 4, getWidth() / 2, getHeight() / 2, null);
    }

    public void paintShip(Graphics g, Image shipImg, int x, int y, int width, int height, boolean isHorizontal) {           //Отрисовка и возможный переворто 1го корабля
        Graphics2D g2d = (Graphics2D) g;

        if (width <= 0) {                                                                                                  //Костыль для рисования в другую сторону
            width -= 2 * cellSize;
            x += cellSize;
        }
        if (height <= 0) {
            height -= 2 * cellSize;
            y += cellSize;
        }

        if (!isHorizontal) {
            double rotationRequired = Math.PI / 2;
            double locationX = shipImg.getWidth(null) / 4.;
            double locationY = shipImg.getHeight(null) / 4.;
            AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
            g2d.drawImage(op.filter((BufferedImage) shipImg, null), x, y, width, height, null);
        } else {
            g2d.drawImage(shipImg, x, y, width, height, null);
        }
    }

    public void paintButtons(Graphics g) {
        for (int i = 0; i < Buttons.size(); i++) {
            Button b = Buttons.get(i);
            if (Button.conditionForNotUsing(b.getTypeOfScreen()))
                continue;
            b.setCoordinates(getWidth(), getHeight());
            g.drawImage(b.getButtonImg(), b.getX0(), b.getY0(), b.getWidth(), b.getHeight(), null);
        }
    }

    public void paintStartWindow(Graphics g) {
        g.drawImage(getStartScreenImg(), 0, 0, getWidth(), getHeight(), null);
        paintLoadingMark(g);
    }

    public void paintLoadingMark(Graphics g) {
        Image loadingImg = getLoadingImg();

        if (isStartOfGame) {
            rotationOfLoadingMark = 0;
            isStartOfGame = false;
        }
        rotationOfLoadingMark += 0.034;
        double locationX = loadingImg.getWidth(null) / 2.;
        double locationY = loadingImg.getHeight(null) / 2.;
        AffineTransform tx = AffineTransform.getRotateInstance(rotationOfLoadingMark, locationX, locationY);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        Graphics2D g2d = (Graphics2D) g;
        if (isPlayPressed) {
//            System.out.println(rotationOfLoadingMark);
            g2d.drawImage(op.filter((BufferedImage) loadingImg, null), getWidth() - 60, getHeight() - 60, 50, 50, null);
        }
        if (isReady) {
//            System.out.println(rotationOfLoadingMark);
            g2d.drawImage(op.filter((BufferedImage) loadingImg, null), getWidth() - 60, getHeight() - 60, 50, 50, null);
        }
    }


    public boolean isBattleCondition() {
        return battleCondition;
    }

    public void setBattleCondition(boolean battleCondition) {
        this.battleCondition = battleCondition;
    }

    public boolean isIPDetected() {
        return IPDetected;
    }

    public void setIPDetected(boolean IPDetected) {
        this.IPDetected = IPDetected;
    }

    public void resetGameField() {
        gameField = new String[10][10];
    }

    public void resetNumberOfShips() {
        numberOfShips = new int[]{0, 4, 3, 2, 1};
    }

    public void resetShips() {
        Ships = new ArrayList<Ship>(10);
    }

    public int[] getNumberOfShips() {
        return numberOfShips;
    }

    public String getIPAddressOfPlayer() {
        return IPAddressOfPlayer;
    }

    public void setIPAddressOfPlayer(String IPAddressOfPlayer) {
        this.IPAddressOfPlayer = IPAddressOfPlayer;
    }

    public MainWindow1 getMv() {
        return mv;
    }

    public String[][] getGameField() {
        return gameField;
    }

    public void setGameField(String[][] gameField) {
        this.gameField = gameField;
    }

    public boolean isSideIPDetected() {
        return sideIPDetected;
    }

    public void setSideIPDetected(boolean sideIPDetected) {
        this.sideIPDetected = sideIPDetected;
    }

    public String getSideIPAddress() {
        return sideIPAddress;
    }

    public void setSideIPAddress(String setIPAddress) {
        this.sideIPAddress = setIPAddress;
    }

    public boolean isLocalConnection() {
        return localConnection;
    }

    public void setLocalConnection(boolean localConnection) {
        this.localConnection = localConnection;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public boolean isPlayPressed() {
        return isPlayPressed;
    }

    public void setPlayPressed(boolean isPlayPreessed) {
        this.isPlayPressed = isPlayPreessed;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public boolean isPlayersTurn() {
        return isPlayersTurn;
    }

    public void setPlayersTurn(boolean playersTurn) {
        isPlayersTurn = playersTurn;
    }

    public boolean isShotCommitted() {
        return isShotCommitted;
    }

    public void setShotCommitted(boolean shotCommitted) {
        isShotCommitted = shotCommitted;
    }

    public boolean isShotRegistered() {
        return isShotRegistered;
    }

    public void setShotRegistered(boolean shotRegistered) {
        isShotRegistered = shotRegistered;
    }

    public Pair getShotPlacement() {
        return shotPlacement;
    }

    public void setShotPlacement(Pair shotPlacement) {
        this.shotPlacement = shotPlacement;
    }

    public String getIsShotHit() {
        return isShotHit;
    }

    public void setIsShotHit(String isShotHit) {
        this.isShotHit = isShotHit;
    }

    public boolean isResultOfShotExpected() {
        return isResultOfShotExpected;
    }

    public void setResultOfShotExpected(boolean resultOfShotExpected) {
        isResultOfShotExpected = resultOfShotExpected;
    }

    public String[][] getOpponentsField() {
        return opponentsField;
    }

    public void setOpponentsField(String[][] opponentsField) {
        this.opponentsField = opponentsField;
    }

    public void preLoadImages() {
        try {
            yourTurnImg = ImageIO.read(getClass().getResource("/resources/yourTurn.png"));
            holeImg = ImageIO.read(getClass().getResource("/resources/hole.png"));
            blowImg = ImageIO.read(getClass().getResource("/resources/blow.png"));
            loseImg = ImageIO.read(getClass().getResource("/resources/lose.png"));
            victoryImg = ImageIO.read(getClass().getResource("/resources/victory.png"));
            acceptImg = ImageIO.read(getClass().getResource("/resources/acceptButton.png"));
            resetImg = ImageIO.read(getClass().getResource("/resources/resetButton.png"));
            startScreenImg = ImageIO.read(getClass().getResource("/resources/startScreen.png"));
            playImg = ImageIO.read(getClass().getResource("/resources/play.png"));
            exitImg = ImageIO.read(getClass().getResource("/resources/exit.png"));
            loadingImg = ImageIO.read(getClass().getResource("/resources/loading.png"));
            shipImg = ImageIO.read(getClass().getResource("/resources/ship.png"));
            destroyedShipImg = ImageIO.read(getClass().getResource("/resources/destroyedShip.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Image getYourTurnImg() {
        return yourTurnImg;
    }

    public Image getHoleImg() {
        return holeImg;
    }

    public Image getBlowImg() {
        return blowImg;
    }

    public Image getLoseImg() {
        return loseImg;
    }

    public Image getVictoryImg() {
        return victoryImg;
    }

    public Image getAcceptImg() {
        return acceptImg;
    }

    public Image getResetImg() {
        return resetImg;
    }

    public Image getStartScreenImg() {
        return startScreenImg;
    }

    public Image getPlayImg() {
        return playImg;
    }

    public Image getExitImg() {
        return exitImg;
    }

    public Image getLoadingImg() {
        return loadingImg;
    }

    public Image getShipImg() {
        return shipImg;
    }

    public void setIsStartOfGame(boolean isStartOfGame) {
        this.isStartOfGame = isStartOfGame;
    }

    public ArrayList<Button> getButtons() {
        return Buttons;
    }

    public boolean isShipWasDestroyed() {
        return shipWasDestroyed;
    }

    public void setShipWasDestroyed(boolean shipWasDestroyed) {
        this.shipWasDestroyed = shipWasDestroyed;
    }

    public int getDestroyedIndex() {
        return destroyedIndex;
    }

    public ArrayList<Ship> getShips() {
        return Ships;
    }

    public ArrayList<Ship> getDestroyedShips() {
        return destroyedShips;
    }

    public void setDestroyedShips(ArrayList<Ship> destroyedShips) {
        this.destroyedShips = destroyedShips;
    }

    public boolean isLoseCondition() {
        return isLoseCondition;
    }

    public void setLoseCondition(boolean loseCondition) {
        isLoseCondition = loseCondition;
    }

    public boolean isVictoryCondition() {
        return isVictoryCondition;
    }

    public void setVictoryCondition(boolean victoryCondition) {
        isVictoryCondition = victoryCondition;
    }

    public int getNumberOfPlayersShipsDestroyed() {
        return numberOfPlayersShipsDestroyed;
    }

    public void opponentsShipDestroyed() {
        this.numberOFOpponentsShipsDestroyed++;
    }

    public int getNumberOFOpponentsShipsDestroyed() {
        return numberOFOpponentsShipsDestroyed;
    }

    public void playersShipDestroyed() {
        this.numberOfPlayersShipsDestroyed++;
    }

    public boolean isEndOfGame() {
        return isEndOfGame;
    }
}
