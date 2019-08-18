package com.shivaryas.repository;

import com.shivaryas.entity.Vehicle;
import org.springframework.data.repository.CrudRepository;

public interface VehicleRepository extends CrudRepository<Vehicle, String> {
    Vehicle findByVin(String vin);

}
