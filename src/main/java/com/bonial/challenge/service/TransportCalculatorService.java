package com.bonial.challenge.service;

import com.bonial.challenge.model.Transport;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

public class TransportCalculatorService {

  private Transport transport;

  @Getter
  @Setter
  private Integer totalPassengerCapacity;

  public TransportCalculatorService(Transport transport, Integer totalPassengerCapacity){
    this.transport = transport;
    this.totalPassengerCapacity = totalPassengerCapacity;
  }

  public void computePassengerCapacity(JSONObject transportJSONRecord){
    totalPassengerCapacity += calculatePassengerCapacity(transportJSONRecord);
  }

  private Integer calculatePassengerCapacity(JSONObject transportJSONRecord){
    return transport.calculatePassengers(transportJSONRecord);
  }

  public Transport getTransportType(){
    return transport;
  }

}
