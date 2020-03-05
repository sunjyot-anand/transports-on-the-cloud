package com.bonial.challenge.model;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class Car implements Transport {

  private static final String PASSENGER_CAPACITY = "passenger-capacity";

  @Override
  public Integer calculatePassengers(JSONObject transportJSONRecord) {
    // A caveat of simple JSON library is that it only returns Long values
    return ((Long)transportJSONRecord.get(PASSENGER_CAPACITY)).intValue();
  }

  @Override
  public String toString() {
    return TransportName.CAR.name();
  }
}
