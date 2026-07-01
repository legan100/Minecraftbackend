package de.niconagel.minecraftbackend.servermanager;

import de.niconagel.minecraftbackend.MinecraftbackendApplication;
import dev.comgaming.framework.Framework;
import lombok.Getter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketServer {

    private static final int PORT = 10000;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final Set<ClientConnection> clients = ConcurrentHashMap.newKeySet();

    @Getter
    private final Set<ServerInfo> registeredServers = ConcurrentHashMap.newKeySet();

    private ServerSocket serverSocket;
    private volatile boolean running;

    // =========================
    // MAINTENANCE STATE
    // =========================
    private final AtomicBoolean maintenance = new AtomicBoolean(false);

    public boolean isMaintenance() {
        return maintenance.get();
    }

    public void setMaintenance(boolean value) {
        maintenance.set(value);
    }

    public void toggleMaintenance() {
        maintenance.set(!maintenance.get());
    }

    // =========================
    // START SERVER
    // =========================
    public void start() {

        try {
            serverSocket = new ServerSocket(PORT);
            running = true;

            Framework.getLogger().info("console",
                    "SocketServer started on port " + PORT);

            executor.submit(() -> {
                while (running) {
                    try {
                        Socket socket = serverSocket.accept();
                        socket.setKeepAlive(true);
                        socket.setTcpNoDelay(true);

                        ClientConnection client = new ClientConnection(socket, this);
                        clients.add(client);

                        Framework.getLogger().info("console",
                                "Client connected: " + socket.getInetAddress());

                        executor.submit(client);

                    } catch (IOException e) {
                        if (running) e.printStackTrace();
                    }
                }
            });

        } catch (IOException e) {
            Framework.getLogger().error("console",
                    "Failed to start SocketServer");
            e.printStackTrace();
        }
    }

    // =========================
    // MESSAGE HANDLING
    // =========================
    public void handleIncomingMessage(ClientConnection client, String message) {

        if (message == null || message.isBlank()) return;

        try {

            // =========================
            // MAINTENANCE BLOCK
            // =========================
            if (isMaintenance()) {

                boolean allowed =
                        message.startsWith("maintenance_bypass") ||
                                message.startsWith("statsport_");

                if (!allowed) {
                    client.send("server_in_maintenance");
                    return;
                }
            }

            // =========================
            // SERVER REGISTRATION
            // =========================
            if (message.startsWith("statsport_")) {
                registerServer(client, message);
                return;
            }

            // =========================
            // BACKEND RESTART
            // =========================
            if (message.equals("bc_backendrestart")) {
                broadcast("bc_backendrestart");
                MinecraftbackendApplication.onStop();
                return;
            }

            // =========================
            // GLOBAL BROADCAST
            // =========================
            if (message.startsWith("bc_globalbroadcast")) {
                String text = message.substring("bc_globalbroadcast".length()).trim();
                broadcast("gbc_" + text);
                return;
            }

            Framework.getLogger().info("console",
                    "[" + client.getSocket().getInetAddress() + "] " + message);

        } catch (Exception e) {
            Framework.getLogger().error("console",
                    "Error handling message");
            e.printStackTrace();
        }
    }

    // =========================
    // REGISTER SERVER NODE
    // =========================
    private void registerServer(ClientConnection client, String message) {

        try {
            String data = message.substring("statsport_".length());
            String[] parts = data.split("_");

            if (parts.length != 2) return;

            String name = parts[0];
            int port = Integer.parseInt(parts[1]);

            client.setServerInfo(name, port);

            registeredServers.add(new ServerInfo(name, port));

            Framework.getLogger().info("clientmanagement",
                    "Registered server: " + name + ":" + port);

        } catch (Exception e) {
            Framework.getLogger().error("clientmanagement",
                    "Invalid server registration: " + message);
        }
    }

    // =========================
    // BROADCAST
    // =========================
    public void broadcast(String message) {
        clients.forEach(c -> {
            try {
                c.send(message);
            } catch (Exception ignored) {}
        });
    }

    // =========================
    // CLIENT REMOVE
    // =========================
    public void removeClient(ClientConnection client) {
        clients.remove(client);

        Framework.getLogger().info("console",
                "Client disconnected. Online: " + clients.size());
    }

    // =========================
    // STOP SERVER
    // =========================
    public void stop() {

        running = false;

        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ignored) {}

        clients.forEach(ClientConnection::disconnect);
        clients.clear();
        registeredServers.clear();

        executor.shutdownNow();

        Framework.getLogger().info("console",
                "SocketServer stopped");
    }
}