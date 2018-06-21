package Types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharVariable extends Variable {

    private final static Pattern VALIDITY_PATTERN = Pattern.compile("[\\\"\\\'].[\\\"\\\']"); //todo should include "" ?

    /**
     *
     * @param variableString
     * @param isGlobal
     * @param isFinal
     */
    public CharVariable(String variableString,boolean isGlobal,boolean isFinal) throws IllegalTypeException {
        super(variableString, isGlobal,isFinal);
        type="char";
    }


    @Override
    public  boolean isValid(String value){
        Matcher charMatcher = VALIDITY_PATTERN.matcher(value);
        if(charMatcher.find())
            return true;
        return false;
    }


}
