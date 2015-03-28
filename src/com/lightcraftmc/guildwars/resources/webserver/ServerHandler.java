package com.lightcraftmc.guildwars.resources.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

import com.lightcraftmc.gw.ServerManager;

public class ServerHandler {

    ServerSocket Server = null;
    private WebServer ws = null;
    public String ip = "";
    public HashMap<String, Boolean> checks = new HashMap<String, Boolean>();

    public boolean checkServer(String ip) {
        try {
            URL u = new URL("http://" + ip + ":" + ServerManager.getManager().getPort() + "/ping");
            HttpURLConnection huc = (HttpURLConnection) u.openConnection();
            huc.setRequestMethod("GET");
            huc.connect();
            if (huc.getResponseCode() == 222) {
                return true;
            }
        } catch (IOException localIOException) {
        }
        return false;
    }

    public void disable() {
        try {
            this.getWs().interrupt();
            this.getWs().running = false;
            this.setWs(null);
            this.Server.close();
        } catch (IOException localIOException) {
        }
        System.out.println("Disabled");
    }

    public String getExternalIP() {
        String i = "";
        try {
            URL whatismyip = new URL("http://automation.whatismyip.com/n09230945.asp");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));

            i = in.readLine();
        } catch (Exception e) {
            return "";
        }
        return i;
    }

    public void enable() {
        System.out.println("Starting server");
        (this.setWs(new WebServer(this))).start();
        try {
            String i = getExternalIP();
            if ((i != "") && (checkServer(i))) {
                System.out.println("Got IP: " + InetAddress.getLocalHost().getHostAddress());
                System.out.println("Warning: It is recommended that you port forward the port defined as only users on your Local Network can access the database!");
                this.ip = InetAddress.getLocalHost().getHostAddress();
                return;
            }
        } catch (UnknownHostException localUnknownHostException) {
            try {
                if (checkServer(InetAddress.getLocalHost().getHostAddress())) {
                    System.out.println("Got IP: " + InetAddress.getLocalHost().getHostAddress());
                    System.out.println("Warning: It is recommended that you port forward the port defined as only users on your Local Networkk can access the database!");
                    this.ip = InetAddress.getLocalHost().getHostAddress();
                    return;
                }
            } catch (UnknownHostException localUnknownHostException1) {
                if (checkServer("localhost")) {
                    System.out.println("Got IP: localhost");
                    System.out.println("Warning: It is recommended that you port forward the port defined as other users cannot access the database!");
                    this.ip = "localhost";
                    return;
                }
            }
        }
    }

    public WebServer getWs() {
        return ws;
    }

    public WebServer setWs(WebServer ws) {
        this.ws = ws;
        return ws;
    }

}
