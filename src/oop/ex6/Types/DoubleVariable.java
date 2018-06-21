package oop.ex6.Types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 *the class represents a double variable
 */
class DoubleVariable extends Variable {

    private final static Pattern VALIDITY_PATTERN = Pattern.compile("(-?\\d+)(.\\d*)?+");  //todo is 093 valid?

    /**
     *
     * @param variableString the variable declaration line
     * @param isGlobal turned on in case it is global
     * @param isFinal turned on in case it is final
     * @throws IllegalTypeException
     */
    public DoubleVariable(String variableString,boolean isGlobal,boolean isFinal) throws IllegalTypeException {
        super(variableString,isGlobal,isFinal);
        type="double";
    }


    @Override
    public  boolean isValid(String value){
        Matcher doubleMatcher = VALIDITY_PATTERN.matcher(value);
        return doubleMatcher.matches();
    }

}
