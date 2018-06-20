package Types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BooleanVariable extends Variable {

    private final static Pattern VALIDITY_PATTERN = Pattern.compile("((-?\\d+(.\\d*)?+)|true|false)");  //todo is 093 valid?

    public BooleanVariable(String variableString,boolean isGlobal,boolean isFinal) throws IllegalTypeException {

        super(isGlobal,isFinal);
        type="boolean";
        if(variableString.contains("=")){ //todo verify empty string wont get ere
            String[] toAssign = splitter(variableString);
            this.name = toAssign[0];
            if(isValid(toAssign[1])) {
                this.value = toAssign[1];
            }
            else
                throw new IllegalTypeException();
        }
        else{
            this.name = variableString;
        }
    }
    @Override
    public  boolean isValid(String value){
        Matcher booleanMatcher = VALIDITY_PATTERN.matcher(value);

        return booleanMatcher.matches();
    }

    @Override
    public void setValue(String value) {
        if(isValid(value))
            this.value =value;
    }
}
