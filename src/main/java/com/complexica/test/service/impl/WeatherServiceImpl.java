package com.complexica.test.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.complexica.test.apiexception.IncorrectResponseException;
import com.complexica.test.config.OpenWeatherConfig;
import com.complexica.test.config.RestTemplateConfig;
import com.complexica.test.model.CityEntity;
import com.complexica.test.model.WeatherEntity;
import com.complexica.test.repository.CityRepository;
import com.complexica.test.repository.WeatherRepository;
import com.complexica.test.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by feiyang on 29/9/21.
 */
@Service
public class WeatherServiceImpl implements WeatherService{
    @Autowired
    private RestTemplateConfig restTemplateConfig;

    @Autowired
    private OpenWeatherConfig openWeatherConfig;

    @Autowired
    public CityRepository cityRepository;

    @Autowired
    public WeatherRepository weatherRepository;


    @Override
    public String saveWeatherByCityName(String cityName) throws IncorrectResponseException{

        String baseURL = openWeatherConfig.getBaseURL();
        String key = openWeatherConfig.getKey();
        String unit = openWeatherConfig.getUnit();

        //TODO: implement a request class to define different API from openWeatherMap
        String url = baseURL + cityName + "&appid=" + key + "&units=" + unit;
        String result = "";
        try {
            ResponseEntity<String> response = restTemplateConfig.restTemplate().getForEntity(url, String.class);
            result = response.getBody();
            saveCityEntity(result);
        } catch (HttpClientErrorException.Unauthorized e){
            throw new IncorrectResponseException("Please check or update your openWeatherMap apikey.", e);
        } catch (HttpClientErrorException.NotFound e){
            throw new IncorrectResponseException("City not found." , e);
        } catch (Exception e){
            throw new IncorrectResponseException("Please check openweathermap api.", e);
        }
        return result;
    }

    @Override
    public List<WeatherEntity> getWeatherByCity(CityEntity cityEntity) {
        return weatherRepository.findByCity(cityEntity);
    }

    @Override
    public List<WeatherEntity> getWeatherByCityAndDate(CityEntity cityEntity, Date startDate, Date endDate){
        return weatherRepository.findAllByCityAndForecastDateBetween(cityEntity, startDate, endDate);
//        List<WeatherEntity> weatherEntities = weatherRepository.findByCity(cityEntity);
//        System.out.println(weatherEntities.size());
//        weatherEntities.forEach(entity ->{
//            System.out.println(entity.getTemperature());
//            if(!entity.getForecastDate().isAfter(startDate) || entity.getForecastDate().isBefore(endDate)){
//                weatherEntities.remove(entity);
//            }
//        });

//        return weatherEntities;
    }




//    @Transactional
//    private List<WeatherEntity> saveWeatherEntity(String result){
//        List<WeatherEntity> weatherEntities = new ArrayList<WeatherEntity>();
//        JSONObject jsonObject = JSON.parseObject(result);
//
//        CityEntity city = saveCityEntity(result);
//
//        JSONArray weatherArray = jsonObject.getJSONArray("list");
//        for(Object i: weatherArray){
//            JSONObject weatherObject = JSON.parseObject(i.toString());
//            WeatherEntity weatherEntity = new WeatherEntity();
//            weatherEntity.setCloud(weatherObject.getJSONObject("clouds").getInteger("all"));
//            weatherEntity.setTemperature(weatherObject.getJSONObject("main").getDouble("temp"));
//            weatherEntity.setForecastDate(weatherObject.getString("dt_txt"));
//            weatherEntity.setCity(city);
////            city.getWeatherEntities().add(weatherEntity);
//            weatherEntities.add(weatherEntity);
//        }
//
//
//        weatherEntities = weatherRepository.saveAll(weatherEntities);
//
//        return weatherEntities;
//
//    }


//    @Transactional
//    private CityEntity saveCityEntity(String result){
//        JSONObject jsonObject = JSON.parseObject(result);
//
//        JSONObject cityObject = jsonObject.getJSONObject("city");
//        String cityName = cityObject.get("name").toString().toLowerCase();
//        String cityCode = cityObject.get("country").toString().toLowerCase();
//
//        CityEntity cityEntity = cityRepository.findByCityNameAndCode(cityName, cityCode);
//        if(cityEntity == null){
//            cityEntity = new CityEntity();
//            cityEntity.setCountry(cityCode);
//            cityEntity.setCityName(cityName);
//
//            cityRepository.save(cityEntity);
//        }
//        return cityEntity;
//
//    }

    @Transactional
    public void deleteWeatherByCity(CityEntity cityEntity){
        weatherRepository.deleteByCity(cityEntity);
    }

    @Transactional
    private CityEntity saveCityEntity(String result){
        JSONObject jsonObject = JSON.parseObject(result);

        JSONObject cityObject = jsonObject.getJSONObject("city");
        String cityName = cityObject.get("name").toString().toLowerCase();
        String cityCode = cityObject.get("country").toString().toLowerCase();
        JSONArray weatherArray = jsonObject.getJSONArray("list");

        List<WeatherEntity> weatherEntities = new ArrayList<>();
        CityEntity cityEntity = new CityEntity();
        cityEntity = new CityEntity();
        cityEntity.setCountry(cityCode);
        cityEntity.setCityName(cityName);


        for(Object i: weatherArray){
            JSONObject weatherObject = JSON.parseObject(i.toString());
            WeatherEntity weatherEntity = new WeatherEntity();
            weatherEntity.setCloud(weatherObject.getJSONObject("clouds").getInteger("all"));
            weatherEntity.setTemperature(weatherObject.getJSONObject("main").getDouble("temp"));
            weatherEntity.setForecastDate(weatherObject.getDate("dt_txt"));
            weatherEntity.setCity(cityEntity);
//            city.getWeatherEntities().add(weatherEntity);
            weatherEntities.add(weatherEntity);
        }

        cityEntity.setWeatherEntities(weatherEntities);
        return cityRepository.save(cityEntity);

    }
}
