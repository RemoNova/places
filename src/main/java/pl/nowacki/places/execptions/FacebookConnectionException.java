package pl.nowacki.places.execptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, reason = "Facebook API is not available")
public class FacebookConnectionException extends RuntimeException {
}
