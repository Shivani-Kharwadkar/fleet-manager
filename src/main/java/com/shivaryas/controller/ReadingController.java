package com.shivaryas.controller;

import com.shivaryas.entity.Reading;
import com.shivaryas.service.ReadingService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/readings")
@CrossOrigin(origins = {"http://mocker.egen.io", "http://mocker.ennate.academy"})
public class ReadingController {

    @Autowired
    private ReadingService service;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Find All Readings",
            notes = "Returns a list of all readings available in the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public List<Reading> findAll() {

        return service.findAll();
    }


    @RequestMapping(method = RequestMethod.GET, value = "/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Find reading by id",
            notes = "Return a single reading or throws 404")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Reading findOne(
            @ApiParam(value = "id of the reading", required = true) @PathVariable("id") String id) {
        return service.findOne(id);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/params",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Find readings by vin",
            notes = "Return a list of readings or throws 404")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Reading Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public List<Reading> findByVin(
            @ApiParam(value = "vin of the vehicle", required = true) @RequestParam("vin") String vin) {
        return service.findByVin(vin);
    }

    @RequestMapping(method = RequestMethod.GET, value = "mapLocation/{vin}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Find location of vehicle in last 30 minutes",
            notes = "Return a list containing a list of geolocation of vehicle in last 30 minutes")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Vehicle Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public List<List<Double>> mapLocationOfLast30Minutes(@PathVariable("vin") String vin){
        return service.getLocationOfLast30Minutes(vin);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Reading create(@RequestBody Reading reading) {

        return service.create(reading);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Reading update(@PathVariable("id") String id, @RequestBody Reading reading) {
        return service.update(id, reading);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public void delete(@PathVariable("id") String id) {

        service.delete(id);
    }
}
