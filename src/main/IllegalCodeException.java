package main;

/**
 * An Illegal Code exception class
 */
public class IllegalCodeException extends Exception{

    private static final String ILLEGAL_CODE = "ERROR: Illegal Code";

    public IllegalCodeException(){
        super(ILLEGAL_CODE);
    }

    public IllegalCodeException(String errorMessage){
        super(errorMessage);
    }
}
