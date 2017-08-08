package pl.nowacki.places.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.nowacki.places.service.PlacesService;

@RestController
public class PlacesRestController {

    private PlacesService placesService;

    @Autowired
    public PlacesRestController(PlacesService placesService) {
        this.placesService = placesService;
    }

    @ResponseBody
    @GetMapping(value = "/{country}/{city}/{name}", produces = APPLICATION_JSON_VALUE)
    String getPlaces(@PathVariable String country, @PathVariable String city, @PathVariable String name) {
        return placesService.searchPlaces(country, city, name);
    }
}
