package de.niconagel.minecraftbackend;

import de.niconagel.minecraftbackend.commands.*;
import de.niconagel.minecraftbackend.servermanager.SocketServer;
import dev.comgaming.framework.Framework;
import dev.comgaming.framework.utils.DatabaseManager;
import dev.comgaming.framework.utils.filemanager.FileManager;
import lombok.Getter;

import java.util.Scanner;

public class MinecraftbackendApplication {

    @Getter
    private static final Scanner scanner = new Scanner(System.in);

    @Getter
    private static SocketServer socketServer;

    private static DatabaseManager databaseManager;

    public static void main(String[] args) {

        long timeStart = System.currentTimeMillis();
        Framework framework = new Framework();
        framework.init();
        Framework.getLogger().info("console", "Backend is starting");

        FileManager fileManager = new FileManager("system.properties");
        fileManager.writeInNextFreeLine("dbconnection: false");
        fileManager.writeInNextFreeLine("port: 10000");


        socketServer = new SocketServer();
        socketServer.start();

        long timeEnd = System.currentTimeMillis();

        Framework.getLogger().info("console","Starttime: " + (timeEnd - timeStart) + " ms.\n");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            handleCommand(input);
        }
    }

    private static void handleCommand(String input) {

        switch (input.toLowerCase()) {
            case "clear", "cls" -> CMD_Clear.onCommand();
            case "help", "?" -> CMD_Help.onCommand();
            case "stop", "end" -> CMD_Stop.onCommand();
            default -> Framework.getLogger().info("console", "Unknown command. Use 'help'.");
        }
    }

    public static void onStop() {
        Framework.getLogger().info("console", "Backend is stopping");
        try {
            if (socketServer != null) {
                socketServer.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Framework.getLogger().info("console", "Backend stopped");
        System.exit(0);
    }
}