package br.com.ginezgit.service.exception;

public class InsufficientAccountBalanceException extends RuntimeException {
    public InsufficientAccountBalanceException() {
    }

    public InsufficientAccountBalanceException(String message) {
        super(message);
    }

    public InsufficientAccountBalanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientAccountBalanceException(Throwable cause) {
        super(cause);
    }

    public InsufficientAccountBalanceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
