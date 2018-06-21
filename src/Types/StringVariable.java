package Types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class StringVariable extends Variable {

    private final static Pattern VALIDITY_PATTERN = Pattern.compile("[\\\"\\\'].*[\\\"\\\']");  //todo include "" ?

    /**
     *
     * @param variableString
     * @param isGlobal
     * @param isFinal
     * @throws IllegalTypeException
     */
    public StringVariable(String variableString, boolean isGlobal, boolean isFinal) throws IllegalTypeException {
        super(variableString,isGlobal, isFinal);
        type = "String";
    }


    @Override
    public  boolean isValid(String value) {
        Matcher stringMatcher = VALIDITY_PATTERN.matcher(value);
        return stringMatcher.matches();
    }

}
