package pl.nowacki.places.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationData {

    @JsonProperty("name")
    private String name;
    @JsonProperty("latitiude")
    private Double latitude;
    @JsonProperty("longitiude")
    private Double longitude;

    public LocationData(String name, Double latitude, Double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
