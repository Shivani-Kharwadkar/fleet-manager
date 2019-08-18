package com.shivaryas.service;

import com.shivaryas.entity.Reading;
import com.shivaryas.entity.Tires;
import com.shivaryas.exception.ResourceNotFoundException;
import com.shivaryas.repository.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class ReadingServiceImplTest {

    @TestConfiguration
    public static class ReadingServiceImplTestConfiguration{
        @Bean
        public ReadingService getService(){
            return new ReadingServiceImpl();
        }
    }

    @Autowired
    private ReadingService service;

    @MockBean
    private ReadingsRepository repository;

    @MockBean
    private VehicleRepository vehicleRepository;

    @MockBean
    private AlertService alertService;

    private List<Reading> readings;

    @Before
    public void setUp() throws Exception {
        Reading reading = new Reading();
        reading.setVin("1234567");
        reading.setLatitude(41.803194);
        reading.setLongitude(-88.144406);
        reading.setSpeed(55.5);
        reading.setFuelVolume(30.0);
        reading.setEngineRpm(6600);
        reading.setEngineHp(34.4);
        reading.setCheckEngineLightOn(true);
        reading.setCruiseControlOn(false);
        reading.setEngineCoolantLow(false);
        String dateStr = "2019-08-07 10:24:24.4";
        java.util.Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s").parse(dateStr);
        reading.setTimestamp(new java.sql.Date(date1.getTime()));

        Tires tire = new Tires();
        tire.setFrontLeft(33);
        tire.setFrontRight(34);
        tire.setRearLeft(29);
        tire.setRearRight(36);
        reading.setTires(tire);

        readings = Collections.singletonList(reading);

        Mockito.when(repository.findAll())
                .thenReturn(readings);

        Mockito.when(repository.findById(reading.getId()))
                .thenReturn(Optional.of(reading));

        Mockito.when(repository.findByVin(reading.getVin()))
                .thenReturn(readings);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findAll() {
        List<Reading> result = service.findAll();
        Assert.assertEquals("reading list should match", readings, result);
    }

    @Test
    public void findOne() {
        Reading result = service.findOne(readings.get(0).getId());
        Assert.assertEquals("reading should match", readings.get(0), result);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void findOneNotFound() {
        Reading result = service.findOne("1234");
    }

    @Test
    public void findByVin() {
        List<Reading> result = service.findByVin(readings.get(0).getVin());
        Assert.assertEquals("reading should match", readings, result);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void findByVinNotFound() {
        List<Reading>  result = service.findByVin("1234");
    }

    @Test
    public void create() throws ParseException {
        Reading reading = new Reading();
        reading.setId(null);
        reading.setVin("123456c78b9a");
        reading.setLatitude(41.803004);
        reading.setLongitude(-89.144406);
        reading.setSpeed(55.5);
        reading.setFuelVolume(30.0);
        reading.setEngineRpm(6100);
        reading.setEngineHp(34.4);
        reading.setCheckEngineLightOn(true);
        reading.setCruiseControlOn(false);
        reading.setEngineCoolantLow(false);
        String dateStr = "2019-08-09 10:24:24.4";
        java.util.Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s").parse(dateStr);
        reading.setTimestamp(new java.sql.Date(date1.getTime()));

        Mockito.when(repository.save(reading))
                .then(invocationOnMock -> {
                    reading.setId("new-id");
                    return reading;
                });

        Reading newReading = service.create(reading);

        Assert.assertNotNull("new reading id should exist", newReading.getId());

        Assert.assertEquals("reading id should be same", "new-id", newReading.getId());

        Mockito.verify(repository, Mockito.times(1))
                .save(reading);

    }

    @Test
    public void update() {
    }

    @Test
    public void delete() {
    }

    @Test
    public void getLocationOfLast30Minutes() {
    }
}