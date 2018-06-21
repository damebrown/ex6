package Types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BooleanVariable extends Variable {

    private final static Pattern VALIDITY_PATTERN = Pattern.compile("((-?\\d+(.\\d*)?+)|true|false)");  //todo is 093 valid?

    public BooleanVariable(String variableString,boolean isGlobal,boolean isFinal) throws IllegalTypeException {
        super(variableString,isGlobal,isFinal);
        type="boolean";
    }


    @Override
    public  boolean isValid(String value){
        Matcher booleanMatcher = VALIDITY_PATTERN.matcher(value);
        return booleanMatcher.matches();
    }


}
