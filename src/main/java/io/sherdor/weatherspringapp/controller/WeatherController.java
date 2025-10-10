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

    private volatile GeocodingResponse cachedDefaultLocation;
    private volatile WeatherResponse cachedDefaultWeather;
    private volatile long lastCacheUpdate = 0;
    private static final long CACHE_TIMEOUT = 300000;

    private static final String DEFAULT_CITY = "Manhattan";

    @GetMapping("/")
    public String home(@RequestParam(required = false) String city, Model model) {
        try {
            city = StringUtils.hasText(city) ? city : DEFAULT_CITY;

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

    private WeatherResponse getCachedDefaultWeather() {
        Long currentTime = System.currentTimeMillis();
        if (cachedDefaultWeather != null && cachedDefaultLocation != null && (currentTime - lastCacheUpdate) < CACHE_TIMEOUT) {
            return cachedDefaultWeather;
        }
        return null;
    }

    private void cacheDefaultWeather(GeocodingResponse location, WeatherResponse weather) {
        this.cachedDefaultLocation = location;
        this.cachedDefaultWeather = weather;
        this.lastCacheUpdate = System.currentTimeMillis();
        System.out.println("Cached default weather for " + DEFAULT_CITY);
    }

    private void populateModelWithWeather(Model model, WeatherResponse weather, GeocodingResponse location, String city) {
        Map<String, String> weatherInfo = weatherCodeUtil.getWeatherInfo(weather.getCurrentWeather().getWeatherCode());
        model.addAttribute("weather", weather);
        model.addAttribute("location", location.getDisplayName());

        if (weather.getHourlyWeather() != null
            && weather.getHourlyWeather().getWeatherCodes() != null
            && !weather.getHourlyWeather().getWeatherCodes().isEmpty()) {

            Map<String, String> firstHourWeather = weatherCodeUtil.getWeatherInfo(weather.getHourlyWeather().getWeatherCodes().getFirst());
            model.addAttribute("firstHourIcon", firstHourWeather.get("icon"));

        }
        model.addAttribute("weatherIcon", weatherInfo.get("icon"));
        model.addAttribute("weatherText", weatherInfo.get("text"));    }

    private void handleException(Model model, Exception e, String city) {
        String errorMessage = "Error: " + e.getMessage();
        if (e.getMessage().contains("timeout") || e.getMessage().contains("connection")) {
            errorMessage = "Connection timeout. Please check your internet connection and try again.";
        }
        if (e.getMessage().contains("error") || e.getMessage().contains("404")) {
            errorMessage = "Weather service temporarily unavailable. Please try again later.";
        }
        model.addAttribute("error", errorMessage);
        model.addAttribute("weather", createDefaultWeatherResponse());
        model.addAttribute("location", StringUtils.hasText(city) ? city : DEFAULT_CITY);
        model.addAttribute("weatherIcon", "cloud-question");
        model.addAttribute("weatherText", "No data available");
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