package de.niconagel.minecraftbackend.commands;

import de.niconagel.minecraftbackend.MinecraftbackendApplication;

public class CMD_Stop {

    public static void onCommand() {
        MinecraftbackendApplication.onStop();
    }

}