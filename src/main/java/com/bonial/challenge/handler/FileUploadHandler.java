package com.bonial.challenge.handler;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification;

import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;

import com.amazonaws.services.s3.model.S3Object;
import com.bonial.challenge.model.TransportName;
import com.bonial.challenge.service.TransportFactory;
import com.bonial.challenge.service.TransportCalculatorService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.json.simple.parser.JSONParser;

import javax.annotation.PostConstruct;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class FileUploadHandler implements Function <S3EventNotification, String> {


    static final Logger log = LoggerFactory.getLogger(FileUploadHandler.class);

    @Value("${aws.accesskey}")
    private String awsAccessKey;
    @Value("${aws.secretkey}")
    private String awsSecretKey;
    @Value("${aws.region}")
    private String awsRegion;

    @Value("${records.folder.path}")
    private  String recordsFolderPath;
    @Value("${summary.folder.path}")
    private  String summaryFolderPath;

    private AmazonS3 s3;

    List<TransportCalculatorService> transportCalculatorServices;
    private TransportFactory transportFactory;

    @PostConstruct
    private void initializeS3(){
        s3 =
                AmazonS3ClientBuilder
                        .standard()
                        .withRegion(awsRegion)
                        .withForceGlobalBucketAccessEnabled(true)
                        .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey)))
                        .build();
    }

    public FileUploadHandler(List<TransportCalculatorService> transportCalculatorServices, TransportFactory transportFactory){
        this.transportFactory = transportFactory;
        this.transportCalculatorServices = transportCalculatorServices;
    }

    @Override
    public String apply(S3EventNotification s3EventNotification) {
        log.info("Lambda function is invoked:" + s3EventNotification.toJson());
        s3EventNotification.getRecords().forEach(record -> processInputRecord(record));
        return "OK";
    }

    private void processInputRecord(S3EventNotificationRecord record) {
        JSONParser parser = new JSONParser();
        String partnerFileContents = getRecordContents(record);
        Map<String,TransportCalculatorService> transportServicesMap = generateTransportServicesMap(transportCalculatorServices);
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(partnerFileContents);
            JSONArray transportsArray = (JSONArray) jsonObject.get("transports");
            initializeCounters(transportServicesMap); // Resets counters in case multiple files are uploaded
            transportsArray.forEach(currentTransportRecord -> computeCapacity((JSONObject) currentTransportRecord, transportServicesMap));
            logPassengerCapacity(transportServicesMap);
            uploadResult(record,transportServicesMap);
        } catch (ParseException e) {
            log.info("Invalid input received. Please provide valid input.");
        }
    }

    private void initializeCounters(Map<String,TransportCalculatorService> transportServicesMap){
        transportServicesMap.get(TransportName.CAR.name()).setTotalPassengerCapacity(0);
        transportServicesMap.get(TransportName.TRAIN.name()).setTotalPassengerCapacity(0);
        transportServicesMap.get(TransportName.PLANE.name()).setTotalPassengerCapacity(0);
    }

    private void logPassengerCapacity(Map<String,TransportCalculatorService> transportServicesMap) {
        log.info("total car passengers : " + transportServicesMap.get(TransportName.CAR.name()).getTotalPassengerCapacity());
        log.info("total train passengers : " + transportServicesMap.get(TransportName.TRAIN.name()).getTotalPassengerCapacity());
        log.info("total plane passengers : " + transportServicesMap.get(TransportName.PLANE.name()).getTotalPassengerCapacity());
    }

    private void computeCapacity(JSONObject transportJSONRecord, Map<String,TransportCalculatorService> transportServicesMap){
        TransportCalculatorService transportCalculatorService = transportFactory.getTransportType(transportJSONRecord, log, transportServicesMap);
        transportCalculatorService.computePassengerCapacity(transportJSONRecord);
    }

    private Map<String, TransportCalculatorService> generateTransportServicesMap(List<TransportCalculatorService> transportCalculatorServices) {
        Map<String,TransportCalculatorService> transportServicesMap = new HashMap<>();
        for(TransportCalculatorService transportService : transportCalculatorServices){
            transportServicesMap
                    .put(transportService.getTransportType().toString(), transportService);
        }
        return transportServicesMap;
    }

    public String getRecordContents(S3EventNotificationRecord record){
        String s3Key = record.getS3().getObject().getKey();
        String s3Bucket = record.getS3().getBucket().getName();

        S3Object partnerFile = s3.getObject(s3Bucket,s3Key);
        BufferedReader partnerFileReader = new BufferedReader(new InputStreamReader(partnerFile.getObjectContent()));

        String partnerFileContents = partnerFileReader.lines().collect(Collectors.joining("\n"));
        return partnerFileContents;
    }

    public void uploadResult(S3EventNotificationRecord record, Map<String, TransportCalculatorService> transportServicesMap) {
        String s3Key = record.getS3().getObject().getKey();
        String s3Bucket = record.getS3().getBucket().getName();

        String summaryS3Key = s3Key.replaceAll("\\A" + recordsFolderPath, summaryFolderPath).concat("-summary");

        Map<String, Integer> summary = new HashMap<String, Integer>();
        summary.put("cars", transportServicesMap.get(TransportName.CAR.name()).getTotalPassengerCapacity());
        summary.put("trains", transportServicesMap.get(TransportName.TRAIN.name()).getTotalPassengerCapacity());
        summary.put("planes", transportServicesMap.get(TransportName.PLANE.name()).getTotalPassengerCapacity());

        s3.putObject(s3Bucket,summaryS3Key, JSONValue.toJSONString(summary));
    }

}

