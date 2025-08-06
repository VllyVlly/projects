package org.example;
import java.util.*;


public class Main{

    private static boolean terminate = false;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        // Startup message, loop program until terminated
        while (!terminate) {
            System.out.println("Hello. Welcome to the chat room.");
            System.out.println("What would you like to do?");
            System.out.println(" ");

            System.out.println("Please enter a single number");
            System.out.println("1. Create a chatroom");
            System.out.println("2. Join a chatroom");
            System.out.println("3. Terminate Program");
            System.out.println(" ");
            menuInput();
        }

    }

    // Create server
    public static void start(){
        Server server = new Server();
        server.run();
        System.out.println("");
    }

    // Join server
    public static void join(){
        Client client = new Client();
        client.run();
        System.out.println("");
    }

    // Display options
    public static void menuInput() {
        String in;
        switch (in = scanner.nextLine().trim()) {
            case "1" -> start();
            case "2" -> join();
            case "3" -> {
                terminate = true;
                System.out.println("Terminating...");
            }
            default -> {
                System.out.println("Invalid input.");
                menuInput();
            }
        }
    }


}




