package com.dsf.librar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LibrarApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibrarApplication.class, args);
    }

}
