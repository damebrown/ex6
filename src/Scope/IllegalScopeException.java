package Scope;

import main.IllegalCodeException;

public class IllegalScopeException extends IllegalCodeException {

    public IllegalScopeException(String errorMessage){
        super(errorMessage);
    }

    public IllegalScopeException(){
        super();
    }
}
