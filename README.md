# Transports on the cloud
Transport company is tracking mobility records on their partners using JSON files and storing them on S3. Each file that a partner uploads on S3 contains up to 100 records of up to 3 different transport types: cars, trains and planes.

## Project Structure and Run Instructions

The application is built using Spring Boot and contains a project named "transports-on-the-cloud", which is a Java 8 Maven project.

To compile the project, run the following command :

`mvn clean package`

This command will build the project and also run all tests.

### Creating a Lambda Function which is triggered on s3 Object creation triggers

Prerequisites :
- Having an AWS account with access to AWS Lambda, S3 and CloudWatch(for logs)
- Having Maven & Java 8 installed and on $PATH

Steps :
1. Login in to AWS and open AWS management console.
2. Search for "lambda" and select Lambda -> Functions -> Create Function Button.
3. Select "Author from scratch" option.<br/>
   Here enter the function name, set runtime to Java 8 and also select an Execution role which has access to S3.
4. Click on "Create function" button
5. Navigate to Lambda -> Functions -> FunctionName and in the Configuration tab, select "Add Trigger" Button
6. Choose "S3" from the drop down.<br/>
   Set Bucket field to the bucket which has `records` and `summary` folders.<br/>
   Set Event type as "All Object Create Events" from the dropdown.<br/>
   Set Prefix to `records/`(This will trigger when objects are created inside the records folder)
7. Click on "Add" Button to create this trigger.
8. Navigate to Lambda -> Functions -> FunctionName tab and in the Configuration tab, change the value of Timeout to 30 seconds      in "Basic Settings" section.
9. Save this Configuration by clicking on the "Save" button on the top right hand side.

### Configuring the jar to be used by the Lambda Function

Navigate to Lambda -> Functions -> FunctionName and in the Configuration tab, in the "Function Code" section,
- Input "com.bonial.challenge.AWSLambdaHandler" as Handler.
- Set Runtime to Java 8.
- Select "transports-on-the-cloud-1.0.0.jar" from target folder created after building the project for Function Package.

Save this Configuration by clicking on the "Save" button on the top right hand side.

You are ready to start uploading Files for processing now!!

## Uploading files to S3

Upload relevant files to `records` folder and `PARTNER ID` sub folder in the configured S3 Bucket.

Note : Creation of sub folders for new Partner IDs will also trigger the Lambda Function. However, No summary files will be uploaded for this trigger and the Logs will reflect this with the message : <br/>
"Folder was created for partner. Lambda was invoked but no summary files were uploaded." 

## Checking result summary files from S3

For every file uploaded in the `records/partnerID` folder, a summary file with the same name but suffixed with "-summary" will be created in the `summary/partnerID` folder.

**This achieves the following two bonus tasks <br/>
"store the summarize file in same bucket using path /summary/partnerId" <br/>
"processing multiple partners"**
