package pl.nowacki.places.service

import pl.nowacki.places.utils.JsonConverter
import spock.lang.Specification

class JsonConventerSpecification extends Specification {

    def "Should convert object to json"() {
        given:
        def valueA = '1'
        def valueB = '2'
        def list = []
        list << new TestObject(valueA, valueB)

        when:
        def json = JsonConverter.convertToJson(list)

        then:
        json.contains("\"key1\":\"${valueA}\"")
        json.contains("\"key2\":\"${valueB}\"")
    }

    private class TestObject {
        String key1
        String key2

        private TestObject(String a, String b) {
            this.key1 = a
            this.key2 = b
        }
    }
}
