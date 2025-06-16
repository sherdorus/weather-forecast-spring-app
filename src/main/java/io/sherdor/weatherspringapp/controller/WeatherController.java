package io.sherdor.weatherspringapp.controller;

import io.sherdor.weatherspringapp.dto.GeocodingResponse;
import io.sherdor.weatherspringapp.dto.WeatherResponse;
import io.sherdor.weatherspringapp.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;
    private final WeatherCodeUtil weatherCodeUtil;

    @GetMapping("/")
    public String home(@RequestParam(required = false) String city, Model model) {
        try {
            city = StringUtils.hasText(city) ? city : "New York";

            List<GeocodingResponse> locations = weatherService.searchCity(city);
            if (locations.isEmpty()) {
                model.addAttribute("error", "City '" + city + "' not found");
                return "weather";
            }

            GeocodingResponse location = locations.getFirst();
            WeatherResponse weather = weatherService.getWeather(
                    location.getLatitude(),
                    location.getLongitude()
            );

            Map<String, String> weatherInfo = weatherCodeUtil.getWeatherInfo(
                    weather.getCurrentWeather().getWeatherCode()
            );

            model.addAttribute("weather", weather);
            model.addAttribute("location", location.getDisplayName());

            if (weather.getHourlyWeather() != null
                    && weather.getHourlyWeather().getWeatherCodes() != null
                    && !weather.getHourlyWeather().getWeatherCodes().isEmpty()) {

                Map<String, String> firstHourWeather = weatherCodeUtil.getWeatherInfo(
                        weather.getHourlyWeather().getWeatherCodes().getFirst()
                );
                model.addAttribute("firstHourIcon", firstHourWeather.get("icon"));
            }
            model.addAttribute("weatherIcon", weatherInfo.get("icon"));
            model.addAttribute("weatherText", weatherInfo.get("text"));

        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
        }
        return "weather";
    }
}