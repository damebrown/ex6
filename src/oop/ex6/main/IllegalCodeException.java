package oop.ex6.main;

/**
 * An Illegal Code exception class. the super class of scope exception and type exception.
 * extends java's Exceptions.
 */
public class IllegalCodeException extends Exception {

    private static final String ILLEGAL_CODE = "ERROR: Illegal Code";

    /*Constructors*/

    /**
     * default IllegalCodeException constructor. calls super();
     */
    public IllegalCodeException() {
        super(ILLEGAL_CODE);
    }

    /**
     * error message argument constructor.
     * @param errorMessage the error message.
     */
    public IllegalCodeException(String errorMessage) {
        super(errorMessage);
    }
}
