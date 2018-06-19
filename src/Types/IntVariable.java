package Types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class IntVariable extends Variable{

    private final static Pattern VALIDITY_PATTERN = Pattern.compile("-?\\d+");

    public IntVariable(String variableString,boolean isGlobal,boolean isFinal) throws IllegalTypeException {

        super(isGlobal,isFinal);
        if(variableString.contains("=")){
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
        Matcher intMatcher = VALIDITY_PATTERN.matcher(value);

        return intMatcher.matches();
    }

    @Override
    public void setValue(String value) {
        if(isValid(value))
            this.value =value;
    }
}
