package oop.ex6.Types;

import oop.ex6.main.IllegalCodeException;

/**
 * An Illegal Type exception class.
 * extends IllegalCodeException.
 */
public class IllegalTypeException extends IllegalCodeException {

    /*Constructors*/

    /**
     * default IllegalScopeException constructor. calls super();
     */
    public IllegalTypeException() {
        super();
    }

    /**
     * error message argument constructor.
     * @param errorMessage the error message.
     */
    public IllegalTypeException(String errorMessage) {
        super(errorMessage);
    }


}
