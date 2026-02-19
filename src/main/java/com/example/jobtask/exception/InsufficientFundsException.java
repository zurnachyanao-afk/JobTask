package com.example.jobtask.exception;

public class InsufficientFundsException extends RuntimeException{

    public InsufficientFundsException() {
        super("Insufficient funds");
    }

}
