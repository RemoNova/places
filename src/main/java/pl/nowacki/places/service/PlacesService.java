package pl.nowacki.places.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import facebook4j.*;
import org.springframework.stereotype.Service;
import pl.nowacki.places.dao.LocationData;
import pl.nowacki.places.execptions.FacebookConnectionException;
import pl.nowacki.places.execptions.PlaceNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlacesService {

    private static final String NAME = "name";
    private static final String LOCATION = "location";
    private Facebook facebook;

    public PlacesService(Facebook facebook){
        this.facebook = facebook;
    }

    public String searchPlaces(String country, String city, String name) {
        ResponseList<Place> places;
        try {
            places = getPlaces(country, city, name, facebook);

            if (places.size() == 0) {
                throw new PlaceNotFoundException();
            } else {
                return convertResponse(filterResponse(places, country, city));
            }
        } catch (FacebookException e) {
            e.printStackTrace();
            throw new FacebookConnectionException();
        }

    }

    private ResponseList<Place> getPlaces(String country, String city, String name,
                                          Facebook facebook) throws FacebookException {
        return facebook.searchPlaces(country + " " + city + " " + name, getReadingParams());
    }

    private List<Place> filterResponse(ResponseList<Place> places, String country, String city) {
        return places.stream().parallel().filter(place -> isValidPlace(country, city, place))
                .collect(Collectors.toList());
    }

    private boolean isValidPlace(String country, String city, Place place) {
        return place.getLocation().getCountry().equalsIgnoreCase(country) &&
                place.getLocation().getCity().equalsIgnoreCase(city);
    }

    private String convertResponse(List<Place> places) {

        return convertResponseToJson(places.stream().
                parallel().map(this::getLocationData).collect(Collectors.toList()));
    }

    private LocationData getLocationData(Place place) {
        return new LocationData(place.getName(), place.getLocation().getLatitude(), place.getLocation().getLongitude());
    }

    private Reading getReadingParams() {
        return new Reading().fields(NAME, LOCATION);
    }

    private String convertResponseToJson(List<LocationData> locations) {
        return convertToJson(locations.size() == 1 ? locations.get(0) : locations);
    }

    private static String convertToJson(Object data) {
        try {
            return new ObjectMapper().writeValueAsString(data);
        } catch (IOException e) {
            throw new RuntimeJsonMappingException("Failed to convert response to JSON");
        }
    }
}
