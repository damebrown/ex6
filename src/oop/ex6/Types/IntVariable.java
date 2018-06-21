package oop.ex6.Types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * the class represents an integer variable
 */
class IntVariable extends Variable{

    private final static Pattern VALIDITY_PATTERN = Pattern.compile("-?\\d+");

    /**
     *
     * @param variableString the variable declaration line
     * @param isGlobal turned on in case it is global
     * @param isFinal turned on in case it is final
     * @throws IllegalTypeException
     */
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
