package com.lightcraftmc.guildwars.resources.webserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.lightcraftmc.gw.ServerManager;

public class WebServer extends Thread {
    public ServerHandler ft = null;
    public boolean running = true;

    public WebServer(ServerHandler f) {
        this.ft = f;
    }

    public void run() {
        try {
            this.ft.Server = new ServerSocket(ServerManager.getManager().getPort(), 10, InetAddress.getByName("0"));
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        while (this.running) {
            @SuppressWarnings("unused")
            Socket connected = null;
            try {
                new WebHandler(this.ft.Server.accept(), this).start();
            } catch (IOException localIOException1) {
            }
        }
    }
}
