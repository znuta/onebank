package com.onebank.onebank.exception;


public class BranchlessBankingException extends RuntimeException {

    public BranchlessBankingException() {
        super("Failed to perform the requested action");
    }

    public BranchlessBankingException(Throwable cause) {
        super("Failed to perform the requested action", cause);
    }

    public BranchlessBankingException(String message) {
        super(message);
    }

    public BranchlessBankingException(String message, Throwable cause) {
        super(message, cause);
    }
}
