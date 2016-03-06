
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author aadi
 */
public class ClientThread implements Runnable {

    Socket sock;
    Scanner sc;
    PrintWriter out;
    boolean server;
    String name;
    ClientHome client;
    chatWindow chat;
    String username;

    ClientThread(Socket sock, String name, String username) {
        this.sock = sock;
        this.name = name;
        this.username = username;
        server = false;
    }

    ClientThread(Socket sock, String name, ClientHome client, String username) {
        this.sock = sock;
        this.name = name;
        this.client = client;
        this.username = username;
        server = true;
    }

    ClientThread(Socket sock, String name, chatWindow chat, String username) {
        this.sock = sock;
        this.name = name;
        this.username = username;
        server = false;
        this.chat = chat;
    }

    @Override
    public void run() {
        try {
            sc = new Scanner(sock.getInputStream());
            out = new PrintWriter(sock.getOutputStream());
        } catch (IOException e) {
            System.out.println(e);
        }
        if (server) {
            while (sc.hasNext()) {
                String userDetail = sc.nextLine();
                String[] arr = userDetail.split("\t");
                String name = arr[0];
                String host = arr[1];
                int port = Integer.parseInt(arr[2]);
                Client x = new Client(name, host, port);
                int idx = ClientHome.myFriends.indexOf(x);
                if (idx == -1) {
                    ClientHome.myFriends.add(x);
                    client.m.addElement(x.name);
                } else {
                    ClientHome.myFriends.set(idx, x);
                }

            }
        } else {
            chat.clients.add(out);
            out.println(username);
            out.flush();
            System.out.println("Sent Message : " + username);
            while (sc.hasNext()) {
                String msg = sc.nextLine();
                chat.chatHistoryTA.append(msg + "\n");
                for (int i = 0; i < chat.clients.size(); i++) {
                    if (chat.clients.get(i) == out) {
                        continue;
                    }
                    chat.clients.get(i).println(name + " : " + msg);
                    chat.clients.get(i).flush();
                }
            }
        }
    }

}

class ClientClientThread implements Runnable {

    static ServerSocket server;

    @Override
    public void run() {
        Scanner sc;
        PrintWriter out;
        while (true) {
            Socket s;
            try {
                s = server.accept();
            } catch (IOException ex) {
                continue;
            }
            try {
                sc = new Scanner(s.getInputStream());
                out = new PrintWriter(s.getOutputStream());
            } catch (Exception e) {
                continue;
            }
            String name = sc.nextLine();
            int idx = ClientHome.myFriends.indexOf(new Client(name));
            if (idx == -1) {
                Client c = new Client(name);
                c.host = s.getInetAddress().getHostName();
                c.sc = sc;
                c.out = out;

                chatWindow x = new chatWindow(name);
                if (ClientHome.chats.indexOf(x) != -1) {
                    System.out.println("Already connected");
                    continue;
                }

                ClientHome.chats.add(x);
                ClientThread t = new ClientThread(s, c.name, x, ClientHome.clientUsername);
                x.setTitle("Your chat with " + c.name);
                x.setVisible(true);
                Thread tx = new Thread(t);
                tx.setName("Chat with " + c.name);
                tx.start();
                continue;
            }

            Client c = ClientHome.myFriends.get(idx);

            chatWindow x = new chatWindow(c.name);
            if (ClientHome.chats.indexOf(x) != -1) {
                System.out.println("Already connected");
                continue;
            }

            ClientHome.chats.add(x);
            ClientThread t = new ClientThread(s, c.name, x, ClientHome.clientUsername);
            x.setTitle("Your chat with " + c.name);
            x.setVisible(true);
            Thread tx = new Thread(t);
            tx.setName("Chat with " + c.name);
            tx.start();
        }
    }
}
