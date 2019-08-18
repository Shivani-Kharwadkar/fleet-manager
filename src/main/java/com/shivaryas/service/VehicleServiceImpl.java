package com.shivaryas.service;

import com.shivaryas.entity.Vehicle;
import com.shivaryas.exception.BadRequestException;
import com.shivaryas.exception.ResourceNotFoundException;
import com.shivaryas.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    VehicleRepository repository;

    @Transactional(readOnly = true)
    public List<Vehicle> findAll() {
        return (List<Vehicle>) repository.findAll();
    }

    @Transactional(readOnly = true)
    public Vehicle findByVin(String vin) {
        Optional<Vehicle> existing = repository.findById(vin);
        if(!existing.isPresent())
            throw new ResourceNotFoundException("Vehicle with VIN:- "+vin+" not found");
        return existing.get();
    }

    @Transactional
    public List<Vehicle> create(List<Vehicle> vehicles) {

        List<Vehicle> list = new ArrayList<>();
        for(Vehicle vehicle: vehicles) {

            Optional<Vehicle> existing = repository.findById(vehicle.getVin());
            if (existing.isPresent()) {
                update(vehicle.getVin(), vehicle);
            }
            list.add(repository.save(vehicle));
        }
        return list;
    }

    @Transactional
    public Vehicle update(String vin, Vehicle vehicle) {
        Optional<Vehicle> existing = repository.findById(vin);
        if (!existing.isPresent()) {
            throw new ResourceNotFoundException("Vehicle with vin " + vin + " does not exist.");
        }
        return repository.save(vehicle);
    }

    @Transactional
    public void delete(String vin) {
        Optional<Vehicle> existing = repository.findById(vin);
        if (!existing.isPresent()) {
            throw new BadRequestException("Vehicle with vin " + vin + " does not exist.");
        }
        repository.delete(existing.get());
    }

}
