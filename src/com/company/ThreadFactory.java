package com.company;

public class ThreadFactory {

    public static final int THREAD_FOR_SERVER = 1;
    public static final int THREAD_FOR_BROADCAST = 2;
    public static final int THREAD_FOR_SERVER_DETECTION = 3;
    public static final int THREAD_FOR_LOADING = 4;
    public static final int THREAD_FOR_WAITING = 5;

    public static void createThread(int type, Game game) {
        Thread result;
        switch (type) {
            case THREAD_FOR_SERVER:
                result = new MServerThread(game);
                result.start();
                return;
            case THREAD_FOR_BROADCAST:
                result = new BCClientThread(game);
                result.start();
                return;
            case THREAD_FOR_SERVER_DETECTION:
                result = new BCServerThread(game);
                result.start();
                return;
            case THREAD_FOR_LOADING:
                result = new LoadingMark(game);
                result.start();
            case THREAD_FOR_WAITING:
//                result = new Waiting(game);
//                result.start();
                if(game.isEndOfGame())
                    Thread.interrupted();
        }
    }

    public static class BCClientThread extends Thread {
        private Game game;

        public BCClientThread(Game game) {
            this.game = game;
        }

        @Override
        public void run() {
            super.run();
            Client client = new Client(game);
            if(game.isEndOfGame())
                Thread.interrupted();
            try {
                client.sendPacketToDetect();
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    }

    public static class BCServerThread extends Thread {
        private Game game;

        public BCServerThread(Game game) {
            this.game = game;
        }

        @Override
        public void run() {
            super.run();
            if(game.isEndOfGame())
                Thread.interrupted();
            Server server = new Server(game);
            server.serverToDetect();
        }
    }

    public static class MServerThread extends Thread {
        private Game game;

        public MServerThread(Game game) {
            this.game = game;
        }

        @Override
        public void run() {
            if(game.isEndOfGame())
                Thread.interrupted();
            super.run();
            Server server = new Server(game);
            server.mainServer();
        }
    }

//    public static class Waiting extends Thread {                                                                        //для победы или поражения
//
//        private Game game;
//
//        public Waiting(Game game){this.game = game;}
//
//        @Override
//        public void run() {
//            super.run();
//            try {
//                Thread.sleep(500000);
//                game.repaint();
//
//            } catch (InterruptedException e) {
////                e.printStackTrace();
//            }
//        }
//    }

    public static class LoadingMark extends Thread {
        private Game game;
        private boolean running = true;

        public LoadingMark(Game game) {
            this.game = game;
        }

        @Override
        public void run() {
            super.run();
            if(game.isEndOfGame())
                Thread.interrupted();
            while (running) {
                game.repaint();
                try {
                    if(game.isReady()) {
                        Thread.sleep(1);
//                        System.out.println("DA");
                    }
                    else
                        Thread.sleep(50);
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                }
                if (!(game.isGameStarted() == game.isReady())) {
                    running = false;
                    interrupt();
                    break;
                }
            }
        }
    }
}