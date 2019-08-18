package com.shivaryas.repository;

import com.shivaryas.entity.Reading;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReadingsRepository extends CrudRepository<Reading, String> {

    List<Reading> findByVin(String vin);

    List<Reading> findByVinOrderByTimestampDesc(String vin);
}
