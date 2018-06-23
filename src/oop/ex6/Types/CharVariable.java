package oop.ex6.Types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * the class represents a char variable
 */
public class CharVariable extends Variable {

    private final static Pattern VALIDITY_PATTERN = Pattern.compile("[\\\"\\\'].[\\\"\\\']");

    /**
     * @param variableString the variable declaration line
     * @param isGlobal       turned on in case it is global
     * @param isFinal        turned on in case it is final
     * @throws IllegalTypeException in case of wrong variable instanceiation
     */
    CharVariable(String variableString, boolean isGlobal, boolean isFinal) throws IllegalTypeException {
        super(variableString, isGlobal, isFinal);
        type = "char";
    }


    @Override
    /*
    checks for this class's type's variable's validity
     */
    public boolean isValid(String value) {
        Matcher charMatcher = VALIDITY_PATTERN.matcher(value);
        return charMatcher.find();
    }


}
