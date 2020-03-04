package com.bonial.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.bonial.challenge")
@SpringBootApplication
public class AWSLambdaSpringApplication extends SpringBootServletInitializer {

        public static void main(String[] args) {
            SpringApplication.run(AWSLambdaSpringApplication.class, args);
        }

}
