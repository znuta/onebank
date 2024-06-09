package com.onebank.onebank.exception;

public class DuplicateObjectException extends BranchlessBankingException {

    public DuplicateObjectException(){super("The target object already exists");}

    public DuplicateObjectException(String message){super(message);}
}
