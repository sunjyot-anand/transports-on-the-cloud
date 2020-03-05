package com.bonial.challenge;

import lombok.extern.log4j.Log4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

@Log4j
@ComponentScan("com.bonial.challenge")
@SpringBootApplication
public class AWSLambdaSpringApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
            SpringApplication.run(AWSLambdaSpringApplication.class, args);
        }

    @PostConstruct
    public void init(){
        log.info("Application started succefully!!");
    }

}
