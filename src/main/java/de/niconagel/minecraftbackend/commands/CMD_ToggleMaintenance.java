package de.niconagel.minecraftbackend.commands;

import dev.comgaming.framework.Framework;
import dev.comgaming.framework.utils.InternalMethods;

public class CMD_ToggleMaintenance {

    public static void onCommand(){
       InternalMethods.toggleMaintenance("Console");
       Framework.getLogger().info("console", "changed Maintenance-Mode to " + InternalMethods.isMaintenance());

    }

}