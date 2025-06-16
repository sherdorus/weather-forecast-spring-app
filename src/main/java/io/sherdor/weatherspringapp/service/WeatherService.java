package io.sherdor.weatherspringapp.service;

import io.sherdor.weatherspringapp.dto.GeocodingResponse;
import io.sherdor.weatherspringapp.dto.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class WeatherService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private final RestTemplate restTemplate;

    @Value("${geocoding.api.url}")
    private String geocodingUrl;

    @Value("${weather.api.url}")
    private String weatherUrl;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<GeocodingResponse> searchCity(String city) {
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
            String url = geocodingUrl + "?q=" + encodedCity + "&format=json&limit=1&addressdetails=1";

            logger.info("Sending request to: {}", url);
            GeocodingResponse[] response = restTemplate.getForObject(url, GeocodingResponse[].class);

            if (response == null || response.length == 0) {
                logger.warn("No results for city: {}", city);
                return Collections.emptyList();
            }

            logger.info("Received {} results", response.length);
            return Arrays.asList(response);

        } catch (Exception e) {
            logger.error("Error searching city: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public WeatherResponse getWeather(double lat, double lon) {
        String url = String.format(
                weatherUrl + "?latitude=%.6f&longitude=%.6f&current=temperature_2m,relative_humidity_2m,is_day,weather_code,wind_speed_10m,wind_direction_10m,precipitation,cloud_cover,&hourly=temperature_2m,relative_humidity_2m,weathercode&timezone=auto&temperature_unit=fahrenheit",
                lat,
                lon
        );

        WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);

        if (response != null && response.getHourlyWeather() != null) {
            WeatherResponse.HourlyWeather hourly = response.getHourlyWeather();
            if (hourly.getWeatherCodes() == null) hourly.setWeatherCodes(new ArrayList<>());
            if (hourly.getTemperatures() == null) hourly.setTemperatures(new ArrayList<>());
            if (hourly.getHumidities() == null) hourly.setHumidities(new ArrayList<>());
        }

        return response;
    }
}