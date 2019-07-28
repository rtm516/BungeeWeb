package com.rtm516.BungeeWeb;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeeWeb extends Plugin {
    @Override
    public void onEnable() {
        // You should not put an enable message in your plugin.
        // BungeeCord already does so
        getLogger().info("Yay! It loads!");
        HTTPServer.start();
    }
    
    @Override
    public void onDisable() {
    	HTTPServer.stop();
    }
}