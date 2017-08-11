package pl.nowacki.places.configuration

import facebook4j.Facebook
import spock.lang.Specification
import spock.lang.Subject

class FacebookConfigurationSpecification extends Specification {

    @Subject
    FacebookConfiguration facebookConfiguration = new FacebookConfiguration();

    def "should return facebook instance"() {
        when:
        def instance = facebookConfiguration.getFacebookInstance()
        then:
        noExceptionThrown()
        assert (instance instanceof Facebook)
    }

}
