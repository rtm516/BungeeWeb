package com.rtm516.BungeeWeb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.bstats.bungeecord.Metrics;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeWeb extends Plugin {
	public static BungeeWeb instance;
	private Configuration configuration;
	private List<WebLink> webLinks = new ArrayList<>();
	
    @Override
    public void onEnable() {
    	instance = this;
    	
    	@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this);
    	
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
    	
    	File contentDir = new File(getDataFolder() + "/content/");
    	if (!contentDir.exists())
    		contentDir.mkdir();

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
        
        Configuration links = (Configuration) BungeeWeb.instance.getConfig().get("links");
        for(String id : links.getKeys()){
        	Configuration linkInfo = (Configuration) links.get(id);
        	WebLink tmpLink = new WebLink();
        	
        	tmpLink.id = id;
        	tmpLink.name = linkInfo.getString("name");
        	tmpLink.server = linkInfo.getString("server");
        	tmpLink.port = linkInfo.getInt("port");
        	
        	webLinks.add(tmpLink);
        }
    }
    
    public Configuration getConfig() {
    	return configuration;
    }
    
    public List<WebLink> getLinks() {
    	return webLinks;
    }
    
    public void debugPrint(String msg) {
    	if (BungeeWeb.instance.getConfig().getBoolean("debug")) {
    		BungeeWeb.instance.getLogger().info(msg);
    	}
    }
}