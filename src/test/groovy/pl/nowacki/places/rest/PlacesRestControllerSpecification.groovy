package pl.nowacki.places.rest

import facebook4j.Facebook
import facebook4j.Place
import facebook4j.ResponseList
import facebook4j.internal.json.PlaceJSONImpl
import facebook4j.internal.json.ResponseListImpl
import facebook4j.internal.org.json.JSONArray
import facebook4j.internal.org.json.JSONObject
import pl.nowacki.places.data.LocationData
import pl.nowacki.places.execptions.FacebookConnectionException
import pl.nowacki.places.execptions.PlaceNotFoundException
import pl.nowacki.places.service.PlacesService
import pl.nowacki.places.utils.JsonConverter
import spock.lang.Specification
import spock.lang.Subject

class PlacesRestControllerSpecification extends Specification {

    def country = 'Poland'
    def city = 'Poznan'
    def name = 'Egnyte Poland'
    def latitude = 52.404167557908
    def longitude = 16.940044275923
    def emptyListJson = '[]'
    def placeTemplate = '{"name":%s,"location":{"zip":"00-000","country":"%s",' +
            '"city":"%s","street":"Mostowa","latitude":%s,"longitude":%s},"id":"1"}'
    def singlePlaceTemplate = '{"data":[' + placeTemplate + ']}'
    def placesListTemplate = '{"data":[' + placeTemplate + ', ' + placeTemplate + ']}'
    Facebook facebookMock = Mock(Facebook)
    PlacesService placesService = new PlacesService(facebookMock)

    @Subject
    PlacesRestController placesRestController = new PlacesRestController(placesService)

    def "Should return single place as json"() {
        given:
        def name = 'Test Place'
        facebookMock.searchPlaces(_, _) >> getMockedResult(generateSinglePlaceAsJson(name, latitude, longitude))

        when:
        def jsonResult = placesRestController.getPlaces(country, city, this.name)

        then:
        noExceptionThrown()
        assert !isList(jsonResult)
        jsonResult == JsonConverter.convertToJson(new LocationData(name, latitude, longitude))
    }

    def "Should return list of places when more than one result found"() {
        given:
        def name1 = 'Test Name'
        def name2 = 'Test Name'
        facebookMock.searchPlaces(_, _) >> getMockedResult(generatePlacesListAsJson(name1, name2, latitude,
                longitude))

        when:
        def jsonResult = placesRestController.getPlaces(country, city, name)

        then:
        assert isList(jsonResult)
        jsonResult == JsonConverter.convertToJson([new LocationData(name1, latitude, longitude),
                                                   new LocationData(name2, latitude, longitude)])
    }

    def "Should filter result if there is incorrect city in response"() {
        given:
        facebookMock.searchPlaces(_, _) >> getMockedResult(generateSinglePlaceAsJson(name, latitude, longitude))

        when:
        placesRestController.getPlaces(country, 'bydgoszcz', name)

        then:
        thrown(PlaceNotFoundException)
    }

    def "Should throw PlaceNotFoundException when no correct place found"() {
        given:
        facebookMock.searchPlaces(_, _) >> getMockedResult(
                generateSingleIncorrectPlaceAsJson("incorrect_place", latitude, longitude)
        )

        when:
        placesRestController.getPlaces(country, city, name)

        then:
        thrown(PlaceNotFoundException)
    }

    def "Should throw PlaceNotFoundException when no results"() {
        given:
        def input = '{"data":' + emptyListJson + '}'
        facebookMock.searchPlaces(_, _) >> getMockedResult(input)

        when:
        placesRestController.getPlaces(country, city, name)

        then:
        thrown(PlaceNotFoundException)
    }

    def "Should throw FacebookConnectionException when no connection to FB API"() {
        given:
        facebookMock.searchPlaces(_, _) >> { throw new FacebookConnectionException() }

        when:
        placesRestController.getPlaces(country, city, name)

        then:
        thrown(FacebookConnectionException)
    }

    def isList(String jsonResult) {
        char firstChar = '['
        char lastChar = ']'
        jsonResult.charAt(0) == firstChar && jsonResult.charAt(jsonResult.length() - 1) == lastChar
    }

    def getMockedResult(jsonInput) {
        JSONObject json = new JSONObject(jsonInput)
        JSONArray list = json.getJSONArray('data')
        def size = list.length()
        ResponseList<Place> places = new ResponseListImpl<Place>(size, json)
        size.times {
            places.add(new PlaceJSONImpl(list.getJSONObject(it)))
        }
        places
    }

    def generateSinglePlaceAsJson(name, latitude, longitude) {
        String.format(singlePlaceTemplate, name, country, city, latitude, longitude)
    }

    def generatePlacesListAsJson(name1, name2, latitude, longitude) {
        String.format(placesListTemplate, name1, country, city, latitude, longitude,
                name2, country, city, latitude, longitude)
    }

    def generateSingleIncorrectPlaceAsJson(name, latitude, longitude) {
        String.format(singlePlaceTemplate, name, null, null, latitude, longitude)
    }
}
