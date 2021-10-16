package com.company;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {

    private String message;                                                                                             //сообщение
    private String addressOfNetwork;
    private boolean running;
    private Game game;
    private boolean isMyAddressWroteToOpponent = false;
    private boolean isFirst = true;

    public Client(Game game) {
        this.game = game;
        this.message = "1";
        this.running = true;
        this.addressOfNetwork = "10.1.1.255";
        try {
            addressOfNetwork();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public void addressOfNetwork() throws Exception {                                                                   //создает адресс сети (можно убрать)
//        InetAddress.getByName(addressOfNetwork);
//        String adr = InetAddress.getLocalHost().getHostAddress();
        String adr = InetAddress.getByName(addressOfNetwork).getLocalHost().getHostAddress();
        StringBuilder sb = new StringBuilder();
        Scanner scaner = new Scanner(adr);
        scaner.useDelimiter("\\.");
        sb.append(scaner.next()).append(".");                                                                           //sb.append(scaner.next() + ".");
        sb.append(scaner.next()).append(".");
        sb.append(scaner.next()).append(".");
        sb.append(255);
        this.addressOfNetwork = sb.toString();
    }

    public void sendPacketToDetect() throws Exception {
        if (game.isIPDetected() && game.isSideIPDetected())
            return;

        MulticastSocket ms = new MulticastSocket(1024);
        byte[] buffer;

        buffer = message.getBytes();
        InetAddress ipAddress = InetAddress.getByName(addressOfNetwork);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, ipAddress, 9999);
        while (running) {
            ms.send(packet);
            System.out.println("SEND");
            Thread.sleep(5000);
            if (game.isIPDetected() && game.isSideIPDetected()) {
                mainClient();
            }
            if (game.isLocalConnection()) {
                return;
            }
        }
    }

    public void mainClient() {
        String address = game.getSideIPAddress();

        try {
            InetAddress ipAddress = InetAddress.getByName(address);
            int port = 9000;
            Socket socket = new Socket(ipAddress, port);
            OutputStream os = socket.getOutputStream();
            DataOutputStream out = new DataOutputStream(os);

            while (running) {
                if (!isMyAddressWroteToOpponent) {
                    isMyAddressWroteToOpponent = true;
                    out.writeUTF(game.getIPAddressOfPlayer());
                } else {
                    Thread.sleep(10);
                    if(game.isReady()) {
                        out.writeUTF("Ready");
                        out.writeUTF(game.getNumberOfPlayer()+"");//
                    }
                    if(!game.isReady() && isFirst){
                        isFirst = false;
                        out.writeUTF("Ready");
                        Thread.sleep(10);
//                        while(!Server.isTurnSeted){
//                            out.writeUTF("2");
//                            Thread.sleep(5);
//                        }
                    }
                    if (Server.isTurnSeted && game.isShotCommitted()) {
                        game.setShotCommitted(false);
                        out.writeUTF(game.getShotPlacement().getFirst().toString() + " " + game.getShotPlacement().getSecond().toString());
                        game.setResultOfShotExpected(true);
                    } else if (game.isShotRegistered() && Server.isTurnSeted) {
                        if(game.isShipWasDestroyed()) {
                            Thread.sleep(15);
                            Ship ship = game.getShips().get(game.getDestroyedIndex());
                            int x0 = Math.min(ship.getX0(), ship.getX1()), x1 = Math.max(ship.getX0(), ship.getX1()),
                                    y0 = Math.min(ship.getY0(), ship.getY1()), y1 = Math.max(ship.getY0(), ship.getY1());
                            out.writeUTF(x0 + " " + y0 + " " + x1 + " " + y1);
                            game.setShipWasDestroyed(false);
                            continue;
                        }
                        out.writeUTF(game.getIsShotHit());
                        game.setShotRegistered(false);
                        game.setPlayersTurn(!game.isPlayersTurn());
                        System.out.println("changed");
                        if (game.getIsShotHit().equals("-1")) {
                            game.setPlayersTurn(!game.isPlayersTurn());
                            System.out.println("changed");
                        }

//                        System.out.println("U");
                    }
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
}