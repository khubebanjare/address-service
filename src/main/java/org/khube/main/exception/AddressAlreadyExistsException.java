package org.khube.main.exception;

public class AddressAlreadyExistsException extends RuntimeException{

    public AddressAlreadyExistsException(String message) {
        super(message);
    }

    public AddressAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public AddressAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
