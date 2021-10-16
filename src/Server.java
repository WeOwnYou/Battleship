package com.company;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    public static boolean isTurnSeted = false;
    private Game game;
    private int port;
    private boolean running;
    private boolean isItIp = true;

    public Server(Game game) {
        port = 9000;
        running = true;
        this.game = game;
    }

    public void serverToDetect() {
        if (game.isIPDetected()) {
            return;
        }
        while (running) {
            try {
                DatagramSocket ds = new DatagramSocket(9999);
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    ds.receive(packet);
                    System.out.println("DETECTED");


                    if (!game.isIPDetected()) {
                        game.setIPDetected(true);
                        game.setIPAddressOfPlayer(packet.getAddress().getHostAddress());
                    } else {
                        if (!game.isSideIPDetected()) {
                            if (game.getIPAddressOfPlayer().equals(packet.getAddress().getHostAddress()))
                                continue;
                            game.setSideIPDetected(true);
                            game.setSideIPAddress(packet.getAddress().getHostAddress());
                        } else {
                            running = false;
                            return;
                        }
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    }

    public void mainServer() {
        try {

            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            game.setLocalConnection(true);
            InputStream sin = socket.getInputStream();
            DataInputStream in = new DataInputStream(sin);
            String line = null;
            while (running) {
                try {
                    line = in.readUTF();
                    System.out.println(line);
                    if (isItIp) {
                        line.split(".");
                        if (!game.isSideIPDetected()) {
                            game.setSideIPAddress(line);
                            game.setSideIPDetected(true);
                        }
                        isItIp = false;

                        game.setLocalConnection(true);
                        game.setGameStarted(true);
                        game.repaint();

                    } else if (line.equals("Ready") && game.isReady() && !isTurnSeted) {

                        if (!isTurnSeted) {                                                                                                                                                                                                                             //&& Integer.parseInt(game.getSideIPAdress().split("\\.")[3]) > Integer.parseInt(game.getIPAdressOfOpponent().split("\\.")[3])) {
                            game.setPlayersTurn(true);
                            isTurnSeted = true;
                        }
                        ListenerFactory.deleteMouseListener(ListenerFactory.MOUSE_LISTENER_FOR_BUTTONS, game);
                        ListenerFactory.deleteMouseListener(ListenerFactory.MOUSE_LISTENER_WHILE_PLACING, game);
                        game.getMv().addMouseListener(ListenerFactory.createMouseListener((ListenerFactory.MOUSE_LISTENER_WHILE_FIGHTNG), game));
                        game.setBattleCondition(true);
                        game.setReady(false);
                        game.repaint();
                    } else if (isTurnSeted && line.split(" ").length == 4) {
                        String[][] opponentsField = game.getOpponentsField();
                        int x0 = Integer.parseInt(line.split(" ")[0]), y0 = Integer.parseInt(line.split(" ")[1]), x1 = Integer.parseInt(line.split(" ")[2]), y1 = Integer.parseInt(line.split(" ")[3]);
                        for (int ix = x0; ix <= x1; ix++)
                            for (int iy = y0; iy <= y1; iy++)
                                opponentsField[iy][ix] = "-3";
                            ArrayList<Ship> destroyedShips = game.getDestroyedShips();
                            game.opponentsShipDestroyed();
                            if(game.getNumberOFOpponentsShipsDestroyed() == 10)
                                game.setVictoryCondition(true);
                            if(x0-x1 == 0)
                                destroyedShips.add(new Ship(x0, y0, x1, y1, false));
                            else
                                destroyedShips.add(new Ship(x0, y0, x1, y1, true));
                        game.setOpponentsField(opponentsField);
                        game.setDestroyedShips(destroyedShips);
                        game.repaint();
                    } else if (isTurnSeted && line.split(" ").length == 2) {
                        game.shooting(Integer.parseInt(line.split(" ")[0]), Integer.parseInt(line.split(" ")[1]));
                    } else if (isTurnSeted && game.isResultOfShotExpected()) {
                        String[][] opponentsField = game.getOpponentsField();
                        if(!opponentsField[Integer.parseInt(game.getShotPlacement().getSecond().toString())][Integer.parseInt(game.getShotPlacement().getFirst().toString())].equals("-3")) {
                            opponentsField[Integer.parseInt(game.getShotPlacement().getSecond().toString())][Integer.parseInt(game.getShotPlacement().getFirst().toString())] = line;
                            game.setOpponentsField(opponentsField);
                        }
                        if (line.equals("-1"))
                            game.setPlayersTurn(!game.isPlayersTurn());
                        game.setResultOfShotExpected(false);
                        game.repaint();
                        Thread.sleep(7);
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
}