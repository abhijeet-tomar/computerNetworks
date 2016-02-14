/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aadi
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

class Client implements Runnable, Comparable<Client> {

    String name;
    String password;
    String host;
    int port;
    Socket connection;
    Scanner sc;
    PrintWriter out;

    Client(String name, Socket connection) {
        this.name = name;
        this.connection = connection;
    }

    Client(Socket connection) {
        this.name = null;
        this.connection = connection;
    }

    Client(String name) {
        this.name = name;
    }
    Client(String name, String host, int port){
        this.name = name;
        this.host = host;
        this.port = port;
    }

    public boolean isAlive() {
        return connection.isConnected();
//        connection.
    }

    @Override
    public void run() {
        boolean loggedIn = false;
        try {
            sc = new Scanner(connection.getInputStream());
            out = new PrintWriter(connection.getOutputStream());
            while (sc.hasNext()) {
                int code = Integer.parseInt(sc.nextLine());
                if (code == 0) {
                    //signup
                    System.out.println("New Signup request");
                    name = sc.nextLine();
                    password = sc.nextLine();
                    if (Server.allClients.indexOf(this) == -1) {
                        Server.allClients.add(this);
                        out.println("true");
                        System.out.println("Registered new user " + this);
                    } else {
                        out.println("false");
                        System.out.println("Can't register this user " + this);
                    }
                    out.flush();
                    continue;
                } else if (code == 1) {
                    //login
                    System.out.println("New login request");
                    name = sc.nextLine();
                    password = sc.nextLine();
                    System.out.println("Message from " + this.name);
                    int idx = Server.allClients.indexOf(this);
                    if (idx == -1) {
                        out.println("false");
                        out.flush();
                        continue;
                    } else {
                        Client x = Server.allClients.get(idx);
                        if (password.equals(x.password)) {
                            loggedIn = true;
//                            System.out.println("Logged in " + this);
                            out.println("true");
                            out.flush();
                        } else {
                            out.println("false");
                            out.flush();
                            continue;
                        }
                    }
                    host = connection.getInetAddress().getHostAddress();
                    port = Integer.parseInt(sc.nextLine());
                    System.out.println("Logged in : " + this);
                    for (int i = 0; i < Server.allClients.size(); i++) {
                        Client xx = Server.allClients.get(i);
                        if(xx == this)
                            continue;
                        out.println(xx);
                    }
                    out.flush();
                } else if (code == 2) {
                    //add friend
                    System.out.println("New add friend request");
                    String name = sc.nextLine();
                    Client x = new Client(name);
                    int idx = Server.allClients.indexOf(x);
                    if(idx == -1){
                        continue;
                    } 
                    x = Server.allClients.get(idx);
                    x.MSG(this.toString());
                    out.println(x);
                    out.flush();
                } else if (code == 3) {
                    System.out.println("New logout request");
                    //logout
                } else {
                    System.out.println("Invald request");
                    out.println("false");
                    out.flush();
                    continue;
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void MSG(String msg) {
        out.println(msg);
        out.flush();
    }

    @Override
    public int compareTo(Client t) {
        System.out.println(this.name.compareTo(t.name));
        return this.name.compareTo(t.name);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Client)) {
            return false;
        }

        Client c = (Client) o;

        return c.name.equals(this.name);
    }

    @Override
    public String toString() {
//        return this.name + "\t" + this.password + "\t" + this.host + "\t" + this.port;
        return this.name + "\t" + this.host + "\t" + this.port;
    }
}

public class Server {
//    ArrayList<Client> activeClient = new ArrayList<>();
//    Collections.synchronizedList(new ArrayList<Client>());

    static List<Client> allClients = Collections.synchronizedList(new ArrayList());

    public static void main(String[] args) {
        final int port = 5687;
        ServerSocket server;
        try {
            server = new ServerSocket(port);
        } catch (IOException ex) {
            System.out.println(ex);
            System.out.println("Can't start server");
            return;
        }
        System.out.println("Waiting for clients");

        while (true) {
            Socket newConn;
            try {
                newConn = server.accept();
            } catch (IOException ex) {
                //Logger.getLogger(Sever.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex);
                System.out.println("Can't start connection with client");
                continue;
            }

            System.out.println("Connected to " + newConn.getLocalAddress() + ":" + newConn.getLocalPort());

            Thread newCli = new Thread(new Client(newConn));
            newCli.start();
        }
    }
}
