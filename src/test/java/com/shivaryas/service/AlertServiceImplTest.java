package com.shivaryas.service;

import com.shivaryas.entity.Alert;
import com.shivaryas.exception.ResourceNotFoundException;
import com.shivaryas.repository.AlertRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class AlertServiceImplTest {

    @TestConfiguration
    public static class AlertServiceImplTestConfiguration{
        @Bean
        public AlertService getService(){
            return new AlertServiceImpl();
        }
    }

    @Autowired
    private AlertService service;

    @MockBean
    private AlertRepository repository;

    private List<Alert> alerts = new ArrayList<>();
    private List<Alert> alerts2= new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        Alert alert1 = new Alert();
        alert1.setVin("a5s6d73g4j");
        alert1.setPriority("High");
        alert1.setDescription("Engine RPM greater than red line RPM");
        String dateStr = "2019-09-07 10:01:24.4";
        java.util.Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s").parse(dateStr);
        alert1.setTimestamp(new java.sql.Date(date1.getTime()));

        Alert alert2 = new Alert();
        alert2.setVin("a5s68953g4j");
        alert2.setPriority("Low");
        alert2.setDescription("Engine light is ON");
        String dateStr2 = "2019-10-07 10:01:24.4";
        java.util.Date date2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s").parse(dateStr2);
        alert2.setTimestamp(new java.sql.Date(date2.getTime()));

        alerts.add(alert1);
        alerts.add(alert2);

        Mockito.when(repository.findAll())
                .thenReturn(alerts);

        Mockito.when(repository.findById(alert1.getId()))
                .thenReturn(Optional.of(alert1));

        alerts2.add(alert1);
        Mockito.when(repository.findByVin(alert1.getVin()))
                .thenReturn(alerts2);

        Mockito.when(repository.findByPriority(alert1.getPriority()))
                .thenReturn(alerts2);

        Mockito.when(repository.findByPriorityOrderByTimestampDesc(alert1.getPriority()))
                .thenReturn(alerts2);


    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findAll() {
        List<Alert> result = service.findAll();
        Assert.assertEquals("alert list should match", alerts, result);
    }

    @Test
    public void findOne() {
        Alert result = service.findOne(alerts.get(0).getId());
        Assert.assertEquals("alert should match", alerts.get(0), result);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void findOneNotFound() {
        Alert result = service.findOne("12345");
    }

    @Test
    public void findAlertsByVin() {
        List<Alert> result = service.findAlertsByVin(alerts.get(0).getVin());
        Assert.assertEquals("alert should match", alerts2, result);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void findAlertsByVinNotFound() {
        List<Alert> result = service.findAlertsByVin("6654vth98");
    }

    @Test
    public void findByPriority() {
        List<Alert> result = service.findByPriority(alerts.get(0).getPriority());
        Assert.assertEquals("alert should match", alerts2, result);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void findByPriorityNotFound() {
        List<Alert> result = service.findByPriority("Medium");
    }


    @Test
    public void create() throws ParseException {
        Alert alert1 = new Alert();
        alert1.setId(null);
        alert1.setVin("adjh35663j");
        alert1.setPriority("High");
        alert1.setDescription("Engine RPM greater than red line RPM");
        String dateStr = "2018-09-07 10:01:24.4";
        java.util.Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s").parse(dateStr);
        alert1.setTimestamp(new java.sql.Date(date1.getTime()));

        Mockito.when(repository.save(alert1))
                .then(invocationOnMock -> {
                    alert1.setId("new-id");
                    return alert1;
                });

        Alert newAlert = service.create(alert1);

        Assert.assertNotNull("new reading id should exist", newAlert.getId());

        Assert.assertEquals("reading id should be same", "new-id", newAlert.getId());

        Mockito.verify(repository, Mockito.times(1))
                .save(alert1);
    }

    @Test
    public void getAlertForLastTwoHours() {
        List<Alert> result = service.getAlertForLastTwoHours("High");
        Assert.assertEquals("alerts should match", alerts2, result);
    }
}