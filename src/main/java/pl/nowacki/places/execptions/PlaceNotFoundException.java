package pl.nowacki.places.execptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No place found")
public class PlaceNotFoundException extends RuntimeException {
}
