package com.lightcraftmc.guildwars.resources.webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

import com.lightcraftmc.gw.GWResources;

public class WebHandler extends Thread {
    private Socket client;
    private BufferedReader inFromClient = null;
    private DataOutputStream outToClient = null;
    @SuppressWarnings("unused")
    private WebServer ws = null;

    public WebHandler(Socket c, WebServer w) {
        this.client = c;
        this.ws = w;
    }

    @SuppressWarnings("unused")
    public void run() {
        try {
            this.inFromClient = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
            this.outToClient = new DataOutputStream(this.client.getOutputStream());
            String requestString = this.inFromClient.readLine();
            String headerLine = requestString;
            StringTokenizer tokenizer = new StringTokenizer(headerLine);
            String httpMethod = tokenizer.nextToken();
            String httpQueryString = tokenizer.nextToken();
            while (this.inFromClient.ready()) {
                requestString = this.inFromClient.readLine();
                String[] v = requestString.split(": ");
            }

            if (httpMethod.equals("GET")) {
                if (!httpQueryString.contains(",")) {
                    this.sendResponse(404, "Page not found.", false);
                    return;
                }
                String[] args = httpQueryString.split(",");
                if (args.length < 2) {
                    this.sendResponse(404, "Page not found.", false);
                    return;
                }
                final String username = args[1];
                if (!GWResources.getInstance().acceptedPacks.contains(username)) {
                    GWResources.getInstance().acceptedPacks.add(username);
                }

                this.sendResponse(404, "", false);
                return;
            }
        } catch (Exception localException) {
        }
    }

    public void sendResponse(int statusCode, String responseString, boolean isFile) throws Exception {
        String statusLine = null;
        String serverdetails = "Server: Java HTTPServer";
        String contentLengthLine = null;
        String fileName = null;
        String contentTypeLine = "Content-Type: text/html\r\n";
        FileInputStream fin = null;
        statusLine = "HTTP/1.1 200 OK\r\n";
        switch (statusCode) {
        case 200:
            statusLine = "HTTP/1.1 200 OK\r\n";
            break;
        case 500:
            statusLine = "HTTP/1.1 500 Internal Server Error\r\n";
            break;
        case 222:
            statusLine = "HTTP/1.1 222 Ping Response\r\n";
            break;
        default:
            statusLine = "HTTP/1.1 404 Not Found\r\n";
        }
        if (isFile) {
            fileName = responseString;
            fin = new FileInputStream(fileName);
            contentLengthLine = "Content-Length: " + Integer.toString(fin.available()) + "\r\n";
            if ((!fileName.endsWith(".htm")) && (!fileName.endsWith(".html"))) {
                contentTypeLine = "Content-Type: application/zip\r\n";
            }
        } else {
            contentLengthLine = "Content-Length: " + responseString.length() + "\r\n";
        }
        if (!this.client.isClosed()) {
            this.outToClient.writeBytes(statusLine);
            this.outToClient.writeBytes(serverdetails);
            this.outToClient.writeBytes(contentTypeLine);
            this.outToClient.writeBytes(contentLengthLine);
            this.outToClient.writeBytes("Connection: close\r\n");
            this.outToClient.writeBytes("\r\n");
            if (isFile) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fin.read(buffer)) != -1) {
                    // int bytesRead;
                    this.outToClient.write(buffer, 0, bytesRead);
                }
                fin.close();
            } else {
                this.outToClient.writeBytes(responseString);
            }
            this.outToClient.close();
        }
    }
}