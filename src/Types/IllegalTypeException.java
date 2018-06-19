package Types;

import main.IllegalCodeException;

public class IllegalTypeException extends IllegalCodeException {

    IllegalTypeException(String errorMessage){
        super(errorMessage);
    }

    IllegalTypeException(){
        super();
    }
}
