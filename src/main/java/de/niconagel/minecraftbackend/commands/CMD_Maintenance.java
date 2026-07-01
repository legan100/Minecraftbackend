package de.niconagel.minecraftbackend.commands;

import dev.comgaming.framework.Framework;
import dev.comgaming.framework.utils.InternalMethods;

public class CMD_Maintenance  {

    public static void onCommand(){
        if (InternalMethods.isMaintenance()) {
            Framework.getLogger().info("console", "Maintenance is enabled.");
        } else {
            Framework.getLogger().info("console","Maintenance is disabled.");
        }
    }

}