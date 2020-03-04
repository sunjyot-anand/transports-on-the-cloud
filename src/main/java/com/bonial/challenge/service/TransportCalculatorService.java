package com.bonial.challenge.service;

import com.bonial.challenge.model.Transport;
import org.json.simple.JSONObject;

public class TransportCalculatorService {

  private Transport transport;
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

  public Integer getTotalPassengerCapacity() {
    return this.totalPassengerCapacity;
  }

  public void setTotalPassengerCapacity(Integer totalPassengerCapacity) {
    this.totalPassengerCapacity = totalPassengerCapacity;
  }

}
