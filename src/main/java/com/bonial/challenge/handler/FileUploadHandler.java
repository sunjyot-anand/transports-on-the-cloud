package com.bonial.challenge.handler;

import com.amazonaws.services.s3.event.S3EventNotification;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class FileUploadHandler implements Function <S3EventNotification, String> {

    static final Logger log = LoggerFactory.getLogger(FileUploadHandler.class);

    @Override
    public String apply(S3EventNotification s3EventNotification) {
        log.info("Lambda function is invoked:" + s3EventNotification.toJson());
        return "OK";
    }
}
