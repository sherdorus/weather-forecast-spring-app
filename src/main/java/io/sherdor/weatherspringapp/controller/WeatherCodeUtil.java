package io.sherdor.weatherspringapp.controller;

import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WeatherCodeUtil {
    public enum WeatherCode {
        CLEAR(0, "sun", "Clear"),
        PARTLY_CLOUDY(new int[]{1, 2, 3}, "cloud-sun", "Partly cloudy"),
        FOG(new int[]{45, 48}, "cloud-fog", "Fog"),
        DRIZZLE(new int[]{51, 53, 55}, "cloud-drizzle", "Drizzle"),
        FREEZING_DRIZZLE(new int[]{56, 57}, "cloud-sleet", "Freezing drizzle"),
        RAIN(new int[]{61, 63, 65}, "cloud-rain", "Rain"),
        FREEZING_RAIN(new int[]{66, 67}, "cloud-snow", "Freezing rain"),
        SNOW(new int[]{71, 73, 75}, "snow", "Snow"),
        SNOW_GRAINS(77, "snowflake", "Snow grains"),
        HEAVY_RAIN(new int[]{80, 81, 82}, "cloud-rain-heavy", "Heavy rain"),
        SNOWFALL(new int[]{85, 86}, "cloud-snow", "Snowfall"),
        THUNDERSTORM(new int[]{95, 96, 99}, "cloud-lightning", "Thunderstorm"),
        UNKNOWN(-1, "question-circle", "Unknown");

        private final int[] codes;
        private final String icon;
        private final String text;

        WeatherCode(int code, String icon, String text) {
            this.codes = new int[]{code};
            this.icon = icon;
            this.text = text;
        }

        WeatherCode(int[] codes, String icon, String text) {
            this.codes = codes;
            this.icon = icon;
            this.text = text;
        }

        public static WeatherCode fromCode(@Nullable Integer code) {
            if (code == null) {
                return UNKNOWN;
            }
            for (WeatherCode wc : values()) {
                for (int c : wc.codes) {
                    if (c == code) {
                        return wc;
                    }
                }
            }
            return UNKNOWN;
        }

        public Map<String, String> toMap() {
            Map<String, String> info = new HashMap<>();
            info.put("icon", icon);
            info.put("text", text);
            return info;
        }
    }

    public Map<String, String> getWeatherInfo(@Nullable Integer code) {
        return WeatherCode.fromCode(code).toMap();
    }
}