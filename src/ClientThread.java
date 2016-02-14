
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Scanner;
import javax.swing.DefaultListModel;

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

    ClientThread(Socket sock, String name) {
        this.sock = sock;
        this.name = name;
        server = false;
    }

    ClientThread(Socket sock, String name, ClientHome client) {
        this.sock = sock;
        this.name = name;
        server = true;
    }

    ClientThread(Socket sock, String name, chatWindow chat) {
        this.sock = sock;
        this.name = name;
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
                ClientHome.myFriends.add(x);
                System.out.println(ClientHome.myFriends);
                DefaultListModel m = new DefaultListModel();
                for (Iterator<Client> it = ClientHome.myFriends.iterator(); it.hasNext();) {
                    Client xx = it.next();
                    m.addElement(xx.name);
                }
                client.friendsList.setModel(m);
            }
        } else {
            chat.out = out;
            while (sc.hasNext()) {
                String msg = sc.nextLine();
                chat.chatHistoryTA.append(name + " : " + msg + "\n");
            }
        }
    }

}

class ClientClientThread {

    void startServer(ServerSocket server) {
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
                System.out.println("Message from non friend. Terminated!");
                continue;
            }
            Client c = ClientHome.myFriends.get(idx);

            chatWindow x = new chatWindow(c.name);
            if (ClientHome.chats.indexOf(x) != -1) {
                System.out.println("Already connected");
                continue;
            }

            ClientHome.chats.add(x);
            ClientThread t = new ClientThread(s, c.name, x);
            x.setTitle(c.name);
            x.setVisible(true);
            Thread tx = new Thread(t);
            tx.start();
        }
    }
}
