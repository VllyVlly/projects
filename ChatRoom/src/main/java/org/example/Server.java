package org.example;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.util.Iterator;

public class Server implements Runnable {
    private ServerSocket serversocket;
    static int count = 1;
    private ArrayList<ClientHandler> ar;
    private boolean done;
    private ExecutorService pool;
    private String hostname;
    private int serverID;
    private String printedserverID;
    private String chatname;
    private volatile boolean exit = false;
    private FileWriter history;

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

            try {
                serversocket = new ServerSocket(port);
                System.out.println("Server with port " + port + " and name " + chatname + " has been created.");
                // Get port number
            } catch (Exception e) {
                System.out.println("This port is not available.");
                return;
            }



            try {
                history = new FileWriter(chatname+"history.txt", true);
            } catch (IOException e) {
                System.out.println("Error creating file");
                return;
            }
            // Create chat history file

            pool = Executors.newCachedThreadPool();

            // Handle inputs from server
            Thread serverinput = new Thread(() -> {
                Scanner serverscanner = new Scanner(System.in);
                try {
                    while (!exit){
                        String serverMessage;
                        String currenttime;
                        while ((serverMessage = serverscanner.nextLine()) != null) {
                            if (serverMessage.startsWith("/rename")){
                                try {
                                    String split = serverMessage.substring(8).trim();
                                    if(!split.isEmpty()){
                                        currenttime = getTime();
                                        broadcast(currenttime + " " + printedserverID + " " + hostname + " has renamed themselves to " + split);
                                        System.out.println(currenttime + " " + printedserverID + " " + hostname + " has renamed themselves to " + split);
                                        record(currenttime + " " + printedserverID + " " + hostname + " has renamed themselves to " + split);
                                        hostname = split;
                                    } else {
                                        currenttime = getTime();
                                        System.out.println(currenttime + " " + "Syntax error. Please try again.");
                                    }
                                } catch (Exception e) {
                                    currenttime = getTime();
                                    System.out.println(currenttime + " " + "Syntax error. Please try again.");
                                }

                            } else if (serverMessage.startsWith("/stop")) {
                                currenttime = getTime();
                                System.out.println(currenttime + " " + "Closing chatroom...");
                                record(currenttime + " " + "Closing chatroom...");
                                broadcast(currenttime + " " + "Closing chatroom...");
                                broadcast("/stop");
                                close();
                                exit = true;
                                return;
                            } else if (serverMessage.startsWith("/members")) {
                                showmembers();
                            } else if (serverMessage.startsWith("/help")) {
                                help();
                            } else if (serverMessage.startsWith("/count")) {
                                count();
                            } else if (serverMessage.startsWith("/dm")) {
                                dm(serverMessage);
                            } else if (serverMessage.startsWith("/search")){
                                try {
                                    String filename = chatname+"history.txt";
                                    String keyword = serverMessage.substring(8).trim();
                                    if(!keyword.isEmpty()){
                                        search(filename, keyword);
                                    } else {
                                        System.out.println("Syntax error. Please try again.");
                                    }
                                } catch (Exception e) {
                                    System.out.println("Syntax error. Please try again.");
                                }
                            } else {
                                currenttime = getTime();
                                broadcast(currenttime + " " + printedserverID + " " + hostname + ": " + serverMessage);
                                System.out.println(currenttime + " " + printedserverID + " " + hostname + ": " + serverMessage);
                                record(currenttime + " " + printedserverID + " " + hostname + ": " + serverMessage);
                            }
                        }
                    }
                } catch (Exception e) {
                    // ignore
                }
                serverscanner.close();
                return;
            });
            serverinput.start();


