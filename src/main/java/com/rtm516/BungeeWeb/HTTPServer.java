package com.rtm516.BungeeWeb;

import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;

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
				byte response[] = "test".getBytes("UTF-8");

				httpExchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
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
				"<body>\n";
		
		content += "Test\n";
		
		content += "</body>\n" + 
				"</html>";
		
		return content;
	}
}