package com.shivaryas.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.shivaryas.entity.*;
import com.shivaryas.exception.BadRequestException;
import com.shivaryas.exception.ResourceNotFoundException;
import com.shivaryas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ReadingServiceImpl implements ReadingService {

    @Autowired
    private ReadingsRepository repository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private AlertService alertService;

    @Transactional(readOnly = true)
    public List<Reading> findAll() {
        return (List<Reading>) repository.findAll();
    }

    @Transactional(readOnly = true)
    public Reading findOne(String id) {
        Optional<Reading> existing = repository.findById(id);
        if(!existing.isPresent())
            throw new ResourceNotFoundException("Reading with id: " + id + " not found.");
        return existing.get();
    }

    @Transactional(readOnly = true)
    public List<Reading> findByVin(String vin) {
        List<Reading> readings = repository.findByVin(vin);
        if(readings.isEmpty()){
            throw new ResourceNotFoundException("Reading with vin " + vin + " does not exist.");
        }
        return readings;
    }

    @Transactional
    public Reading create(Reading reading) {
        Optional<Vehicle> existing = vehicleRepository.findById(reading.getVin());
        if(existing.isPresent()) {
            checkForAlert(reading, existing);
        }
        return repository.save(reading);
    }

    private void checkForAlert(Reading reading, Optional<Vehicle> existing) {
        //Rule 1
        if (reading.getEngineRpm() > existing.get().getRedlineRpm()) {
            saveAlert("High","EngineRpm is greater than RedLineRpm", reading);
        }

        //Rule 2
        if (reading.getFuelVolume() < (existing.get().getMaxFuelVolume() * 0.10)) {
            saveAlert("Low", "Fuel volume is low", reading);
        }
        //rule 3
        Tires tire = reading.getTires();
        if(tire != null) {
            int frontLeft = tire.getFrontLeft();
            int frontRight = tire.getFrontRight();
            int rearLeft = tire.getRearLeft();
            int rearRight = tire.getRearRight();
            if (frontLeft < 32 || frontLeft > 36 ||
                    frontRight < 32 || frontRight > 36 ||
                    rearLeft < 32 || rearLeft > 36 ||
                    rearRight < 32 || rearRight > 36) {
                saveAlert("Low", "Tire Pressure is not optimum", reading);
            }
        }

        //Rule 4.1
        if(reading.isCheckEngineLightOn()){
            saveAlert("Low", "Engine Light is ON", reading);
        }

        //Rule 4.2
        if(reading.isEngineCoolantLow()){
            saveAlert("Low", "Engine Coolant is low", reading);
        }


    }

    private void saveAlert(String priority, String description, Reading reading) {
        Alert alert  = new Alert();
        alert.setVin(reading.getVin());
        alert.setTimestamp(reading.getTimestamp());
        alert.setPriority(priority);
        alert.setDescription(description);

        alertService.create(alert);

        if (priority == "High"){
            notifyUser(reading.getVin(), description);
        }
    }

    private void notifyUser(String vin, String description){
        String FROM = "shivani.kharwadkar@gmail.com";
        String TO = "shivani.kharwadkar@gmail.com";
        String SUBJECT = "High alert triggered";
        String HTMLBODY = "<h1>High Alert Triggered</h1>"
                + "<p id='vin'>A High alert was triggered for vehicle because "
                + "its current RPM is higher than red line RPM of this vehicle.";
        String TEXTBODY = "High Alert Triggered \n "
                + "A High alert was triggered for vehicle with vin number: "
                + vin + " because " + description;

        AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion(Regions.US_WEST_2).build();
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(TO))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(HTMLBODY))
                                .withText(new Content()
                                        .withCharset("UTF-8").withData(TEXTBODY)))
                        .withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
                .withSource(FROM);
        client.sendEmail(request);
    }

    @Transactional
    public Reading update(String id, Reading reading) {
        Optional<Reading> existing = repository.findById(id);
        if (!existing.isPresent()) {
            throw new ResourceNotFoundException("Reading with id " + id + " does not exist.");
        }
        return repository.save(reading);
    }

    @Transactional
    public void delete(String id) {
        Optional<Reading> existing = repository.findById(id);
        if (!existing.isPresent()) {
            throw new BadRequestException("Reading with id " + id + " does not exist.");
        }
        repository.delete(existing.get());
    }

    @Transactional(readOnly = true)
    public List<List<Double>> getLocationOfLast30Minutes(String vin) {
        List<Reading> readings = repository.findByVinOrderByTimestampDesc(vin);
        if(readings.isEmpty())
            throw new ResourceNotFoundException("Readings for vehicle with VIN:- "+vin+" not found");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -30 );
        Date date = cal.getTime();

        List<List<Double>> geolocations = new ArrayList<>();
        for(Reading r: readings){
            if(r.getTimestamp().after(date)){
                List<Double> location = new ArrayList<>();
                location.add(r.getLatitude());
                location.add(r.getLongitude());

                geolocations.add(location);
            }
        }
        return geolocations;
    }
}
