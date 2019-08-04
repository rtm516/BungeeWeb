package com.rtm516.BungeeWeb;

import com.sun.net.httpserver.HttpServer;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class HTTPServer {
	static HttpServer server;

	public static void start(int port) {
		if (port <= 0) {
			port = 8080;
		}
		
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
			
			server.createContext("/", httpExchange -> {
				String extra = "";
				
				byte response[] = (renderHome() + extra).getBytes("UTF-8");

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
					response = "Invalid path".getBytes("UTF-8");
				} else {
					response = Files.readAllBytes(file.toPath());
				}
				
				String contentType;
				switch (Utils.getFileExtension(file)) {
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
			
			for (WebLink link : BungeeWeb.instance.getLinks()) {
				server.createContext("/" + link.id + "/", httpExchange -> {
					String reqFile = httpExchange.getRequestURI().getPath().substring(("/" + link.id + "/").length());
					reqFile = Utils.encodePath(reqFile);
					
					BungeeWeb.instance.getLogger().info(reqFile);					
					
					//URL obj = new URL("http://" + ProxyServer.getInstance().getServerInfo(link.server).getAddress().getHostString() + ":" + Integer.toString(link.port) + "/" + reqFile);
					URL obj = new URL("http://192.168.1.2/" + reqFile);
					HttpURLConnection con = (HttpURLConnection) obj.openConnection();
					
					BungeeWeb.instance.getLogger().info("Request");
					con.setRequestMethod(httpExchange.getRequestMethod());
					for (Entry<String, List<String>> header : httpExchange.getRequestHeaders().entrySet()) {
						if (header.getKey() == null) { continue; }
						//if (header.getKey().toLowerCase().equals("content-type")) { continue; }
						for (String value : header.getValue()) {
							con.setRequestProperty(header.getKey(), value);
						}
					}
					
					
					/*String contentStr = con.getContentType();
					if (con.getContentEncoding() != null) {
						contentStr += "; charset=" + con.getContentEncoding();
					} else {
						contentStr += "; charset=UTF-8";
					}*/
					
					//con.setRequestProperty("Content-Type", contentStr);
					
					//BungeeWeb.instance.getLogger().info(con.getContentType());
					//BungeeWeb.instance.getLogger().info(con.getContentEncoding());
					
					Scanner s = new Scanner(httpExchange.getRequestBody());
					s.useDelimiter("\\A");
					String result = s.hasNext() ? s.next() : "";
					s.close();
					
					if (result.length() > 0) {
						con.setDoOutput(true);
						DataOutputStream wr = new DataOutputStream(con.getOutputStream());
						wr.writeBytes(result);
						wr.flush();
						wr.close();
					}
					
					int responseCode = con.getResponseCode();
					
					Scanner responseScanner = new Scanner(con.getInputStream());
					responseScanner.useDelimiter("\\A");
					String responseMessage = responseScanner.hasNext() ? responseScanner.next() : "";
					responseScanner.close();
					
					byte[] response = responseMessage.getBytes();
					
					BungeeWeb.instance.getLogger().info("Response");
					for (Entry<String, List<String>> header : con.getHeaderFields().entrySet()) {
						if (header.getKey() == null) { continue; }
						for (String value : header.getValue()) {
							BungeeWeb.instance.getLogger().info(header.getKey() + ": " + value);
							httpExchange.getResponseHeaders().set(header.getKey(), value);
						}
					}
					BungeeWeb.instance.getLogger().info("1");
					
					if (con.getContentType() != null) {
						//httpExchange.getResponseHeaders().set("Content-Type", con.getContentType());
					}
					
					/*for (Entry<String, Charset> ch : java.nio.charset.Charset.availableCharsets().entrySet()) {
						BungeeWeb.instance.getLogger().info(ch.getKey());
					}*/
					
					//BungeeWeb.instance.getLogger().info(java.nio.charset.Charset.defaultCharset().displayName());
					
					// Force transfer encoding to prevent the page from not loading
					httpExchange.getResponseHeaders().set("Transfer-Encoding", "identity");
					
					//BungeeWeb.instance.getLogger().info(responseMessage);
					
					BungeeWeb.instance.getLogger().info("2");
					
					BungeeWeb.instance.getLogger().info(Integer.toString(responseCode));
					
					httpExchange.sendResponseHeaders(responseCode, response.length);
					
					BungeeWeb.instance.getLogger().info("3");
	
					OutputStream out = httpExchange.getResponseBody();
					out.write(response);
					out.close();
					
					BungeeWeb.instance.getLogger().info("4");
				});
			}

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