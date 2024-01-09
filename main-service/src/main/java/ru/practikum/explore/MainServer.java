package ru.practikum.explore;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class MainServer {

    public static void main(String[] args) {
        SpringApplication.run(MainServer.class, args);
        TestClient.testClient();
    }
}
