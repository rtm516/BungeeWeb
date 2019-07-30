package com.rtm516.BungeeWeb;

import com.sun.net.httpserver.HttpServer;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTTPServer {
	static HttpServer server;

	public static void start(int port) {
		if (port <= 0) {
			port = 8080;
		}
		
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
			
			server.createContext("/", httpExchange -> {
				byte response[] = renderHome().getBytes("UTF-8");

				httpExchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
				httpExchange.sendResponseHeaders(200, response.length);

				OutputStream out = httpExchange.getResponseBody();
				out.write(response);
				out.close();
			});
			
			server.createContext("/content/", httpExchange -> {
				String reqFile = httpExchange.getRequestURI().getPath().substring("/content/".length());
				File file = new File(BungeeWeb.instance.getDataFolder() + "/content/", reqFile);
				
				byte response[];
				
				// Make sure there is no DIR traversal and there still in /content/
				if (new File(file.getCanonicalPath()).getParentFile().getCanonicalPath() != new File(BungeeWeb.instance.getDataFolder() + "/content/").getCanonicalPath()) {
					response = "Invalid path".getBytes("utf-8");
				} else {
					response = Files.readAllBytes(file.toPath());
				}
				
				String contentType;
				switch (getFileExtension(file)) {
					case ".css":
						contentType = "text/css";
						break;

					case ".png":
						contentType = "image/png";
						break;

					case ".jpeg":
						contentType = "image/jpeg";
						break;

					case ".jpg":
						contentType = "image/jpeg";
						break;
	
					default:
						contentType = "text/plain";
						break;
				}

				httpExchange.getResponseHeaders().add("Content-Type", contentType + "; charset=UTF-8");
				httpExchange.sendResponseHeaders(200, response.length);

				OutputStream out = httpExchange.getResponseBody();
				out.write(response);
				out.close();
			});

			server.start();
		} catch (Throwable tr) {
			tr.printStackTrace();
		}
	}
	
	public static void stop() {
		if (server != null) {
			server.stop(0);
		}
	}
	
	private static String getFileExtension(File file) {
        String extension = "";
 
        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                extension = name.substring(name.lastIndexOf("."));
            }
        } catch (Exception e) {
            extension = "";
        }
 
        return extension; 
    }
	
	private static String renderHome() {
		String content = "";
		
		content += "<!DOCTYPE html>\n" + 
				"<html>\n" + 
				"<head>\n" + 
				"	<meta charset='utf-8'>\n" + 
				"	<meta http-equiv='X-UA-Compatible' content='IE=edge'>\n" + 
				"	<title>Page Title</title>\n" + 
				"	<meta name='viewport' content='width=device-width, initial-scale=1'>\n" + 
				"	<link rel='stylesheet' type='text/css' media='screen' href='main.css'>\n" + 
				"</head>\n" + 
				"<body>\n" +
				"<div id='buttons'>\n";
		
		//List<String> links = BungeeWeb.instance.getConfig().getStringList("links");
		
        //for(String entry : links){
        //	BungeeWeb.instance.getLogger().info(entry);
        //	for(Field field: entry.getClass().getDeclaredFields()){
        //    	BungeeWeb.instance.getLogger().info(field.toString());
        //    }
        //}
        
        for (WebLink link : BungeeWeb.instance.getLinks()) {
        	content += "<a href='/" + link.id + "/'>" + link.name + "</a>\n";
        }
		
		content += "</div>\n" +
				"</body>\n" + 
				"</html>";
		
		return content;
	}
}