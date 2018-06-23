package oop.ex6.Scope;

import oop.ex6.main.IllegalCodeException;

/**
 * An Illegal Scope exception class.
 * extends IllegalCodeException.
 */
class IllegalScopeException extends IllegalCodeException {

    /*Constructors*/

    /**
     * default IllegalScopeException constructor. calls super();
     */
    IllegalScopeException() {
        super();
    }

    /**
     * error message argument constructor.
     * @param errorMessage the error message.
     */
    IllegalScopeException(String errorMessage) {
        super(errorMessage);
    }

}
