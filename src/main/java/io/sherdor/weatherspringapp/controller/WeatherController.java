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

            if (DEFAULT_CITY.equals(city)) {
                WeatherResponse cachedWeather = getCachedDefaultWeather();
                if (cachedWeather != null) {
                    System.out.println("Using cached weather for " + DEFAULT_CITY);
                    populateModelWithWeather(model, cachedWeather, cachedDefaultLocation, city);
                    return "weather";
                }
            }

            List<GeocodingResponse> locations = searchCityWithRetry(city, 3);

            if (locations == null || locations.isEmpty()) {
                if (DEFAULT_CITY.equals(city)) {
                    String[] alternativeCities = {"London", "Paris", "Tokyo", "Moscow", "Berlin"};
                    for (String alternativeCity : alternativeCities) {
                        locations = searchCityWithRetry(alternativeCity, 3);
                        if (locations != null && !locations.isEmpty()) {
                            city = alternativeCity;
                            model.addAttribute("error", "Default city not available. Showing weather for " + alternativeCity + " instead.");
                            break;
                        }
                    }
                }

                if (locations == null || locations.isEmpty()) {
                    model.addAttribute("error", "Unable to find city '" + city + "'. Please try again or check your internet connection.");
                    model.addAttribute("weather", createDefaultWeatherResponse());
                    model.addAttribute("location", city);
                    model.addAttribute("weatherIcon", "cloud-question");
                    model.addAttribute("weatherText", "No data available");
                    return "weather";
                }
            }

            GeocodingResponse location = locations.get(0);
            WeatherResponse weather = getWeatherWithRetry(location.getLatitude(), location.getLongitude(), 3);

            if (weather == null || weather.getCurrentWeather() == null) {
                model.addAttribute("error", "Unable to fetch weather data for " + city + ". Please try again.");
                model.addAttribute("weather", createDefaultWeatherResponse());
                model.addAttribute("location", location.getDisplayName());
                model.addAttribute("weatherIcon", "cloud-question");
                model.addAttribute("weatherText", "No data available");
                return "weather";
            }

            if (DEFAULT_CITY.equals(city)) {
                cacheDefaultWeather(location, weather);
            }

            populateModelWithWeather(model, weather, location, city);

        } catch (Exception e) {
            System.err.println("Exception in WeatherController: " + e.getMessage());
            e.printStackTrace();

            handleException(model, e, city);
        }
        return "weather";
    }

    private List<GeocodingResponse> searchCityWithRetry(String city, int maxRetries) {
        List<GeocodingResponse> locations = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                System.out.println("Searching for city: " + city + " (attempt " + attempt + "/" + maxRetries + ")");
                locations = weatherService.searchCity(city);

                if (locations != null && !locations.isEmpty()) {
                    System.out.println("Successfully found " + locations.size() + " locations for " + city);
                    return locations;

                }
                System.out.println("No locations found for " + city + " on attempt " + attempt);

                if (attempt < maxRetries) {
                    Thread.sleep(1000 * attempt);
                }
            } catch (Exception e) {
                System.err.println("Error searching for city " + city + " on attempt " + attempt + ": " + e.getMessage());
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(1000 * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        return locations;
    }

    private WeatherResponse getWeatherWithRetry(double latitude, double longitude, int maxRetries) {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                System.out.println("Getting weather for coordinates: " + latitude + ", " + longitude + " (attempt " + attempt + "/" + maxRetries + ")");
                WeatherResponse weather = weatherService.getWeather(latitude, longitude);

                if (weather != null && weather.getCurrentWeather() != null) {
                    System.out.println("Successfully retrieved weather data");

                    return weather;
                }

                System.out.println("Weather data is null on attempt " + attempt);

                if (attempt < maxRetries) {
                    Thread.sleep(1000 * attempt);
                }

            } catch (Exception e) {
                System.err.println("Error getting weather on attempt " + attempt + ": " + e.getMessage());

                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(1000 * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;

                    }
                }
            }
        }
        return null;
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

    private void populateModelWithWeather(Model model, WeatherResponse weather, GeocodingResponse
            location, String city) {
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
        model.addAttribute("weatherText", weatherInfo.get("text"));
    }

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