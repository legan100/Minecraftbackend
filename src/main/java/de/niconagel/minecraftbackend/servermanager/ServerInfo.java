package de.niconagel.minecraftbackend.servermanager;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerInfo {
    private final String name;
    private final int port;
}