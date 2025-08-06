package org.example;
import java.io.*;
import java.net.*;
import java.util.*;
import java.io.PrintWriter;

public class Client implements Runnable {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;


    @Override
    public void run() {
        try {

            // Ask for port
            System.out.println("Please input port number...");
            Scanner sc = new Scanner(System.in);
            boolean correctPort = false;
            int port = 0;

            while (!correctPort) {
                try {
                    port = sc.nextInt();
                    if (port >= 1024 && port <= 65535) {
                        correctPort = true;
                    } else {
                        System.out.println("Port must be between 1024 and 65535. Try again.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input. Please enter a number.");
                    sc.next();
                }
            }

            Socket client = new Socket("localhost", port);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            System.out.println("You are now connected to port " + port);

            InputHandler inputHandler = new InputHandler();
            Thread thread = new Thread(inputHandler);
            thread.start();
            // Handle inputs separately


            String input;
            while ((input = in.readLine()) != null) {
                if (input.startsWith("/stop")){
                    close();
                    return;
                } else {
                    System.out.println(input);
                }
            }
            // Print out text received from clients and the server

        } catch (IOException e) {

            try {
                close();
            } catch (Exception a) {
                System.out.println("No active server found. Please try again.");
            }
            
        }
    }

    public void close(){
        try {
            done = true;
            in.close();
            out.close();
            if(client == null){
                return;
            }
            if(!client.isClosed()){
                client.close();
            }
        } catch (IOException e) {
            // ignore
        }
    }

    class InputHandler implements Runnable {

        @Override
        public void run() {
            BufferedReader in2 = new BufferedReader(new InputStreamReader(System.in));
            try {
                while (!done){
                    String message = in2.readLine();
                    if (done) {
                        break;
                    }
                    if (message.equals("/exit")) {
                        out.println(message);
                        System.out.println("Leaving chat room...");
                        close();
                        done = true;
                        return;
                    } else {
                        out.println(message);
                    }
                }
            } catch (IOException e){
                close();
                done = true;
            }

            try {
                in2.close();
                close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
}

