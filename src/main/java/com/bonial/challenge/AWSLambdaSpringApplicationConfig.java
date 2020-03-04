package com.bonial.challenge;

import com.bonial.challenge.model.Car;
import com.bonial.challenge.model.Plane;
import com.bonial.challenge.model.Train;
import com.bonial.challenge.service.TransportCalculatorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSLambdaSpringApplicationConfig {

    @Bean
    public TransportCalculatorService carCalculatorService(){
        return new TransportCalculatorService(new Car(), 0);
    }

    @Bean
    public TransportCalculatorService trainCalculatorService(){
        return new TransportCalculatorService(new Train(), 0);
    }

    @Bean
    public TransportCalculatorService planeCalculatorService(){
        return new TransportCalculatorService(new Plane(), 0);
    }

}
