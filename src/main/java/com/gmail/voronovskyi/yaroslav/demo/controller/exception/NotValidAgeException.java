package com.gmail.voronovskyi.yaroslav.demo.controller.exception;

public class NotValidAgeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotValidAgeException(String message) {
        super(message);
    }
}
