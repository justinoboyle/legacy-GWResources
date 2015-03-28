package com.lightcraftmc.gw;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class GWResources extends JavaPlugin implements Listener {

    private static GWResources instance;
    public ArrayList<String> acceptedPacks = new ArrayList<String>();
    private String ip;

    public static GWResources getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        getConfig();
        reloadConfig();
        if (getConfig().get("port") == null || getConfig().getInt("port") == 0) {
            getConfig().set("port", 5555);
        }
        if (getConfig().get("ip") == null || getConfig().getString("ip").equals("")) {
            getConfig().set("ip", "127.0.0.1");
        }
        if (getConfig().get("resource-pack") == null || getConfig().getString("resource-pack").equals("")) {
            getConfig().set("resource-pack", "http://arrayprolc.com/guildwars-texturepackv9.zip");
        }
        saveConfig();
        ip = getConfig().getString("ip");
        int port = getConfig().getInt("port");
        ServerManager.getManager().setPort(port);
        ServerManager.getManager().startServer();
    }

    public void onDisable() {
        ServerManager.getManager().shutdownServer();
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(GWResources.getInstance(), new Runnable() {
            public void run() {
                p.setResourcePack("http://" + ip + ":" + getConfig().getInt("port") + "/texturepack," + p.getName() + "," + new Random().nextInt() + ",.zip");
            }
        }, 1);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(GWResources.getInstance(), new Runnable() {
            public void run() {
                p.setResourcePack(GWResources.getInstance().getConfig().getString("resource-pack"));
            }
        }, 20 * 4);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (acceptedPacks.contains(e.getPlayer().getName())) {
            acceptedPacks.remove(e.getPlayer().getName());
        }
    }

    public static boolean hasAcceptedPlayer(OfflinePlayer p) {
        return getInstance().acceptedPacks.contains(p.getName());
    }

}
