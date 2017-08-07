package pl.nowacki.places.rest;

import facebook4j.*;
import org.springframework.web.bind.annotation.*;
import pl.nowacki.places.dao.LocationData;


import java.util.LinkedList;
import java.util.List;

@RestController
public class PlacesRestController {
    @ResponseBody
    @GetMapping(value = "/{country}/{city}/{name}")
    ResponseList<Place> getPlaces(@PathVariable String country, @PathVariable String city, @PathVariable String name) {
        List<LocationData> locationDataList = new LinkedList<>();
        LocationData locationData = new LocationData();
        locationData.setName(name);
        locationData.setLatitude(5300000L);
        locationData.setLongitude(545000L);
        locationDataList.add(locationData);

        Facebook facebook = new FacebookFactory().getInstance();
        ResponseList<Place> places = null;
        try {
            places = facebook.searchPlaces(name);
        } catch (FacebookException e) {
            e.printStackTrace();
        }

        return places;
    }
}
