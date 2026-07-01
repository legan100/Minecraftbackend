package de.niconagel.minecraftbackend.commands;

import dev.comgaming.framework.Framework;

public class CMD_Help {

    public static void onCommand() {
        Framework.getLogger().info("backend", "----------[Help]----------");

        printCommand("clear", "clear the console");
        printCommand("help", "Show all available commands");
        printCommand("getmaintenance", "Show maintenancemode");
        printCommand("stop", "Stop the backend");
        printCommand("ToggleMaintenance", "Toggle maintenance");
    }

    private static void printCommand(String command, String description) {
        Framework.getLogger().info(
                "backend",
                String.format("%-20s %s", command, description)
        );
    }
}