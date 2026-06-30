package cl.usm.sansaweigh.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalWeighingStateException extends RuntimeException {
    public IllegalWeighingStateException(String message) {
        super(message);
    }
}