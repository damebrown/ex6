package Types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class IntVariable extends Variable{

    private final static Pattern VALIDITY_PATTERN = Pattern.compile("-?\\d+");

    public IntVariable(String variableString,boolean isGlobal,boolean isFinal) throws IllegalTypeException {

        super(isGlobal,isFinal);
        type="int";
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
    public void setValue(String assignValue) throws IllegalTypeException { //todo way more indicative!!!!!!!!
        String varToAssign = Variable.referenceAssign(assignValue);

        if(!varToAssign.equals(""))
            assignValue = varToAssign;
        if(isValid(assignValue)) {
            if(!this.isFinal) {
                this.value = assignValue;
            }throw new IllegalTypeException("Value cannot be assigned into final variable");
        }throw new IllegalTypeException("Illegal type, should be integer type value");
    }

}
