package com.shivaryas.service;

import com.shivaryas.entity.Vehicle;
import com.shivaryas.exception.ResourceNotFoundException;
import com.shivaryas.repository.VehicleRepository;
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
public class VehicleServiceImplTest {

    @TestConfiguration
    public static class VehicleServiceImplTestConfiguration {

        @Bean
        public VehicleService getService() {
            return new VehicleServiceImpl();
        }
    }

    @Autowired
    private VehicleService service;

    @MockBean
    private VehicleRepository repository;

    private List<Vehicle> vehicles;

    @Before
    public void startup() throws ParseException {
        Vehicle vehicle = new Vehicle();
        vehicle.setVin("123456");
        vehicle.setMake("Honda");
        vehicle.setModel("Civic");
        vehicle.setYear(2018);
        vehicle.setRedlineRpm(6400);
        vehicle.setMaxFuelVolume(55);

        String dateStr = "2019-08-07 10:24:24.4";
        java.util.Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s").parse(dateStr);
        vehicle.setLastServiceDate(new java.sql.Date(date1.getTime()));

        vehicles = Collections.singletonList(vehicle);

        Mockito.when(repository.findAll())
                .thenReturn(vehicles);

        Mockito.when(repository.findById(vehicle.getVin()))
                .thenReturn(Optional.of(vehicle));

    }

    @After
    public void cleanup(){

    }

    @Test
    public void findAll() {
        List<Vehicle> result = service.findAll();
        Assert.assertEquals("vehicle list should match", vehicles, result);
    }

    @Test
    public void findByVin() {
        Vehicle result = service.findByVin(vehicles.get(0).getVin());
        Assert.assertEquals("vehicle should match", vehicles.get(0), result);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void findByVinNotFound() {
        Vehicle result = service.findByVin("756453");
    }

    @Test
    public void create() throws ParseException {
        Vehicle vehicle = new Vehicle();
        vehicle.setVin("123456abcd");
        vehicle.setMake("Honda");
        vehicle.setModel("CRV");
        vehicle.setYear(2017);
        vehicle.setRedlineRpm(6500);
        vehicle.setMaxFuelVolume(55);

        String dateStr = "2019-09-07 10:24:24.4";
        java.util.Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s").parse(dateStr);
        vehicle.setLastServiceDate(new java.sql.Date(date1.getTime()));

        Mockito.when(repository.save(vehicle))
                .then(invocationOnMock -> {
                    vehicle.setVin("new-id");
                    return vehicle;
                });

        List<Vehicle> newVehicles = service.create(Collections.singletonList(vehicle));

        //verify that the id is not null
        Assert.assertNotNull("new vehicle id(vin) should exist", newVehicles.get(0).getVin());

        //verify that the id is same as the mock id
        Assert.assertEquals("vehicle id should be same", "new-id", newVehicles.get(0).getVin());

        //verify that the repository.save() was called 1 times
        Mockito.verify(repository, Mockito.times(1))
                .save(vehicle);
    }

    @Test
    public void update() {
    }

    @Test
    public void delete() {
    }
}