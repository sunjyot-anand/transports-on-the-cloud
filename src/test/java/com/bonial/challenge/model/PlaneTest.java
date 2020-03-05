package com.bonial.challenge.model;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class PlaneTest {

  private Plane plane = new Plane();
  private JSONParser parser = new JSONParser();
  private static final Integer B_PASSENGER_CAPACITY = 14;
  private static final Integer E_PASSENGER_CAPACITY = 300;

  @Test
  public void shouldCalculatePassengers() throws IOException, ParseException {
    JSONObject planeJson = getJsonFromFile("mobility-records/plane.json");
    Integer passengerCapacity = plane.calculatePassengers(planeJson);
    assertEquals(B_PASSENGER_CAPACITY+E_PASSENGER_CAPACITY, passengerCapacity.intValue());
  }

  private JSONObject getJsonFromFile(String filePath) throws IOException, ParseException {
    String payload =
        FileUtils.readFileToString(
            new ClassPathResource(filePath).getFile(), Charset.defaultCharset());
    JSONObject jsonObject = (JSONObject) parser.parse(payload);
    return jsonObject;
  }
}
