package com.shivaryas.service;

import com.shivaryas.entity.Vehicle;

import java.util.List;

public interface VehicleService {
    List<Vehicle> findAll();

    Vehicle findByVin(String vin);

    List<Vehicle> create(List<Vehicle> vehicle);

    Vehicle update(String vin, Vehicle vehicle);

    void delete(String vin);

}
