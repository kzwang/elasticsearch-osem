package com.github.kzwang.osem.exception;


public class ElasticSearchOsemException extends RuntimeException {

    public ElasticSearchOsemException(String message) {
        super(message);
    }

    public ElasticSearchOsemException(String message, Throwable cause) {
        super(message, cause);
    }

    public ElasticSearchOsemException(Throwable cause) {
        super(cause);
    }

}
