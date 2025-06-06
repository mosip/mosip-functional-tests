package io.mosip.testrig.authentication.demo.service.exception;

public class PartnerKeyException extends Exception{

    public PartnerKeyException(String message) {
        super(message);
    }

    public PartnerKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
