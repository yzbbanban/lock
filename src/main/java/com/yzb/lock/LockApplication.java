package com.yzb.lock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LockApplication {

	public static void main(String[] args) {
		SpringApplication.run(LockApplication.class, args);
		TCPServer server = new TCPServer(20048);
		server.startServer();
	}

}

