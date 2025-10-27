package com.pizzeria.pizzeriaservice.geocoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Component
public class GeocodingClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeocodingClient.class);

    private final RestClient restClient;
    private final boolean geocodingEnabled;

    public GeocodingClient(@Value("${geocoding.enabled:true}") boolean geocodingEnabled,
                           @Value("${geocoding.user-agent:PizzeriaDemo/1.0}") String userAgent) {
        this.geocodingEnabled = geocodingEnabled;
        this.restClient = RestClient.builder()
                .baseUrl("https://nominatim.openstreetmap.org")
                .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Optional<LatLon> geocode(String address, String city) {
        if (!geocodingEnabled || !StringUtils.hasText(address)) {
            return Optional.empty();
        }

        String query = StringUtils.hasText(city) ? "%s, %s".formatted(address, city) : address;

        try {
            GeocodingResponse[] response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("format", "json")
                            .queryParam("limit", "1")
                            .queryParam("q", query)
                            .build())
                    .retrieve()
                    .body(GeocodingResponse[].class);

            if (response != null && response.length > 0 && response[0].isValid()) {
                try {
                    double lat = Double.parseDouble(response[0].lat());
                    double lon = Double.parseDouble(response[0].lon());
                    return Optional.of(new LatLon(lat, lon));
                } catch (NumberFormatException ex) {
                    LOGGER.warn("Unable to parse geocoding response for '{}': {}", query, ex.getMessage());
                }
            }
        } catch (RestClientException ex) {
            LOGGER.warn("Geocoding request failed for '{}': {}", query, ex.getMessage());
        }

        return Optional.empty();
    }

    public record LatLon(double latitude, double longitude) {
    }

    private record GeocodingResponse(String lat, String lon) {
        boolean isValid() {
            return StringUtils.hasText(lat) && StringUtils.hasText(lon);
        }
    }
}
