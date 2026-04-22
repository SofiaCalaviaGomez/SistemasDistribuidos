package com.practica2.peliculas.exception;
import lombok.Getter;

@Getter
public class PythonApiException extends RuntimeException {
    private final String errorCode;

    public PythonApiException(String errorCode){
        this.errorCode = errorCode;
    }
}
