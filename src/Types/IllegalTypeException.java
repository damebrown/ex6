package Types;

import main.IllegalCodeException;

public class IllegalTypeException extends IllegalCodeException {

    public IllegalTypeException(String errorMessage){
        super(errorMessage);
    }

    public IllegalTypeException(){
        super();
    }
}
