package oop.ex6.Scope;

import oop.ex6.main.IllegalCodeException;

public class IllegalScopeException extends IllegalCodeException {

    public IllegalScopeException(String errorMessage) {
        super(errorMessage);
    }

    public IllegalScopeException() {
        super();
    }
}
