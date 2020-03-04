package com.bonial.challenge;

import com.amazonaws.services.s3.event.S3EventNotification;
import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;

public class AWSLambdaHandler extends SpringBootRequestHandler<S3EventNotification, String> {

}
