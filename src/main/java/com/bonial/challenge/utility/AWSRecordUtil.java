package com.bonial.challenge.utility;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.S3Object;
import com.bonial.challenge.model.TransportName;
import com.bonial.challenge.service.TransportCalculatorService;
import javax.annotation.PostConstruct;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AWSRecordUtil {

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

    s3.putObject(s3Bucket,summaryS3Key,JSONValue.toJSONString(summary));
  }

  public boolean isFolderCreationNotification(S3EventNotificationRecord record) {
    return (record.getS3().getObject().getKey()).endsWith("/");
  }
}
