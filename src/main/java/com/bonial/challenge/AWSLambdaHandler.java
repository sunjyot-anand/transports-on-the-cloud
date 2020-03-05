package com.bonial.challenge;

import com.amazonaws.services.s3.event.S3EventNotification;
import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;

public class AWSLambdaHandler extends SpringBootRequestHandler<S3EventNotification, String> {
/*
This is the Handler invoked by S3 Object creation event triggers.
The handleRequest() method is called & we won't be overriding it.
We have set "function.name" property to invoke the our handler & allow extensibility.
*/
}
