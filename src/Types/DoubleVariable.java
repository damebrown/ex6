package Types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DoubleVariable extends Variable {

    private final static Pattern VALIDITY_PATTERN = Pattern.compile("(-?\\d+)(.\\d*)?+");  //todo is 093 valid?

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
