package oop.ex6.Types;

import oop.ex6.main.IllegalCodeException;

public class IllegalTypeException extends IllegalCodeException {

    public IllegalTypeException(String errorMessage){
        super(errorMessage);
    }

    public IllegalTypeException(){
        super();
    }
}
