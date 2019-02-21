package br.com.ginezgit.controller.exception;

public class InvalidInputParameterValueException extends RuntimeException {

    public InvalidInputParameterValueException() {
    }

    public InvalidInputParameterValueException(String message) {
        super(message);
    }

    public InvalidInputParameterValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInputParameterValueException(Throwable cause) {
        super(cause);
    }

    public InvalidInputParameterValueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static String buildInvalidParameterValueMessage(String field) {
        return field + " value is invalid or not set";
    }
}
