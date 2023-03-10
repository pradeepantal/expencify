package com.expensify.expensify.util;

public class CustomException extends RuntimeException{

    final String message;

    public CustomException(String message){
        super();
        this.message = message;
    }
}
