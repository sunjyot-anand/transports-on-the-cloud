package com.bonial.challenge.model;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class Train implements Transport {

  private static final String NUMBER_WAGONS = "number-wagons";
  private static final String W_PASSENGER_CAPACITY = "w-passenger-capacity";

  @Override
  public Integer calculatePassengers(JSONObject transportJSONRecord) {
    // A caveat of simple JSON library is that it only returns Long values
    return ((Long)transportJSONRecord.get(NUMBER_WAGONS)).intValue()*((Long)transportJSONRecord.get(W_PASSENGER_CAPACITY)).intValue();
  }

}
