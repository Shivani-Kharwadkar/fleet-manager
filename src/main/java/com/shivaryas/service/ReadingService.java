package com.shivaryas.service;

import com.shivaryas.entity.Reading;

import java.util.List;

public interface ReadingService {
    List<Reading> findAll();

    Reading findOne(String id);

    List<Reading> findByVin(String vin);

    Reading create(Reading reading);

    Reading update(String id, Reading reading);

    void delete(String id);

    List<List<Double>> getLocationOfLast30Minutes(String vin);
}
