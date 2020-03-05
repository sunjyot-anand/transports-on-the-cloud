package com.bonial.challenge.model;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class Plane implements Transport {

  private static final String B_PASSENGER_CAPACITY = "b-passenger-capacity";
  private static final String E_PASSENGER_CAPACITY = "e-passenger-capacity";

  @Override
  public Integer calculatePassengers(JSONObject transportJSONRecord) {
    // A caveat of simple JSON library is that it only returns Long values
    return ((Long)transportJSONRecord.get(B_PASSENGER_CAPACITY)).intValue()+((Long)transportJSONRecord.get(E_PASSENGER_CAPACITY)).intValue();
  }

  @Override
  public String toString() {
    return TransportName.PLANE.name();
  }

}
