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

public class TrainTest {

  private Train train = new Train();
  private JSONParser parser = new JSONParser();
  private static final Integer NUMBER_WAGONS = 5;
  private static final Integer W_PASSENGER_CAPACITY = 30;

  @Test
  public void shouldCalculatePassengers() throws IOException, ParseException {
    JSONObject trainJson = getJsonFromFile("mobility-records/train.json");
    Integer passengerCapacity = train.calculatePassengers(trainJson);
    assertEquals(NUMBER_WAGONS* W_PASSENGER_CAPACITY, passengerCapacity.intValue());
  }

  private JSONObject getJsonFromFile(String filePath) throws IOException, ParseException {
    String payload =
        FileUtils.readFileToString(
            new ClassPathResource(filePath).getFile(), Charset.defaultCharset());
    JSONObject jsonObject = (JSONObject) parser.parse(payload);
    return jsonObject;
  }
}
