package com.shivaryas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivaryas.entity.Reading;
import com.shivaryas.entity.Tires;
import com.shivaryas.repository.ReadingsRepository;
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

import java.text.SimpleDateFormat;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc
@ActiveProfiles("integrationtest")
public class ReadingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ReadingsRepository repository;

    @Before
    public void setUp() throws Exception {
        Reading reading = new Reading();
        reading.setId("reading1");
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
        String dateStr = "2018-08-07 10:24:24.4";
        java.util.Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s").parse(dateStr);
        reading.setTimestamp(new java.sql.Date(date1.getTime()));

        Tires tire = new Tires();
        tire.setFrontLeft(33);
        tire.setFrontRight(34);
        tire.setRearLeft(29);
        tire.setRearRight(36);
        reading.setTires(tire);

        repository.save(reading);
    }

    @After
    public void tearDown(){
        repository.deleteAll();
    }

    @Test
    public void findAll() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/readings"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    public void findOne() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/readings/reading1"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.vin", Matchers.is("1234567")));
    }

    @Test
    public void findOne404() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/readings/asfdg"))
                .andExpect(MockMvcResultMatchers.status()
                        .isNotFound());
    }

    @Test
    public void findByVin() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/readings/params?vin=1234567"))
                    .andExpect(MockMvcResultMatchers.status()
                            .isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    public void findByVin404() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/readings/params?vin=1234ab"))
                .andExpect(MockMvcResultMatchers.status()
                        .isNotFound());
    }

    @Test
    public void mapLocationOfLast30Minutes() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/readings/mapLocation/1234567"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    public void create() throws Exception{
        ObjectMapper mapper = new ObjectMapper();

        Reading reading1 = new Reading();
        reading1.setVin("123456c78b9a");
        reading1.setLatitude(41.803004);
        reading1.setLongitude(-89.144406);
        reading1.setSpeed(55.5);
        reading1.setFuelVolume(30.0);
        reading1.setEngineRpm(6100);
        reading1.setEngineHp(34.4);
        reading1.setCheckEngineLightOn(true);
        reading1.setCruiseControlOn(false);
        reading1.setEngineCoolantLow(false);
        String dateStr = "2019-08-09 10:24:24.4";
        java.util.Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s").parse(dateStr);
        reading1.setTimestamp(new java.sql.Date(date1.getTime()));

        mvc.perform(MockMvcRequestBuilders.post("/readings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(reading1))
        )
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.vin", Matchers.is("123456c78b9a")));

        //Check whether new reading is added to the h2 DB
        mvc.perform(MockMvcRequestBuilders.get("/readings"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)));

    }

    @Test
    public void update() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Reading reading1 = new Reading();
        reading1.setId("reading1");
        reading1.setVin("123456c78b9a");
        reading1.setLatitude(41.803004);
        reading1.setLongitude(-89.144406);
        reading1.setSpeed(55.5);
        reading1.setFuelVolume(30.0);
        reading1.setEngineRpm(6100);
        reading1.setEngineHp(34.4);
        reading1.setCheckEngineLightOn(true);
        reading1.setCruiseControlOn(false);
        reading1.setEngineCoolantLow(false);
        String dateStr = "2019-08-09 10:24:24.4";
        java.util.Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s").parse(dateStr);
        reading1.setTimestamp(new java.sql.Date(date1.getTime()));

        mvc.perform(MockMvcRequestBuilders.put("/readings/reading1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(reading1))
        )
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.vin", Matchers.is("123456c78b9a")));

        //Check whether new reading is added to the h2 DB
        mvc.perform(MockMvcRequestBuilders.get("/readings"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    public void update404() throws Exception{
        ObjectMapper mapper = new ObjectMapper();

        Reading reading1 = new Reading();
        reading1.setVin("123456c78b9a");
        reading1.setLatitude(41.803004);
        reading1.setLongitude(-89.144406);
        reading1.setSpeed(55.5);
        reading1.setFuelVolume(30.0);
        reading1.setEngineRpm(6100);
        reading1.setEngineHp(34.4);
        reading1.setCheckEngineLightOn(true);
        reading1.setCruiseControlOn(false);
        reading1.setEngineCoolantLow(false);
        String dateStr = "2019-08-09 10:24:24.4";
        java.util.Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s").parse(dateStr);
        reading1.setTimestamp(new java.sql.Date(date1.getTime()));

        mvc.perform(MockMvcRequestBuilders.put("/readings/reading3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(reading1))
        )
                .andExpect(MockMvcResultMatchers.status()
                        .isNotFound());
    }

    @Test
    public void delete() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/readings/reading1"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk());

        //Check whether reading was deleted from the h2 DB
        mvc.perform(MockMvcRequestBuilders.get("/readings"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    public void delete400() throws Exception {

        mvc.perform(MockMvcRequestBuilders.delete("/readings/reading12"))
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest());

        //Check whether reading was deleted from the h2 DB
        mvc.perform(MockMvcRequestBuilders.get("/readings"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
    }
}