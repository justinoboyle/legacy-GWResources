package com.lightcraftmc.gw;

import com.lightcraftmc.guildwars.resources.webserver.ServerHandler;
import com.lightcraftmc.guildwars.resources.webserver.WebServer;

public class ServerManager {

    private int port;
    private boolean serverStarted = false;
    private WebServer server;
    private ServerHandler handler;
    private static ServerManager instance;

    public static ServerManager getManager() {
        if (instance == null) {
            instance = new ServerManager();
        }
        return instance;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isServerStarted() {
        return serverStarted;
    }

    public void shutdownServer() {
        handler.disable();
    }

    public void startServer() {
        handler = new ServerHandler();
        handler.enable();
    }

    public WebServer getWebServer() {
        return server;
    }

}
