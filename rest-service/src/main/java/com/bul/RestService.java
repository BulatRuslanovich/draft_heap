package com.bul;

import lombok.extern.log4j.Log4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j
@SpringBootApplication
public class RestService {
    public static void main(String[] args) {
        SpringApplication.run(RestService.class);
        log.debug("RestService is started!");
    }
}
