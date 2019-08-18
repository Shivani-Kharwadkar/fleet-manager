package com.shivaryas.service;

import com.shivaryas.entity.Alert;
import com.shivaryas.exception.BadRequestException;
import com.shivaryas.exception.ResourceNotFoundException;
import com.shivaryas.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AlertServiceImpl implements AlertService {

    @Autowired
    AlertRepository repository;

    @Transactional(readOnly = true)
    public List<Alert> findAll() {
        return (List<Alert>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Alert findOne(String id) {
        Optional<Alert> existing = repository.findById(id);
        if(!existing.isPresent())
            throw new ResourceNotFoundException("Alert with id: " + id + " not found.");
        return existing.get();
    }

    @Transactional(readOnly = true)
    public List<Alert> findAlertsByVin(String vin) {
        List<Alert> alerts = repository.findByVin(vin);
        if(alerts.isEmpty()){
            throw new ResourceNotFoundException("Alert with vin " + vin + " does not exist");
        }
        return alerts;
    }

    @Transactional(readOnly = true)
    public List<Alert> findByPriority(String priority) {
        List<Alert> alerts =  repository.findByPriority(priority);
        if(alerts.isEmpty())
            throw new ResourceNotFoundException("Alerts with priority " + priority + " does not exist.");

        return alerts;
    }

    @Transactional
    public Alert create(Alert alert) {
        if (repository.findById(alert.getId()).isPresent()) {
            throw new BadRequestException("Alert with id " + alert.getId() + " already exists.");
        }
        return repository.save(alert);
    }

    @Transactional(readOnly = true)
    public List<Alert> getAlertForLastTwoHours(String priority) {
        List<Alert> alerts = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, - 2);
        Date date = cal.getTime();
        List<Alert> allAlerts = repository.findByPriorityOrderByTimestampDesc(priority);
        if(allAlerts.isEmpty())
            throw new ResourceNotFoundException("No alert Found with High Priority");
        for (Alert a:allAlerts){
            if ((a.getTimestamp()).after(date)){
                alerts.add(a);
            }
        }
        return alerts;
    }

}
