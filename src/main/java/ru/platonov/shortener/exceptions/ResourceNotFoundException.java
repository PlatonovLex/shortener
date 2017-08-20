package ru.platonov.shortener.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ResourceNotFoundException.
 * <p>
 *     Appears when some resource not found.
 *     <br>Tells the controller that a page with 404 error should be displayed
 * </p>
 *
 * @author Platonov Alexey
 * @since 18.08.2017
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{

    private static final long serialVersionUID = -3916774765912003290L;

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}