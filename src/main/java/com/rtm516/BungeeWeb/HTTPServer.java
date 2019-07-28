package com.rtm516.BungeeWeb;

import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;

public class HTTPServer {
	static final int port = 8080;
	static HttpServer server;

	public static void start() {
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);

			server.createContext("/", httpExchange -> {
				byte response[] = "Hello, World!".getBytes("UTF-8");

				httpExchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
				httpExchange.sendResponseHeaders(200, response.length);

				OutputStream out = httpExchange.getResponseBody();
				out.write(response);
				out.close();
			});
			
			server.createContext("/", httpExchange -> {
				byte response[] = "Hello, World!".getBytes("UTF-8");

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
		server.stop(0);
	}

}