            while (!done) {
                Socket client = serversocket.accept();
                ClientHandler handler = new ClientHandler(client);
                ar.add(handler);
                pool.execute(handler);
            }
            // Accept clients

        } catch (IOException e) {
            close();
        }

        try {
            history.close();
        } catch (IOException e) {
            // ignore
        }

        return;
    }

    public Server() {
        ar = new ArrayList<>();
        done = false;
        Scanner scn1 = new Scanner(System.in);
        System.out.println("Please input a username...");
        hostname = scn1.nextLine();
        serverID = getID();
        printedserverID = "@" + String.format("%05d", serverID);
        System.out.println("Name successfully set to " + hostname);
        System.out.println("Your ID is " + printedserverID);
        System.out.println("Please input a name for the chatroom...");
        chatname = scn1.nextLine();
    }

    public void broadcast(String message) {
        // Send message to all clients
        for (ClientHandler ch : ar) {
            if(ch != null){
                ch.sendMessage(message);
            }
        }
    }

    public void close() {
        // Close all clients
        try{
            done = true;
            if (!serversocket.isClosed()) {
                serversocket.close();
            }
            for (ClientHandler c : ar) {
                c.close();
            }
        }  catch (IOException e) {
            // ignore
        }
        pool.shutdown();
    }

    public int getID () {
        // Unique ID generator
        int id = (int)(Math.random() * 99999) + 1;
        if (id == serverID){
            return getID();
        }
        for(ClientHandler ch : ar){
            if(id == ch.clientID){
                return getID();
            }
        }
        return id;
    }

    public String getTime() {
        // Time function
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = time.format(myFormatObj);
        return formattedDate;
    }

    public void showmembers() {
        // Show members in room function
        String output = "";
        String currenttime = getTime();
        output = output + printedserverID + " " + hostname;
        for (ClientHandler ch : ar) {
            output = output + ", " + ch.printedclientID + " " + ch.username;
        }
        System.out.println(currenttime + " " + "Members in this chatroom: " + output);
    }

    public void record(String message) {
        // Record chats to file
        try {
            history.write(message + "\n");
            history.flush();
        } catch (IOException e) {
            System.out.println("Error writing to log file.");
        }
    }

    public void dm(String input) {
        // Private message function
        try{
            String[] split = input.split(" ", 3);
            String ID = split[1];
            String message = split[2];
            String currenttime = getTime();
            boolean found = false;
            for (ClientHandler ch : ar) {
                if (ID.trim().equalsIgnoreCase(ch.printedclientID.trim())){
                    ch.sendMessage("(Private) " + currenttime + " " + printedserverID + " " + hostname + ": " + message);
                    System.out.println("Private message sent.");
                    found = true;
                    return;
                } else {
                    found = false;
                }
            }
            if (!found) {
                System.out.println("Recipient not found. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Syntax error. Please try again.");
        }
    }

    public void help() {
            // List commands
            System.out.println("List of all available commands: ");
            System.out.println("/dm [ID of recipient including @] [Message]  --  Send private messages");
            System.out.println("/rename [New Name]  --  Change name");
            System.out.println("/help   --  Display available commands");
            System.out.println("/stop   --  Stops the server");
            System.out.println("/members  --  Display current members");
            System.out.println("/count -- Displays number of members in server");
            System.out.println("/search [keyword] -- Finds every instance of keyword in a line");
    }

    public void count() {
        // Display number of members
        System.out.println("Number of participants: " + count);
    }

    public void search(String fileName,String keyword) {
        // Search for keyword in file
        try {
            Scanner scan = new Scanner(new File(fileName));
            boolean found = false;
            while(scan.hasNext()){
                String line = scan.nextLine();
                if(line.contains(keyword)){
                    System.out.println(line);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("No lines with keyword found.");
            }
        } catch (Exception r) {
            System.out.println("File not found.");
        }
    }

    class ClientHandler implements Runnable {
        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String username;
        private int clientID;
        private String printedclientID;

        public ClientHandler(Socket client) {
            this.client = client;
            // Handle inputs for each client separately
        }

        public void run() {
            try {
                String currenttime;
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("Please enter your username...");

                clientID = getID();
                printedclientID = "@" + String.format("%05d", clientID);
                username = in.readLine();

                out.println("Your ID is " + printedclientID);
                showmembers();
                count++;
                currenttime = getTime();
                record(currenttime + " " + printedclientID + " " + username + " has joined.");
                broadcast(currenttime + " " + printedclientID + " " + username + " has joined.");
                System.out.println(currenttime + " " + printedclientID + " " + username + " has joined.");
                String input;

                while ((input = in.readLine()) != null) {
                    if (input.startsWith("/exit")) {
                        currenttime = getTime();
                        broadcast(currenttime + " " + printedclientID + " " + username + " has left.");
                        record(currenttime + " " + printedclientID + " " + username + " has left.");
                        System.out.println(currenttime + " " + printedclientID + " " + username + " has left.");
                        removeClient(printedclientID);
                        count--;
                        close();
                    } else if (input.startsWith("/rename")) {
                        try {
                            String t = input.substring(8).trim();
                            if (!t.isEmpty()){
                                broadcast(currenttime + " " + printedclientID + " " + username + " has renamed themselves to " + t);
                                record(currenttime + " " + printedclientID + " " + username + " has renamed themselves to " + t);
                                System.out.println(currenttime + " " + printedclientID + " " + username + " has renamed themselves to " + t);
                                username = t;
                            } else {
                                out.println("Syntax error. Please try again.");
                            }
                        } catch (Exception e) {
                            out.println("Syntax error. Please try again.");
                        }
                    } else if (input.startsWith("/members")){
                        showmembers();
                    } else if (input.startsWith("/help")){
                        help();
                    } else if (input.startsWith("/dm")){
                        dm(input);
                    } else if (input.startsWith("/count")){
                        count();
                    } else if (input.startsWith("/history")){
                        out.println("Sending chat history...");
                        sendChatHistory(client);
                    } else if (input.startsWith("/search")){
                        try {
                            String filename = chatname+"history.txt";
                            String keyword = input.substring(8).trim();
                            if(!keyword.isEmpty()){
                                search(filename, keyword);
                            } else {
                                out.println("Syntax error. Please try again.");
                            }
                        } catch (Exception e) {
                            out.println("Syntax error. Please try again.");
                        }
                    } else {
                        currenttime = getTime();
                        broadcast(currenttime + " " + printedclientID + " " + username + ": " + input);
                        System.out.println(currenttime + " " + printedclientID + " " + username + ": " + input);
                        record(currenttime + " " + printedclientID + " " + username + ": " + input);
                    }
                }

            } catch (IOException e) {
                close();
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public void close() {
            try{
                in.close();
                out.close();
                if(!client.isClosed()){
                    client.close();
                }
            } catch (IOException e) {
                // ignore
            }

        }

        public void count() {
            out.println("Number of participants: " + count);
        }

        public void showmembers() {
            String currenttime = getTime();
            String output = "";
            output = output + printedserverID + " " + hostname;
            for (ClientHandler ch : ar) {
                output = output + ", " + ch.printedclientID + " " + ch.username;
            }
            sendMessage(currenttime + " " + "Members in this chatroom: " + output);
        }

        public void dm(String input) {
            // Private message function
            try{
                String[] split = input.split(" ", 3);
                String ID = split[1];
                String message = split[2];
                String currenttime = getTime();
                boolean found = false;
                for (ClientHandler ch : ar) {
                    if (ID.trim().equalsIgnoreCase(ch.printedclientID.trim())){
                        ch.sendMessage("(Private) " + currenttime + " " + printedclientID + " " + username + ": " + message);
                        out.println("Private message sent.");
                        found = true;
                        return;
                    } else if (ID.equals(printedserverID)){
                        System.out.println("(Private) " + currenttime + " " + printedclientID + " " + username + ": " + message);
                        out.println("Private message sent.");
                        found = true;
                        return;
                    } else {
                        found = false;
                    }
                }
                if (!found) {
                    out.println("Recipient not found. Please try again.");
                }
            } catch (Exception e) {
                out.println("Syntax error. Please try again.");
            }
        }

        public void sendChatHistory(Socket socket) {
            try {
                File file = new File(chatname+"history.txt");
                if (!file.exists()) {
                    System.out.println("Chat history file not found.");
                    return;
                }

                OutputStream outputStream = socket.getOutputStream();
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = fileInputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.flush();
                fileInputStream.close();
            } catch (IOException e) {
                System.out.println("Error sending chat history.");
            }
        }

        public void help() {
            out.println("List of all available commands: ");
            out.println("/dm [ID of recipient including @] [Message]  --  Send private messages");
            out.println("/rename [New Name]  --  Change name");
            out.println("/help   --  Display available commands");
            out.println("/exit   --  Exits the server");
            out.println("/members  --  Display current members");
            out.println("/history -- Displays chat history of server");
            out.println("/count -- Displays number of members in server");
            out.println("/search [keyword] -- Finds every instance of keyword in a line");
        }

        public void search(String fileName,String keyword) {
            try {
                Scanner scan = new Scanner(new File(fileName));
                boolean found = false;
                while(scan.hasNext()){
                    String line = scan.nextLine();
                    if(line.contains(keyword)){
                        out.println(line);
                        found = true;
                    }
                }
                if (!found) {
                    out.println("No lines with keyword found.");
                }
            } catch (Exception r) {
                out.println("File not found.");
            }
        }

        public void removeClient(String clientid) {
            Iterator<ClientHandler> iterator = ar.iterator();
            while (iterator.hasNext()) {
                ClientHandler ch = iterator.next();
                if (ch.printedclientID.equals(clientid)) {
                    iterator.remove();
                }
            }
        }

    }
}
