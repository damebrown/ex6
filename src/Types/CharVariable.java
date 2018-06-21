package Types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * the class represents a char variable
 */
public class CharVariable extends Variable {

    private final static Pattern VALIDITY_PATTERN = Pattern.compile("[\\\"\\\'].[\\\"\\\']"); //todo should include "" ?

    /**
     * @param variableString the variable declaration line
     * @param isGlobal turned on in case it is global
     * @param isFinal turned on in case it is final
     * @throws IllegalTypeException
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
