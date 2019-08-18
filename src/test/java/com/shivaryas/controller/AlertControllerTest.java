package com.shivaryas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivaryas.entity.Alert;
import com.shivaryas.repository.AlertRepository;
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
public class AlertControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AlertRepository repository;

    @Before
    public void setUp() throws Exception {
        Alert alert1 = new Alert();
        alert1.setId("alert-1");
        alert1.setVin("a5s6d73g4j");
        alert1.setPriority("High");
        alert1.setDescription("Engine RPM greater than red line RPM");
        String dateStr = "2018-09-07 10:01:24.4";
        java.util.Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s").parse(dateStr);
        alert1.setTimestamp(new java.sql.Date(date1.getTime()));

        repository.save(alert1);
    }

    @After
    public void tearDown() throws Exception {
        repository.deleteAll();
    }

    @Test
    public void findAll() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/alerts"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    public void findByVin() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/alerts/a5s6d73g4j"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    public void findByVin404() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/alerts/a5s612344j"))
                .andExpect(MockMvcResultMatchers.status()
                        .isNotFound() );
    }

    @Test
    public void create() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Alert alert2 = new Alert();
        alert2.setVin("a5s68953g4j");
        alert2.setPriority("Low");
        alert2.setDescription("Engine light is ON");
        String dateStr2 = "2019-10-07 10:01:24.4";
        java.util.Date date2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s").parse(dateStr2);
        alert2.setTimestamp(new java.sql.Date(date2.getTime()));

        mvc.perform(MockMvcRequestBuilders.post("/alerts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(alert2))
        )
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.vin", Matchers.is("a5s68953g4j")));

        //Check whether new reading is added to the h2 DB
        mvc.perform(MockMvcRequestBuilders.get("/alerts"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    public void create400() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Alert alert2 = new Alert();
        alert2.setId("alert-1");
        alert2.setVin("a5s68953g4j");
        alert2.setPriority("Low");
        alert2.setDescription("Engine light is ON");
        String dateStr2 = "2019-10-07 10:01:24.4";
        java.util.Date date2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s").parse(dateStr2);
        alert2.setTimestamp(new java.sql.Date(date2.getTime()));

        mvc.perform(MockMvcRequestBuilders.post("/alerts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(alert2))
        )
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest());

        //Check whether new reading is added to the h2 DB
        mvc.perform(MockMvcRequestBuilders.get("/alerts"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    public void findHighAlertsForLastTwoHours() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/alerts/high"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(0)));
    }
}