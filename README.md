# Transports on the cloud
Transport company is tracking mobility records on their partners using JSON files and storing them on S3. Each file that a partner uploads on S3 contains up to 100 records of up to 3 different transport types: cars, trains and planes.

## Run Instructions

The application is built using Spring Boot and contains a project named "transports-on-the-cloud", which is a Java 8 Maven project.
The properties file needs to be updated and `aws.accesskey`, `aws.secretkey` and `aws.region` should be set correctly(By default its takes credentials for my personal test bucket on S3). You can find region name mappings here : https://docs.aws.amazon.com/general/latest/gr/rande.html

To compile the project, run the following command :

`mvn clean package`

This command will build the project and also run all tests.

### Creating a Lambda function which is triggered on S3 Object creation

Prerequisites :
- Having an AWS account with access to AWS Lambda, S3 and CloudWatch(for logs)
- Having Maven & Java 8 installed and on $PATH

Steps :
1. Login in to AWS and open AWS management console.
2. Search for "lambda" and select Lambda -> Functions -> Create Function Button.
3. Select "Author from scratch" option.<br/>
   Here enter the function name, set runtime to Java 8 and also select an Execution role which has access to S3.
4. Click on "Create function" button
5. Navigate to Lambda -> Functions -> FUNCTION_NAME and in the Configuration tab, select "Add Trigger" Button
6. Choose "S3" from the drop down.<br/>
   Set Bucket field to the bucket which has `records` and `summary` folders.<br/>
   Set Event type as "All Object Create Events" from the dropdown.<br/>
   Set Prefix to `records/`(This will trigger when objects are created inside the records folder)
7. Click on "Add" Button to create this trigger.
8. Navigate to Lambda -> Functions -> FUNCTION_NAME tab and in the Configuration tab, change the value of Timeout to 30 seconds      in "Basic Settings" section.
9. Save this Configuration by clicking on the "Save" button on the top right hand side.

### Configuring the jar to be used by the Lambda Function

Navigate to Lambda -> Functions -> FUNCTION_NAME and in the Configuration tab, in the "Function Code" section,
- Input "com.bonial.challenge.AWSLambdaHandler" as Handler.
- Set Runtime to Java 8.
- Select "transports-on-the-cloud-1.0.0.jar" from target folder created after building the project for Function Package(About 23 MB in size)

Save this Configuration by clicking on the "Save" button on the top right hand side.

You are ready to start uploading Files for processing now!!

### Uploading files to S3

Upload relevant files to `records` folder and `PARTNER_ID` sub folder in the configured S3 Bucket.

Note : Creation of empty sub directories for new Partner IDs will also trigger the Lambda Function. However, No summary files will be uploaded for this trigger and the Logs will reflect this with the message : <br/>
"Folder was created for partner. Lambda was invoked but no summary files were uploaded."

For files which do not conform to the schema for Transport records or basic JSON formatting, an IllegalArgumentException will be thrown which will log that a malformed file was uploaded. No summary files will be uploaded

### Checking result summary files from S3

For every file uploaded in the `records/PARTNER_ID/` folder, a summary file with the same name but suffixed with "-summary" will be created in the `summary/PARTNER_ID/` folder. 
This enables processing for multiple partners. Every partner will have a folder in the `records/` folder and another folder in the `summary/` folder. 

### Checking logs on CloudWatch

By default we are writing logs to CloudWatch. No prior configuration is required to make this work.

Navigate to <br/>
AWS Management Console -> CloudWatch -> Logs -> Log Groups

Here, you will find a log group "/aws/lambda/FUNCTION_NAME". All logs for Lambda execution are generated in this log group :)


## Bonus tasks
The aforementioned setup achieves the following two bonus tasks:

***1. Store the summarize file in same bucket using path /summary/partnerId <br/>
2. Processing multiple partners***

### For the third Bonus task

***Locally Testing the Lambda Function with SAM***

Prerequisites :
- Docker installed locally (I have used `Docker version 19.03.5, build 633a0ea`)
- SAM installed locally (I have used `sam version 0.2.11`)

Navigate to Project home directory and run:

```sam local generate-event s3 put --bucket S3_BUCKET_NAME --key TEST_FILE_NAME | sam local invoke TransportsOnTheCloudLambda```

The result of the Lambda will be printed to Logs and summary file will be uploaded to S3 in same bucket as the Test file. The path of the test file should be an absolute path with respect to the bucket.

Note: The Bucket `S3_BUCKET_NAME` should be already created on S3(No special permissions need to be set or regions need to be specified)

## Troubleshooting

Assuming that the Lambda Fuction and its trigger have been created on AWS, if the logs show failure of our Spring Boot Application, an easy way would be to turn debug level logs on and check the request the Lambda is receiving. <br/>
This will also print keys for all Transport records to Logs.

In case the failure still persists please feel free to reach out to me : sunjyotsinghanand@gmail.com
