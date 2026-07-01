package de.niconagel.minecraftbackend.commands;

import de.niconagel.minecraftbackend.servermanager.ServerInfo;
import de.niconagel.minecraftbackend.servermanager.SocketServer;
import dev.comgaming.framework.Framework;

public class CMD_ListClients {

    static SocketServer server = null;

    public static void onCommand(){
        Framework.getLogger().info("console","=== Registered Servers ===");

        if (server.getRegisteredServers().isEmpty()) {
            Framework.getLogger().info("console","No registered servers.");
            return;
        }

        int i = 0;
        for (ServerInfo info : server.getRegisteredServers()) {
            Framework.getLogger().info("console","[" + (i++) + "] " + info.getName() + ":" + info.getPort());
        }
    }
}