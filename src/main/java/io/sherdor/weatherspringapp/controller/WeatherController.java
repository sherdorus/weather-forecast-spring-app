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
                model.addAttribute("weather", createDefaultWeatherResponse());
                model.addAttribute("location", city);
                return "weather";
            }

            GeocodingResponse location = locations.getFirst();
            WeatherResponse weather = weatherService.getWeather(location.getLatitude(), location.getLongitude());

            if (weather == null || weather.getCurrentWeather() == null) {
                model.addAttribute("error", "Unable to fetch weather data for " + city);
                model.addAttribute("weather", createDefaultWeatherResponse());
                model.addAttribute("location", location.getDisplayName());
                return "weather";
            }

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
            model.addAttribute("weather", createDefaultWeatherResponse());
            model.addAttribute("location", StringUtils.hasText(city) ? city : "New York");
        }
        return "weather";
    }

    private WeatherResponse createDefaultWeatherResponse() {
        WeatherResponse weather = new WeatherResponse();

        WeatherResponse.CurrentWeather currentWeather = new WeatherResponse.CurrentWeather();
        currentWeather.setTemperature(0);
        currentWeather.setHumidity(0);
        currentWeather.setPrecipitation(0.0);
        currentWeather.setCloudCover(00);
        currentWeather.setWindSpeed(0.0);
        currentWeather.setWeatherCode(0); // Код для ясной погоды

        weather.setCurrentWeather(currentWeather);

        return weather;
    }
}