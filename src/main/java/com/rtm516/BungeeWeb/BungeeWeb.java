package com.rtm516.BungeeWeb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeWeb extends Plugin {
	private Configuration configuration;
	
    @Override
    public void onEnable() {        
        setupConfig();
        
        HTTPServer.start(configuration.getInt("port"));
    }
    
    @Override
    public void onDisable() {
    	HTTPServer.stop();
    }
    
    private void setupConfig() {
    	if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

   
        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        try {
			configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException e) {
			e.printStackTrace();
			
			getProxy().getPluginManager().unregisterListeners(this);
	        getProxy().getPluginManager().unregisterCommands(this);
			this.onDisable();
		}
    }
}