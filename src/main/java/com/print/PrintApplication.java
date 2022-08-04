package com.print;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {
        "com.print"
})
@SpringBootApplication
public class PrintApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrintApplication.class, args);
    }

}
