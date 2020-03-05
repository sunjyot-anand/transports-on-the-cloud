package com.bonial.challenge.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

import com.bonial.challenge.model.Transport;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class TransportCalculatorServiceTest {

  JSONParser parser = new JSONParser();
  private Transport transport = mock(Transport.class);
  private TransportCalculatorService transportCalculatorService;

  @Before
  public void setUp(){
    transportCalculatorService = new TransportCalculatorService(transport, 0);
  }

  @Test
  public void shouldComputePassengerCapacity() throws IOException, ParseException {
    JSONObject carJson = getJsonFromFile("mobility-records/car.json");
    when(transport.calculatePassengers(carJson)).thenReturn(4);
    transportCalculatorService.computePassengerCapacity(carJson);
    assertEquals(4, transportCalculatorService.getTotalPassengerCapacity().intValue());
    transportCalculatorService.computePassengerCapacity(carJson);
    assertEquals(8, transportCalculatorService.getTotalPassengerCapacity().intValue());

  }

  private JSONObject getJsonFromFile(String filePath) throws IOException, ParseException {
    String payload =
        FileUtils.readFileToString(
            new ClassPathResource(filePath).getFile(), Charset.defaultCharset());
    JSONObject jsonObject = (JSONObject) parser.parse(payload);
    return jsonObject;
  }

}
