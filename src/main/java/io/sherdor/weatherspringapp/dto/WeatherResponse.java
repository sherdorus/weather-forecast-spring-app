package io.sherdor.weatherspringapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class WeatherResponse {
    @JsonProperty("current")
    private CurrentWeather currentWeather;

    @JsonProperty("hourly")
    private HourlyWeather hourlyWeather;

    @Data
    public static class CurrentWeather {
        @JsonProperty("temperature_2m")
        private int temperature;
        @JsonProperty("wind_speed_10m")
        private double windSpeed;
        @JsonProperty("wind_direction_10m")
        private int windDirection;
        @JsonProperty("weather_code")
        private int weatherCode;
        @JsonProperty("relative_humidity_2m")
        private int humidity;
        @JsonProperty("is_day")
        private int isDay;
        @JsonProperty("precipitation")
        private double precipitation;
        @JsonProperty("cloud_cover")
        private int cloudCover;
    }

    @Data
    public static class HourlyWeather {
        @JsonProperty("temperature_2m")
        private List<Double> temperatures;
        private List<String> time;
        @JsonProperty("weathercode")
        private List<Integer> weatherCodes;
        @JsonProperty("relative_humidity_2m")
        private List<Integer> humidities;
    }
}