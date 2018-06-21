package Types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class IntVariable extends Variable{

    private final static Pattern VALIDITY_PATTERN = Pattern.compile("-?\\d+");

    public IntVariable(String variableString,boolean isGlobal,boolean isFinal) throws IllegalTypeException {
        super(variableString,isGlobal,isFinal);
        type="int";
    }


    @Override
    public  boolean isValid(String value){
        Matcher intMatcher = VALIDITY_PATTERN.matcher(value);
        return intMatcher.matches();
    }


}
