package com.bonial.challenge.handler;

import com.amazonaws.services.s3.event.S3EventNotification;

import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;

import com.bonial.challenge.model.TransportName;
import com.bonial.challenge.service.TransportFactory;
import com.bonial.challenge.service.TransportCalculatorService;

import java.util.*;

import com.bonial.challenge.utility.AWSRecordUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.json.simple.parser.JSONParser;

import java.util.function.Function;

@Component
public class FileUploadHandler implements Function <S3EventNotification, String> {


    static final Logger log = LoggerFactory.getLogger(FileUploadHandler.class);
    List<TransportCalculatorService> transportCalculatorServices;
    private TransportFactory transportFactory;
    private AWSRecordUtil awsRecordUtil;

    public FileUploadHandler(List<TransportCalculatorService> transportCalculatorServices, TransportFactory transportFactory, AWSRecordUtil awsRecordUtil){
        this.transportFactory = transportFactory;
        this.transportCalculatorServices = transportCalculatorServices;
        this.awsRecordUtil = awsRecordUtil;
    }

    @Override
    public String apply(S3EventNotification s3EventNotification) {
        log.info("Lambda function is invoked:" + s3EventNotification.toJson());
        s3EventNotification.getRecords().forEach(record -> processInputRecord(record));
        return "OK";
    }

    private void processInputRecord(S3EventNotificationRecord record) {
        JSONParser parser = new JSONParser();
        String partnerFileContents = awsRecordUtil.getRecordContents(record);
        Map<String,TransportCalculatorService> transportServicesMap = generateTransportServicesMap(transportCalculatorServices);
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(partnerFileContents);
            JSONArray transportsArray = (JSONArray) jsonObject.get("transports");
            initializeCounters(transportServicesMap); // Resets counters in case multiple files are uploaded
            transportsArray.forEach(currentTransportRecord -> computeCapacity((JSONObject) currentTransportRecord, transportServicesMap));
            logPassengerCapacity(transportServicesMap);
            awsRecordUtil.uploadResult(record,transportServicesMap);
        } catch (ParseException e) {
            if (partnerFileContents.isEmpty() && awsRecordUtil.isFolderCreationNotification(record)){
                log.info("Folder was created for partner. Lambda was invoked but no summary files were uploaded.");
            }
            else
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

}
