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

public class CarTest {

  private Car car = new Car();
  private JSONParser parser = new JSONParser();
  private static final Integer PASSENGER_CAPACITY = 4;

  @Test
  public void shouldCalculatePassengers() throws IOException, ParseException {
    JSONObject carJson = getJsonFromFile("mobility-records/car.json");
    Integer passengerCapacity = car.calculatePassengers(carJson);
    assertEquals(PASSENGER_CAPACITY, passengerCapacity);
  }

  private JSONObject getJsonFromFile(String filePath) throws IOException, ParseException {
    String payload =
        FileUtils.readFileToString(
            new ClassPathResource(filePath).getFile(), Charset.defaultCharset());
    JSONObject jsonObject = (JSONObject) parser.parse(payload);
    return jsonObject;
  }
}
