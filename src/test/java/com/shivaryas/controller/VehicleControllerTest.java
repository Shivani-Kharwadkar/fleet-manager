package com.shivaryas.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivaryas.entity.Vehicle;
import com.shivaryas.repository.VehicleRepository;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc
@ActiveProfiles("integrationtest")
public class VehicleControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private VehicleRepository repository;

    @Before
    public void setUp() throws Exception {
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

        repository.save(vehicle);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setVin("vehicle2");
        vehicle2.setMake("Honda");
        vehicle2.setModel("Civic");
        vehicle2.setYear(2016);
        vehicle2.setRedlineRpm(6100);
        vehicle2.setMaxFuelVolume(55);
        vehicle2.setLastServiceDate(new java.sql.Date(date1.getTime()));

        repository.save(vehicle2);
    }

    @After
    public void tearDown() throws Exception {
        repository.deleteAll();
    }

    @Test
    public void findAll() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/vehicles"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    public void findOne() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/vehicles/vehicle2"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.make", Matchers.is("Honda")));
    }

    @Test
    public void findOne404() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/vehicles/1234"))
                .andExpect(MockMvcResultMatchers.status()
                        .isNotFound());
    }

    @Test
    public void create() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Vehicle vehicle = new Vehicle();
        vehicle.setVin("vehicle3");
        vehicle.setMake("Honda");
        vehicle.setModel("CRV");
        vehicle.setYear(2008);
        vehicle.setRedlineRpm(6400);
        vehicle.setMaxFuelVolume(55);

        String dateStr = "2016-08-07 10:24:24.4";
        java.util.Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s").parse(dateStr);
        vehicle.setLastServiceDate(new java.sql.Date(date1.getTime()));

        List<Vehicle> list = Collections.singletonList(vehicle);
        mvc.perform(MockMvcRequestBuilders.put("/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(list))
                )
                .andExpect(MockMvcResultMatchers.status()
                        .isOk());

        //Check whether new vehicle is added to the h2 DB
        mvc.perform(MockMvcRequestBuilders.get("/vehicles"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)));

    }

    @Test
    public void update() throws Exception{
        ObjectMapper mapper = new ObjectMapper();

        Vehicle vehicle = new Vehicle();
        vehicle.setVin("vehicle2");
        vehicle.setMake("Honda");
        vehicle.setModel("CRV");
        vehicle.setYear(2008);
        vehicle.setRedlineRpm(6400);
        vehicle.setMaxFuelVolume(55);

        String dateStr = "2016-08-07 10:24:24.4";
        java.util.Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s").parse(dateStr);
        vehicle.setLastServiceDate(new java.sql.Date(date1.getTime()));

        mvc.perform(MockMvcRequestBuilders.put("/vehicles/vehicle2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(vehicle))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.make", Matchers.is("Honda")));

        //Check whether new vehicle is updated to the h2 DB
        mvc.perform(MockMvcRequestBuilders.get("/vehicles"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    public void update404() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Vehicle vehicle = new Vehicle();
        vehicle.setVin("vehicle3");
        vehicle.setMake("Honda");
        vehicle.setModel("CRV");
        vehicle.setYear(2008);
        vehicle.setRedlineRpm(6400);
        vehicle.setMaxFuelVolume(55);

        String dateStr = "2016-08-07 10:24:24.4";
        java.util.Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s").parse(dateStr);
        vehicle.setLastServiceDate(new java.sql.Date(date1.getTime()));

        mvc.perform(MockMvcRequestBuilders.put("/vehicles/vehicle3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(vehicle))
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound());


    }



    @Test
    public void delete() throws Exception {

        mvc.perform(MockMvcRequestBuilders.delete("/vehicles/vehicle2"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk());

        //Check whether new vehicle is deleted from the h2 DB
        mvc.perform(MockMvcRequestBuilders.get("/vehicles"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    public void delete400() throws Exception {

        mvc.perform(MockMvcRequestBuilders.delete("/vehicles/vehicle22"))
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest());

        //Check whether new vehicle is deleted from the h2 DB
        mvc.perform(MockMvcRequestBuilders.get("/vehicles"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)));
    }
}