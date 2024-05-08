package com.bul;

import lombok.extern.log4j.Log4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j
@SpringBootApplication
public class MailApplication {
    public static void main(String[] args) {
        SpringApplication.run(MailApplication.class);
        log.debug("Mail app is started!");
    }
}
