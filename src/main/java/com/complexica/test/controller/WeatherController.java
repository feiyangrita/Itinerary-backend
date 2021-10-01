package com.complexica.test.controller;

import com.complexica.test.model.CityEntity;
import com.complexica.test.model.WeatherEntity;
import com.complexica.test.service.CityService;
import com.complexica.test.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by feiyang on 29/9/21.
 */
@RestController
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private CityService cityService;

    @RequestMapping(value = "/weather", method = RequestMethod.GET)
    @ResponseBody
    public List<CityEntity> getWeatherInfo(
            @RequestParam(name = "city", required = true) String cityName,
            @RequestParam("date") @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate) {
        String result  = weatherService.saveWeatherByCityName(cityName);
        List<CityEntity> cityEntities = cityService.getCitiesByCityName(cityName);
        System.out.println(cityEntities.size());
        return cityEntities;

    }
}
