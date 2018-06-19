package Scope;

import main.IllegalCodeException;

public class IllegalScopeException extends IllegalCodeException {

    IllegalScopeException(String errorMessage){
        super(errorMessage);
    }

    IllegalScopeException(){
        super();
    }
}
