package pl.nowacki.places.utils

import spock.lang.Specification

class JsonConventerSpecification extends Specification {

    def "Should convert object to json"() {
        given:
        def value1 = '1'
        def value2 = '2'
        def list = []
        list << new TestObject(value1, value2)

        when:
        def json = JsonConverter.convertToJson(list)

        then:
        json.contains("\"value1\":\"${value1}\"")
        json.contains("\"value2\":\"${value2}\"")
    }

    private class TestObject {
        String value1, value2

        private TestObject(String a, String b) {
            this.value1 = a
            this.value2 = b
        }
    }
}
