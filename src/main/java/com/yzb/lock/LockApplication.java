package com.yzb.lock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@MapperScan("com.yzb.lock.dao")
@SpringBootApplication
public class LockApplication {

    public static void main(String[] args) {
        SpringApplication.run(LockApplication.class, args);
        SpringApplication.run(LockApplication.class, args);
        TCPServer server = new TCPServer(20048);
        server.startServer();
    }

}

