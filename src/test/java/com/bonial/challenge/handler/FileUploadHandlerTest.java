package com.bonial.challenge.handler;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.bonial.challenge.model.Car;
import com.bonial.challenge.model.Plane;
import com.bonial.challenge.model.Train;
import com.bonial.challenge.service.TransportCalculatorService;
import com.bonial.challenge.service.TransportFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.bonial.challenge.utility.AWSRecordUtil;
import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

@SpringBootTest
public class FileUploadHandlerTest {

  private TransportFactory transportFactory = mock(TransportFactory.class);
  private AWSRecordUtil awsRecordUtil = mock(AWSRecordUtil.class);
  private S3EventNotification s3EventNotification = mock(S3EventNotification.class);
  private S3EventNotificationRecord record = mock(S3EventNotificationRecord.class);

  @InjectMocks
  FileUploadHandler fileUploadHandler = new FileUploadHandler(getTransportServices(), transportFactory, awsRecordUtil);

  @Before
  public void setUp() throws IOException, ParseException {
    TransportCalculatorService transportCalculatorService = mock(TransportCalculatorService.class);
    List<S3EventNotificationRecord> records = new ArrayList<>();
    records.add(record);
    when(s3EventNotification.getRecords()).thenReturn(records);
    when(transportFactory.getTransportType(any(), any())).thenReturn(transportCalculatorService);
  }

  @Test
  public void shouldApplySuccessfully(){
    String input = convertFileToString("mobility-records/transports.json");
    when(awsRecordUtil.getRecordContents(record)).thenReturn(input);
    String result = fileUploadHandler.apply(s3EventNotification);
    assertEquals("OK", result);
    verify(transportFactory, times(6)).getTransportType(any(), any());
    verify(awsRecordUtil).getRecordContents(any());
    verify(awsRecordUtil).uploadResult(any(), any());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionForInvalidInput() {
    String input = convertFileToString("mobility-records/invalid-input.txt");
    when(awsRecordUtil.getRecordContents(record)).thenReturn(input);
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(outContent));
    String expectedString = "File uploaded is malformed. Please provide valid input.";

    fileUploadHandler.apply(s3EventNotification);

    assertThat(outContent.toString(), containsString(expectedString));
    System.setOut(originalOut);
  }

  private List<TransportCalculatorService> getTransportServices(){
    List<TransportCalculatorService> transportCalculatorServices = new ArrayList<>();
    TransportCalculatorService planeCalculatorService = new TransportCalculatorService(new Plane(), 0);
    TransportCalculatorService trainCalculatorService = new TransportCalculatorService(new Train(), 0);
    TransportCalculatorService carCalculatorService = new TransportCalculatorService(new Car(), 0);
    transportCalculatorServices.add(planeCalculatorService);
    transportCalculatorServices.add(trainCalculatorService);
    transportCalculatorServices.add(carCalculatorService);
    return transportCalculatorServices;
  }

  private String convertFileToString(String filePath) {
    String payload = "";
    try {
      payload =
          FileUtils.readFileToString(
              new ClassPathResource(filePath).getFile(), Charset.defaultCharset());

    } catch(Exception e){
      e.printStackTrace();
    }
    return payload;
  }


}
