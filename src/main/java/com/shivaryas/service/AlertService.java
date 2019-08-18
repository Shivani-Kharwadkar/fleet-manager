package com.shivaryas.service;

import com.shivaryas.entity.Alert;

import java.util.List;

public interface AlertService {
    List<Alert> findAll();

    Alert findOne(String id);

    List<Alert> findAlertsByVin(String vin);

    List<Alert> findByPriority(String priority);

    Alert create(Alert alert);

    List<Alert> getAlertForLastTwoHours(String priority);
}
