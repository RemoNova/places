package pl.nowacki.places.configuration;

import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FacebookConfiguration {

    @Bean
    public Facebook getFacebookInstance() {
        return new FacebookFactory().getInstance();
    }
}
