package de.niconagel.minecraftbackend.servermanager;

import dev.comgaming.framework.Framework;
import lombok.Getter;

import java.io.*;
import java.net.Socket;

public class ClientConnection implements Runnable {

    @Getter
    private final Socket socket;

    private final SocketServer server;

    private BufferedReader in;
    private PrintWriter out;

    @Getter
    private String serverName;

    @Getter
    private int serverPort;

    public ClientConnection(Socket socket, SocketServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            Framework.getLogger().info("clientmanagement","Client connected: " + socket.getInetAddress());

            String line;

            while ((line = in.readLine()) != null) {
                server.handleIncomingMessage(this, line);
            }

        } catch (Exception e) {
            Framework.getLogger().info("clientmanagement", "Client lost: " + socket.getInetAddress());
        } finally {
            disconnect();
            server.removeClient(this);
        }
    }

    public void send(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }

    public void setServerInfo(String name, int port) {
        this.serverName = name;
        this.serverPort = port;
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (Exception ignored) {}
    }
}