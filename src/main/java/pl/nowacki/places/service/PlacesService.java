package pl.nowacki.places.service;

import facebook4j.*;
import org.springframework.stereotype.Service;
import pl.nowacki.places.data.LocationData;
import pl.nowacki.places.execptions.FacebookConnectionException;
import pl.nowacki.places.execptions.PlaceNotFoundException;
import pl.nowacki.places.utils.JsonConverter;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlacesService {

    private static final String NAME = "name";
    private static final String LOCATION = "location";
    private Facebook facebook;

    public PlacesService(Facebook facebook) {
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
        return facebook.searchPlaces(String.format("%s %s %s", country, city, name), getReadingParams());
    }

    private List<Place> filterResponse(ResponseList<Place> places, String country, String city) {
        return places.stream().filter(place -> isValidPlace(country, city, place)).collect(Collectors.toList());
    }

    private boolean isValidPlace(String country, String city, Place place) {
        if (hasAllDetails(place)) {
            return (place.getLocation().getCountry().equalsIgnoreCase(country) &&
                    place.getLocation().getCity().equalsIgnoreCase(city));
        } else {
            return false;
        }
    }

    private boolean hasAllDetails(Place place) {
        return place.getLocation().getCountry() != null && place.getLocation().getCity() != null
                && place.getLocation().getLatitude() != null & place.getLocation().getLongitude() != null;
    }

    private String convertResponse(List<Place> places) {
        return convertResponseToJson(places.stream().map(this::getLocationData).collect(Collectors.toList()));
    }

    private LocationData getLocationData(Place place) {
        return new LocationData(place.getName(), place.getLocation().getLatitude(), place.getLocation().getLongitude());
    }

    private Reading getReadingParams() {
        return new Reading().fields(NAME, LOCATION);
    }

    private String convertResponseToJson(List<LocationData> locations) {
        if (locations.size() == 0)
            throw new PlaceNotFoundException();
        return JsonConverter.convertToJson(locations.size() == 1 ? locations.get(0) : locations);
    }
}
