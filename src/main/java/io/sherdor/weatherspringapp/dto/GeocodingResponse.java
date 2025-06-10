package io.sherdor.weatherspringapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GeocodingResponse {
    @JsonProperty("lat")
    private double latitude;

    @JsonProperty("lon")
    private double longitude;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("address")
    private Address address;

    @Data
    public static class Address {
        private String city;
        private String town;
        private String village;

        public String getCityName() {
            return city != null ? city :
                    town != null ? town :
                            village != null ? village : "Unknown";
        }
    }

    public String getDisplayName() {
        return address != null ? address.getCityName() : displayName;
    }
}