package ru.practicum.explore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "ru.practicum.explore.client")
@ComponentScan(value = "ru.practicum.explore")
public class MainServer {

    public static void main(String[] args) {
        SpringApplication.run(MainServer.class, args);
    }
}
