package com.shivaryas.controller;

import com.shivaryas.entity.Alert;
import com.shivaryas.service.AlertService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/alerts")
public class AlertController {

    @Autowired
    private AlertService service;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Find All alerts",
            notes = "Returns a list of all alerts available in the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public List<Alert> findAll() {

        return service.findAll();
    }

/*
    @RequestMapping(method = RequestMethod.GET, value = "/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Find alert by id",
            notes = "Return a single alert or throws 404")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Alert Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Alert findOne(
            @ApiParam(value = "id of the Alert", required = true) @PathVariable("id") String id) {
        return service.findOne(id);
    }

 */

    @RequestMapping(method = RequestMethod.GET, value = "/{vin}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Find alerts by vin",
            notes = "Return a list of alerts or throws 404")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public List<Alert> findByVin(
            @ApiParam(value = "vin of the vehicle", required = true) @PathVariable("vin") String vin) {
        return service.findAlertsByVin(vin);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Alert create(@RequestBody Alert alert) {

        return service.create(alert);
    }

    @RequestMapping(method = RequestMethod.GET, value="high", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Find All High Alerts Within Last 2 hours",
            notes = "Returns a list of vehicles with high alert")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
    })
    public List<Alert> findHighAlertsForLastTwoHours(){
        return service.getAlertForLastTwoHours("High");
    }

}
