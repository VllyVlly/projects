package org.example.gomoku;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server implements Runnable {

    private ServerSocket serverSocket;
    private int count = 0;
    private HashMap<String, ConnectionHandler> connections = new HashMap<>();
    private GameSettings settings;
    private int connectioncount = 0;

    private GameController gameController;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Accept only 2 players
    @Override
    public void run() {
        while (count < 2){
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                connectioncount++;
            } catch (Exception e) {
                // ignore
            }
            if(socket != null){
                count++;
                ConnectionHandler connectionHandler = new ConnectionHandler(socket);
                if(count == 1) {
                    connections.put("Host", connectionHandler);
                } else {
                    connections.put("Client", connectionHandler);
                }
                Thread thread = new Thread(connectionHandler);
                thread.start();
            }
        }

        gameController.makeTimer();
        gameController.startTimer();
    }

    // Shutdown functions
    public void stopHandlers(){
        for(ConnectionHandler connectionHandler : connections.values()) {
            connectionHandler.stop();
        }
        connections.clear();
    }

    public void serverStop(){
        try{
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public boolean hasClient() {
        return connectioncount == 2;
    }

    class ConnectionHandler implements Runnable {

        // Handle connections to host and client separately

        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private volatile boolean stop = true;

        public ConnectionHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try{
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(count == 1){
                try {
                    settings = (GameSettings) in.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            if(count == 2){
                connections.get("Client").send(settings);
            }


            while(stop) {
                try{
                    GameInfo gameInfo = (GameInfo) in.readObject();
                    if (gameInfo != null) {
                        if(gameInfo.hasColor()){
                            if(gameInfo.getColor().equalsIgnoreCase("black")){
                                connections.get("Client").send(gameInfo);
                            } else {
                                connections.get("Host").send(gameInfo);
                            }
                        } else if(gameInfo.hasWinner()){
                            switch (gameInfo.getWinner()) {
                                case "1", "3" -> connections.get("Client").send(gameInfo);
                                case "2", "4" -> connections.get("Host").send(gameInfo);
                            }
                        } else if(gameInfo.startTimer()){
                            connections.get("Host").send(gameInfo);
                            connections.get("Client").send(gameInfo);
                        } else {
                            if(gameInfo.getSendToWho().equalsIgnoreCase("Player 1")){
                                connections.get("Host").send(gameInfo);
                            } else {
                                connections.get("Client").send(gameInfo);
                            }
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    break;
                }
            }

        }

        public void send(GameSettings settings){
            try{
                out.writeObject(settings);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void send(GameInfo piece){
            try{
                out.writeObject(piece);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void stop(){
            stop = false;
            try {
                if (this.in != null) this.in.close();
                if (this.out != null) this.out.close();
                if (this.socket != null && !this.socket.isClosed()) this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
